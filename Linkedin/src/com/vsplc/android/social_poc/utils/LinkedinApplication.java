package com.vsplc.android.social_poc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.res.Configuration;

import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.social_poc.model.LinkedinUser;

/**
 * This application class have ACRA integration which send a app crash report to specified email address.  
 * @Date 13 May, 2014
 * @author VSPL
 */
@ReportsCrashes(
		formKey = "", // will not be used
        mailTo = "swapnil.sonar@vsplc.com",
        // customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },                
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
        
)

public class LinkedinApplication extends Application { 

	public static List<LinkedinUser> listGlobalConnections = new ArrayList<LinkedinUser>();
	public static Set<String> setOfGlobalCountries = new HashSet<String>();
	public static Set<String> setOfGlobalIndustryNames = new HashSet<String>();
	public static Map<String, List<LinkedinUser>> mapCountrywiseConnections = new HashMap<String, List<LinkedinUser>>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		//Log.d("SpyWarnApplication", "onCreate");
		
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
		
	}
		
	@Override
	public void onTerminate() {
		super.onTerminate();
		//Log.d("SpyWarnApplication", "onTerminate");
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
}
