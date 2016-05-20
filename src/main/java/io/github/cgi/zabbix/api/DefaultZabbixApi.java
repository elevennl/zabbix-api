package io.github.cgi.zabbix.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultZabbixApi implements ZabbixApi {
	private static final Logger logger = LoggerFactory.getLogger(DefaultZabbixApi.class);

	private HttpClient httpClient;

	private URI uri;

	private String auth;

	ObjectMapper mapper = new ObjectMapper();

	public DefaultZabbixApi(String url) {
		try {
			uri = new URI(url.trim());
		} catch (URISyntaxException e) {
			throw new RuntimeException("url invalid", e);
		}
	}

	public DefaultZabbixApi(URI uri) {
		this.uri = uri;
	}

	public DefaultZabbixApi(String url, HttpClient httpClient) {
		this(url);
		this.httpClient = httpClient;
	}

	public DefaultZabbixApi(URI uri, HttpClient httpClient) {
		this(uri);
		this.httpClient = httpClient;
	}

	@Override
	public void init() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
	}

	@Override
	public void destroy() {
		if (httpClient != null) {
			httpClient = null;
		}
	}

	@Override
	public boolean login(String user, String password) {
		this.auth=null;
		Request request = RequestBuilder.newBuilder().paramEntry("user", user)
				.paramEntry("password", password).method("user.login").build();
		JsonNode response = call(request);
		String auth = response.path("result").getTextValue();
		if (auth != null && !auth.isEmpty()) {
			this.auth = auth;
			return true;
		}
		return false;
	}

	@Override
	public String apiVersion() {
		Request request = RequestBuilder.newBuilder().method("apiinfo.version")
				.build();
		JsonNode response = call(request);
		return response.path("result").getTextValue();
	}

	public boolean hostExists(String name) {
		Request request = RequestBuilder.newBuilder().method("host.exists")
				.paramEntry("name", name).build();
		JsonNode response = call(request);
		return response.path("result").getBooleanValue();
	}

	public String hostCreate(String host, String groupId) {
		ArrayNode groups = mapper.createArrayNode();
		ObjectNode group = mapper.createObjectNode();

		group.put("groupid", groupId);
		groups.add(group);
		Request request = RequestBuilder.newBuilder().method("host.create")
				.paramEntry("host", host).paramEntry("groups", groups).build();
		JsonNode response = call(request);
		return response.path("result").path("hostids").path(0).getTextValue();
	}

	public boolean hostgroupExists(String name) {
		Request request = RequestBuilder.newBuilder()
				.method("hostgroup.exists").paramEntry("name", name).build();
		JsonNode response = call(request);
		return response.path("result").getBooleanValue();
	}

	/**
	 * 
	 * @param name
	 * @return groupId
	 */
	public String hostgroupCreate(String name) {
		Request request = RequestBuilder.newBuilder()
				.method("hostgroup.create").paramEntry("name", name).build();
		JsonNode response = call(request);
		return response.get("result").get("groupids").get(0).getTextValue();
	}

	@Override
	public JsonNode call(Request request) {
		if (request.getAuth() == null) {
			request.setAuth(this.auth);
		}

		try {
			HttpPost httpRequest = new HttpPost(uri);
			String requestStr = mapper.writeValueAsString(request);
			httpRequest.addHeader("Content-Type", "application/json");
			httpRequest.setEntity(new StringEntity(requestStr, ContentType.APPLICATION_JSON));
			HttpResponse response = httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			return mapper.readTree(entity.getContent()) ;
		} catch (IOException e) {
			throw new RuntimeException("DefaultZabbixApi call exception!", e);
		}
	}

}
