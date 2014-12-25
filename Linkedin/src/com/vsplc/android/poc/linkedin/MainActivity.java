package com.vsplc.android.poc.linkedin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vsplc.android.poc.linkedin.activity.LKConnectionsListAdapter;
import com.vsplc.android.poc.linkedin.linkedin_api.interfaces.Callback;
import com.vsplc.android.poc.linkedin.linkedin_api.interfaces.DownloadObserver;
import com.vsplc.android.poc.linkedin.linkedin_api.model.EasyLinkedIn;
import com.vsplc.android.poc.linkedin.linkedin_api.utils.Config;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.networking.ResponseManager;
import com.vsplc.android.poc.linkedin.utils.ConstantUtils;
import com.vsplc.android.poc.linkedin.utils.DataWrapper;
import com.vsplc.android.poc.linkedin.utils.LinkedinApplication;
import com.vsplc.android.poc.linkedin.utils.MethodUtils;

/**
 * Fetching user's linkedin connections, following companies and user profile info. 
 * @Date 17 Sept, 2014
 * @author VSPLC
 *
 */
public class MainActivity extends Activity implements View.OnClickListener{

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
	
	private ProgressDialog progressDialog;
	
	public boolean isConnectionsRequested = false;
	public boolean isConnectionsWorkCompleted = false;	
	
	private SharedPreferences  mPrefs;
	private Editor prefsEditor;
	 
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

			Logger.vLog("DoingLengthyTask","doInBackground");	
			
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
			
			Logger.vLog("DoingLengthyTask","onPostExecute");	
			
			isConnectionsWorkCompleted = true;
			/*if (progressDialog.isShowing()) {
				progressDialog.dismiss();
				Logger.vLog("onPostExecute : ", "listLinkedinUsers : "+listLinkedinUsers.size());
				Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();	
			}*/
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			progressDialog = ProgressDialog.show(MainActivity.this, "Wait", "Downloading.."); 
		}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}
	
	
	public class AsyncGetAllConnections extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {			
			
			Logger.vLog("AsyncGetAllConnections","doInBackground");			
			
			_EasyLinkedIn.getConnections(mContext, getUserInfoDownloadObserver, ConstantUtils.USER_INFO_FEILDS);			
			return "Success";
		}

		@Override
		protected void onPostExecute(String result) {
			
			Logger.vLog("AsyncGetAllConnections","onPostExecute");	
			
			if (isConnectionsRequested && isConnectionsWorkCompleted && result.equals("Success")) {
				
				// Open up connection list layout
				progressDialog.dismiss();
				
				Intent intent = new Intent(MainActivity.this, CustomizedListActivity.class);
				DataWrapper dataWrapper = new DataWrapper((ArrayList<LinkedinUser>)LinkedinApplication.listGlobalConnections);
		        intent.putExtra("data", dataWrapper);
		        startActivity(intent);
		        
			}else{
				// NOP				
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}
	
	
    @SuppressLint({ "InlinedApi", "NewApi" })
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        mContext = MainActivity.this;
        
        initUI();
        
        _EasyLinkedIn = EasyLinkedIn.getInstance(this, Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET, 
    			"https://www.linkedin.com", "", "");
        
        listLinkedinUsers = new ArrayList<LinkedinUser>();
        
        if (EasyLinkedIn.hasAccessToken()) {
			// User Access Token
        	Logger.vLog("Access Token", EasyLinkedIn.getAccessToken());
            Logger.vLog("Access Secret", EasyLinkedIn.getAccessSecret());
            btnLogin.setVisibility(View.INVISIBLE);
            
            AsyncGetAllConnections asyncConnections = new AsyncGetAllConnections();
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            	asyncConnections.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
            else
            	asyncConnections.execute((Void[])null);
            
		}else{
			linOptions.setVisibility(View.INVISIBLE);
		}
        
//        lvLKConnections = (ListView) findViewById(R.id.lvLinkedinConnections);
//
        
        
//        new AsyncGetAllConnections().execute();
        
        
        /*// LinkedinApplication.listGlobalConnections.size() >=  LinkedinApplication.iConnectionCount &&
        if (LinkedinApplication.listGlobalConnections.size() > 0) {
        	//NOP
        }else{
        	_EasyLinkedIn.getConnections(mContext, getUserInfoDownloadObserver, USER_INFO_FEILDS);
        }*/
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
        
        mPrefs = getPreferences(MODE_PRIVATE);
        prefsEditor = mPrefs.edit();
	}

	private DownloadObserver getUserInfoDownloadObserver = new DownloadObserver() {

    	@Override
    	public void onDownloadingStart() {}

    	@SuppressLint("NewApi")
		@Override
    	public void onDownloadingComplete(Object data) {
    		Log.v("onDownloadingComplete : ", ""+data.toString());
    		
    		ResponseManager manager = new ResponseManager();
    		
    		try {
    			
    			int connection_count = manager.parseConnectionCount(data);
    			
    			// result jo mil raha h wo compare karo with stored result
    			if (LinkedinApplication.listCityInfo.size() >= connection_count) {
					// NOP
    				isConnectionsWorkCompleted = true;
				}else{
					
					DoingLengthyTask asyncLengthyTask = new DoingLengthyTask();
					
					if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
						asyncLengthyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object)data);
					else
						asyncLengthyTask.execute((Object)data);
					
//					new DoingLengthyTask().execute(data);
				}
    			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
    		
    	}

    	@Override
    	public void onDownloadFailure(Object errorData) {}
    };
    
	private DownloadObserver getUserDetailsDownloadObserver = new DownloadObserver() {

    	@Override
    	public void onDownloadingStart() {}

    	@SuppressLint("NewApi")
		@Override
    	public void onDownloadingComplete(Object data) {
    		Log.v("onDownloadingComplete : ", ""+data.toString());
    		
    		ResponseManager manager = new ResponseManager();
    		
    		try {
				LinkedinUser user = manager.parseUserResponse(data);
				Logger.vLog("getUserDetailsDownloadObserver", user.toString());
				
				saveObject(user);
				
				LinkedinUser linkedinUser = getObject();
				Logger.vLog("getUserDetailsDownloadObserver", linkedinUser.toString());
				
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}    		
    		
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

		        @SuppressLint("NewApi")
				@Override
		        public void onSucess(Object data) {
		        	
		        	Logger.vLog("onSucess : ", ""+data.toString());
		        	btnLogin.setVisibility(View.INVISIBLE);
		        	linOptions.setVisibility(View.VISIBLE);
		        	
		        	AsyncGetAllConnections asyncConnections = new AsyncGetAllConnections();
		            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
		            	asyncConnections.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
		            else
		            	asyncConnections.execute((Void[])null);
		        			        	
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
			
			isConnectionsRequested = true ;
			
			if (isConnectionsWorkCompleted) {
				
				progressDialog.dismiss();
				
				Intent intent = new Intent(MainActivity.this, CustomizedListActivity.class);
				DataWrapper dataWrapper = new DataWrapper((ArrayList<LinkedinUser>)LinkedinApplication.listGlobalConnections);
		        intent.putExtra("data", dataWrapper);
		        startActivity(intent);
				
			}else{
				// Make webservice call to fetch all the connnction data
		        progressDialog.setMessage("Fetching Connections..");
		        progressDialog.show();
			}
			
			
			/*if (LinkedinApplication.listGlobalConnections.size() >=  LinkedinApplication.iConnectionCount) {
				
				
								
			}else{
				
			}*/
			                      
           break;

		case R.id.btnGetUserDetails:
			// Get user info
			_EasyLinkedIn.getUserInfo(MainActivity.this, getUserDetailsDownloadObserver, ConstantUtils.USER_INFO_FEILDS);
			break;
			
		case R.id.btnGetLocationCoordinates:
			
			MethodUtils.getLatLngFromGivenAddressGeoCoder(MainActivity.this, "");
			
//			_EasyLinkedIn.getLatAndLongFromLocation(MainActivity.this, "Greater Philadelphia,United States", getLocationInfoDownloadObserver);
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
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		isConnectionsRequested = false;
		isConnectionsWorkCompleted = false;
	}
	
    public LinkedinUser getObject(){
    	Gson gson = new Gson();
        String json = mPrefs.getString("LinkedinUser", "");
        LinkedinUser user = gson.fromJson(json, LinkedinUser.class);
        return user;
    }
    
    public void saveObject(LinkedinUser object){
    	 Gson gson = new Gson();
         String json = gson.toJson(object);
         prefsEditor.putString("LinkedinUser", json);
         prefsEditor.commit();
         
//         Toast.makeText(mContext, "Object Saved..", Toast.LENGTH_SHORT).show();
         //Toast.makeText(getApplicationContext(), "Object Stored", 1000).show();
    }
}
