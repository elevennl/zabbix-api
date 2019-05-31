# zabbix-api
Fork of zabbix-api for java.
Planed changes:
- java level down to 6 - Done (Now fersion up to Java 7)
- down Zabbix version to 2.2 - Done (Not tested for long time)
- potential rework to use jaxon JSON parser and serializer for minifiing dependencies - done. 
- down httpclient version - done
- rework of tests for support local params for Zabbix connections. - Done

https://www.zabbix.com/wiki/doc/api

https://www.zabbix.com/documentation/2.2/manual/api/reference/user/login

Based on zabbix api version 2.2.

##Info
API is simple, beacuse java can not process json like dynamic language. 

You can build you own ```Request``` Object.

```java
public interface ZabbixApi {

	void init();

	void destroy();

	String apiVersion();

	JsonNode call(Request request);

	boolean login(String user, String password);
}
```

## Example - Need to update!
```java
		String url = "http://192.168.90.102/zabbix/api_jsonrpc.php";
		zabbixApi = new DefaultZabbixApi(url);
		zabbixApi.init();
		
		boolean login = zabbixApi.login("zabbix.dev", "goK0Loqua4Eipoe");
		System.err.println("login:" + login);

        Map filter = new HashMap();

        filter.put("host", new String[]{host});
        Request getRequest = RequestBuilder.newBuilder().method("host.get")
                .paramEntry("filter", filter).build();
   
        JsonNode getResponse = zabbixApi.call(getRequest);
   
        System.err.println(getResponse);
        String hostid = getResponse.path("result").path(0).path("hostid").getTextValue();
        System.err.println(hostid);
 ```
 

You can add dependency to Maven project:
```XML
        <dependency>
            <groupId>io.github.cgi</groupId>
            <artifactId>zabbix-api</artifactId>
            <version>0.0.5</version>
        </dependency>
```

##Licence
Apache License V2
