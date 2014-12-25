package com.vsplc.android.poc.linkedin;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vsplc.android.poc.linkedin.adapter.NavDrawerListAdapter;
import com.vsplc.android.poc.linkedin.fragments.ProfileFragment;
import com.vsplc.android.poc.linkedin.linkedin_api.model.EasyLinkedIn;
import com.vsplc.android.poc.linkedin.linkedin_api.utils.Config;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.model.NavDrawerItem;
import com.vsplc.android.poc.linkedin.utils.ConstantUtils;
import com.vsplc.android.poc.linkedin.utils.FontUtils;
import com.vsplc.android.poc.linkedin.utils.MethodUtils;

public class BaseActivity extends FragmentActivity implements View.OnClickListener {
	
	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	
	// Navigation drawer header
	@SuppressWarnings("unused")
	private ImageView mImageProfile;
	private TextView mProfileName, mProfileID;	
	
	private Context mContext;
	
	//Linkedin Auth variables
	@SuppressWarnings("unused")
	private EasyLinkedIn _EasyLinkedIn;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.base_activity);
	
		 mContext = BaseActivity.this;
		 
		 initUI();
		 
		 _EasyLinkedIn = EasyLinkedIn.getInstance(mContext, Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET, 
	    			"https://www.linkedin.com", "", "");
		 
		 FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		 
		 if (EasyLinkedIn.hasAccessToken()) {
			 // User Access Token
			 Logger.vLog("Access Token", EasyLinkedIn.getAccessToken());
			 Logger.vLog("Access Secret", EasyLinkedIn.getAccessSecret());
			 
			 // Create fragment and give it an argument for the selected article
			 ProfileFragment newFragment = (ProfileFragment) Fragment.instantiate(mContext, 
					 				ConstantUtils.PROFILE_FRAGMENT);

			 LinkedinUser linkedinUser = MethodUtils.getObject(mContext);
			 Logger.vLog("BaseActivity", linkedinUser.toString());

			 Bundle bundle = new Bundle();
			 bundle.putSerializable("data", linkedinUser);
			 newFragment.setArguments(bundle);

			 // Replace whatever is in the fragment_container view with this fragment,
			 // and add the transaction to the back stack so the user can navigate back
			 transaction.replace(R.id.fragment_container, newFragment);
			 
			 
		 }else{
			 transaction.replace(R.id.fragment_container, Fragment.instantiate(mContext, ConstantUtils.LOGIN_FRAGMENT));
		 }
		 
		 transaction.addToBackStack(null);		 
		 transaction.commit();
		
	}

	private void initUI() {
		// TODO Auto-generated method stub
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		mImageProfile = (ImageView) findViewById(R.id.iv_profile);
		mProfileName = (TextView) findViewById(R.id.tv_profile_name);
		mProfileID = (TextView) findViewById(R.id.tv_profile_email);
		
		mProfileName.setTypeface(FontUtils.getLatoBlackTypeface(mContext));
		mProfileID.setTypeface(FontUtils.getLatoRegularTypeface(mContext));		
		
		navDrawerItems = new ArrayList<NavDrawerItem>();

		for (int i = 0; i < navMenuTitles.length; i++) {
			// adding nav drawer items to array
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1), false, "22"));
		}
		
		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);
	}

	/**
	 * Slide menu item click listener
	 **/
	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
//			displayView(position);
			Toast.makeText(mContext, navDrawerItems.get(position).getTitle(), Toast.LENGTH_SHORT).show();
		}
	}

	public void showHideNevigationDrawer(){
		
		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		} else {
			mDrawerLayout.openDrawer(Gravity.LEFT); 
		} 
		
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		int key = view.getId();
		
		switch (key) {
			
		default:
			break;
		}
	}
}
