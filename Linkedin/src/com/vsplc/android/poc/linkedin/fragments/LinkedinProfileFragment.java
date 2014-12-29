package com.vsplc.android.poc.linkedin.fragments;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.vsplc.android.poc.linkedin.BaseActivity;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.logger.Logger;

public class LinkedinProfileFragment extends Fragment{

	@SuppressWarnings("unused")
	private FragmentActivity mFragActivityContext;
	private WebView webView;
	
	private Button btnLeft;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

		mFragActivityContext = getActivity();

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.linkedinprofile_fragment, container, false);
		
		webView = (WebView) view.findViewById(R.id.webview);
		btnLeft = (Button) view.findViewById(R.id.btn_left);		
		
		return view;
	}

	
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Bundle args = getArguments(); 

		if (args != null) { // load web link received through bundle

			String url = args.getString("url");
			Logger.vLog("LinkedinProfileFragment ", "Received URL : "+url);
			webView.loadUrl(url);
		} 

		btnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				((BaseActivity) getActivity()).showHideNevigationDrawer();
			}
		});

		// enable Java Script
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportMultipleWindows(true);
		webView.getSettings().setPluginState(PluginState.ON);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);

		// override the web client to open all links in the same webview
		webView.setWebViewClient(new MyWebViewClient());
	}
	
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

//			if (Uri.parse(url).getHost().equals("https://www.linkedin.com")){
//				
//				// This is my web site, so do not override; let my WebView load the page
//				return false;
//			}
//			// Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//			startActivity(intent);
//			return true;

			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}
	}
}
