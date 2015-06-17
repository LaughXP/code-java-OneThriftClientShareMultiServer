/**
 * 
 */
package com.jiuyan.commons.remote.sample;

import org.apache.thrift.TException;

/**
 *@Title: ThriftServiceSample3Impl.java
 *@Package com.jiuyan.commons.remote.sample
 *@Description:
 *@author xiaoyu
 *@date 2015年6月17日 下午9:24:22
 */
public class ThriftServiceSample3Impl implements ThriftServiceSample3.Iface {

	@Override
	public String add() throws TException {
		
		return "ThriftServiceSample3Impl.add";
	}

	@Override
	public String update(String methodName, long fromTime, long toTime) throws TException {
		
		return "ThriftServiceSample3Impl.update";
	}

}
