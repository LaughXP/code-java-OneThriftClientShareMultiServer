/**
 * 
 */
package com.jiuyan.commons.remote.server;

import org.apache.thrift.ProcessFunction;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TType;

/**
 *@Title: ThriftBaseProcessor.java
 *@Package com.jiuyan.commons.thrift.server
 *@Description:静态方法从容器中获取processor
 *@author xiaoyu
 *@date 2015年4月7日 下午8:25:21
 */
public class ThriftBaseProcessor {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean process(TProtocol in, TProtocol out) throws TException {
	    TMessage msg = in.readMessageBegin();
	    ProcessFunction fn = (ProcessFunction)ThriftProcessorFactory.getMethod(msg.name);
	    if (fn == null) {
	      TProtocolUtil.skip(in, TType.STRUCT);
	      in.readMessageEnd();
	      TApplicationException x = new TApplicationException(TApplicationException.UNKNOWN_METHOD, "Invalid method name: '"+msg.name+"'");
	      out.writeMessageBegin(new TMessage(msg.name, TMessageType.EXCEPTION, msg.seqid));
	      x.write(out);
	      out.writeMessageEnd();
	      out.getTransport().flush();
	      return true;
	    }
	    fn.process(msg.seqid, in, out, ThriftProcessorFactory.getService(ThriftProcessorFactory.getProcessor(msg.name)));
	    return true;
	  }
}
