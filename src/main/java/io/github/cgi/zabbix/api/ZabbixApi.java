package io.github.cgi.zabbix.api;

import com.fasterxml.jackson.databind.JsonNode;

public interface ZabbixApi {

	void init();

	void destroy();

	String apiVersion();

	JsonNode call(Request request);

	JsonNode call(Request request, boolean needAuth );

	boolean login(String user, String password);
}
