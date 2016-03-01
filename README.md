# zabbix-api
Fork of zabbix-api for java.
Planed changes:
- java level down to 6 - Done
- down Zabbix version to 2.2 - Done
- potential rework to use jaxon JSON parser and serializer for minifiing dependencies - done. 
- down httpclient version - done
- rework of tests for support local params for Zabbix connections.


As this project became incompatible with original group id for artefact was changed.


https://www.zabbix.com/wiki/doc/api

https://www.zabbix.com/documentation/2.4/manual/api/reference/user/login

Based on zabbix api version 2.4.

**Zabbix api version 2.2 will throw a exception.**

##Info
API is simple, beacuse java can not process json like dynamic language. 

You can build you own ```Request``` Object.

```java
public interface ZabbixApi {

	public void init();

	public void destory();

	public String apiVersion();

	public JSONObject call(Request request);

	public boolean login(String user, String password);
}
```

##Example
```java
		String url = "http://192.168.90.102/zabbix/api_jsonrpc.php";
		zabbixApi = new DefaultZabbixApi(url);
		zabbixApi.init();
		
		boolean login = zabbixApi.login("zabbix.dev", "goK0Loqua4Eipoe");
		System.err.println("login:" + login);
		
		String host = "192.168.66.29";
		JSONObject filter = new JSONObject();
		
		filter.put("host", new String[] { host });
		Request getRequest = RequestBuilder.newBuilder()
				.method("host.get").paramEntry("filter", filter)
				.build();
		JSONObject getResponse = zabbixApi.call(getRequest);
		System.err.println(getResponse);
		String hostid = getResponse.getJSONArray("result")
				.getJSONObject(0).getString("hostid");
		System.err.println(hostid);
```

You can set your own ```HttpClient```.

```java
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(5 * 1000).setConnectionRequestTimeout(5 * 1000)
				.setSocketTimeout(5 * 1000).build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(); 
		
		CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connManager)
				.setDefaultRequestConfig(requestConfig).build();
		
		ZabbixApi zabbixApi = new DefaultZabbixApi(
				"http://localhost:10051/zabbix/api_jsonrpc.php", httpclient);
		zabbixApi.init();
		
		String apiVersion = zabbixApi.apiVersion();
		
		System.out.println("api version:" + apiVersion);
		
		zabbixApi.destory();
```

You can add dependency to MAven project:
```XML
        <dependency>
            <groupId>io.github.cgi</groupId>
            <artifactId>zabbix-api</artifactId>
            <version>0.0.2-SNAPSHOT</version>
        </dependency>
```

##Licence
Apache License V2
