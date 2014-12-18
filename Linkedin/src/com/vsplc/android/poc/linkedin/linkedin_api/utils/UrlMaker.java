package com.vsplc.android.poc.linkedin.linkedin_api.utils;

import com.vsplc.android.poc.linkedin.linkedin_api.model.EasyLinkedIn;

public class UrlMaker {

	private static UrlMaker _UrlMakerReference = null;

	private static final String SERVER_NAME = "https://api.linkedin.com/v1/";

	public static final int USER_INFO = 100;
	public static final int GET_CONNECTIONS = 101;
	public static final int GET_FOLLOWING_COMPANIES = 102;
	public static final int SHARE_POST_ON_LINKEDIN = 103;

	private UrlMaker() {

	}

	public static UrlMaker getInstance() {

		if (_UrlMakerReference == null)
			_UrlMakerReference = new UrlMaker();
		return _UrlMakerReference;
	}

	public String getUrl(int code, String fields) {

		switch (code) {
		case USER_INFO:
			return getUserInfoUrl(fields);

		case GET_CONNECTIONS:
			return getConnections(fields);

		case GET_FOLLOWING_COMPANIES:
			return getFollowingCompanies(fields);
			
		default:
			return null;
		}
	}


	private String getUserInfoUrl(String fields) {

		String fieldsContainer = getFieldContainer(fields);
		return SERVER_NAME + "people/~" + fieldsContainer
				+ "?format=json&oauth2_access_token="
				+ EasyLinkedIn.getAccessToken();

	}

	private String getConnections(String fields) {

		String fieldsContainer = getFieldContainer(fields);
		return SERVER_NAME+"people/~/connections"+ fieldsContainer 
				+ "?format=json&oauth2_access_token="
				+ EasyLinkedIn.getAccessToken();
	
	}
	
	private String getFollowingCompanies(String fields) {

		String fieldsContainer = getFieldContainer(fields);
		return SERVER_NAME+"people/~/following/companies"+ fieldsContainer 
				+ "?format=json&oauth2_access_token="
				+ EasyLinkedIn.getAccessToken();
	
	}
	
	private String getFieldContainer(String fields) {
		String fieldsContainer = null;

		if (fields == null || fields.trim().equals(""))
			fieldsContainer = "";
		else
			fieldsContainer = ":(" + fields + ")";
		return fieldsContainer;
	}
}
