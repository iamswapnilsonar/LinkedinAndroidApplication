package com.vsplc.android.poc.linkedin.fragments;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vsplc.android.poc.linkedin.BaseActivity;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.City;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.utils.ConstantUtils;
import com.vsplc.android.poc.linkedin.utils.DataWrapper;
import com.vsplc.android.poc.linkedin.utils.LinkedinApplication;
import com.vsplc.android.poc.linkedin.utils.MethodUtils;

public class GoogleMapFragment extends Fragment implements OnClickListener, OnInfoWindowClickListener{
	
	private GoogleMap map;
	private SupportMapFragment fragment;
	private Marker marker;
//	private MarkerOptions markerOptions;
	
	private Button btnLeft;
	private TextView tvListAll;
	
	static final LatLng INDIA = new LatLng(21.0000, 78.0000);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    
    private FragmentActivity mFragActivityContext;
    
	private double minLatitude = Integer.MAX_VALUE;
	private double maxLatitude = Integer.MIN_VALUE;
	private double minLongitude = Integer.MAX_VALUE;
	private double maxLongitude = Integer.MIN_VALUE;
	
	private ProgressDialog progressDialog;
	
	private String[] arrOfCities;
	private List<LinkedinUser> mConnections = new ArrayList<LinkedinUser>();
	
	private String markerType;
	private LinkedinUser linkedinUser;	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mFragActivityContext = getActivity();
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.map_fragment, container, false);
		
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		tvListAll = (TextView) view.findViewById(R.id.tv_show_list);
		
        return view;
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, fragment).commit();
		}
		
	}
	
	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been applied to the fragment at this point so we can safely call the
		// method below that sets the article text.
		Bundle args = getArguments();
		
		if (args != null) {
			
			markerType = args.getString("marker_type");
			
			if (markerType.equals("MapAll")) {
				
				arrOfCities = args.getStringArray("city_markers");
				
				DataWrapper dataWrapper = (DataWrapper) args.getSerializable("connection_list");
				mConnections = dataWrapper.getList();
				Logger.vLog("GoogleMapFragment", "mConnections : "+mConnections.size());	
				
				for (String name : arrOfCities) {
					Logger.vLog("onStart", "City : "+name);
				}
				
			}else{
				
				linkedinUser = (LinkedinUser) args.getSerializable("user");				
			}
		}
		
		btnLeft.setOnClickListener(this);
		tvListAll.setOnClickListener(this);
		
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		LatLng mLinkedinUserPosition = new LatLng(0.0, 0.0);

		if (map == null) {
			map = fragment.getMap();						
		}

		if (markerType.equals("MapAll")) {
			
			// use google map for mulitple markers
			new AyscTaskForSettingOfMarkers().execute(arrOfCities);
			map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
			map.setOnInfoWindowClickListener(this);
			
		}else{			

			tvListAll.setVisibility(View.INVISIBLE);
			btnLeft.setBackgroundResource(R.drawable.btn_back_tap_effect);
			
			// try indivisual marker
			if (linkedinUser.location != null && linkedinUser.country_code != null) {

				String mCity = MethodUtils.getCityNameFromLocation(linkedinUser.location, linkedinUser.country_code);
				String mCountry = MethodUtils.getISOCountryNameFromCC(linkedinUser.country_code);

				String address;

				if (mCity.equals("NA"))
					address = mCountry;
				else
					address = mCity + "," + mCountry;

				if (Geocoder.isPresent())
					mLinkedinUserPosition = MethodUtils.getLatLngFromGivenAddressGeoCoder(mFragActivityContext, address);
				else
					mLinkedinUserPosition = MethodUtils.getLatLongFromGivenAddress(address);


				Marker linkedinUserMarker = map.addMarker(new MarkerOptions()
				.position(mLinkedinUserPosition)
				.title(linkedinUser.fname +" "+linkedinUser.lname)
				.snippet(linkedinUser.location+", "+MethodUtils.getISOCountryNameFromCC(linkedinUser.country_code))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.img_marker)));

				// Move the camera instantly to hamburg with a zoom of 15.
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(mLinkedinUserPosition, 15));

				// Zoom in, animating the camera.
				map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

			}else {
				Logger.vLog("GoogleMapFragment", "User Location not available..");
			}

		}

	}
	
	private class AyscTaskForSettingOfMarkers extends AsyncTask<String[], String, String> {

		@Override
		protected String doInBackground(String[]... params) {

			String[] arrCityMarkers = params[0];
			
			Logger.vLog("doInBackground", "Array of Cities : "+arrCityMarkers.length);

			for (int i = 0; i < arrCityMarkers.length; i++) {

				String cityName = arrCityMarkers[i];

				if (LinkedinApplication.hashTableOfCityInfo.containsKey(cityName)) {
					// If city is available
					publishProgress(cityName);
				}
			}

			return "Completed";
		}

		@SuppressLint("NewApi")
		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
			String mCity = values[0];
			
			Logger.vLog("onProgressUpdate - City ", mCity);
			
			City city = LinkedinApplication.hashTableOfCityInfo.get(mCity);
			Logger.vLog("AyscGettingCityInfo : City ", city.name);

			if (city.latitude.equals("NA") && city.longitude.equals("NA")) {
				String address = city.name + "," + city.country;
				
				if (Geocoder.isPresent()) {
					
					LatLng latLng = MethodUtils.getLatLngFromGivenAddressGeoCoder(mFragActivityContext, address);
					
					city.latitude = String.valueOf(latLng.latitude);
					city.longitude = String.valueOf(latLng.longitude);
					
				}else{
					
					LatLng latLng = MethodUtils.getLatLongFromGivenAddress(address);
					
					city.latitude = String.valueOf(latLng.latitude);
					city.longitude = String.valueOf(latLng.longitude);
					
				}
				
			}					
			
			if (map != null) {

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
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.img_marker));
					
					Marker marker = map.addMarker(markerOptions);

					Logger.vLog("AyscTaskForSettingOfMarkers", "Marker "+ marker.getTitle().toString() + "added");
				} catch (Exception ex) {
					Logger.vLog("AyscTaskForSettingOfMarkers","Error while creating markers"+ex.toString());
				}

			}else{
				Logger.vLog("AyscTaskForSettingOfMarkers","Google Map is NULL");
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
	
			progressDialog.dismiss();
			
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
			
			// Move the camera instantly to hamburg with a zoom of 15.
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(INDIA, 7));

			// Zoom in, animating the camera.
			map.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			 progressDialog = new ProgressDialog(mFragActivityContext);
			 progressDialog.setCancelable(false);
			 progressDialog.setMessage("GoogleMap preparing..");
		}
		
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		int key = view.getId();
		
		switch (key) {
		case R.id.btn_left:
			
			if (markerType.equals("MapAll")) {
				((BaseActivity) getActivity()).showHideNevigationDrawer();
			}else{
				getActivity().onBackPressed();
			}
			
//			getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//			getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
			
			
			break;

		case R.id.tv_show_list:
			break;
			
		default:
			break;
		}
		
	}
	
	private class CustomInfoWindowAdapter implements InfoWindowAdapter {

		private View view;

		public CustomInfoWindowAdapter() {
			view = mFragActivityContext.getLayoutInflater().inflate(R.layout.marker_infowindow_layout, null);
		}

		@Override
		public View getInfoContents(Marker marker) {

			if (GoogleMapFragment.this.marker != null
					&& GoogleMapFragment.this.marker.isInfoWindowShown()) {
				
				GoogleMapFragment.this.marker.hideInfoWindow();
//				CustomizedListActivity.this.marker.showInfoWindow();
				
			}
			return null;
		}

		@Override
		public View getInfoWindow(final Marker marker) {
			
			GoogleMapFragment.this.marker = marker;
			
			final String text = marker.getTitle() +","+marker.getSnippet();
			
			final TextView titleUi = ((TextView) view.findViewById(R.id.tv_marker_location));
			
			if (text != null) {
				titleUi.setText(text);
			}

			return view;
		}
	}


	@Override
	public void onInfoWindowClick(final Marker marker) {
		// TODO Auto-generated method stub
			
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		}
		
		if (marker.getTitle() != null && marker.getSnippet() != null) {
			Toast.makeText(mFragActivityContext, marker.getTitle(), Toast.LENGTH_SHORT).show();
		}
		
		
		FragmentTransaction transaction = mFragActivityContext.getSupportFragmentManager().beginTransaction();

		// Create fragment and give it an argument for the selected article
		ConnectionFragment connectionFragment = (ConnectionFragment) Fragment.instantiate(mFragActivityContext, 
				ConstantUtils.CONNECTION_FRAGMENT);
		
		Bundle bundle = new Bundle();
		
//		bundle.putString("parentFragment", "");
		
//		String city = args.getString("city");
//		String country = args.getString("country");
//		
//		ArrayList<LinkedinUser> mConnections = MethodUtils.getCitywiseConnections(city, country); 
		
		Logger.vLog("onInfoWindowClick","City : "+marker.getTitle());
		Logger.vLog("onInfoWindowClick","Country : "+marker.getSnippet());
		
		if (mConnections.size() > 0) {
			
			ArrayList<LinkedinUser> mConns = MethodUtils.getCitywiseConnections(mConnections, marker.getTitle(), marker.getSnippet());
			
			DataWrapper dataWrapper = new DataWrapper(mConns);
			bundle.putSerializable("connection_list", dataWrapper);
			
		}
		
		connectionFragment.setArguments(bundle);

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.fragment_container, connectionFragment, "connections");
		
		transaction.addToBackStack(null);		 
		transaction.commit();
		
		
//			new GetCityWiseConnections().execute(marker.getTitle(), marker.getSnippet());
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
