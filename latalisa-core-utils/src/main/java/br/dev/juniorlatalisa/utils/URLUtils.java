package br.dev.juniorlatalisa.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class URLUtils {

	public static boolean checkHttpURLConnection(String url, int timeout) {
		try {
			return checkHttpURLConnection(new URL(url), timeout);
		} catch (MalformedURLException e) {
			return false;
		}
	}

	public static boolean checkHttpURLConnection(URL url, int timeout) {
		try {
			URLConnection con = url.openConnection();
			return con instanceof HttpURLConnection ? checkHttpURLConnection((HttpURLConnection) con, timeout) : false;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean checkHttpURLConnection(HttpURLConnection con, int timeout) {
		try {
			con.setConnectTimeout(timeout);
			con.connect();
			try {
				return HttpURLConnection.HTTP_OK == con.getResponseCode();
			} finally {
				con.disconnect();
			}
		} catch (IOException e) {
			return false;
		}
	}
}
