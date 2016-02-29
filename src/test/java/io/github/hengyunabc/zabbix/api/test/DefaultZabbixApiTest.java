package io.github.hengyunabc.zabbix.api.test;

import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

@RunWith(Parameterized.class)
public class DefaultZabbixApiTest {

	private ZabbixApi zabbixApi;
	private String user_login;
	private String user_password;
	private Properties params;

	public DefaultZabbixApiTest(String url, String login, String password, Properties p){
		zabbixApi = new DefaultZabbixApi(url);
		zabbixApi.init();
		user_login = login;
		user_password = password;
		params = p;
	}


	@Override
	protected void finalize() throws Throwable {
		zabbixApi.destroy();
		super.finalize();
	}

	@Parameterized.Parameters
	public static Collection<Object[]> data() {

		Properties p = new Properties();
		InputStream prStream = null;
		try {
			prStream = DefaultZabbixApiTest.class.getResourceAsStream("zabbix.local.properties");
			p.load(prStream);
		} catch (Exception e){
			// We haven't such file use project defaults
			// e.printStackTrace();
		}finally {
			if (prStream != null)
				try {
					prStream.close();
				} catch (IOException e) {
					// nothing required
				}
		}
		Collection<Object[]> al = new ArrayList<Object[]>();
		al.add(new Object[]{
				p.getProperty("url", "http://localhost:49156/zabbix/api_jsonrpc.php"),
				p.getProperty("login", "admin"),
				p.getProperty("password", "zabbix"),
				p });
		return  al;
	}

	@Test
	public void testVersion() {
		String version = zabbixApi.apiVersion();
		System.err.println(version);
	}

	@Test
	public void testLogin() {
		String user = user_login;
		String password = user_password;
		boolean login = zabbixApi.login(user, password);
		System.out.println("login result:" + login);

		if (login) {
			Request request = RequestBuilder.newBuilder().method("user.get")
					.paramEntry("output", "extend").build();
			JSONObject response = zabbixApi.call(request);
			System.err.println(JSON.toJSONString(response, true));
		}
	}

	@Test
	public void testHostGet() {
		boolean login = zabbixApi.login(user_login, user_password);
		System.err.println("login:" + login);

		String host = params.getProperty("testHostGet.host", "192.168.0.1");
		JSONObject filter = new JSONObject();

		filter.put("host", new String[] { host });
		Request getRequest = RequestBuilder.newBuilder().method("host.get")
				.paramEntry("filter", filter).build();
		JSONObject getResponse = zabbixApi.call(getRequest);
		System.err.println(getResponse);
		String hostid = getResponse.getJSONArray("result").getJSONObject(0)
				.getString("hostid");
		System.err.println(hostid);
	}

	//@Test
	public void testItemCreate() {
		boolean login = zabbixApi.login(user_login, user_password);
		System.err.println("login:" + login);
		String name = "testItem";
		String key = name;
		String hostid = params.getProperty("testItemCreate.hostid", "1");
		int type = 2; // trapper
		int value_type = 0; // float
		int delay = 30;

		String interfaceid = "123";

		Request request = RequestBuilder.newBuilder().method("item.create")
				.paramEntry("name", name).paramEntry("key_", key)
				.paramEntry("hostid", hostid).paramEntry("type", type)
				.paramEntry("value_type", value_type)
				.paramEntry("delay", delay)
				.paramEntry("interfaceid", interfaceid).build();

		System.err.println(JSON.toJSONString(request));

		JSONObject result = zabbixApi.call(request);

		System.err.println(JSON.toJSONString(result, true));
	}

	@Test
	public void testGetTrigger() {
		boolean login = zabbixApi.login(user_login, user_password);
		System.err.println("login:" + login);
		String triggerId = params.getProperty("testGetTrigger.triggerId", "2322");

		Request request = RequestBuilder.newBuilder().method("trigger.get")
				.paramEntry("triggerids", triggerId).paramEntry("output", "extend")
				.paramEntry("selectFunctions", "extend").build();

		System.err.println(JSON.toJSONString(request));

		JSONObject result = zabbixApi.call(request);

		System.err.println(JSON.toJSONString(result, true));
	}
}
