package com.vsplc.android.poc.linkedin.linkedin_api.interfaces;

public interface DownloadObserver {

	void onDownloadingStart();
	
	void onDownloadingComplete(Object data);
	
	void onDownloadFailure(Object errorData);
}
