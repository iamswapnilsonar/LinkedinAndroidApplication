package com.vsplc.android.social_poc.linkedin_api.webservices;

import org.json.JSONObject;

import android.content.Context;

import com.vsplc.android.social_poc.linkedin_api.android.volley.RequestQueue;
import com.vsplc.android.social_poc.linkedin_api.android.volley.Response;
import com.vsplc.android.social_poc.linkedin_api.android.volley.VolleyError;
import com.vsplc.android.social_poc.linkedin_api.android.volley.Response.ErrorListener;
import com.vsplc.android.social_poc.linkedin_api.android.volley.Response.Listener;
import com.vsplc.android.social_poc.linkedin_api.android.volley.toolbox.JsonObjectRequest;
import com.vsplc.android.social_poc.linkedin_api.android.volley.toolbox.Volley;
import com.vsplc.android.social_poc.linkedin_api.interfaces.DownloadObserver;
import com.vsplc.android.social_poc.linkedin_api.interfaces.Executor;

public abstract class WebService implements Executor {

	protected RequestQueue _RequestQueue = null;

	protected DownloadObserver _DownloadObserver = null;

	protected JsonObjectRequest _Request = null;


	public WebService(Context context, DownloadObserver _DownloadObserver) {

		this._RequestQueue = Volley.newRequestQueue(context);
		this._DownloadObserver = _DownloadObserver;
	}

	protected Response.Listener<JSONObject> reponseListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject response) {

			_DownloadObserver.onDownloadingComplete(response);
		}
	};

	protected Response.ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {

			_DownloadObserver.onDownloadFailure(error);

		}

	};

	@Override
	public void execute() {

		_RequestQueue.add(_Request);
		_DownloadObserver.onDownloadingStart();

	}

}
