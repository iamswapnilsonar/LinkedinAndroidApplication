package com.vsplc.android.poc.linkedin.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;

public class IndustryListAdapter extends BaseAdapter {
    
    @SuppressWarnings("unused")
	private Activity activity;
    private Set<String> data;
    private static LayoutInflater inflater=null; 
    private List<String> listIndustries;
    
    
    public IndustryListAdapter(Activity activity, List<String> data) {
        this.activity = activity;
        this.listIndustries=data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        listIndustries = new ArrayList<String>(data);
        
        for(String var: listIndustries){
        	Logger.vLog("IndustryListAdapter - Constructor", ""+var);
        }
    }

    public int getCount() {
        return listIndustries.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        
        if(convertView == null)
            vi = inflater.inflate(R.layout.list_industries_row, null);

        TextView name = (TextView)vi.findViewById(R.id.ind_name); // title
//        TextView industry = (TextView)vi.findViewById(R.id.industry); // artist name
//        TextView location = (TextView)vi.findViewById(R.id.location); // duration
//        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        
//        HashMap<String, String> song = new HashMap<String, String>();
        
        for(String var: listIndustries){
        	Logger.vLog("IndustryListAdapter", ""+var);
        }
        
        // Setting all values in listview
        name.setText(listIndustries.get(position));
//        industry.setText(user.industry);
//        location.setText(user.location);
        
        return vi;
    }
}