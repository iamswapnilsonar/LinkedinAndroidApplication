package com.vsplc.android.social_poc.lasy_loading;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.social_poc.model.LinkedinUser;

public class LazyAdapter extends BaseAdapter {
    
    @SuppressWarnings("unused")
	private Activity activity;
    private ArrayList<LinkedinUser> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity activity, ArrayList<LinkedinUser> data) {
        this.activity = activity;
        this.data=data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
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
            vi = inflater.inflate(R.layout.list_row, null);

        TextView name = (TextView)vi.findViewById(R.id.name); // title
        TextView industry = (TextView)vi.findViewById(R.id.industry); // artist name
        TextView location = (TextView)vi.findViewById(R.id.location); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        
//        HashMap<String, String> song = new HashMap<String, String>();
        LinkedinUser user = data.get(position);
        
        // Setting all values in listview
        name.setText(user.fname +" "+user.lname);
        industry.setText(user.industry);
        location.setText(user.location);
        imageLoader.DisplayImage(user.profilepicture, thumb_image);
        
        return vi;
    }
}