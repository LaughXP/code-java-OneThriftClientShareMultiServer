/**
 * 
 */
package com.jiuyan.commons.remote.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 *@Title: ExecutorsUtil.java
 *@Package com.jiuyan.IcMonitor.util
 *@Description:
 *@author xiaoyu
 *@date 2015年3月25日 下午2:48:35
 */
/**
 * @author xiaoyu
 *
 */
public class CollectionsUtil {
	
	public static final ConcurrentMap<String,ArrayBlockingQueue<String>> dataMap = new ConcurrentHashMap<String,ArrayBlockingQueue<String>>();

}
