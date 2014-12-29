package com.vsplc.android.poc.linkedin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vsplc.android.poc.linkedin.adapter.IndustryListAdapter;
import com.vsplc.android.poc.linkedin.lasy_loading.LazyAdapter;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.City;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.utils.DataWrapper;
import com.vsplc.android.poc.linkedin.utils.LinkedinApplication;
import com.vsplc.android.poc.linkedin.utils.MethodUtils;

public class CustomizedListActivity extends FragmentActivity implements OnMarkerClickListener, OnInfoWindowClickListener{

	private ListView list;
	private LazyAdapter adapter;
	private ArrayList<LinkedinUser> listLinkedinUsers;

	private ListView industriesList;
	private IndustryListAdapter adapterIndustryListAdapter;
	private List<String> listIndustryNames;
	
	private Button btnMapAll, btnShowIndustries;
	private ProgressDialog progressDialog;

	private Context mContext;

	static int filteringPercentage = 0;

	public static Set<String> cities = new HashSet<String>();
	public Set<String> industries = new HashSet<String>();

	public boolean isGoogleMapRequested = false;
	public boolean isCitysWorkCompleted = false;
	
	private GoogleMap googleMap;
	private MarkerOptions markerOptions;

	private Double lat, longi;
	
	private double minLatitude = Integer.MAX_VALUE;
	private double maxLatitude = Integer.MIN_VALUE;
	private double minLongitude = Integer.MAX_VALUE;
	private double maxLongitude = Integer.MIN_VALUE;

	
	private Marker marker;	
	
	private WebView webView;	
	
	private class AsyncTaskForCities extends AsyncTask<Object, Void, String> {

		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub

			Logger.vLog("LinkedinApplication.listCityInfo : ", "Size : "+LinkedinApplication.listCityInfo.size());
			
			@SuppressWarnings("unchecked")
			ArrayList<LinkedinUser> connections = (ArrayList<LinkedinUser>) params[0];

			for (int i = 0; i < connections.size(); i++) {

				LinkedinUser user = connections.get(i);

				if (user.location != null && user.country_code != null) {

					String mCity = MethodUtils.getCityNameFromLocation(user.location, user.country_code);

					// Prepared how many city connections available..
					if (mCity != null && cities.add(mCity)) {

						if (LinkedinApplication.listCityInfo.contains(mCity)) {
							//NOP
						}else{
							
							City city = new City(mCity);
							String mcountry = MethodUtils.getISOCountryNameFromCC(user.country_code);
							city.country = mcountry;
							
							LinkedinApplication.listCityInfo.add(city);
							
						}
					
					} else {
						Logger.vLog("listCityInfo", mCity + " already present.");
					}

				}

			}

			return "Success";
		}

		@Override
		protected void onPostExecute(String result) {
			// progressDialog.dismiss();
//			Toast.makeText(mContext, result + " in retrieval of cities.", Toast.LENGTH_SHORT).show();			
			if (result.equals("Success")) {
				new AyscGettingCityInfo().execute();
			}					
		}

	}

	private class AyscGettingCityInfo extends AsyncTask<Void, Integer, String> {

		@Override
		protected String doInBackground(Void... params) {

			for (int i = 0; i < LinkedinApplication.listCityInfo.size(); i++) {

				City city = LinkedinApplication.listCityInfo.get(i);
				Logger.vLog("AyscGettingCityInfo : City ", city.name);

				
				/*if (city.latLng == null) {
					
					String address = city.name + "," + city.country;
//					address = address.replaceAll("\\s", "%20");
					city.latLng = MethodUtils.getLatLngFromGivenAddressGeoCoder(CustomizedListActivity.this, address);
					
				}else{
					// NOP
				}*/
				
				if (city.latitude.equals("NA") && city.longitude.equals("NA")) {
					String address = city.name + "," + city.country;
					
					LatLng latLng = MethodUtils.getLatLngFromGivenAddressGeoCoder(CustomizedListActivity.this, address);
					
					city.latitude = String.valueOf(latLng.latitude);
					city.longitude = String.valueOf(latLng.longitude);
				}else{
					// NOP
				}
				
				publishProgress(i);
			}

			return "Completed";
		}

		@Override
		protected void onPostExecute(String result) {

			isCitysWorkCompleted = true;
			
			for (int i = 0; i< LinkedinApplication.listCityInfo.size(); i++) {

				City city = LinkedinApplication.listCityInfo.get(i);
				
				
				
				try {

					Double mLat = Double.parseDouble(city.latitude);
					Double mLong = Double.parseDouble(city.longitude);
					
					LatLng latLng = new LatLng(mLat, mLong);
					
					Logger.vLog("AyscGettingCityInfo : ", "City - Latitude "+ mLat);
					Logger.vLog("AyscGettingCityInfo : ", "City - Longitude "+ mLong);

					maxLatitude = Math.max(mLat, maxLatitude);
					minLatitude = Math.min(mLat, minLatitude);
					maxLongitude = Math.max(mLong, maxLongitude);
					minLongitude = Math.min(mLong, minLongitude);

					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.position(latLng);
					markerOptions.title(city.name);
					markerOptions.snippet(city.country);
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.img_pin));
					
					Marker marker = googleMap.addMarker(markerOptions);

					Logger.vLog("DoingLengthyTask", "Marker "+ marker.getTitle().toString() + "added");

				} catch (Exception ex) {
					Logger.vLog("DoingLengthyTask","Error while creating markers");
				}
			}
			// progressDialog.show();

			if (isGoogleMapRequested && isCitysWorkCompleted && result.equals("Completed")) {
				
				progressDialog.dismiss();
				
				Bundle bundle = new Bundle();
				
				String[] arr = (String[]) cities.toArray();
				
				if (list.getVisibility() == View.VISIBLE
						|| industriesList.getVisibility() == View.VISIBLE 
						|| webView.getVisibility() == View.VISIBLE) {

					list.setVisibility(View.GONE);
					industriesList.setVisibility(View.GONE);
					webView.setVisibility(View.GONE);
					
					((RelativeLayout) findViewById(R.id.rl_googlemap_view)).setVisibility(View.VISIBLE);

				}
				
			}
			
			/**
			 * Display all the pin center of screen. All pins are visible.
			 * 
			 * @Date 15 December, 2015
			 * @author Swapnil
			 */
			/*if (googleMap != null) {

				googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {

					@Override
					public void onCameraChange(CameraPosition cameraPosition) {

						try{

							Logger.vLog("setOnCameraChangeListener :", "minLatitude : "+minLatitude+" \n minLongitude : "+minLongitude+
									"maxLatitude : "+maxLatitude+" \n maxLongitude : "+maxLongitude);

							// Move camera.						
							googleMap.moveCamera(CameraUpdateFactory
									.newLatLngBounds(new LatLngBounds(new LatLng(minLatitude, minLongitude),
											new LatLng(maxLatitude, maxLongitude)), 20));

							// Remove listener to prevent position reset on camera move.
							googleMap.setOnCameraChangeListener(null);

						}catch(Exception ex){
							Logger.vLog("Exception :", ex.toString());
						}

					}
				});

			}*/

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

	private class AsyncTaskForIndustriesNames extends AsyncTask<Object, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			if (!progressDialog.isShowing()) {
				progressDialog.setMessage("Get Industries details");
				progressDialog.show();
			}

		}

		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub

			@SuppressWarnings("unchecked")
			ArrayList<LinkedinUser> connections = (ArrayList<LinkedinUser>) params[0];

			// clear all previous industry list
			industries.clear();
			listIndustryNames.clear();
			
			for (int i = 0; i < connections.size(); i++) {

				LinkedinUser user = connections.get(i);

				// Prepared how many city connections available..
				if (user.industry != null && industries.add(user.industry)) {
					// NOP
					listIndustryNames.add(user.industry);
				} else {
					Logger.vLog("AsyncTaskForIndustriesNames", user.industry
							+ " already present.");
				}

			}

			return "Success";
		}

		@Override
		protected void onPostExecute(String result) {

			if (progressDialog.isShowing() && result.equals("Success")) {
				
				Logger.vLog("AsyncTaskForIndustriesNames : onPostExecute", "Hello");
				
				progressDialog.dismiss();
				
				if (((RelativeLayout) findViewById(R.id.rl_googlemap_view)).getVisibility() == View.VISIBLE
						|| list.getVisibility() == View.VISIBLE 
						|| webView.getVisibility() == View.VISIBLE) {
					
					list.setVisibility(View.GONE);					
					((RelativeLayout) findViewById(R.id.rl_googlemap_view)).setVisibility(View.GONE);					
					webView.setVisibility(View.GONE);

					adapterIndustryListAdapter.notifyDataSetChanged();
					industriesList.setVisibility(View.VISIBLE);
				}
								
			}
		}

	}

	private class GetCityWiseConnections extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			if (!progressDialog.isShowing()) {
				progressDialog.setMessage("Get City wise Connections..");
				progressDialog.show();				
			}

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String city = params[0];
			String country = params[1];
			
			
			ArrayList<LinkedinUser> temp = MethodUtils.getCitywiseConnections(city, country); 
			listLinkedinUsers.clear();
			listLinkedinUsers.addAll(temp);
			
			Logger.vLog("doInBackground", "Size of listLinkedinUsers : "+listLinkedinUsers.size());
			
			return "Success";
		}

		@Override
		protected void onPostExecute(String result) {

			if (progressDialog.isShowing() && result.equals("Success")) {
				
				progressDialog.dismiss();

				
				
				if (((RelativeLayout) findViewById(R.id.rl_googlemap_view)).getVisibility() == View.VISIBLE
						|| industriesList.getVisibility() == View.VISIBLE 
						|| webView.getVisibility() == View.VISIBLE) {
										
					industriesList.setVisibility(View.GONE);
					webView.setVisibility(View.GONE);
					((RelativeLayout) findViewById(R.id.rl_googlemap_view)).setVisibility(View.GONE);

					adapter.notifyDataSetChanged();
					list.setVisibility(View.VISIBLE);
				}
			}
		}

	}
	
	private class GetIndustryWiseConnections extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			if (!progressDialog.isShowing()) {
				progressDialog.setMessage("Get Industry wise Connections..");
				progressDialog.show();				
			}

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String industry = params[0];
//			String country = params[1];
						
			ArrayList<LinkedinUser> temp = MethodUtils.getIndustrywiseConnections(industry, listLinkedinUsers); 
			listLinkedinUsers.clear();
			listLinkedinUsers.addAll(temp);
			
			Logger.vLog("GetIndustryWiseConnections : doInBackground", "Size of listLinkedinUsers : "+listLinkedinUsers.size());
			
			return "Success";
		}

		@Override
		protected void onPostExecute(String result) {

			if (progressDialog.isShowing() && result.equals("Success")) {
				
				progressDialog.dismiss();

				
				
				if (((RelativeLayout) findViewById(R.id.rl_googlemap_view)).getVisibility() == View.VISIBLE
						|| industriesList.getVisibility() == View.VISIBLE || webView.getVisibility() == View.VISIBLE) {
					
					industriesList.setVisibility(View.GONE);
					webView.setVisibility(View.GONE);
					((RelativeLayout) findViewById(R.id.rl_googlemap_view)).setVisibility(View.GONE);
					
					adapter.notifyDataSetChanged();
					list.setVisibility(View.VISIBLE);

				}
			}
		}

	}
	
	/*
	 * private DownloadObserver getLocationInfoDownloadObserver = new
	 * DownloadObserver() {
	 * 
	 * @Override public void onDownloadingStart() {}
	 * 
	 * @Override public void onDownloadingComplete(Object data) {
	 * Log.v("onDownloadingComplete : ", ""+data.toString()); new
	 * ResponseManager().parseGeocoderWebserviceResult(data.toString()); }
	 * 
	 * @Override public void onDownloadFailure(Object errorData) {} };
	 */

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			// if (Uri.parse(url).getHost().equals("https://www.linkedin.com"))
			// {
			// // This is my web site, so do not override; let my WebView load
			// // the page
			// return false;
			// }
			// // Otherwise, the link is not for a page on my site, so launch
			// // another Activity that handles URLs
			// Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			// startActivity(intent);
			// return true;

			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);

		mContext = CustomizedListActivity.this;

		DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
		listLinkedinUsers = dw.getList();

		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		
		googleMap.setOnMarkerClickListener(this);
		googleMap.setOnInfoWindowClickListener(this);

		
		// get and store information about cities
		new AsyncTaskForCities().execute(listLinkedinUsers);		

		// showing progress dialog while performing heavy tasks..
		progressDialog = new ProgressDialog(mContext);

		// load profile-url into webview
		webView = (WebView) findViewById(R.id.webview);
		
		// enable Java Script
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportMultipleWindows(true);
		webView.getSettings().setPluginState(PluginState.ON);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		
		// override the web client to open all links in the same webview
		webView.setWebViewClient(new MyWebViewClient());
		
		list = (ListView) findViewById(R.id.list);

		// Getting adapter by passing xml data ArrayList
		adapter = new LazyAdapter(this, listLinkedinUsers);
		list.setAdapter(adapter);

		// Click event for single list row
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (list.getVisibility() == View.VISIBLE 
						|| industriesList.getVisibility() == View.VISIBLE 
						|| ((RelativeLayout) findViewById(R.id.rl_googlemap_view)).getVisibility() == View.VISIBLE) {

					list.setVisibility(View.GONE);
					industriesList.setVisibility(View.GONE);
					((RelativeLayout) findViewById(R.id.rl_googlemap_view)).setVisibility(View.GONE);
					
					webView.setVisibility(View.VISIBLE);
					
					LinkedinUser user = listLinkedinUsers.get(position);
					Logger.vLog("User : Profile URL ",""+user.profileurl);				
					webView.loadUrl(user.profileurl);

				}
											
			}
		});

		industriesList = (ListView) findViewById(R.id.industries_list);
		// industries.add("Sample");

		listIndustryNames = new ArrayList<String>();

		// Getting adapter by passing xml data ArrayList
		adapterIndustryListAdapter = new IndustryListAdapter(this,
				listIndustryNames);
		industriesList.setAdapter(adapterIndustryListAdapter);

		// Click event for single list row
		industriesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				String industry = listIndustryNames.get(position);
				Logger.vLog("industriesList : onItemClick",""+industry);
				
				new GetIndustryWiseConnections().execute(industry);
			}
		});

		btnMapAll = (Button) findViewById(R.id.btnMapAll);

		btnMapAll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
//				 new DoingLengthyTask().execute(listLinkedinUsers);

				isGoogleMapRequested = true;
				
				if (isCitysWorkCompleted) {
					
					if (list.getVisibility() == View.VISIBLE
							|| industriesList.getVisibility() == View.VISIBLE 
							|| webView.getVisibility() == View.VISIBLE) {

						list.setVisibility(View.GONE);
						industriesList.setVisibility(View.GONE);
						webView.setVisibility(View.GONE);
						
						((RelativeLayout) findViewById(R.id.rl_googlemap_view)).setVisibility(View.VISIBLE);

					}
					
				}else{
					progressDialog.setMessage("Preparing Google Map..");
					progressDialog.show();
				}
				
				/*Logger.vLog("btnMapAll : ", "filteringPercentage : "+ filteringPercentage + " listCityInfo : "
						+ LinkedinApplication.listCityInfo.size());

				if (filteringPercentage < LinkedinApplication.listCityInfo.size() - 1) {

					

				} else {
					Toast.makeText(mContext, "All heavy work is completed..", Toast.LENGTH_SHORT).show();
				}*/

			}
		});

		btnShowIndustries = (Button) findViewById(R.id.btnShowIndustries);
		btnShowIndustries.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AsyncTaskForIndustriesNames().execute(listLinkedinUsers);
			}
		});

	}

	private class CustomInfoWindowAdapter implements InfoWindowAdapter {

		private View view;

		public CustomInfoWindowAdapter() {
			view = getLayoutInflater().inflate(R.layout.marker_infowindow_layout, null);
		}

		@Override
		public View getInfoContents(Marker marker) {

			if (CustomizedListActivity.this.marker != null
					&& CustomizedListActivity.this.marker.isInfoWindowShown()) {
				
				CustomizedListActivity.this.marker.hideInfoWindow();
//				CustomizedListActivity.this.marker.showInfoWindow();
				
			}
			return null;
		}

		@Override
		public View getInfoWindow(final Marker marker) {
			
			CustomizedListActivity.this.marker = marker;
			
//			final String title = marker.getTitle();
//			
//			final TextView titleUi = ((TextView) view.findViewById(R.id.balloon_item_title));
//			if (title != null) {
//				titleUi.setText(title);
//			} else {
//				titleUi.setText("");
//			}

			return view;
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		Logger.vLog("onMarkerClick", "Marker Title : " + marker.getTitle() + " Snippet : " + marker.getSnippet());
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub
//		Toast.makeText(CustomizedListActivity.this, marker.getSnippet(), Toast.LENGTH_SHORT).show();
		
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		}
		
		if (marker.getTitle() != null && marker.getSnippet() != null) {
			new GetCityWiseConnections().execute(marker.getTitle(), marker.getSnippet());
			/*listLinkedinUsers.clear();
			listLinkedinUsers = MethodUtils.getCitywiseConnections(marker.getTitle(), marker.getSnippet());
			
			Toast.makeText(CustomizedListActivity.this, "Connection Size : "+listLinkedinUsers.size(), Toast.LENGTH_SHORT).show();
			
			adapter.notifyDataSetChanged();
			
			if (((RelativeLayout) findViewById(R.id.rl_googlemap_view)).getVisibility() == View.VISIBLE
					|| industriesList.getVisibility() == View.VISIBLE) {

				list.setVisibility(View.VISIBLE);
				industriesList.setVisibility(View.GONE);
				((RelativeLayout) findViewById(R.id.rl_googlemap_view)).setVisibility(View.GONE);

			}*/
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		isCitysWorkCompleted = false;
		isGoogleMapRequested = false;
	}
}