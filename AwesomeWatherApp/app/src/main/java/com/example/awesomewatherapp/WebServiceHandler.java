package com.example.awesomewatherapp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class WebServiceHandler {
	
	public static String getJSONWeatherData(String url) {
		String response = null;
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpEntity entity = null;
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = client.execute(httpGet);
			entity = httpResponse.getEntity();
			response = EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}
}
