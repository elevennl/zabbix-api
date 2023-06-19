package io.github.elevennl.zabbix.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;


public class DefaultZabbixApi implements ZabbixApi {
    private static final Logger logger = LoggerFactory.getLogger(DefaultZabbixApi.class);
    private final URI uri;
    ObjectMapper mapper = new ObjectMapper();
    private HttpClient httpClient;
    private String auth;

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
            httpClient = HttpClientBuilder.create().build();
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
        this.auth = null;

        Request request = RequestBuilder.newBuilder().paramEntry("username", user)
                .paramEntry("password", password).method("user.login").build();
        JsonNode response = call(request, false);
        String auth = response.findValue("result").textValue();
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
        JsonNode response = call(request, false);
        return response.path("result").textValue();
    }

    public boolean hostExists(String name) {
        Request request = RequestBuilder.newBuilder().method("host.exists")
                .paramEntry("name", name).build();
        JsonNode response = call(request);
        return response.path("result").booleanValue();
    }

    public String hostCreate(String host, String groupId) {
        ArrayNode groups = mapper.createArrayNode();
        ObjectNode group = mapper.createObjectNode();

        group.put("groupid", groupId);
        groups.add(group);
        Request request = RequestBuilder.newBuilder().method("host.create")
                .paramEntry("host", host).paramEntry("groups", groups).build();
        JsonNode response = call(request);
        return response.path("result").path("hostids").path(0).textValue();
    }

    public boolean hostgroupExists(String name) {
        Request request = RequestBuilder.newBuilder()
                .method("hostgroup.exists").paramEntry("name", name).build();
        JsonNode response = call(request);
        return response.path("result").booleanValue();
    }

    /**
     * @param name
     * @return groupId
     */
    public String hostgroupCreate(String name) {
        Request request = RequestBuilder.newBuilder()
                .method("hostgroup.create").paramEntry("name", name).build();
        JsonNode response = call(request);
        return response.get("result").get("groupids").get(0).textValue();
    }

    @Override
    public JsonNode call(Request request) {
        return call(request, true);
    }

    public JsonNode call(Request request, boolean needAuth) {
        if (needAuth && request.getAuth() == null) {
            request.setAuth(this.auth);
        }

        byte[] byteArray = null;
        try {
            HttpPost httpRequest = new HttpPost(uri);
            String requestStr = mapper.writeValueAsString(request);
            httpRequest.setHeader("Content-type", "application/json-rpc");
            if (request.getAuth() != null) {
                httpRequest.setHeader("Authorization", "Bearer %s".formatted(request.getAuth()));
            }
            httpRequest.setEntity(new StringEntity(requestStr, ContentType.APPLICATION_JSON));
            HttpResponse response = httpClient.execute(httpRequest);
            HttpEntity entity = response.getEntity();

            byteArray = Utils.getBytesFromInputStream(entity.getContent());
            InputStream inputStream = new ByteArrayInputStream(byteArray);

            return mapper.readTree(inputStream);
        } catch (IOException e) {
            if (byteArray == null) {
                throw new RuntimeException("DefaultZabbixApi call exception! empty response, ", e);
            } else {
                throw new RuntimeException("DefaultZabbixApi call exception! " + new String(byteArray, StandardCharsets.UTF_8) + ", ", e);
            }
        }
    }

}
