package com.vsplc.android.social_poc.linkedin_api.webservices;

import org.json.JSONObject;

import android.content.Context;

import com.vsplc.android.social_poc.linkedin_api.interfaces.DownloadObserver;

public class AccessTokenWebService extends PostWebService{

	public AccessTokenWebService(Context context,
			DownloadObserver downloadObserver, String url, JSONObject data) {
		super(context, downloadObserver, url, data);
	}

}
