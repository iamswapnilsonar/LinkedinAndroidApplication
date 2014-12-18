package com.vsplc.android.poc.linkedin.linkedin_api.webservices;

import org.json.JSONObject;

import android.content.Context;

import com.vsplc.android.poc.linkedin.linkedin_api.interfaces.DownloadObserver;

public class AccessTokenWebService extends PostWebService{

	public AccessTokenWebService(Context context,
			DownloadObserver downloadObserver, String url, JSONObject data) {
		super(context, downloadObserver, url, data);
	}

}
