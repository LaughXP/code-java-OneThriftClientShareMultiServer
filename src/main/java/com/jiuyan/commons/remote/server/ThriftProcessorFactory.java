/**
 * 
 */
package com.jiuyan.commons.remote.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.ProcessFunction;
import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *@Title: ThriftProcessorFactory.java
 *@Package com.jiuyan.commons.thrift.server
 *@Description:通过反射初始化所有的processor并保存在容器中
 *@author xiaoyu
 *@date 2015年4月7日 下午6:14:09
 */
public class ThriftProcessorFactory {
	
	private static Logger logger = LoggerFactory.getLogger(ThriftProcessorFactory. class );
	
	private static Map<String , Object> processorConnectService = new HashMap<String , Object>();
	
	@SuppressWarnings("rawtypes")
	private static Map<String,  ProcessFunction<?, ? extends TBase>> methodMap = new HashMap<String, ProcessFunction<?, ? extends TBase>>();
	
	private static Map<String , String> methodConnectprocessor =  new HashMap<String , String>();
	
	public static String getProcessor(String method) {
		return methodConnectprocessor.get(method);
	}
	
	public static Object getService(String processorKey) {
		return processorConnectService.get(processorKey);
	}
	
	public static Object getMethod(String key) {
		return methodMap.get(key);
	}
	
	public static void init(ServerConfig serverConfig) {
		List<ServerConfig.Service> services = serverConfig.getServices();
		for(ServerConfig.Service service : services) {
			if(StringUtils.isBlank(service.getProcessorName()) || StringUtils.isBlank(service.getServiceImplName())) {
				continue;
			}
			String[] classNameProcessorArray = service.getProcessorName().split("\\.");
			String classNameProcessor = classNameProcessorArray[classNameProcessorArray.length - 1].split("\\$")[0];
			initProcessorAndMethod(classNameProcessor, service.getProcessorName(), service.getServiceImplName());
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void initProcessorAndMethod(String processorKey ,String processorName , String serviceImpl) {
		Object ob = null;
		try {
			Class<?> processor = Class.forName(processorName);
			Constructor<?> cons[]=processor.getConstructors();
			Class<?> serviceClazz = Class.forName(serviceImpl);
			Object service = serviceClazz.newInstance();
			for(int i = 0 , len = cons.length; i < len ; i++) {
				if(cons[i].getParameterTypes().length == 1) {
					ob = cons[i].newInstance(service);
					break;
				}
			}
			if( ob == null) {
				logger.error("cant not new "+processorName);
				throw new RuntimeException("cant not new "+processorName);
			}
			Method getProcessMap = ob.getClass().getDeclaredMethod("getProcessMap", Map.class);
			if(getProcessMap == null) {
				logger.error("cant not get getProcessMap method in "+processorName);
				throw new NullPointerException("cant not get getProcessMap method in "+processorName);
			}
			getProcessMap.setAccessible(true);
			Object result = getProcessMap.invoke(ob, new Object[]{new HashMap<String, ProcessFunction<?, ? extends TBase>>()});
			methodMap.putAll((Map<String, ProcessFunction<?, ? extends TBase>>)result);
			//记录method:service
			for(Method method: service.getClass().getDeclaredMethods()) {
				String methodName = method.getName();
				if(methodConnectprocessor.get(methodName) != null) {
					logger.error("has nulti methodName : "+methodName+", serviceImpl:"+serviceImpl);
					throw new RuntimeException("has multi methodName : "+methodName+", serviceImpl:"+serviceImpl);
				}
				methodConnectprocessor.put(methodName, processorKey);
			}
			processorConnectService.put(processorKey,service);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
