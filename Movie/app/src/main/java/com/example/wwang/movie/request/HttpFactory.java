package com.example.wwang.movie.request;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class HttpFactory {
	public static HttpClient getHttpClient(){
		HttpClient hc = new DefaultHttpClient();
		hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				20000);
		hc.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		return hc;
	}
}
