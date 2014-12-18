package com.vsplc.android.poc.linkedin.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.linkedin_api.utils.Config;
import com.vsplc.android.poc.linkedin.logger.Logger;

public class LK_SharePostActivity extends Activity {

	private Button btnSharePost;
	private Button btnGetUserDetails;

	private Context mContext;

	public LinkedInApiClientFactory factory;
	public LinkedInOAuthService oAuthService;
	public LinkedInRequestToken liToken;

	public LinkedInApiClient linkedInApiClient;

	private class LongOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			HttpResponse response = null;
			
			OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
					Config.LINKEDIN_CONSUMER_KEY,
					Config.LINKEDIN_CONSUMER_SECRET);
			
			consumer.setTokenWithSecret(liToken.getToken(), liToken.getTokenSecret());

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"https://api.linkedin.com/v1/people/~");

			try {
				consumer.sign(post);
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // here need the consumer for sign in for post the share

			try {

				response = httpclient.execute(post);
//				Toast.makeText(mContext, "User Details get sucessfully", Toast.LENGTH_SHORT).show();
				Log.i("Get Connections : ", "" + response);

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return response.toString();
		
		}

		@Override
		protected void onPostExecute(String result) {
			
			Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}
	
	private class LongOperationForSharePost extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			HttpResponse response = null;
			
			OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
					Config.LINKEDIN_CONSUMER_KEY,
					Config.LINKEDIN_CONSUMER_SECRET);
			consumer.setTokenWithSecret(liToken.getToken(),
					liToken.getTokenSecret());

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"https://api.linkedin.com/v1/people/~/connections");

			try {
				consumer.sign(post);
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // here need the consumer for sign in for post the share

			post.setHeader("content-type", "text/XML");
			String myEntity = "<share><comment>"
					+ params[0]
					+ "</comment><visibility><code>anyone</code></visibility></share>";
							
			try {

				StringEntity str_entity = new StringEntity(myEntity);
				post.setEntity(str_entity);
				
				response = httpclient.execute(post);
//				Toast.makeText(mContext, "Shared sucessfully", Toast.LENGTH_SHORT).show();

				Log.i("Share Post : ", "" + response);

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return response.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			
			Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		mContext = LK_SharePostActivity.this;

		setContentView(R.layout.activity_main);

		oAuthService = LinkedInOAuthServiceFactory.getInstance()
				.createLinkedInOAuthService(Config.LINKEDIN_CONSUMER_KEY,
						Config.LINKEDIN_CONSUMER_SECRET);

		factory = LinkedInApiClientFactory.newInstance(
				Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET);

		liToken = oAuthService.getOAuthRequestToken(Config.OAUTH_CALLBACK_URL);

		Logger.vLog("LINKEDIN_CONSUMER_KEY", "" + Config.LINKEDIN_CONSUMER_KEY);
		Logger.vLog("LINKEDIN_CONSUMER_SECRET", ""
				+ Config.LINKEDIN_CONSUMER_SECRET);

		Logger.vLog("getToken", "" + liToken.getToken());
		Logger.vLog("getTokenSecret", "" + liToken.getTokenSecret());
		Logger.vLog("getExpirationTime", "" + liToken.getExpirationTime());

		// linkedInApiClient = factory.createLinkedInApiClient(accessToken)

		btnSharePost = (Button) findViewById(R.id.btnPost);
		btnGetUserDetails = (Button) findViewById(R.id.btnGetUserDetails);
		
		btnSharePost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				String share = "Linkedin Share Message";

				if (null != share && !share.equalsIgnoreCase("")) {
					new LongOperationForSharePost().execute(share);
				} else {
					Toast.makeText(mContext, "Please enter the text to share",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		btnGetUserDetails.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				new LongOperation().execute("");
			}
		});

	}

}
