
namespace java com.jiuyan.commons.remote.sample
  
service ThriftServiceSample2 {    
    string helloWord();
    string getStatData(1:string methodName,2:i64 fromTime,3:i64 toTime);    
}

 