package com.vsplc.android.poc.linkedin.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.internal.mc;
import com.google.android.gms.internal.mf;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vsplc.android.poc.linkedin.BaseActivity;
import com.vsplc.android.poc.linkedin.CustomizedListActivity;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.City;
import com.vsplc.android.poc.linkedin.utils.LinkedinApplication;
import com.vsplc.android.poc.linkedin.utils.MethodUtils;

public class GoogleMapFragment extends Fragment implements OnClickListener{
	
	private GoogleMap map;
	private SupportMapFragment fragment;
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
			arrOfCities = args.getStringArray("city_markers");
		}
		
		btnLeft.setOnClickListener(this);
		tvListAll.setOnClickListener(this);
		
		for (String name : arrOfCities) {
			Logger.vLog("onStart", "City : "+name);
		}
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (map == null) {
			map = fragment.getMap();
		}

		new AyscTaskForSettingOfMarkers().execute(arrOfCities);
			
		/*Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG).title("Hamburg"));

			Marker kiel = map.addMarker(new MarkerOptions()
			.position(KIEL)
			.title("Kiel")
			.snippet("Kiel is cool")
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));

			// Move the camera instantly to hamburg with a zoom of 15.
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

			// Zoom in, animating the camera.
			map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);*/
		
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
				
				LatLng latLng = MethodUtils.getLatLngFromGivenAddressGeoCoder(mFragActivityContext, address);
				
				city.latitude = String.valueOf(latLng.latitude);
				city.longitude = String.valueOf(latLng.longitude);
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
			 progressDialog.setMessage("GoogleMap preparing..");
		}
		
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		int key = view.getId();
		
		switch (key) {
		case R.id.btn_left:
			((BaseActivity) getActivity()).showHideNevigationDrawer();
			break;

		case R.id.tv_show_list:
			break;
			
		default:
			break;
		}
		
	}
	
}
