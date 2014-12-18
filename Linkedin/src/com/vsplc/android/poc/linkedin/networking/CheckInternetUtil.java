package com.vsplc.android.poc.linkedin.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternetUtil {
	
	/**
	 * @Date - 19 Sept, 2014
	 * Check whether the Internet is available or not, it may be WiFi, Mobile network..
	 * @param context
	 * @return boolean
	 */
	public static boolean isNetAvailable(Context context){

        boolean isNetAvailable=false;

        if ( context != null ){
        	
            ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if ( mgr != null ){
                boolean mobileNetwork = false;
                boolean wifiNetwork = false;
                boolean wiMaxNetwork = false;

                boolean mobileNetworkConnecetd = false;
                boolean wifiNetworkConnecetd = false;
                boolean wiMaxNetworkConnected = false;

                NetworkInfo mobileInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo wifiInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);               
                NetworkInfo wiMaxInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
               
                if ( mobileInfo != null )
                    mobileNetwork = mobileInfo.isAvailable();                   

                if ( wifiInfo != null )
                    wifiNetwork = wifiInfo.isAvailable();

                if(wiMaxInfo != null)
                    wiMaxNetwork = wiMaxInfo.isAvailable();
                
                
                if(wifiNetwork == true || mobileNetwork == true || wiMaxNetwork == true){
                	mobileNetworkConnecetd = mobileInfo.isConnectedOrConnecting();
                	wifiNetworkConnecetd = wifiInfo.isConnectedOrConnecting();
                	try {
                		wiMaxNetworkConnected = wiMaxInfo.isConnectedOrConnecting();
                	} catch (Exception e) {
                		// TODO: handle exception
                	}

                }
                
                // If any one of connected means returns true..
                isNetAvailable = ( mobileNetworkConnecetd || wifiNetworkConnecetd || wiMaxNetworkConnected );               
            }
        }
        return isNetAvailable;
    }

}
