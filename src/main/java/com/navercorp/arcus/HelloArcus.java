// HelloArcus.java
package com.navercorp.arcus;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.Random;

import net.spy.memcached.ArcusClient;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.collection.CollectionAttributes;
import net.spy.memcached.collection.ElementFlagFilter;
import net.spy.memcached.collection.ElementFlagFilter.CompOperands;
import net.spy.memcached.internal.CollectionFuture;
import net.spy.memcached.collection.Element;
import net.spy.memcached.collection.CollectionResponse;

public class HelloArcus {

    private String key;
    private String arcusAdmin;
    private String serviceCode;
    private ArcusClient arcusClient;

    public HelloArcus(String arcusAdmin, String serviceCode) {
	this.key = "Prefix:new3BTreeKey";
        this.arcusAdmin = arcusAdmin;
        this.serviceCode = serviceCode;

        // log4j logger를 사용하도록 설정합니다.
        // 코드에 직접 추가하지 않고 아래의 JVM 환경변수를 사용해도 됩니다.
        //   -Dnet.spy.log.LoggerImpl=net.spy.memcached.compat.log.Log4JLogger
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");

        // Arcus 클라이언트 객체를 생성합니다.
        // - arcusAdmin : Arcus 캐시 서버들의 그룹을 관리하는 admin 서버(ZooKeeper)의 주소입니다.
        // - serviceCode : 사용자에게 할당된 Arcus 캐시 서버들의 집합에 대한 코드값입니다. 
        // - connectionFactoryBuilder : 클라이언트 생성 옵션을 지정할 수 있습니다.
        //
        // 정리하면 arcusAdmin과 serviceCode의 조합을 통해 유일한 캐시 서버들의 집합을 얻어 연결할 수 있는 것입니다.
        this.arcusClient = ArcusClient.createArcusClient(arcusAdmin, serviceCode, new ConnectionFactoryBuilder());
    }


    public boolean sayHello() {
	byte[] eflag = new byte[] { 1, 1, 1, 1 };
	Random rand=new Random(); 

        boolean setSuccess = false;
	CollectionAttributes attrs  = new CollectionAttributes();
	CollectionFuture<Boolean> future = null;

	for(int i = 0; i < 10000 ; i++)
	{
		long bkey = 1+rand.nextInt(100000);
    		String value = Long.toString(bkey);
		try {
	    	    this.arcusClient.asyncBopInsert(key, bkey, eflag, value, attrs).get();
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	}
	return setSuccess;
    }


    public String listenHello() {
	long bkey = 1L;

	ElementFlagFilter filter = new ElementFlagFilter(CompOperands.Equal, new byte[] { 1, 1 });
	CollectionFuture<Map<Long, Element<Object>>> future = null;

        String resultString = "Not OK.";

	try 
	{
	    future = this.arcusClient.asyncBopGet(key, 1000L, 2000L, filter, 0, 30, false, false);
	}  
	catch (Exception e) {
            e.printStackTrace();	
	}
	if (future == null)
	    return "BopGet실패";

	try 
	{
	    Map<Long, Element<Object>> result = future.get(1000L, TimeUnit.MILLISECONDS);
	    CollectionResponse response = future.getOperationStatus().getResponse();
	    if(result != null)
	    {
		resultString = "성공";
		for (Map.Entry<Long, Element<Object>> entry : result.entrySet()) {
		    if( entry.getKey() != Long.parseLong((String)entry.getValue().getValue()))
		    {
			resultString = "error) key:" + entry.getKey() + ", value:" + (String)entry.getValue().getValue();
			break;
		    }
		}

	    }
	    else if (response.equals(CollectionResponse.NOT_FOUND)) 
	    {
		resultString = "Key가 없습니다.(Key에 저장된 B+ tree가 없음.";
	    } 
	    else if (response.equals(CollectionResponse.NOT_FOUND_ELEMENT)) 
	    {
		resultString = "Key에 B+ tree는 존재하지만 저장된 값 중 조건에 맞는 것이 없음.";
	    }

	} 
	catch (Exception e) 
	{
	    if (future != null) 
		future.cancel(true);
            e.printStackTrace();
	}
	this.arcusClient.flush("");

	this.arcusClient.shutdown();
        return resultString;
    }



    public static void main(String[] args){
	HelloArcus helloArcus = new HelloArcus("127.0.0.1:2181", "test");
	long startTime = System.currentTimeMillis();
	helloArcus.sayHello();

	helloArcus.listenHello();
	long endTime = System.currentTimeMillis();
	System.out.println("##  소요시간(초.0f) : " + ( endTime - startTime )/1000.0f +"초"); 
    }
}
