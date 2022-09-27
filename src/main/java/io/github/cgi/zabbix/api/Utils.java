package io.github.cgi.zabbix.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cgi on 01.03.2016.
 * Support Class for converting Objects to JSON
 */
public class Utils {
	static ObjectMapper mapper = new ObjectMapper();

	public static byte[] getBytesFromInputStream(InputStream inputStream) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			byte[] buffer = new byte[1024];
			int length;

			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}

			outputStream.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return outputStream.toByteArray();
	}

	public static String inputStreamToString(byte[] bytes) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String string = null;

		try {

			outputStream.write(bytes);
			string = outputStream.toString("UTF-8");

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