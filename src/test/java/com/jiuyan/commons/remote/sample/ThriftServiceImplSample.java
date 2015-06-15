/**
 * 
 */
package com.jiuyan.commons.remote.sample;

import org.apache.thrift.TException;

/**
 *@Title: ThriftServiceImplSample.java
 *@Package com.jiuyan.commons.thrift.sample
 *@Description:
 *@author xiaoyu
 *@date 2015年4月7日 下午8:50:25
 */
public class ThriftServiceImplSample implements ThriftServiceSample.Iface {

	@Override
	public String testPrint() throws TException {
		
		return "testPrint";
	}

}
