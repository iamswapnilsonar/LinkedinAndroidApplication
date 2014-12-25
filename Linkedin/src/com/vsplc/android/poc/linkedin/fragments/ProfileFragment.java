package com.vsplc.android.poc.linkedin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.vsplc.android.poc.linkedin.BaseActivity;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.utils.CircleTransform;
import com.vsplc.android.poc.linkedin.utils.FontUtils;

public class ProfileFragment extends Fragment {
	
	private TextView tvProfileName, tvProfileHeading, tvProfileLocation;
	
	@SuppressWarnings("unused")
	private TextView tvConnectionCount, tvSkills, tvLanguages, tvIndustry, tvProfileURL, tvProfileSummary;
	private ImageView ivProfileImage;
	
	private FragmentActivity mFragActivityContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mFragActivityContext = getActivity();
		
		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.

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
		
		tvProfileURL.setSelected(true);
				
		Button btnRight = (Button) view.findViewById(R.id.btn_right);
		btnRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				((BaseActivity) getActivity()).showHideNevigationDrawer();
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

		
		Bundle bundle = getArguments(); 

		if (bundle != null) { // Set article based on argument passed in
			LinkedinUser user = (LinkedinUser) bundle.getSerializable("data");
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
			}
			
		}		
		
		// set font to all texts on Profile Fragment..
		RelativeLayout layout = ((RelativeLayout)getActivity().findViewById(R.id.rel_profile_fragment));
		overrideFonts(getActivity(), layout);	
		
		// black font for Profile Name..
		TextView tvProfileName = (TextView) getActivity().findViewById(R.id.tv_profile_name);
//		tvProfileHeading.setSelected(true);
		tvProfileName.setTypeface(FontUtils.getLatoBlackTypeface(getActivity()));

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

}