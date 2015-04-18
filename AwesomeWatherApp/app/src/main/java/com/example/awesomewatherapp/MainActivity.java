package com.example.awesomewatherapp;

import java.util.HashMap;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends ActionBarActivity implements
		com.google.android.gms.location.LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = "MainActivity";
	
	private TextView tvWeather;
	private ImageView tvWeatherImage;
	private HashMap<String, Integer> weatherImageMap;
	
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private Location mCurrentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initialize();
		buildGoogleApiClient();
	}

	private void initialize() {
		tvWeather = (TextView) findViewById(R.id.tvWeather);
		tvWeatherImage = (ImageView) findViewById(R.id.tvWeatherIcon);
		
		weatherImageMap = new HashMap<String, Integer>();
		weatherImageMap.put("01d", R.drawable.clear_sky_d);
		weatherImageMap.put("01n", R.drawable.clear_sky_n);
		weatherImageMap.put("02d", R.drawable.few_clouds_d);
		weatherImageMap.put("02n", R.drawable.few_clouds_n);
		weatherImageMap.put("03d", R.drawable.scattered_clouds_d);
		weatherImageMap.put("03n", R.drawable.scattered_clouds_n);
		weatherImageMap.put("04d", R.drawable.broken_clouds_d);
		weatherImageMap.put("04n", R.drawable.broken_clouds_n);
		weatherImageMap.put("09d", R.drawable.shower_rain_d);
		weatherImageMap.put("09n", R.drawable.shower_rain_n);
		weatherImageMap.put("10d", R.drawable.rain_d);
		weatherImageMap.put("10n", R.drawable.rain_n);
		weatherImageMap.put("11d", R.drawable.thunderstrom_d);
		weatherImageMap.put("11n", R.drawable.thunderstrom_n);
		weatherImageMap.put("13d", R.drawable.snow_d);
		weatherImageMap.put("13n", R.drawable.snow_n);
		weatherImageMap.put("50d", R.drawable.mist_d);
		weatherImageMap.put("50n", R.drawable.mist_n);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }
    
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

	@Override
	public void onLocationChanged(Location location) {
		new WeatherTask().execute(location);
	}
	
	public class WeatherTask extends AsyncTask<Location, Void, Weather> {
		
		@Override
		protected Weather doInBackground(Location... params) {
			Weather weather = null;
			Location location = params[0];
			Geocoder geocoder = new Geocoder(getApplicationContext());
			List<Address> addresses = null;
			try {
				addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				Address address = addresses.get(0);
				
				String city = address.getLocality();
				String country = address.getCountryName();
				
				weather = WeatherManager.getWeather(city, country);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return weather;
		}
		
		@Override
		protected void onPostExecute(Weather result) {
			if (result != null) {
				tvWeather.setText(result.getMain());
				tvWeatherImage.setBackground(getResources().getDrawable(
						weatherImageMap.get(result.getIcon())));
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

	}

	@Override
	public void onConnected(Bundle arg0) {
		if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		}
		startLocationUpdates();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
		mGoogleApiClient.disconnect();
	}
}
