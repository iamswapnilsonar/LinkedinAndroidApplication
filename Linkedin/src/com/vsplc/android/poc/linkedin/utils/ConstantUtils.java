package com.vsplc.android.poc.linkedin.utils;

public class ConstantUtils {

	// Linked-in Connection Profile information
	public static final String USER_INFO_FEILDS = "id,first-name,last-name,headline,location:(name,country:(code)),industry,picture-url,public-profile-url";
	
	// Linked-in User Profile information
	public static final String SIGNED_USER_INFO_FEILDS = "id,first-name,last-name,headline,location:(name,country:(code)),industry,picture-url,public-profile-url,num-connections,skills,languages,summary";
	
	// Linked-in Company Profile information
	public static final String COMPANY_INFO_FEILDS = "id,name";
	
	public static final String LOGIN_FRAGMENT = "com.vsplc.android.poc.linkedin.fragments.LoginFragment";
	public static final String PROFILE_FRAGMENT = "com.vsplc.android.poc.linkedin.fragments.ProfileFragment";	
	public static final String CONNECTION_FRAGMENT = "com.vsplc.android.poc.linkedin.fragments.ConnectionFragment";
	public static final String GOOGLE_MAP_FRAGMENT = "com.vsplc.android.poc.linkedin.fragments.GoogleMapFragment";
	
}
