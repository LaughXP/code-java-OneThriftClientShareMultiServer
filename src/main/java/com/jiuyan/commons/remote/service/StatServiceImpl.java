/**
 * 
 */
package com.jiuyan.commons.remote.service;

import org.apache.thrift.TException;
/**
 *@Title: TestServiceImplSample.java
 *@Package com.jiuyan.commonsthrift.service.impl
 *@Description:
 *@author xiaoyu
 *@date 2015年4月3日 下午5:56:40
 */
public class StatServiceImpl implements StatService.Iface {

	@Override
	public String getStatData(String methodName, long fromTime, long toTime) throws TException {
		return "getStatData";
	}

}
