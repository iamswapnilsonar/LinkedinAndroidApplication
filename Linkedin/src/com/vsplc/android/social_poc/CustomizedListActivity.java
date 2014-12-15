package com.vsplc.android.social_poc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.social_poc.lasy_loading.LazyAdapter;
import com.vsplc.android.social_poc.logger.Logger;
import com.vsplc.android.social_poc.model.City;
import com.vsplc.android.social_poc.model.LinkedinUser;
import com.vsplc.android.social_poc.utils.DataWrapper;
import com.vsplc.android.social_poc.utils.MethodUtils;

public class CustomizedListActivity extends Activity {
	
	private ListView list;
	private LazyAdapter adapter;
	private ArrayList<LinkedinUser> listLinkedinUsers;

	public static ArrayList<City> listCityInfo = new ArrayList<City>();
	
	private Button btnMapAll;
	private ProgressDialog progressDialog;
	
	private Context mContext;
	
	static int filteringPercentage = 0;
	
	public Set<String> cities = new HashSet<String>();
	
	public static LatLng getLatLongFromGivenAddress(String youraddress) {
	    
		double lat = 0;
		double lng = 0;
		
		String uri = "http://maps.google.com/maps/api/geocode/json?address=" + youraddress + "&sensor=false";
		
	    HttpGet httpGet = new HttpGet(uri);
	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response;
	    StringBuilder stringBuilder = new StringBuilder();

	    try {
	        response = client.execute(httpGet);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
	        while ((b = stream.read()) != -1) {
	            stringBuilder.append((char) b);
	        }
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    JSONObject jsonObject = new JSONObject();
	    try {
	        jsonObject = new JSONObject(stringBuilder.toString());

	        lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	            .getJSONObject("geometry").getJSONObject("location")
	            .getDouble("lng");

	        lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	            .getJSONObject("geometry").getJSONObject("location")
	            .getDouble("lat");

	        Logger.vLog("latitude", ""+lat);
	        Logger.vLog("longitude", ""+lng);
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }
	    
	    return new LatLng(lat, lng);

	}
	
	private class AsyncTaskForCities extends AsyncTask<Object, Void, String>{

		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			
			@SuppressWarnings("unchecked")
			ArrayList<LinkedinUser> connections = (ArrayList<LinkedinUser>) params[0];

			for (int i=0; i < connections.size(); i++) {

				LinkedinUser user = connections.get(i);

				if (user.location != null && user.country_code != null) {
					
					String mCity = MethodUtils.getCityNameFromLocation(user.location, user.country_code);

					// Prepared how many city connections available..
					if (mCity != null && cities.add(mCity)) {

						final City city = new City(mCity);

						String mcountry = MethodUtils.getISOCountryNameFromCC(user.country_code); 						
						city.country = mcountry; 										
						listCityInfo.add(city);
					}else{
						Logger.vLog("listCityInfo", mCity+" already present.");
					}						

				}
				
			}
			
			return "Success";
		}
		
		@Override
		protected void onPostExecute(String result) {
//			progressDialog.dismiss();
			Toast.makeText(mContext, result+" in retrieval of cities.", Toast.LENGTH_SHORT).show();
			new DoingLengthyTask().execute();
		}
		
	}
	
	private class DoingLengthyTask extends AsyncTask<Void, Integer, String> {

		@Override
		protected String doInBackground(Void... params) {

			for (int i = 0; i < listCityInfo.size(); i++) {
				
				City city = listCityInfo.get(i);
				
				String address = city.name+","+city.country;
				address = address.replaceAll("\\s","%20");
				city.latLng = getLatLongFromGivenAddress(address);	
				
				publishProgress(i);
			}
						
			return "Completed";
		}

		@Override
		protected void onPostExecute(String result) {
			
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			}
			
//			 progressDialog.show();			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progressDialog = ProgressDialog.show(MainActivity.this, "Wait",
			// "Downloading..");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			filteringPercentage = values[0];
		}
	}

	/*private DownloadObserver getLocationInfoDownloadObserver = new DownloadObserver() {

    	@Override
    	public void onDownloadingStart() {}

    	@Override
    	public void onDownloadingComplete(Object data) {
    		Log.v("onDownloadingComplete : ", ""+data.toString());
    		new ResponseManager().parseGeocoderWebserviceResult(data.toString());
    	}

    	@Override
    	public void onDownloadFailure(Object errorData) {}
    };*/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);

		mContext = CustomizedListActivity.this;
		
		DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
		listLinkedinUsers = dw.getList();

		// get and store information about cities
		new AsyncTaskForCities().execute(listLinkedinUsers);
		
		// showing progress dialog while performing heavy tasks..
        progressDialog = new ProgressDialog(mContext);
		
		list = (ListView) findViewById(R.id.list);

		// Getting adapter by passing xml data ArrayList
		adapter = new LazyAdapter(this, listLinkedinUsers);
		list.setAdapter(adapter);

		// Click event for single list row
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
		});

		btnMapAll = (Button) findViewById(R.id.btnMapAll);

		btnMapAll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
//				new DoingLengthyTask().execute(listLinkedinUsers);
				
				if (filteringPercentage < cities.size()) {
					
					progressDialog.setMessage("Preparing Google Map..");
			        progressDialog.show();
			        
				}else{
					Toast.makeText(mContext, "All heavy work is completed..", Toast.LENGTH_SHORT).show();
				}
								
			}
		});
	}
}