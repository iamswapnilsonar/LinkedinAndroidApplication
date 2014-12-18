package com.vsplc.android.poc.linkedin.linkedin_api.utils;

public class Config {

	public static String LINKEDIN_CONSUMER_KEY = "75x3j7pf2lwipf";
	public static String LINKEDIN_CONSUMER_SECRET = "h3fcy3BVowuFsPXs";
	public static String LINKEDIN_SCOPE_PERMISSION = "w_company_admin+r_emailaddress+w_messages+r_network+r_basicprofile+r_fullprofile+rw_groups+r_contactinfo+rw_nus";
	
	public static String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
	public static String OAUTH_CALLBACK_HOST = "callback";
	public static String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	
	public static boolean showConnections = true;
}
