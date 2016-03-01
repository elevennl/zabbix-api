package io.github.cgi.zabbix.api;

import org.codehaus.jackson.JsonNode;

public interface ZabbixApi {

	void init();

	void destroy();

	String apiVersion();

	JsonNode call(Request request);

	boolean login(String user, String password);
}
