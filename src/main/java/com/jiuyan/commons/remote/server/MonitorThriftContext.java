/**
 * 
 */
package com.jiuyan.commons.remote.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.thrift.server.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: MonitorThriftContext.java
 * @Package com.jiuyan.commons.thrift.server
 * @Description:
 * @author xiaoyu
 * @date 2015年4月8日 下午2:41:49
 */
public class MonitorThriftContext implements ServerContext {
	
	private static Logger logger = LoggerFactory.getLogger(MonitorThriftContext. class );
	
	private String method;

	private AtomicLong count;

	private AtomicLong lantency;

	private AtomicLong logStart;

	public MonitorThriftContext(String method, long logStart) {
		this.method = method;
		this.count = new AtomicLong(0);
		this.lantency = new AtomicLong(0);
		this.logStart = new AtomicLong(logStart);
	}

	public long getCount() {
		return count.get();
	}

	public String getMethod() {
		return method;
	}

	public void incrementAndReset(ArrayBlockingQueue<String> methodQpsQueue , long tLantency) {
		long now = System.currentTimeMillis();
		long dura = now - logStart.longValue();
		if (dura >= 10 * 1000) { // need echo QPS/AL
			synchronized (logStart) {
				now = System.currentTimeMillis();
				dura = now - logStart.longValue();
				count.incrementAndGet();
				lantency.addAndGet(tLantency);
				if (dura >= 10 * 1000) {
					long secs = dura / 1000;
					if (secs == 0) {
						secs = 1;
					}
					long countV = count.longValue();
					long lentencyV = lantency.longValue();
					logger.warn(String.format("echo context:%s QPS:%2.1f AL:%2.1f", this, (1.0 * countV / secs), (1.0 * lentencyV / countV)));
					// reset
					logStart.set(now);
					count.set(0);
					lantency.set(0);
				}
			}
		} else {
			count.incrementAndGet();
			lantency.addAndGet(tLantency);
		}
	}

	public void destroy() {
	}

	@Override
	public String toString() {
		return "@" + this.hashCode() + " [method=" + method + ", count=" + count + ", logStart=" + logStart + ", logDuration=" + 10 * 1000 + "]";
	}
}
