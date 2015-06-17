/**
 * 
 */
package com.jiuyan.commons.remote.sample;

import org.apache.thrift.TException;

/**
 *@Title: ThriftServiceSample1Impl.java
 *@Package com.jiuyan.commons.remote.sample
 *@Description:
 *@author xiaoyu
 *@date 2015年6月17日 下午9:20:43
 */
public class ThriftServiceSample1Impl implements ThriftServiceSample1.Iface {

	@Override
	public String testPrint() throws TException {
		
		return "ThriftServiceSample1Impl.testPrint";
	}

	@Override
	public String getStatData(String methodName, long fromTime, long toTime) throws TException {
		
		return "ThriftServiceSample1Impl.getStatData";
	}

}
