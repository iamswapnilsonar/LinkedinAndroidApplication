package com.vsplc.android.poc.linkedin.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.vsplc.android.poc.linkedin.utils.LinkedinApplication;

public class IndustriesFragment extends Fragment implements OnClickListener{

	private FragmentActivity mFragActivityContext;
	private ListView list;
	
	private Button btnLeft;
	private IndustryListAdapter adapter;
	
	private List<String> industryList;
	
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
}
