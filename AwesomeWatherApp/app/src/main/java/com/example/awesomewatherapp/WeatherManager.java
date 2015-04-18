package com.example.awesomewatherapp;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherManager {

	private static String openWeatherMapApi = "http://api.openweathermap.org/data/2.5/weather?q=";
	private static final String WEATHER_TAG = "weather";
	private static final String MAIN_TAG = "main";
	private static final String ICON_TAG = "icon";
	
	public static Weather getWeather(String city, String country) {
		Weather weather = null;

		try {
			String response = WebServiceHandler.getJSONWeatherData(openWeatherMapApi + city + "," + country);
			
			JSONObject object = new JSONObject(response);
			
			JSONArray weatherArray = object.getJSONArray(WEATHER_TAG);
			
//			for (int i = 0; i < weatherArray.length(); i++) {
//				JSONObject weatherJSONObj = weatherArray.getJSONObject(i);
//				
//				String main = weatherJSONObj.getString(MAIN_TAG);
//				String icon = weatherJSONObj.getString(ICON_TAG);
//			}

			JSONObject weatherJSONObj = weatherArray.getJSONObject(0);
			
			String main = weatherJSONObj.getString(MAIN_TAG);
			String icon = weatherJSONObj.getString(ICON_TAG);
			
			weather = new Weather();
			weather.setMain(main);
			weather.setIcon(icon);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return weather;
	}
}
