
namespace java com.jiuyan.commons.remote.sample
  
service ThriftServiceSample3 {    
    string add();
    string update(1:string methodName,2:i64 fromTime,3:i64 toTime);    
}

 