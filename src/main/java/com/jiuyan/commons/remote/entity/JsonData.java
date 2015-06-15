/**
 * 
 */
package com.jiuyan.commons.remote.entity;

import java.util.List;

/**
 *@Title: JsonData.java
 *@Package com.jiuyan.ic.monitor.entity
 *@Description:
 *@author xiaoyu
 *@date 2015年3月30日 上午10:51:21
 */
public class JsonData {
	
	private String type;
	private List<String> data;
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the data
	 */
	public List<String> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(List<String> data) {
		this.data = data;
	}
	
	
}
