package com.vsplc.android.poc.linkedin.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.vsplc.android.poc.linkedin.BaseActivity;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.fragments.ConnectionFragment;
import com.vsplc.android.poc.linkedin.fragments.MessageFragment;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.utils.CircleTransform;
import com.vsplc.android.poc.linkedin.utils.ConstantUtils;
import com.vsplc.android.poc.linkedin.utils.DataWrapper;

public class ConnectionListAdapter extends BaseAdapter implements Filterable, OnClickListener{
    
	private Activity activity;
    public ArrayList<LinkedinUser> data;
    private static LayoutInflater inflater = null; 
    private ArrayList<LinkedinUser> originalData;
    private Filter connectionFilter;
    private ConnectionFragment connectionFragment;
    
    public ConnectionListAdapter(Fragment fragment, Activity activity, ArrayList<LinkedinUser> data) {
        
    	this.activity = activity;
        this.data = data;
        this.originalData = data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        this.connectionFragment = (ConnectionFragment) fragment;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
       
    	View vi = convertView;
        
    	if(convertView == null)
            vi = inflater.inflate(R.layout.connection_list_row, null);

        TextView name = (TextView)vi.findViewById(R.id.name); // title
        TextView industry = (TextView)vi.findViewById(R.id.industry); // artist name
        TextView location = (TextView)vi.findViewById(R.id.location); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.thumbnail); // thumb image
        Button btn_message = (Button) vi.findViewById(R.id.btn_single_message); 
        
//        HashMap<String, String> song = new HashMap<String, String>();
        LinkedinUser user = data.get(position);
        
        // Setting all values in listview
        name.setText(user.fname +" "+user.lname);
        industry.setText(user.industry);
        location.setText(user.location);
        
        btn_message.setTag(user);
        btn_message.setOnClickListener(this);
        
        Picasso picasso = Picasso.with(activity);
		RequestCreator creator = picasso.load(user.profilepicture);
		creator.resize(120, 120);
		creator.centerCrop();
		creator.placeholder(R.drawable.btn_viewprofile_pressed);
		creator.error(R.drawable.btn_viewprofile_pressed);
		creator.transform(new CircleTransform());
		creator.into(thumb_image);
		
        
        return vi;
    }
    
	/*
	 * We create our filter	
	 */
	
	@Override
	public Filter getFilter() {
		if (connectionFilter == null)
			connectionFilter = new ConnectionFilter();
		
		return connectionFilter;
	}
    
    public void resetData() {
		data = originalData;
	}
    
	public class ConnectionFilter extends Filter {
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			
			// We implement here the filter logic
			if (constraint == null || constraint.length() == 0) {
				// No filter implemented we return all the list
				results.values = originalData;
				results.count = originalData.size();
				
			}
			else {
				// We perform filtering operation
				ArrayList<LinkedinUser> mLinkedinUserList = new ArrayList<LinkedinUser>();
				
				for (LinkedinUser user : data) {
					
					String userFName = user.fname.toUpperCase();					
					String userLName = user.lname.toUpperCase();
					String userName = user.fname.toUpperCase() + " "+user.lname.toUpperCase();
					
					String enteredText = constraint.toString().toUpperCase();
					
					if (userName.startsWith(enteredText) || userFName.startsWith(enteredText) || userLName.startsWith(enteredText))
						mLinkedinUserList.add(user);
				}
				
				results.values = mLinkedinUserList;
				results.count = mLinkedinUserList.size();

			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			
			// Now we have to inform the adapter about the new list filtered
			if (results.count == 0)
				notifyDataSetInvalidated();
			else {
				data = (ArrayList<LinkedinUser>) results.values;
				notifyDataSetChanged();
			}
			
		}
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		int key = view.getId();
		
		switch (key) {
		case R.id.btn_single_message:
			
			connectionFragment.hideKeyboardAndClearSearchText();
			
			LinkedinUser user = (LinkedinUser) view.getTag();
			
			List<LinkedinUser> listRecipients  = new ArrayList<LinkedinUser>();
 			listRecipients.add(user);
 			
//			new LongOperationForSendMessage().execute(listRecipients);
						
			// Create fragment and give it an argument for the selected article
            MessageFragment messageFragment = (MessageFragment) Fragment.instantiate(activity, 
            						ConstantUtils.MESSAGE_FRAGMENT);	           

            Bundle bundle3 = new Bundle();            
			            
			DataWrapper dataWrapper = new DataWrapper((ArrayList<LinkedinUser>)listRecipients);
			bundle3.putSerializable("connection_list", dataWrapper);			
			bundle3.putString("callingFrom","ProfileFragment");
			messageFragment.setArguments(bundle3);
			
            FragmentTransaction transaction3 = ((BaseActivity)activity).getSupportFragmentManager().beginTransaction();
            
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction3.replace(R.id.fragment_container, messageFragment, "message");
            transaction3.addToBackStack(null);

            // Commit the transaction
            transaction3.commit();
			
			break;

		default:
			break;
		}
		
	}
    
}