package com.vsplc.android.social_poc.linkedin_api.webservices;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.vsplc.android.social_poc.linkedin_api.android.volley.AuthFailureError;
import com.vsplc.android.social_poc.linkedin_api.android.volley.Request;
import com.vsplc.android.social_poc.linkedin_api.android.volley.toolbox.JsonObjectRequest;
import com.vsplc.android.social_poc.linkedin_api.interfaces.DownloadObserver;

public abstract class PostWebService extends WebService {

	private JSONObject _Data = null;

	public PostWebService(Context context, DownloadObserver downloadObserver,
			String url, JSONObject data) {

		super(context, downloadObserver);

		_Data = data;

		_Request = new JsonObjectRequest(Request.Method.POST, url, _Data,
				reponseListener, errorListener);

	}

	public PostWebService(Context context, DownloadObserver downloadObserver,
			String url, JSONObject data, final Map<String, String> header) {

		super(context, downloadObserver);

		_Data = data;
		
//		 JSONObject model = (JSONObject) Json.createObjectBuilder().add("share", 
//				 Json.createObjectBuilder().add("comment", "This is comment from mobile application")
//				 .add("visibility", Json.createObjectBuilder().add("code", "anyone"))).build();
		
		// Here we convert Java Object to JSON 
		JSONObject jsonObjParent = new JSONObject();
					
		// Here we convert Java Object to JSON 
		JSONObject jsonObjShare = new JSONObject();
		
		try {
			jsonObjShare.put("comment", "Hello");
			// Here we convert Java Object to JSON 
			JSONObject jsonObjVisibility = new JSONObject();
			jsonObjVisibility.put("code", "anyone");							
			
			jsonObjShare.putOpt("visibility", jsonObjVisibility);
			jsonObjParent.putOpt("share", jsonObjShare);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("JSON : ", "JSON: "+jsonObjParent.toString());
		
		_Request = new JsonObjectRequest(Request.Method.POST, url, jsonObjParent,
				reponseListener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return header;
			}
		};

	}

}
