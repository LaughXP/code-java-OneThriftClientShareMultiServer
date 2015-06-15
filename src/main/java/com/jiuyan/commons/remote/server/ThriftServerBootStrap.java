/**
 * 
 */
package com.jiuyan.commons.remote.server;

import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: BootStrap.java
 * @Package com.jiuyan.commonsthrift.bootstrap
 * @Description:启动thrift server
 * @author xiaoyu
 * @date 2015年4月2日 下午6:13:26
 */
public class ThriftServerBootStrap {
	
	private static Logger logger = LoggerFactory.getLogger(ThriftServerBootStrap. class );
	
	public static void startUp(final ServerConfigFactory config) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ThriftProcessorFactory.init(config.getProcessorName(), config.getServiceImplName());
					config.getTServer(config.getAbstractServerArgs(config.getTransport())).serve();
				} catch (TTransportException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}).start();
	}
	
}
