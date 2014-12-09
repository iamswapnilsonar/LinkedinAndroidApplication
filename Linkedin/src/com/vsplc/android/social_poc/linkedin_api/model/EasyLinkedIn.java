package com.vsplc.android.social_poc.linkedin_api.model;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vsplc.android.social_poc.linkedin_api.interfaces.Callback;
import com.vsplc.android.social_poc.linkedin_api.interfaces.DownloadObserver;
import com.vsplc.android.social_poc.linkedin_api.utils.UrlMaker;
import com.vsplc.android.social_poc.linkedin_api.webservices.GetRequestWebService;
import com.vsplc.android.social_poc.linkedin_api.webservices.PostRequestWebService;
import com.vsplc.android.social_poc.linkedin_api.webservices.PostWebService;

public class EasyLinkedIn {

	private static EasyLinkedIn _EasyLinkedInReference = null;

	private UrlMaker _UrlMaker = null;

	private static String _ConsumerKey;

	private static String _ConsumerSecretKey;

	private static String _CallBackUrl;

	private static String _Scope;

	private static String _State;

	private static SharedPreferences _SharedPreference = null;
	

	public static final String EASY_LINKED_IN_ACCESS_TOKEN = "easy_linked_in_access_token";
	public static final String EASY_LINKED_IN_TOKEN_SECRET = "easy_linked_in_token_secret";

	private GetRequestWebService _GetWebservice = null;
	
	private PostWebService _PostWebService = null;
	
			
	private EasyLinkedIn(String consumerKey, String consumerSecretKey,
			String callBackUrl, String scope, String state) {

		_ConsumerKey = consumerKey;
		_ConsumerSecretKey = consumerSecretKey;
		_CallBackUrl = callBackUrl;
		_Scope = ((scope == null) ? "" : scope);
		_State = ((state == null || state.trim() == "") ? "STATE" : state);
		_UrlMaker = UrlMaker.getInstance();
	}

	/**
	 * 
	 * @param consumerKey
	 * @param consumerSecretKey
	 * @param callBackUrl
	 * @param scope (optional)
	 * @param state (optional)
	 */
	public static final EasyLinkedIn getInstance(Context context,
			String consumerKey, String consumerSecretKey, String callBackUrl,
			String scope, String state) {

		_SharedPreference = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (_EasyLinkedInReference == null)
			_EasyLinkedInReference = new EasyLinkedIn(consumerKey,
					consumerSecretKey, callBackUrl, scope, state);
		return _EasyLinkedInReference;

	}

	static Callback authCallback;
	
	public void authorize(Context context, Callback callback) {

		authCallback = callback;
		Intent intent = new Intent(context, EasyLinkedInAuthActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param context
	 * @param downloadObserver
	 * @param fields (optional) send null or for field names consult this link
	 */
	public void getUserInfo(Context context, DownloadObserver downloadObserver,
			String fields) {

		String url = _UrlMaker.getUrl(UrlMaker.USER_INFO, fields);
		Log.d("Check", url);
		_GetWebservice = new GetRequestWebService(context,
				downloadObserver, url);
		_GetWebservice.execute();
	}

	/**
	 * 
	 * @param context
	 * @param downloadObserver
	 * @param fields (optional) send null or for field names consult Linkedin doc
	 */
	public void getConnections(Context context, DownloadObserver downloadObserver, String fields) {

		String url = _UrlMaker.getUrl(UrlMaker.GET_CONNECTIONS, fields);
		Log.d("Check", url);
		_GetWebservice = new GetRequestWebService(context,
				downloadObserver, url);
		_GetWebservice.execute();

	}

	/**
	 * 
	 * @param context
	 * @param downloadObserver
	 * @param fields (optional) send null or for field names consult Linkedin doc
	 */
	public void getFollowingCompanies(Context context, DownloadObserver downloadObserver, String fields) {

		String url = _UrlMaker.getUrl(UrlMaker.GET_FOLLOWING_COMPANIES, fields);
		Log.d("Check", url);
		_GetWebservice = new GetRequestWebService(context, downloadObserver, url);
		_GetWebservice.execute();

	}
	
	/**
	 * 
	 * @param context
	 * @param downloadObserver
	 * @param url Linkedin URL 
	 */
	public void createCustom(Context context,
			DownloadObserver downloadObserver, String url) {

		Log.d("Check", url);		
		_GetWebservice = new GetRequestWebService(context, downloadObserver, url);
		_GetWebservice.execute();
		
	}
	
	/**
	 * 
	 * @param context
	 * @param downloadObserver
	 * @param url Linkedin URL 
	 */
	public void sharePostsViaLinkedin(Context context,
			DownloadObserver downloadObserver, String url) {

		String lk_url ="https://api.linkedin.com/v1/people/~/shares";
		Log.d("Check", lk_url);
		
		String textToShare = "Hello, This is from android application.";
		
		Map<String, String> header = new HashMap<String, String>();
		header.put("content-type", "application/json");
//		post.setHeader("content-type", "text/XML");
		
		@SuppressWarnings("unused")
		String myEntity = "<share><comment>"+ textToShare +"</comment><visibility><code>anyone</code></visibility></share>";
		
		Log.d("Check", lk_url);
		
//		public GetWebService(Context context, DownloadObserver downloadObserver,
//				String url, String post, final Map<String, String> header)
		
//		_GetWebservice = new GetRequestWebService(context, downloadObserver, url, header);
//		_GetWebservice.execute();
		
		
		_PostWebService = new PostRequestWebService(context, downloadObserver, lk_url, null, header);
		_PostWebService.execute();
	}

	/**
	 * 
	 * @param context
	 * @param downloadObserver
	 * @param url Linkedin URL 
	 */
	public void getLatAndLongFromLocation(Context context, DownloadObserver downloadObserver) {

		String url = "http://maps.google.com/maps/api/geocode/json?address=Pune,India&sensor=false";			
		
		Log.d("getLatAndLongFromLocation : Check", url);		
		_GetWebservice = new GetRequestWebService(context, downloadObserver, url);
		_GetWebservice.execute();
	}
	
	
	
	static String get_ConsumerKey() {
		return _ConsumerKey;
	}

	static String get_ConsumerSecretKey() {
		return _ConsumerSecretKey;
	}

	static String get_CallBackUrl() {
		return _CallBackUrl;
	}

	static String get_Scope() {
		return _Scope;
	}

	static String get_State() {
		return _State;
	}

	public static SharedPreferences.Editor getSharedPreferenceEditor() {

		return _SharedPreference.edit();
	}

	public static boolean hasAccessToken() {

		return _SharedPreference.contains(EASY_LINKED_IN_ACCESS_TOKEN);
	}

	public static String getAccessToken() {
		return _SharedPreference.getString(EASY_LINKED_IN_ACCESS_TOKEN, null);
	}
	
	public static boolean hasAccessSecret() {

		return _SharedPreference.contains(EASY_LINKED_IN_TOKEN_SECRET);
	}

	public static String getAccessSecret() {
		return _SharedPreference.getString(EASY_LINKED_IN_TOKEN_SECRET, null);
	}
}
