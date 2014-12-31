package com.vsplc.android.poc.linkedin.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.vsplc.android.poc.linkedin.BaseActivity;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.adapter.IndustryListAdapter;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.utils.ConstantUtils;
import com.vsplc.android.poc.linkedin.utils.DataWrapper;
import com.vsplc.android.poc.linkedin.utils.LinkedinApplication;
import com.vsplc.android.poc.linkedin.utils.MethodUtils;

public class IndustriesFragment extends Fragment implements OnClickListener{

	private FragmentActivity mFragActivityContext;
	private ListView list;
	
	private Button btnLeft;
	private IndustryListAdapter adapter;
	
	private List<String> industryList;
	private ProgressDialog progressDialog;
	
	private List<LinkedinUser> mConnections = new ArrayList<LinkedinUser>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

		mFragActivityContext = getActivity();

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.industries_fragment, container, false);
		
		btnLeft = (Button) view.findViewById(R.id.btn_left);		
		list = (ListView) view.findViewById(R.id.list);
		
		return view;
	}

	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		industryList = new ArrayList<String>();
		
		// showing progress dialog while performing heavy tasks..
		progressDialog = new ProgressDialog(mFragActivityContext);
		progressDialog.setCancelable(false);
		
		Logger.vLog("IndustriesFragment", "Global Length : "+LinkedinApplication.setOfGlobalIndustryNames.size());		
		for (String industry : LinkedinApplication.setOfGlobalIndustryNames) {
			industryList.add(industry);
		}		
		Logger.vLog("IndustriesFragment", "Local Length : "+industryList.size());
		
		adapter = new IndustryListAdapter(mFragActivityContext, industryList);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(mFragActivityContext, industryList.get(position), Toast.LENGTH_SHORT).show();
				
				String industry = industryList.get(position);
				Logger.vLog("industriesList : onItemClick",""+industry);
				
				new GetIndustryWiseConnections().execute(industry);				
			}
			
		});
		
		btnLeft.setOnClickListener(this);
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		int key = view.getId();
		
		switch (key) {
		case R.id.btn_left:
			((BaseActivity) getActivity()).showHideNevigationDrawer();
			break;

		default:
			break;
		}
		
	}
	
	private class GetIndustryWiseConnections extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			if (!progressDialog.isShowing()) {
				progressDialog.setMessage("Get Industry wise Connections..");
				progressDialog.show();				
			}

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String industry = params[0];
//			String country = params[1];
						
			ArrayList<LinkedinUser> temp = MethodUtils.getIndustrywiseConnections(industry, LinkedinApplication.listGlobalConnections); 
			mConnections.clear();
			mConnections.addAll(temp);
			
			Logger.vLog("GetIndustryWiseConnections : doInBackground", "Size of mConnections : "+mConnections.size());
			
			return "Success";
		}

		@Override
		protected void onPostExecute(String result) {

			if (progressDialog.isShowing() && result.equals("Success")) {
				
				progressDialog.dismiss();

				FragmentTransaction transaction = mFragActivityContext.getSupportFragmentManager().beginTransaction();
				
				// Create fragment and give it an arguments if any
				ConnectionFragment targetFragment = (ConnectionFragment) Fragment.instantiate(mFragActivityContext, ConstantUtils.CONNECTION_FRAGMENT);
				
				Bundle bundle = new Bundle();
				
				DataWrapper dataWrapper = new DataWrapper((ArrayList<LinkedinUser>)mConnections);
				bundle.putSerializable("connection_list", dataWrapper);
				
				targetFragment.setArguments(bundle);
				
				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack so the user can navigate back
				transaction.replace(R.id.fragment_container, targetFragment, "connections");

				transaction.addToBackStack(null);		 
				transaction.commit();
				
			}
		}

	}
}
