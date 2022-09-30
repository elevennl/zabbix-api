package io.github.cgi.zabbix.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by cgi on 01.03.2016.
 * Support Class for converting Objects to JSON
 */
public class Utils {
	static ObjectMapper mapper = new ObjectMapper();

	private Utils() {
		throw new UnsupportedOperationException("Do not instantiate this utility.");
	}

	public static byte[] getBytesFromInputStream(InputStream inputStream) {
		try {
			return inputStream.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	public static String inputStreamToString(byte[] bytes) {
		String string = null;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			outputStream.write(bytes);
			string = outputStream.toString(StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return string;
	}

	public static String toJson(Object o) {
		String s = null;
		try {
			s = mapper.writeValueAsString(o);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

}