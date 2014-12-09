package com.vsplc.android.social_poc.linkedin_api.webservices;

import java.util.Map;

import org.json.JSONObject;

import android.content.Context;

import com.vsplc.android.social_poc.linkedin_api.interfaces.DownloadObserver;

public class PostRequestWebService extends PostWebService{

	public PostRequestWebService(Context context,
			DownloadObserver downloadObserver, String url, JSONObject data) {
		super(context, downloadObserver, url, data);
	
	}

	public PostRequestWebService(Context context,
			DownloadObserver downloadObserver, String url, JSONObject data, Map<String, String> header) {
		super(context, downloadObserver, url, data, header);
	
	}
	
}
