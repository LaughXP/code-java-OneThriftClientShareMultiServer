/**
 * 
 */
package com.jiuyan.commons.remote.server;
/**
 *@Title: ServerConfig.java
 *@Package com.jiuyan.commons.thrift.server
 *@Description:用户需要实现这个类加载基本参数
 *@author xiaoyu
 *@date 2015年4月8日 下午3:55:48
 */
public interface ServerConfig {
	
	public int getPort();
	
	public int getBacklog();
	//获得processor的全路径
	public String getProcessorName();
	//获得service中的Iface的全路径
	public String getServiceImplName();
}
