package com.vsplc.android.poc.linkedin.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.EnumSet;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.activity.LinkedinDialog.OnVerifyListener;
import com.vsplc.android.poc.linkedin.linkedin_api.utils.Config;

/**
 * Linkedin Sample Activity
 * @Date 15 Sept, 2014
 * @author VSPLC
 */
public class LinkedInSampleActivity extends Activity {
	Button login;
	Button share;
	EditText et;
	TextView name;
	ImageView photo;
	public static final String OAUTH_CALLBACK_HOST = "litestcalback";

	final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory
            .getInstance().createLinkedInOAuthService(
                    Config.LINKEDIN_CONSUMER_KEY,Config.LINKEDIN_CONSUMER_SECRET, Config.LINKEDIN_SCOPE_PERMISSION);
	
	final LinkedInApiClientFactory factory = LinkedInApiClientFactory
			.newInstance(Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET);
	
	LinkedInRequestToken liToken;
	LinkedInApiClient client;
	LinkedInAccessToken accessToken = null;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity);
		
		if( Build.VERSION.SDK_INT >= 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy); 
		}
		
//		share = (Button) findViewById(R.id.share);
//		name = (TextView) findViewById(R.id.name);
//		et = (EditText) findViewById(R.id.et_share);
//		login = (Button) findViewById(R.id.login);
//		photo = (ImageView) findViewById(R.id.photo);

		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				linkedInLogin();
			}
		});
		
		// share on linkedin
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (Config.showConnections) {
					
					String share = et.getText().toString();
					
					if (null != share && !share.equalsIgnoreCase("")) {
						
						OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET);
					    consumer.setTokenWithSecret(accessToken.getToken(), accessToken.getTokenSecret());
						DefaultHttpClient httpclient = new DefaultHttpClient();
						HttpPost post = new HttpPost("http://api.linkedin.com/v1/people/~/connections");
			
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
						
//						post.setHeader("content-type", "text/XML");
						@SuppressWarnings("unused")
						String myEntity = "<share><comment>"+ share +"</comment><visibility><code>anyone</code></visibility></share>";
						
						try {
							
//							post.setEntity(new StringEntity(myEntity));
							org.apache.http.HttpResponse response = httpclient.execute(post);
							Toast.makeText(LinkedInSampleActivity.this, "Shared sucessfully", Toast.LENGTH_SHORT).show();
							
							Log.i("Get Connections : ", ""+response);
							
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
					}else {
						Toast.makeText(LinkedInSampleActivity.this, "Please enter the text to share", Toast.LENGTH_SHORT).show();
					}
					
				}else{
					
					String share = et.getText().toString();
					
					if (null != share && !share.equalsIgnoreCase("")) {
						
						OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET);
					    consumer.setTokenWithSecret(accessToken.getToken(), accessToken.getTokenSecret());
						DefaultHttpClient httpclient = new DefaultHttpClient();
						HttpPost post = new HttpPost("https://api.linkedin.com/v1/people/~/shares");
			
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
						String myEntity = "<share><comment>"+ share +"</comment><visibility><code>anyone</code></visibility></share>";
						
						try {
							
							post.setEntity(new StringEntity(myEntity));
							@SuppressWarnings("unused")
							org.apache.http.HttpResponse response = httpclient.execute(post);
							Toast.makeText(LinkedInSampleActivity.this, "Shared sucessfully", Toast.LENGTH_SHORT).show();
							
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
					}else {
						Toast.makeText(LinkedInSampleActivity.this, "Please enter the text to share", Toast.LENGTH_SHORT).show();
					}
					
					/*String share = et.getText().toString();
					if (null != share && !share.equalsIgnoreCase("")) {
						client = factory.createLinkedInApiClient(accessToken);
						client.postNetworkUpdate(share);
						et.setText("");
						Toast.makeText(LinkedInSampleActivity.this,
								"Shared sucessfully", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(LinkedInSampleActivity.this,
								"Please enter the text to share",
								Toast.LENGTH_SHORT).show();
					}*/
					
				}
				
			}
		});
	}

	public Bitmap downloadImageFromServer(String imageUrl){

		String urldisplay = imageUrl;        
		Bitmap downloaded_bitmap = null;

		try {
			InputStream in = new java.net.URL(urldisplay).openStream();
			downloaded_bitmap = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return downloaded_bitmap;

	}
	
	private void linkedInLogin() {
		ProgressDialog progressDialog = new ProgressDialog(
				LinkedInSampleActivity.this);

		LinkedinDialog d = new LinkedinDialog(LinkedInSampleActivity.this,
				progressDialog);
		d.show();

		// set call back listener to get oauth_verifier value
		d.setVerifierListener(new OnVerifyListener() {
			@Override
			public void onVerify(String verifier) {
				try {
					Log.i("LinkedinSample", "verifier: " + verifier);

					accessToken = LinkedinDialog.oAuthService.getOAuthAccessToken(LinkedinDialog.liToken, verifier);
					
					LinkedinDialog.factory.createLinkedInApiClient(accessToken);
					client = factory.createLinkedInApiClient(accessToken);
					
					// client.postNetworkUpdate("Testing by Mukesh!!! LinkedIn wall post from Android app");
					Log.i("LinkedinSample", "ln_access_token: " + accessToken.getToken());
					
					Log.i("LinkedinSample", "ln_access_token: " + accessToken.getTokenSecret());
					
					Person profile = client.getProfileForCurrentUser(EnumSet.of(
			                ProfileField.ID, ProfileField.FIRST_NAME,
			                ProfileField.LAST_NAME, ProfileField.HEADLINE,
			                ProfileField.INDUSTRY, ProfileField.PICTURE_URL,
			                ProfileField.DATE_OF_BIRTH, ProfileField.LOCATION_NAME,
			                ProfileField.MAIN_ADDRESS, ProfileField.LOCATION_COUNTRY, ProfileField.CONNECTIONS, ProfileField.NUM_CONNECTIONS, ProfileField.NUM_CONNECTIONS_CAPPED));
					
					// Person person = client.getProfileForCurrentUser();
					name.setText("Welcome " + profile.getFirstName() + " "+ profile.getLastName());
					
//					Log.v("LinkedInSampleActivity : ", "FirstName : "+person.getFirstName()+"\n LastName : "+person.getLastName());
					
//					ImAccounts accounts = person.getImAccounts();
//					accounts.getImAccountList();
					
//					Log.i("LinkedinSample", " PictureUrl : " + profile.getPictureUrl());
//					
//					String image_url = profile.getPictureUrl();
//					Bitmap bitmap = downloadImageFromServer(image_url);
//					photo.setImageBitmap(bitmap);
//					
//					Connections connections = profile.getConnections();
//					
//					List<Person> listPersons = connections.getPersonList();
//					Log.i("LinkedinSample", "Number of Connections : "+listPersons.size());
//					
//					Log.i("LinkedinSample", " ####### CONECTIONS ########");
//					
//					for (int i = 0; i < listPersons.size(); i++) {
//						Person person = listPersons.get(i);
//						Log.i("Person Name : ", person.getFirstName() +" "+person.getLastName());
//					}
//					
//					Log.i("LinkedinSample", " PictureUrl : " + profile.getConnections());
					
					name.setVisibility(0);
					login.setVisibility(4);
					share.setVisibility(0);
					et.setVisibility(0);

				} catch (Exception e) {
					Log.i("LinkedinSample", "error to get verifier");
					e.printStackTrace();
				}
			}
		});

		// set progress dialog
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
}

