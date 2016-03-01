package io.github.cgi.zabbix.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.IOException;

/**
 * Created by cgi on 01.03.2016.
 * Support Class for converting Objects to JSON
 */
public class Utils {
    static ObjectMapper mapper = new ObjectMapper();
    static ObjectMapper mapperIndent = new ObjectMapper();


    public static String toJson(Object o){
        String s = null;
        try {
            s = mapper.writeValueAsString(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;

    }

    public static String toJsonWithIndent(Object o){
        String s = null;
        mapperIndent.getSerializationConfig().enable(SerializationConfig.Feature.INDENT_OUTPUT);
        try {
            s = mapperIndent.writeValueAsString(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;

    }

}
