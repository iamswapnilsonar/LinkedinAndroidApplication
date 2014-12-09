package com.vsplc.android.social_poc.linkedin_api.interfaces;

public interface DownloadObserver {

	void onDownloadingStart();
	
	void onDownloadingComplete(Object data);
	
	void onDownloadFailure(Object errorData);
}
