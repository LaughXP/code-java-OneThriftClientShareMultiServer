/**
 * 
 */
package com.jiuyan.commons.remote.sample.client;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.jiuyan.commons.remote.sample.ThriftServiceSample1;
import com.jiuyan.commons.remote.sample.ThriftServiceSample3;
/**
 *@Title: TestClientSample.java
 *@Package com.jiuyan.commonsthrift.client
 *@Description:
 *@author xiaoyu
 *@date 2015年4月3日 下午7:17:07
 */
public class ClientSample {

	/** 
	 * @Title: main 
	 * @Description:  
	 * @param @param args
	 * @return void
	 * @throws 
	 */
	public static void main(String[] args) {
		System.out.println("client start!");
		TTransport transport = new TSocket("localhost", 8089); 
		TProtocol protocol = new TBinaryProtocol(transport);  
		ThriftServiceSample1.Client client = new ThriftServiceSample1.Client(protocol);
		ThriftServiceSample3.Client client3 = new ThriftServiceSample3.Client(protocol);
        try {
        	System.out.println("client open");
			transport.open();
			System.out.println(client.testPrint());
			System.out.println(client.getStatData("", 31231, 12321312));
			
			System.out.println(client3.add());
			System.out.println(client3.update("", 321321, 321));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        //transport.close();  
		System.out.println("client end!");
	}

}
