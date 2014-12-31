package com.vsplc.android.poc.linkedin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.vsplc.android.poc.linkedin.BaseActivity;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.utils.CircleTransform;
import com.vsplc.android.poc.linkedin.utils.ConstantUtils;
import com.vsplc.android.poc.linkedin.utils.DataWrapper;
import com.vsplc.android.poc.linkedin.utils.FontUtils;
import com.vsplc.android.poc.linkedin.utils.MethodUtils;

public class ProfileFragment extends Fragment implements OnClickListener{
	
	private TextView tvProfileName, tvProfileHeading, tvProfileLocation;
	
	@SuppressWarnings("unused")
	private TextView tvConnectionCount, tvSkills, tvLanguages, tvIndustry, tvProfileURL, tvProfileSummary;
	private ImageView ivProfileImage;
	private LinearLayout linearLayout;
	private Button btnSeeOnMap, btnViewProfile, btnSendMessage;
	private TextView tvSeeOnMap, tvViewProfile, tvSendMessage;
	
	private FragmentActivity mFragActivityContext;
		
	private LinkedinUser user;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mFragActivityContext = getActivity();
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.profile_fragment, container, false);
		
		ivProfileImage = (ImageView) view.findViewById(R.id.iv_profileimage);
		tvProfileName = (TextView) view.findViewById(R.id.tv_profilename);
		tvProfileHeading = (TextView) view.findViewById(R.id.tv_profile_heading);
		tvProfileLocation = (TextView) view.findViewById(R.id.tv_profile_location);
		
		tvConnectionCount = (TextView) view.findViewById(R.id.tv_connection_count);
		tvSkills = (TextView) view.findViewById(R.id.tv_skills);
		tvLanguages = (TextView) view.findViewById(R.id.tv_languages);
		tvIndustry = (TextView) view.findViewById(R.id.tv_industry);
		tvProfileURL = (TextView) view.findViewById(R.id.tv_profile_url);
		tvProfileSummary = (TextView) view.findViewById(R.id.tv_summary);
		
		linearLayout = (LinearLayout) view.findViewById(R.id.lin_options_user_profile);
		
		btnSeeOnMap = (Button) view.findViewById(R.id.btn_seeonmap);
		btnViewProfile = (Button) view.findViewById(R.id.btn_viewprofile);			
		btnSendMessage = (Button) view.findViewById(R.id.btn_sendmessage);
		
		tvSeeOnMap = (TextView) view.findViewById(R.id.tv_seeonmap);
		tvViewProfile = (TextView) view.findViewById(R.id.tv_viewprofile);			
		tvSendMessage = (TextView) view.findViewById(R.id.tv_sendmessage);
		
		btnSeeOnMap.setOnClickListener(this);
		btnViewProfile.setOnClickListener(this);
		btnSendMessage.setOnClickListener(this);		
		
		tvProfileURL.setSelected(true);
				
		Button btnRight = (Button) view.findViewById(R.id.btn_right);
		btnRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button btnLeft = (Button) view.findViewById(R.id.btn_left);
		btnLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				((BaseActivity) getActivity()).showHideNevigationDrawer();
			}
		});
		
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been applied to the fragment at this point so we can safely call the
		// method below that sets the article text.
		
		// set font to all texts on Profile Fragment..
		RelativeLayout layout = ((RelativeLayout)getActivity().findViewById(R.id.rel_profile_fragment));
		overrideFonts(getActivity(), layout);	

		// tvProfileHeading.setSelected(true);
		tvProfileName.setTypeface(FontUtils.getLatoBlackTypeface(getActivity()));
		
		Bundle args = getArguments(); 

		if (args != null) { 
			
			String profileType = args.getString("profile_type");
			
			if (profileType.equals("AppUser")) {
				
				user = (LinkedinUser) args.getSerializable("user");
				Logger.vLog("ProfileFragment", ""+user.toString());

				if (user != null) {

					tvProfileName.setText(user.fname+" "+user.lname);
					tvProfileHeading.setText(user.headline);
					tvProfileLocation.setText(user.location);

					tvIndustry.setText(user.industry);
					tvProfileURL.setText(user.profileurl);	

					Picasso picasso = Picasso.with(mFragActivityContext);
					RequestCreator creator = picasso.load(user.profilepicture);
					creator.resize(80, 80);
					creator.centerCrop();
					creator.transform(new CircleTransform());
					creator.into(ivProfileImage);
					
					linearLayout.setVisibility(View.GONE);
				}
				
			}else{
				
				user = (LinkedinUser) args.getSerializable("user");
				Logger.vLog("ProfileFragment", ""+user.toString());
				
				if (user != null) {

					tvProfileName.setText(user.fname+" "+user.lname);
					tvProfileHeading.setText(user.headline);
					tvProfileLocation.setText(user.location);

//					tvConnectionCount.setVisibility(View.GONE);
					tvIndustry.setText(user.industry);
					
					((LinearLayout)mFragActivityContext.findViewById(R.id.lin_skills)).setVisibility(View.GONE);
					((LinearLayout)mFragActivityContext.findViewById(R.id.lin_languages)).setVisibility(View.GONE);
					((LinearLayout)mFragActivityContext.findViewById(R.id.lin_profile_url)).setVisibility(View.GONE);
					((LinearLayout)mFragActivityContext.findViewById(R.id.lin_summary)).setVisibility(View.GONE);
					
//					tvSkills.setVisibility(View.GONE);
//					tvLanguages.setVisibility(View.GONE);
//					tvProfileURL.setVisibility(View.GONE);
//					tvProfileSummary.setVisibility(View.GONE);

					Picasso picasso = Picasso.with(mFragActivityContext);
					RequestCreator creator = picasso.load(user.profilepicture);
					creator.resize(80, 80);
					creator.centerCrop();
					creator.transform(new CircleTransform());
					creator.into(ivProfileImage);
					
					linearLayout.setVisibility(View.VISIBLE);
					
//					relativeLayout.setVisibility(View.GONE);
				}
				
			}
		}

	}

	private void overrideFonts(final Context context, final View v) {
		try {
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					overrideFonts(context, child);
				}
			} else if (v instanceof TextView ) {
				((TextView) v).setTypeface(FontUtils.getLatoRegularTypeface(getActivity()));
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		int key = view.getId();
		
		switch (key) {
		
		case R.id.btn_seeonmap:		
//			Toast.makeText(mFragActivityContext, "See on Map", Toast.LENGTH_SHORT).show();
			
			// Create fragment and give it an argument for the selected article
            GoogleMapFragment mapFragment = (GoogleMapFragment) Fragment.instantiate(mFragActivityContext, 
            						ConstantUtils.GOOGLE_MAP_FRAGMENT);	           

            Bundle bundle = new Bundle();            
            bundle.putString("marker_type", "LocateOnMap");
			bundle.putSerializable("user", user);
            
			mapFragment.setArguments(bundle);
			
            FragmentTransaction transaction = mFragActivityContext.getSupportFragmentManager().beginTransaction();
            
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, mapFragment, "googlemap");
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
			
			break;

		case R.id.btn_viewprofile:		
			Toast.makeText(mFragActivityContext, "View Profile", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.btn_sendmessage:			
			Toast.makeText(mFragActivityContext, "Send Message", Toast.LENGTH_SHORT).show();
			break;
			
			
		default:
			break;
		}
		
	}
	
}