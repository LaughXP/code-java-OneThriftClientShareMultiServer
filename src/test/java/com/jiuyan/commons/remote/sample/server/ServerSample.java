/**
 * 
 */
package com.jiuyan.commons.remote.sample.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.AbstractServerArgs;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jiuyan.commons.remote.server.ServerConfig;
import com.jiuyan.commons.remote.server.ServerConfigFactory;
import com.jiuyan.commons.remote.server.ThriftServer;
import com.jiuyan.commons.remote.server.ThriftServerBootStrap;

/**
 *@Title: ServerStartupSample.java
 *@Package com.jiuyan.commonsthrift.test
 *@Description:
 *@author xiaoyu
 *@date 2015年4月3日 下午5:48:04
 */
public class ServerSample {
	
	private static Logger logger = LoggerFactory.getLogger(ServerSample. class );
	
	//自定义配置
	public static class ServerConfigFactorySample extends ServerConfigFactory {
		
		@Override
		public AbstractServerArgs<?> getAbstractServerArgs(TServerTransport serverTransport) {
			ThriftServer.Args trArgs = new ThriftServer.Args(serverTransport);
			trArgs.maxWorkerThreads(128);
			trArgs.minWorkerThreads(64);
			trArgs.stopTimeoutVal = 2 * 1000;
			trArgs.executorService = new ThreadPoolExecutor(trArgs.minWorkerThreads, trArgs.maxWorkerThreads, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(1000));
			trArgs.protocolFactory(new TBinaryProtocol.Factory(true, true));
			trArgs.transportFactory(new TTransportFactory());
			return trArgs;
		}

		@Override
		public TServer getTServer(AbstractServerArgs<?> args) {

			return new ThriftServer((ThriftServer.Args) args);
		}

	}
	
	/** 
	 * @Title: main 
	 * @Description:  
	 * @param @param args
	 * @return void
	 * @throws 
	 */
	public static void main(String[] args) {
		//初始化自己继承的server配置类
		ServerConfigFactory server = new ServerSample.ServerConfigFactorySample();
		//加载基本配置
		List<ServerConfig.Service> services = new ArrayList<ServerConfig.Service>();
		services.add(new ServerConfig.Service("com.jiuyan.commons.remote.sample.ThriftServiceSample1$Processor", "com.jiuyan.commons.remote.sample.ThriftServiceSample1Impl"));
//		services.add(new ServerConfig.Service("com.jiuyan.commons.remote.sample.ThriftServiceSample2$Processor", "com.jiuyan.commons.remote.sample.ThriftServiceSample2Impl"));
		services.add(new ServerConfig.Service("com.jiuyan.commons.remote.sample.ThriftServiceSample3$Processor", "com.jiuyan.commons.remote.sample.ThriftServiceSample3Impl"));
		server.loadConfig(new ServerConfig("127.0.0.1",8089,100,services));
		//启动服务
		ThriftServerBootStrap.startUp(server);
		logger.info("testServer startup");
	}

}
