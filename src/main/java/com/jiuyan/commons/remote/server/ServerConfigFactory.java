/**
 * 
 */
package com.jiuyan.commons.remote.server;

import java.net.InetSocketAddress;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.AbstractServerArgs;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerSocket.ServerSocketTransportArgs;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;


/**
 *@Title: ServerInit.java
 *@Package com.jiuyan.commonsthrift.server
 *@Description:加载server的基本参数，自定义的server要继承这个类 并实现其中的抽象方法
 *@author xiaoyu
 *@date 2015年4月2日 下午5:36:49
 */
public abstract class ServerConfigFactory {
	
	private int port;
	
	private int backlog;
	
	private String processorName;
	
	private String serviceImplName;
	
	//new AbstractServerArgs
	public abstract AbstractServerArgs<?> getAbstractServerArgs(TServerTransport serverTransport );
	//new Tserver
	public abstract TServer getTServer(AbstractServerArgs<?> args);
	
	//new TServerTransport
	public final TServerTransport getTransport() throws TTransportException {
		InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", this.getPort());
		ServerSocketTransportArgs ssta = new ServerSocketTransportArgs();
		ssta.bindAddr(inetSocketAddress);
		ssta.backlog(this.getBacklog());
		return new TServerSocket(ssta);
	}
	
	public final void loadConfig(ServerConfig serverConfig) {
		this.setPort(serverConfig.getPort());
		this.setBacklog(serverConfig.getBacklog());
		this.setProcessorName(serverConfig.getProcessorName());
		this.setServiceImplName(serverConfig.getServiceImplName());
	}

	private int getPort() {
		return port;
	}

	private void setPort(int port) {
		this.port = port;
	}

	private int getBacklog() {
		return backlog;
	}

	private void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public String getProcessorName() {
		return processorName;
	}
	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}
	public String getServiceImplName() {
		return serviceImplName;
	}

	private void setServiceImplName(String serviceImplName) {
		this.serviceImplName = serviceImplName;
	}
}
