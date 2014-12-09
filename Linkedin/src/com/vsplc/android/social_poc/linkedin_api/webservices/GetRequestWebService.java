package com.vsplc.android.social_poc.linkedin_api.webservices;

import java.util.Map;

import android.content.Context;

import com.vsplc.android.social_poc.linkedin_api.interfaces.DownloadObserver;

public class GetRequestWebService extends GetWebService{

	public GetRequestWebService(Context context,
			DownloadObserver downloadObserver, String url) {
		super(context, downloadObserver, url);
	
	}

	public GetRequestWebService(Context context,
			DownloadObserver downloadObserver, String url, Map<String, String> header) {
		super(context, downloadObserver, url, header);
	
	}
	
}
