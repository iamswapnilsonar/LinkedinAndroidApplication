package com.vsplc.android.poc.linkedin.networking;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.utils.LinkedinApplication;

/**
 * This is JSON parsing class which parse the response received from Linkedin Server.
 * 
 * @Date 19 Sept, 2014
 * @author VSPLC
 */
public class ResponseManager {

	// response status values
	private static final String TAG_CONNECTION_COUNT = "_count";
	private static final String TAG_CONNECTION_START = "_start";
	private static final String TAG_CONNECTION_TOTAL = "_total";
	private static final String TAG_CONNECTION_VALUES = "values";

	// users details
	private static final String TAG_LK_USER_ID = "id";
	private static final String TAG_LK_USER_FIRST_NAME = "firstName";
	private static final String TAG_LK_USER_LAST_NAME = "lastName";
	private static final String TAG_LK_USER_INDUSTRY = "industry";
	private static final String TAG_LK_USER_LOCATION = "location";

	// location parameters
	private static final String TAG_LK_USER_LOCATION_COUNTRY = "country";
	private static final String TAG_LK_USER_LOCATION_COUNTRY_CODE = "code";
	private static final String TAG_LK_USER_LOCATION_NAME = "name";

	private static final String TAG_LK_USER_HEADLINE = "headline";
	private static final String TAG_LK_USER_PICTURE_URL = "pictureUrl";	
	private static final String TAG_LK_USER_PROFILE_URL = "publicProfileUrl";
	
	/**
	 * @Date : 29 August, 2013 This is the function to parse Data (where Data is
	 *       in JSON format)
	 * @param res
	 *            - response from web service
	 * @param Url
	 *            - Category for web service
	 **/
	public List<LinkedinUser> parse(Object jsonResponseObject) throws Exception { 

		List<LinkedinUser> listLinkedinUsers = new ArrayList<LinkedinUser>();
		
		JSONObject jsonObject = (JSONObject) jsonResponseObject;

//		Log.v("Linkedin Connections Response : ","" + jsonResponseObject.toString());

		try {

			int iConnectionCount = jsonObject.getInt(TAG_CONNECTION_COUNT);
			Logger.vLog("Connection Count : ", "" + iConnectionCount);
			LinkedinApplication.iConnectionCount = iConnectionCount;
			
			@SuppressWarnings("unused")
			int iConnectionStart = jsonObject.getInt(TAG_CONNECTION_START);
//			Log.v("Connection Start : ", "" + iConnectionStart);

			@SuppressWarnings("unused")
			int iConnectionTotal = jsonObject.getInt(TAG_CONNECTION_TOTAL);
//			Log.v("Connection Total : ", "" + iConnectionTotal);

			// contacts JSONArray
			JSONArray arrLKConnectionValues = jsonObject.getJSONArray(TAG_CONNECTION_VALUES);

			// when error found at server side may this will come as NULL
			if (arrLKConnectionValues != null) {

				// looping through all connections
				for (int i = 0; i < arrLKConnectionValues.length(); i++) {

					String fname = null, lname = null, id = null, industry = null, country_code = null, location = null;
					String profilepicture = null, profileurl = null, headline = null;
					
					LinkedinUser linkedinUser;

					JSONObject jsonObjectLKUser = arrLKConnectionValues.getJSONObject(i);
					fname = jsonObjectLKUser.getString(TAG_LK_USER_FIRST_NAME);
//					Log.v("LinkedinUser : ", "First Name : "+ fname);
					
//					linkedinUser.setStrLinkedinUserFirstName(strLKUserFirstName);
					
					id = jsonObjectLKUser.getString(TAG_LK_USER_ID);
//					Log.v("LinkedinUser : ", "ID : " + id);
//					linkedinUser.setStrLinkedinUserID(strLKUserID);

					try {
						
						headline = jsonObjectLKUser.getString(TAG_LK_USER_HEADLINE);
//						Log.v("LinkedinUser : ", "Industry : "+ industry);
//						linkedinUser.setStrLinkedinUserWorkingDomain(strLKUserIndustry);
						
					} catch (JSONException ex_json) {
						// TODO: handle exception
//						Log.v("JSONException : ", "Industry value not found");
					}
					
					try {
						
						industry = jsonObjectLKUser.getString(TAG_LK_USER_INDUSTRY);
//						Log.v("LinkedinUser : ", "Industry : "+ industry);
//						linkedinUser.setStrLinkedinUserWorkingDomain(strLKUserIndustry);
						
					} catch (JSONException ex_json) {
						// TODO: handle exception
//						Log.v("JSONException : ", "Industry value not found");
					}

					try {
						lname = jsonObjectLKUser.getString(TAG_LK_USER_LAST_NAME);
//						Log.v("LinkedinUser : ", "Last Name : "+ lname);
//						linkedinUser.setStrLinkedinUserLastName(strLKUserLastName);
					} catch (JSONException ex_json) {
						// TODO: handle exception
//						Log.v("JSONException : ", "Last Name value not found");
					}

					try {

						JSONObject jsonObjectLKUserLocation = jsonObjectLKUser.getJSONObject(TAG_LK_USER_LOCATION);

						try {

							JSONObject jsonObjectLKUserLocationCountry = jsonObjectLKUserLocation
									.getJSONObject(TAG_LK_USER_LOCATION_COUNTRY);
							
							country_code = jsonObjectLKUserLocationCountry
									.getString(TAG_LK_USER_LOCATION_COUNTRY_CODE);
//							Log.v("LinkedinUser : ", "Country Code : "+ country_code);
//							linkedinUser.setStrLinkedinUserCountryLocation(strLKUserLocaionCountryCode);

							try {
								location = jsonObjectLKUserLocation.getString(TAG_LK_USER_LOCATION_NAME);
//								Log.v("LinkedinUser : ", "Locaion Area : "+ location);
//								linkedinUser.setStrLinkedinUserLocationArea(strLKUserLocaionArea);
							} catch (JSONException ex_json) {
								// TODO: handle exception
//								Log.v("JSONException : ","Location Area value not found");
							}

						} catch (JSONException ex_json) {
							// TODO: handle exception
//							Log.v("JSONException : ", "Country value not found");
						}

					} catch (JSONException ex_json) {
						// TODO: handle exception
//						Log.v("JSONException : ", "Location value not found");
					}
					
					try {
						
						profilepicture = jsonObjectLKUser.getString(TAG_LK_USER_PICTURE_URL);
//						Log.v("LinkedinUser : ", "Industry : "+ industry);
//						linkedinUser.setStrLinkedinUserWorkingDomain(strLKUserIndustry);
						
					} catch (JSONException ex_json) {
						// TODO: handle exception
//						Log.v("JSONException : ", "Industry value not found");
					}

					try {
						profileurl = jsonObjectLKUser.getString(TAG_LK_USER_PROFILE_URL);
//						Log.v("LinkedinUser : ", "Last Name : "+ lname);
//						linkedinUser.setStrLinkedinUserLastName(strLKUserLastName);
					} catch (JSONException ex_json) {
						// TODO: handle exception
//						Log.v("JSONException : ", "Last Name value not found");
					}

					linkedinUser = new LinkedinUser(id, fname, lname, industry, country_code, location, profilepicture, profileurl, headline);
//					Log.v("ResponseManager : ", linkedinUser.toString());
					
					// Add the LinkedinUser to list
					listLinkedinUsers.add(linkedinUser);
					
					// Prepared how many country connections available..
					if (country_code != null) {
						LinkedinApplication.setOfGlobalCountries.add(country_code);
					}
					
					// how many industry connections available..
					if (industry != null) {
						LinkedinApplication.setOfGlobalIndustryNames.add(industry);
					}
					
				}

			}

		} catch (JSONException jsonException) {
			// TODO: handle exception
		}

//		Logger.vLog("LinkedinApplication ", LinkedinApplication.setOfGlobalCountries);
//		MethodUtils.printSet(LinkedinApplication.setOfGlobalCountries);
		
		// Get an iterator
//		Iterator<String> iterator = LinkedinApplication.setOfGlobalIndustryNames.iterator();

		// Display elements
//		while (iterator.hasNext()) {
//			Logger.vLog("printSet", "Value : " + iterator.next());
//			MethodUtils.getIndustrywiseConnections(iterator.next(), listLinkedinUsers);
//		}
		
		return listLinkedinUsers;
	}// end of parse() method
	
	
	public LatLng parseGeocoderWebserviceResult(String result){
		
		double lat = 0;
		double lng = 0;

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(result);

			lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

			lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			Logger.vLog("latitude", "" + lat);
			Logger.vLog("longitude", "" + lng);

		} catch (JSONException e) {
			Logger.vLog("JSONException", e.toString());
			e.printStackTrace();
		}

		return new LatLng(lat, lng);
		
	}
}
