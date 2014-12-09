package com.vsplc.android.social_poc.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.social_poc.model.LinkedinUser;

public class LKConnectionsListAdapter extends BaseAdapter {
	
	// List context
	private final Context context;
	
	private static LayoutInflater inflater=null;
	
	private List<LinkedinUser> listLKUsers = new ArrayList<LinkedinUser>();
	
//	private List<String> listLKUsers = new ArrayList<String>();
	public LKConnectionsListAdapter(Context context, List<LinkedinUser> listLKUsers) 
	{
		this.context = context;	
		this.listLKUsers = listLKUsers;
		inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * Constructing list element view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		ViewHolder holder; // to reference the child views for later actions		
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.process_view_list_row, parent, false);
			
			// cache view fields into the holder
	        holder = new ViewHolder();
	        
	        holder.tvLinkedinUser = (TextView) rowView.findViewById(R.id.tvLinkedinUser);
	        holder.tvLinkedinUserWorkingIndustry = (TextView) rowView.findViewById(R.id.tvLinkedinUserWorkingIndustry);
	        holder.tvLinkedinUserLocation = (TextView) rowView.findViewById(R.id.tvLinkedinUserLocation);	     		
			
			// associate the holder with the view for later lookup
	        rowView.setTag(holder);
		}else{
			// view already exists, get the holder instance from the view
	        holder = (ViewHolder) rowView.getTag();	
	        rowView = convertView;
		}	

		LinkedinUser mLinkedinUser = listLKUsers.get(position);
		
//		holder.tvLinkedinUser.setText(mLinkedinUser.getStrLinkedinUserFirstName() +" "+mLinkedinUser.getStrLinkedinUserLastName());
//		holder.tvLinkedinUserWorkingIndustry.setText(mLinkedinUser.getStrLinkedinUserWorkingDomain());
//		holder.tvLinkedinUserLocation.setText(mLinkedinUser.getStrLinkedinUserCountryLocation());
		
//		holder.tvLinkedinUser.setText("Swapnil");
//		holder.tvLinkedinUserWorkingIndustry.setText("Sonar");
//		holder.tvLinkedinUserLocation.setText("Pune");
		
		return rowView;		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listLKUsers.size();
	}

	@Override
	public Object getItem(int position) {		
		return listLKUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	// somewhere else in your class definition
	class ViewHolder {
	    
		TextView tvLinkedinUser;
	    TextView tvLinkedinUserWorkingIndustry;
	    TextView tvLinkedinUserLocation;	   
	}
}
