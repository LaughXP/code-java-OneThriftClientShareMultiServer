
namespace java com.jiuyan.commons.remote.sample
  
service ThriftServiceSample1 {    
    string testPrint();
    string getStatData(1:string methodName,2:i64 fromTime,3:i64 toTime);    
}

 