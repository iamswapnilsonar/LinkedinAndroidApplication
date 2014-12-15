package com.vsplc.android.social_poc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.social_poc.activity.LKConnectionsListAdapter;
import com.vsplc.android.social_poc.linkedin_api.interfaces.Callback;
import com.vsplc.android.social_poc.linkedin_api.interfaces.DownloadObserver;
import com.vsplc.android.social_poc.linkedin_api.model.EasyLinkedIn;
import com.vsplc.android.social_poc.linkedin_api.utils.Config;
import com.vsplc.android.social_poc.logger.Logger;
import com.vsplc.android.social_poc.model.LinkedinUser;
import com.vsplc.android.social_poc.networking.ResponseManager;
import com.vsplc.android.social_poc.utils.DataWrapper;
import com.vsplc.android.social_poc.utils.LinkedinApplication;

/**
 * Fetching user's linkedin connections, following companies and user profile info. 
 * @Date 17 Sept, 2014
 * @author VSPLC
 *
 */
public class MainActivity extends Activity implements View.OnClickListener{

	@SuppressWarnings("unused")
	private Context mContext;
	
	private EasyLinkedIn _EasyLinkedIn;
	private Button btnGetLinkedinConnections, btnSharePost, btnLogin, btnGetUserDetails;
	private Button btnGetLocationCoordinates;
	private LinearLayout linOptions;
	
//	private ListView lvLKConnections;
	
	ArrayList<LinkedinUser> listLinkedinUsers;
	LKConnectionsListAdapter adapter;
	
	@SuppressWarnings("unused")
	private LinkedinUser user;	
	
	// Linked-in User Profile information
	public static final String USER_INFO_FEILDS = "id,first-name,last-name,headline,location:(name,country:(code)),industry,picture-url,public-profile-url";
	
	// Linked-in Company Profile information
	public static final String COMPANY_INFO_FEILDS = "id,name";
	
	private ProgressDialog progressDialog;
	
	private class LongOperationForSharePost extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			HttpResponse response = null;
			
			String prepared_url = "https://api.linkedin.com/v1/people/~/shares?format=xml&oauth2_access_token="
						+ EasyLinkedIn.getAccessToken();
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(prepared_url);

			post.setHeader("content-type", "text/XML");
			
			StringBuilder builder = new StringBuilder();
			
			builder.append("<share>");
			builder.append("<comment>This is From Android Linkedin Application.</comment>");
			
			builder.append("<content>");
			builder.append("<title>Android 5.0 Lollipop</title>");
			builder.append("<submitted-url>http://developer.android.com/index.html</submitted-url>");
			builder.append("<submitted-image-url>https://www.google.com/mobile/android/images/android.jpg</submitted-image-url>");
			builder.append("</content>");
			
			builder.append("<visibility>");
			builder.append("<code>anyone</code>");
			builder.append("</visibility>");
			
			builder.append("</share>");
			
			String myEntity = builder.toString();
			
			try {

				StringEntity str_entity = new StringEntity(myEntity);
				post.setEntity(str_entity);
				
				response = httpclient.execute(post);
				Log.i("Share Post : ", ""+response.toString());

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
		
			return "Post Successfully Shared..!!";
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();			
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}

	private class DoingLengthyTask extends AsyncTask<Object, Void, String> {

		@Override
		protected String doInBackground(Object... params) {

			Object data = params[0];

			try {

				List<LinkedinUser> temp = new ResponseManager().parse(data);
				listLinkedinUsers.clear();
				listLinkedinUsers.addAll(temp);		
				
				// save the connections information globally. 
				LinkedinApplication.listGlobalConnections = listLinkedinUsers;
				
				Log.v("MainActivity : ", "Total Connection Size : "+listLinkedinUsers.size());

//				LinkedinApplication.mapCountrywiseConnections = MethodUtils.getCountrywiseConnections(temp);
				// adapter.notifyDataSetChanged();
//				Toast.makeText(MainActivity.this, "Size : "+listLinkedinUsers.size(), Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.v("MainActivity : ", "Exception : "+e.toString());
			}

			return "All Connection are fetched..!!";
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();	
			
			Intent intent = new Intent(MainActivity.this, CustomizedListActivity.class);
//	        intent.putParcelableArrayListExtra("list", listLinkedinUsers);
	        intent.putExtra("data", new DataWrapper(listLinkedinUsers));
	        startActivity(intent);
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			progressDialog = ProgressDialog.show(MainActivity.this, "Wait", "Downloading.."); 
		}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        mContext = MainActivity.this;
        
        initUI();
        
        _EasyLinkedIn = EasyLinkedIn.getInstance(this, Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET, 
    			"https://www.linkedin.com", "", "");
        
        if (EasyLinkedIn.hasAccessToken()) {
			// User Access Token
        	Logger.vLog("Access Token", EasyLinkedIn.getAccessToken());
            Logger.vLog("Access Secret", EasyLinkedIn.getAccessSecret());
            btnLogin.setVisibility(View.INVISIBLE);
            
		}else{
			linOptions.setVisibility(View.INVISIBLE);			
		}
        
//        lvLKConnections = (ListView) findViewById(R.id.lvLinkedinConnections);
//
        listLinkedinUsers = new ArrayList<LinkedinUser>();
//        
//        user = new LinkedinUser();
//        user.setStrLinkedinUserFirstName("Swapnil");
//        user.setStrLinkedinUserCountryLocation("India");
//        user.setStrLinkedinUserID("SMS-123");
//        user.setStrLinkedinUserLastName("Sonar");
//        user.setStrLinkedinUserLocationArea("Pune");
//        user.setStrLinkedinUserWorkingDomain("Compuetr");
//        
//        listLinkedinUsers.add(user);        
//       
//        adapter = new LKConnectionsListAdapter(MainActivity.this, listLinkedinUsers);
//        lvLKConnections.setAdapter(adapter);
        
    }

    private void initUI() {
		// TODO Auto-generated method stub
    	
    	btnLogin = (Button) findViewById(R.id.btnLogin);
    	btnGetLinkedinConnections = (Button) findViewById(R.id.btnGetUserCoonections);
        btnSharePost = (Button) findViewById(R.id.btnPost);
        btnGetUserDetails = (Button) findViewById(R.id.btnGetUserDetails);
        btnGetLocationCoordinates = (Button) findViewById(R.id.btnGetLocationCoordinates);
        
        linOptions = (LinearLayout) findViewById(R.id.lin_options);
        
        btnLogin.setOnClickListener(this);
        btnGetLinkedinConnections.setOnClickListener(this);
        btnSharePost.setOnClickListener(this);
        btnGetUserDetails.setOnClickListener(this);
        btnGetLocationCoordinates.setOnClickListener(this);
        
        // showing progress dialog while performing heavy tasks..
        progressDialog = new ProgressDialog(MainActivity.this);
	}

	private DownloadObserver getUserInfoDownloadObserver = new DownloadObserver() {

    	@Override
    	public void onDownloadingStart() {}

    	@Override
    	public void onDownloadingComplete(Object data) {
    		Log.v("onDownloadingComplete : ", ""+data.toString());
    		new DoingLengthyTask().execute(data);
    	}

    	@Override
    	public void onDownloadFailure(Object errorData) {}
    };
    
    private DownloadObserver getLocationInfoDownloadObserver = new DownloadObserver() {

    	@Override
    	public void onDownloadingStart() {}

    	@Override
    	public void onDownloadingComplete(Object data) {
    		Log.v("onDownloadingComplete : ", ""+data.toString());
    	}

    	@Override
    	public void onDownloadFailure(Object errorData) {}
    };
    
    public void updateReceiptsList(BaseAdapter adapter, List<LinkedinUser> newListLinkedinUsers) {
        listLinkedinUsers.clear();
        listLinkedinUsers.addAll(newListLinkedinUsers);
        adapter.notifyDataSetChanged();
    }

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		int key = view.getId();
		
		switch (key) {
		
		case R.id.btnLogin:
			
		    _EasyLinkedIn.authorize(MainActivity.this, new Callback() {

		        @Override
		        public void onSucess(Object data) {
		        	
		        	Logger.vLog("onSucess : ", ""+data.toString());
		        	btnLogin.setVisibility(View.INVISIBLE);
		        	linOptions.setVisibility(View.VISIBLE);
		        	
		        	// String following_companies_url = "https://api.linkedin.com/v1/people/~/following/companies:(id,name)?format=json&oauth2_access_token="+EasyLinkedIn.getAccessToken();			            
//		            _EasyLinkedIn.getFollowingCompanies(MainActivity.this, getUserInfoDownloadObserver, companyinfo_feilds);
		            
		        }

		        @Override
		        public void onFailure() {

		        }			        
		        
		    });
			
			
			break;
		
		case R.id.btnGetUserCoonections:
			// Get users connection info
			// String connection_url = "https://api.linkedin.com/v1/people/~/connections";
           _EasyLinkedIn.getConnections(MainActivity.this, getUserInfoDownloadObserver, USER_INFO_FEILDS);          
           progressDialog.setMessage("Fetching all connections..");
           progressDialog.show();
           break;

		case R.id.btnGetUserDetails:
			// Get user info
			_EasyLinkedIn.getUserInfo(MainActivity.this, getUserInfoDownloadObserver, USER_INFO_FEILDS);
			break;
			
		case R.id.btnGetLocationCoordinates:
			_EasyLinkedIn.getLatAndLongFromLocation(MainActivity.this, "Greater Philadelphia,United States", getLocationInfoDownloadObserver);
//			MethodUtils.getIndustrywiseConnections("Computer Software", LinkedinApplication.mapCountrywiseConnections.get("in"));
			
			
			/*String location = "Greater Philadelphia,United State";
			Geocoder gc = new Geocoder(mContext);
			List<Address> addresses;
			
			try {
				addresses = gc.getFromLocationName(location, 5);
				
				List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
				for(Address a : addresses){
					if(a.hasLatitude() && a.hasLongitude()){
						Logger.vLog("btnGetLocationCoordinates : ", "Latitude : "+a.getLatitude()+"Longitude : "+a.getLongitude());
						ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
					}  
				}  
				
				Logger.vLog("List : ", ""+ll.size());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // get the found Address Objects*/

			

			
				
						
			break;
			
		case R.id.btnPost:
			new LongOperationForSharePost().execute("");
			break;
		}
		
	}
    
}
