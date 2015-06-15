/**
 * 
 */
package com.jiuyan.commons.remote.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jiuyan.commons.remote.util.CollectionsUtil;

/**
 * @Title: MonitorThriftEventHandler.java
 * @Package com.jiuyan.commons.thrift.server
 * @Description:
 * @author xiaoyu
 * @date 2015年4月3日 下午7:55:14
 */
public class MonitorThriftEventHandler implements TServerEventHandler {
	
	private static Logger logger = LoggerFactory.getLogger(MonitorThriftEventHandler. class );

	private final Map<String, MonitorThriftContext> contexts = new HashMap<String, MonitorThriftContext>();

	@Override
	public void preServe() {
	}

	@Override
	public ServerContext createContext(TProtocol input, TProtocol output) {
		MonitorThriftContext context = null;
		String method = null;
		try {
			method = input.readMessageBegin().name;
		} catch (TException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		if (method != null) {
			MonitorThriftContext exists = contexts.get(method);
			if (exists == null) {
				synchronized (contexts) {
					exists = contexts.get(method);
					if (exists == null) { // double check
						context = new MonitorThriftContext(method, System.currentTimeMillis()); 
						contexts.put(method, context);
					} else {
						context = exists;
					}
				}
			} else {
				context = exists;
			}
		}
		return context;
	}

	@Override
	public void deleteContext(ServerContext serverContext, TProtocol input, TProtocol output) {

	}

	public void processContext(ServerContext serverContext, String methodName, long lantency, TTransport inputTransport, TTransport outputTransport) {
		if (CollectionsUtil.dataMap.get(methodName) == null) {
			CollectionsUtil.dataMap.put(methodName, new ArrayBlockingQueue<String>(10));
		}
		ArrayBlockingQueue<String> methodQpsQueue = CollectionsUtil.dataMap.get(methodName);
		MonitorThriftContext context = (MonitorThriftContext)serverContext;
		context.incrementAndReset(methodQpsQueue , lantency);
	}

	@Override
	public void processContext(ServerContext serverContext, TTransport inputTransport, TTransport outputTransport) {
	}

}
