/**
 * 
 */
package com.jiuyan.commons.remote.sample.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.jiuyan.commons.remote.sample.ThriftServiceSample;
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
		TTransport transport = new TSocket("localhost", 80); 
		TProtocol protocol = new TBinaryProtocol(transport);  
		ThriftServiceSample.Client client = new ThriftServiceSample.Client(protocol);
        try {
        	System.out.println("client open");
			transport.open();
			System.out.println(client.testPrint());
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //transport.close();  
		System.out.println("client end!");
	}

}
