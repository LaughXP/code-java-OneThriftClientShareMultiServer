/**
 * 
 */
package com.jiuyan.commons.remote.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	
	private static Map<String , Object> processorMap = new HashMap<String , Object>();
	
	private static Map<String , Object> methodMap = new HashMap<String , Object>();
	
	public static String monitor_processor = null;
	
	public static String other_processor = null;
	
	private static Map<String , Object> addProcessor(String key , Object processor) {
		processorMap.put(key, processor);
		return processorMap;
	}
	
	public static Object getProcessor(String key) {
		return processorMap.get(key);
	}
	
	private static Map<String , Object> addMethod(String key , Object method) {
		methodMap.put(key, method);
		return methodMap;
	}
	
	public static Object getMethod(String key) {
		return methodMap.get(key);
	}
	
	public static void init(String processorName , String  serviceImpl) {
		monitor_processor = "StatService";
		//获得service的名字
		other_processor = processorName.split("\\.")[5].split("\\$")[0];
		initProcessorAndMethod(other_processor, processorName, serviceImpl);
		initProcessorAndMethod(monitor_processor, "com.jiuyan.commons.remote.service.StatService$Processor", "com.jiuyan.commons.remote.service.StatServiceImpl");
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
			Map<String,  ProcessFunction<?, ? extends TBase>> functionMap = null;
			Object result = getProcessMap.invoke(ob, new Object[]{new HashMap<String, ProcessFunction<?, ? extends TBase>>()});
			functionMap = (Map<String, ProcessFunction<?, ? extends TBase>>)result;
			if(functionMap != null) {
				Set<String> keys = functionMap.keySet();
	              for (String key : keys) {
	                   addMethod(key,functionMap.get(key));
	              }
			}
			addProcessor(processorKey,service);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (SecurityException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
