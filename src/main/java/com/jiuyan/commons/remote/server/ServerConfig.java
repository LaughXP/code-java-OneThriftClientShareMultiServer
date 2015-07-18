/**
 * 
 */
package com.jiuyan.commons.remote.server;

import java.util.List;

/**
 * @Title: ServerConfig.java
 * @Package com.jiuyan.commons.thrift.server
 * @Description:用户需要实现这个类加载基本参数
 * @author xiaoyu
 * @date 2015年4月8日 下午3:55:48
 */
public class ServerConfig {

	private String host;
	private int port;
	private int backlog;
	private List<Service> services;
	
	public ServerConfig() {
		
	}
	
	public ServerConfig(String host, int port, int backlog, List<Service> services) {
		this.host = host;
		this.port = port;
		this.backlog = backlog;
		this.services = services;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public static class Service {
		private String processorName;
		private String serviceImplName;

		public Service(String processorName, String serviceImplName) {
			this.processorName = processorName;
			this.serviceImplName = serviceImplName;
		}

		// 获得processor的全路径
		public String getProcessorName() {
			return processorName;
		}

		// 获得service中的Iface的全路径
		public String getServiceImplName() {
			return serviceImplName;
		}
	}
}
