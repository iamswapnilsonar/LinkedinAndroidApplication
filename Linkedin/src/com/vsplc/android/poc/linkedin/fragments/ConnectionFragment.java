package com.vsplc.android.poc.linkedin.fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.vsplc.android.poc.linkedin.BaseActivity;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.adapter.ConnectionListAdapter;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.City;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.utils.ConstantUtils;
import com.vsplc.android.poc.linkedin.utils.LinkedinApplication;
import com.vsplc.android.poc.linkedin.utils.MethodUtils;

public class ConnectionFragment extends Fragment implements OnClickListener{

	private FragmentActivity mFragActivityContext;

	private ListView list;
	private ConnectionListAdapter adapter;
	private ArrayList<LinkedinUser> listLinkedinUsers;
	
	private Button btnLeft;
	private TextView tvMapAll;
	
	private static Set<String> cities = new HashSet<String>();
	private boolean isCitysWorkCompleted = false;
	private boolean isGoogleMapRequested = false;
	
	private ProgressDialog progressDialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mFragActivityContext = getActivity();
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.connection_fragment, container, false);
		
		list = (ListView) view.findViewById(R.id.list);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		tvMapAll = (TextView) view.findViewById(R.id.tv_map_all);
		
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		listLinkedinUsers = (ArrayList<LinkedinUser>)LinkedinApplication.listGlobalConnections;
		
		if (LinkedinApplication.hashTableOfCityInfo.size() > 0) {
			//NOP
			isCitysWorkCompleted = true;
		}else{
			new AsyncTaskForCities().execute(listLinkedinUsers);
		}

		// Getting adapter by passing xml data ArrayList
		adapter = new ConnectionListAdapter(mFragActivityContext, listLinkedinUsers);
		list.setAdapter(adapter);

		// View linkedin user profile on webview..
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
//				Toast.makeText(mFragActivityContext, "Postion : "+position, Toast.LENGTH_SHORT).show();
				
				LinkedinUser user = listLinkedinUsers.get(position);
				
				Logger.vLog("ConnectionFragment ", "User Public Profile URL : "+user.profileurl);
				
				// Create fragment and give it an argument for the selected article
	            LinkedinProfileFragment linkedinProfileFragment = (LinkedinProfileFragment) Fragment.instantiate(mFragActivityContext, 
	            						ConstantUtils.LINKEDIN_PROFILE_FRAGMENT);	           
	            
	            Bundle bundle = new Bundle();
	            bundle.putString("url", user.profileurl);
	            linkedinProfileFragment.setArguments(bundle);
	            
	            FragmentTransaction transaction = mFragActivityContext.getSupportFragmentManager().beginTransaction();

	            // Replace whatever is in the fragment_container view with this fragment,
	            // and add the transaction to the back stack so the user can navigate back
	            transaction.replace(R.id.fragment_container, linkedinProfileFragment, "linkedinprofile");
	            transaction.addToBackStack(null);

	            // Commit the transaction
	            transaction.commit();
				
			}
		});
		
		btnLeft.setOnClickListener(this);
		tvMapAll.setOnClickListener(this);
		
		progressDialog = new ProgressDialog(mFragActivityContext);		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		int key = view.getId();
		
		switch (key) {
		
		case R.id.btn_left:			
			((BaseActivity) getActivity()).showHideNevigationDrawer();
			break;

		case R.id.tv_map_all:
			
			isGoogleMapRequested = true;

			if (isCitysWorkCompleted) {
				// fragment is ready to open..
				
				// Create fragment and give it an argument for the selected article
	            GoogleMapFragment mapFragment = (GoogleMapFragment) Fragment.instantiate(mFragActivityContext, 
	            						ConstantUtils.GOOGLE_MAP_FRAGMENT);	           
	            
	            Bundle bundle = new Bundle();
	            String[] mArr = cities.toArray(new String[cities.size()]);
	            bundle.putStringArray("city_markers", mArr);
	            mapFragment.setArguments(bundle);
	            
	            FragmentTransaction transaction = mFragActivityContext.getSupportFragmentManager().beginTransaction();

	            // Replace whatever is in the fragment_container view with this fragment,
	            // and add the transaction to the back stack so the user can navigate back
	            transaction.replace(R.id.fragment_container, mapFragment, "googlemap");
	            transaction.addToBackStack(null);

	            // Commit the transaction
	            transaction.commit();
				
				
			} else{
				progressDialog.setMessage("Preparing Google Map..");
				progressDialog.show();
			}
			
			break;
			
		default:
			break;
		}
		
	}

	private class AsyncTaskForCities extends AsyncTask<Object, Void, String> {

		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub

			Logger.vLog("LinkedinApplication.listCityInfo : ", "Size : "+LinkedinApplication.hashTableOfCityInfo.size());
			
			@SuppressWarnings("unchecked")
			ArrayList<LinkedinUser> connections = (ArrayList<LinkedinUser>) params[0];

			for (int i = 0; i < connections.size(); i++) {

				LinkedinUser user = connections.get(i);

				if (user.location != null && user.country_code != null) {

					String mCity = MethodUtils.getCityNameFromLocation(user.location, user.country_code);

					// Prepared how many city connections available..
					if (mCity != null && cities.add(mCity)) {

						if (LinkedinApplication.hashTableOfCityInfo.contains(mCity)) {
							//NOP
						}else{
							
							City city = new City(mCity);
							String mcountry = MethodUtils.getISOCountryNameFromCC(user.country_code);
							city.country = mcountry;
							
//							LinkedinApplication.listCityInfo.add(city);
							LinkedinApplication.hashTableOfCityInfo.put(mCity, city);
						}
					
					} else {
						Logger.vLog("hashTableOfCityInfo", mCity + " already present.");
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

		@SuppressLint("NewApi")
		@Override
		protected String doInBackground(Void... params) {

			for (String city : cities) {
				
				City cityObject = LinkedinApplication.hashTableOfCityInfo.get(city);
				Logger.vLog("AyscGettingCityInfo : City ", cityObject.name);				
				
				if (cityObject.latitude.equals("NA") && cityObject.longitude.equals("NA")) {

					String address = cityObject.name + "," + cityObject.country;
//					cityObject.latLng = MethodUtils.getLatLngFromGivenAddressGeoCoder(mFragActivityContext, address);

					Geocoder geoCoder = new Geocoder(mFragActivityContext);
					
					if (Geocoder.isPresent()) {
						
						LatLng latLng = MethodUtils.getLatLngFromGivenAddressGeoCoder(mFragActivityContext, address);
						
						cityObject.latitude = String.valueOf(latLng.latitude);
						cityObject.longitude = String.valueOf(latLng.longitude);
						
					}else{
						
						LatLng latLng = MethodUtils.getLatLongFromGivenAddress(address);
						
						cityObject.latitude = String.valueOf(latLng.latitude);
						cityObject.longitude = String.valueOf(latLng.longitude);
						
					}
					
					
//					LatLng latLng = MethodUtils.getLatLngFromGivenAddressGeoCoder(mFragActivityContext, address);
//					
//					cityObject.latitude = String.valueOf(latLng.latitude);
//					cityObject.longitude = String.valueOf(latLng.longitude);
					
				}else{
					// NOP
				}

			}

			return "Completed";
		}

		@Override
		protected void onPostExecute(String result) {
			
			isCitysWorkCompleted = true;
			
			if (isGoogleMapRequested && isCitysWorkCompleted && result.equals("Completed")) {
				
				// fragment is ready to open..
				progressDialog.dismiss();
				
				// Create fragment and give it an argument for the selected article
	            GoogleMapFragment mapFragment = (GoogleMapFragment) Fragment.instantiate(mFragActivityContext, 
	            						ConstantUtils.GOOGLE_MAP_FRAGMENT);	           

	            Bundle bundle = new Bundle();
	            String[] mArr = cities.toArray(new String[cities.size()]);
	            bundle.putStringArray("city_markers", mArr);
	            mapFragment.setArguments(bundle);
	            
	            FragmentTransaction transaction = mFragActivityContext.getSupportFragmentManager().beginTransaction();
	            
	            // Replace whatever is in the fragment_container view with this fragment,
	            // and add the transaction to the back stack so the user can navigate back
	            transaction.replace(R.id.fragment_container, mapFragment, "googlemap");
	            transaction.addToBackStack(null);

	            // Commit the transaction
	            transaction.commit();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progressDialog = ProgressDialog.show(MainActivity.this, "Wait",
			// "Downloading..");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// filteringPercentage = values[0];
		}
	}
	
}