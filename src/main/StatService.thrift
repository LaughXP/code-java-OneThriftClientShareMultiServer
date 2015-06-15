
namespace java com.jiuyan.common.remote.service
  
service StatService {    
    string getStatData(1:string methodName,2:i64 fromTime,3:i64 toTime)
}

 