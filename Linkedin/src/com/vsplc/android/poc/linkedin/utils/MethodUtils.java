package com.vsplc.android.poc.linkedin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.vsplc.android.poc.linkedin.BaseActivity;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;

public class MethodUtils {

	public static LatLng getLatLngFromGivenAddressGeoCoder(Context context, String youraddress) {
		
		LatLng position;
		
		double latitude = 0;
		double longitude = 0;
		
		//String addressStr = "faisalabad";/// this give me correct address
//		String addressStr = "Pune,India";
		Geocoder geoCoder = new Geocoder(context);

		try {
			
			List<Address> addresses = geoCoder.getFromLocationName(youraddress, 1); 
			
			if (addresses.size() >  0) {
				latitude = addresses.get(0).getLatitude(); 
				longitude = addresses.get(0).getLongitude(); 
			}

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace(); }

//		Logger.vLog("getLatLngFromGivenAddressGeoCoder", "Address : "+youraddress);
		Logger.vLog("getLatLngFromGivenAddressGeoCoder", "Address : "+youraddress+" Latitude : "+latitude+" Longitude : "+longitude);
		
		position = new LatLng(latitude, longitude);
		
		return position;
	}

	public static LatLng getLatLongFromGivenAddress(String youraddress) {

		double lat = 0;
		double lng = 0;

		String uri = "http://maps.google.com/maps/api/geocode/json?key=AIzaSyBGQ5kh-rId4MY6_W2daPvx0I1gEjDBLE4&address="
				+ youraddress + "&sensor=false";

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

			lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

			Logger.vLog("latitude", "" + lat);
			Logger.vLog("longitude", "" + lng);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new LatLng(lat, lng);

	}
	
	public static void printSet(Set<String> set) {

		// Get an iterator
		Iterator<String> iterator = set.iterator();

		// Display elements
		while (iterator.hasNext()) {
			Logger.vLog("printSet", "Value : " + iterator.next());
		}

	}

	public static Map<String, List<LinkedinUser>> getCountrywiseConnections(
			List<LinkedinUser> listConnections) {

		Map<String, List<LinkedinUser>> mapCountrywiseConnections = new HashMap<String, List<LinkedinUser>>();
		Iterator<String> iterator = LinkedinApplication.setOfGlobalCountries
				.iterator();

		while (iterator.hasNext()) {
			String code = iterator.next();

			List<LinkedinUser> list = new ArrayList<LinkedinUser>();

			for (int i = 0; i < listConnections.size(); i++) {
				LinkedinUser user = listConnections.get(i);

				if (user.country_code != null && user.country_code.equals(code)) {
					// NOP
					list.add(user);
				}
			}

			Logger.vLog("Users", "" + list.size());
			mapCountrywiseConnections.put(code, list);
		}

		// printMap(mapCountrywiseConnections);
		return mapCountrywiseConnections;
	}

	public static List<LinkedinUser> getTotalCountrywiseConnections(String country,
			List<LinkedinUser> listConnections) {

		List<LinkedinUser> list = new ArrayList<LinkedinUser>();

		for (int i = 0; i < listConnections.size(); i++) {
			LinkedinUser user = listConnections.get(i);

			if (user.country_code != null && user.country_code.equals(country)) {
				// NOP
				list.add(user);
			}
		}

		Logger.vLog("Users list : ", "" + list.size());
		return list;
	}
	
	public static ArrayList<LinkedinUser> getIndustrywiseConnections(
			String industry, List<LinkedinUser> connections) {

		ArrayList<LinkedinUser> filteredConnections = new ArrayList<LinkedinUser>();

		for (int i = 0; i < connections.size(); i++) {
			LinkedinUser user = connections.get(i);

			if (user.industry != null && user.industry.equals(industry)) {
				// NOP
				filteredConnections.add(user);
			}
		}

		Logger.vLog(industry + "Users", "" + filteredConnections.size());

		for (LinkedinUser user : filteredConnections)
			Logger.vLog("Print", "Value : " + user.toString());
		return filteredConnections;
	}

	public static ArrayList<LinkedinUser> getCitywiseConnections(String city, String country) {

		List<LinkedinUser> connections = LinkedinApplication.listGlobalConnections;
		Logger.vLog("City : ", city);
		
		ArrayList<LinkedinUser> filteredConnections = new ArrayList<LinkedinUser>();

		for (int i = 0; i < connections.size(); i++) {
			LinkedinUser user = connections.get(i);

			if (user.location != null && user.location.contains(city)) {
				// NOP
				filteredConnections.add(user);
			}
		}

//		Logger.vLog(city + "Users", "" + filteredConnections.size());

//		for (LinkedinUser user : filteredConnections)
//			Logger.vLog("Print", "Value : " + user.toString());
		
		return filteredConnections;
	}
	
	public static void printMap(Map<String, List<LinkedinUser>> map) {

		for (Entry<String, List<LinkedinUser>> entry : map.entrySet()) {

			String country_code = entry.getKey();
			Logger.vLog("CC", country_code);

			List<LinkedinUser> list = entry.getValue();
			Logger.vLog("Users", "" + list.size());

			for (LinkedinUser user : list)
				Logger.vLog("Print", "Value : " + user.toString());
		}
	}

	/**
	 * get city from location area.
	 * 
	 * @param location
	 * @param countryCode
	 * @return
	 */
	public static String getCityNameFromLocation(String location,
			String countryCode) {

		String str = location;

		String strWordArea = "Area";
		String strWordCountry = getISOCountryNameFromCC(countryCode);
		String strWordSemiColon = ",";

		if (str.equalsIgnoreCase(strWordCountry)) {
			// NOP
			str = "NA";
		} else {
			if (str.contains(strWordArea)) {
				str = str.replace(" " + strWordArea, "");
				if (str.contains(strWordCountry)) {
					str = str.replace(" " + strWordCountry, "");
					if (str.contains(strWordSemiColon)) {
						str = str.replace(strWordSemiColon, "");

					}
				}
			}
		}

		// System.out.println(str);
		return str;
	}

	/**
	 * get ISO Country name from Country Code.
	 * 
	 * @param countryCode
	 * @return
	 */
	public static String getISOCountryNameFromCC(String countryCode) {
		Locale obj = new Locale("", countryCode.toUpperCase());
		String strCountryName = obj.getDisplayCountry();
		return strCountryName;
	}
	
	
    public static LinkedinUser getObject(Context context){
    	SharedPreferences mPrefs = ((BaseActivity) context).getPreferences(Context.MODE_PRIVATE);
    	Gson gson = new Gson();
        String json = mPrefs.getString("LinkedinUser", "");
        LinkedinUser user = gson.fromJson(json, LinkedinUser.class);
        return user;
    }
    
    public static void saveObject(Context context, LinkedinUser object){

    	SharedPreferences mPrefs = ((BaseActivity) context).getPreferences(Context.MODE_PRIVATE);
    	Editor prefsEditor = mPrefs.edit();

    	Gson gson = new Gson();
    	String json = gson.toJson(object);
    	prefsEditor.putString("LinkedinUser", json);
    	prefsEditor.commit();

    	Logger.vLog("saveObject", "Object Saved..");
    }
}
