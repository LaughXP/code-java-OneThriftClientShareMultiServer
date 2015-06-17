/**
 * 
 */
package com.jiuyan.commons.remote.sample;

import org.apache.thrift.TException;

/**
 *@Title: ThriftServiceSample2Impl.java
 *@Package com.jiuyan.commons.remote.sample
 *@Description:
 *@author xiaoyu
 *@date 2015年6月17日 下午9:21:51
 */
public class ThriftServiceSample2Impl implements ThriftServiceSample2.Iface {

	@Override
	public String helloWord() throws TException {
		
		return "ThriftServiceSample2Impl.helloWord";
	}

	@Override
	public String getStatData(String methodName, long fromTime, long toTime) throws TException {
		
		return "ThriftServiceSample2Impl.getStatData";
	}

}
