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
	
	private ServerConfig serverConfig;
	
	//new AbstractServerArgs
	public abstract AbstractServerArgs<?> getAbstractServerArgs(TServerTransport serverTransport );
	//new Tserver
	public abstract TServer getTServer(AbstractServerArgs<?> args);
	
	//new TServerTransport
	public final TServerTransport getTransport() throws TTransportException {
		InetSocketAddress inetSocketAddress = new InetSocketAddress(this.serverConfig.getHost(), this.serverConfig.getPort());
		ServerSocketTransportArgs ssta = new ServerSocketTransportArgs();
		ssta.bindAddr(inetSocketAddress);
		ssta.backlog(this.serverConfig.getBacklog());
		return new TServerSocket(ssta);
	}
	
	public final void loadConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	public final ServerConfig getServerConfig() {
		return serverConfig;
	}
}
