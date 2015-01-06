package com.vsplc.android.poc.linkedin.fragments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vsplc.android.poc.linkedin.BaseActivity;
import com.vsplc.android.poc.linkedin.R;
import com.vsplc.android.poc.linkedin.linkedin_api.model.EasyLinkedIn;
import com.vsplc.android.poc.linkedin.logger.Logger;
import com.vsplc.android.poc.linkedin.model.LinkedinUser;
import com.vsplc.android.poc.linkedin.utils.DataWrapper;
import com.vsplc.android.poc.linkedin.utils.FontUtils;
import com.vsplc.android.poc.linkedin.utils.MethodUtils;

public class MessageFragment extends Fragment implements OnClickListener{

	private FragmentActivity mFragActivityContext;
	
	private ArrayList<LinkedinUser> listLinkedinUsers;
	
	private Button btnSend, btnCancel, btnLeft;
	private EditText edtSubject, edtMessage, edtRecipients;
	
	private ProgressDialog pDialog;
	private Typeface typeface;
	
	private String callingFrom;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

		mFragActivityContext = getActivity();

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.message_fragment, container, false);
		
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		btnSend = (Button) view.findViewById(R.id.btn_send);
		btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		
		edtSubject = (EditText) view.findViewById(R.id.edit_message_subject);
		edtMessage = (EditText) view.findViewById(R.id.edit_message_body);
		edtRecipients = (EditText) view.findViewById(R.id.edit_message_receipents); 		
		
		btnLeft.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
				
		return view;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// showing progress dialog while performing heavy tasks..
		pDialog = new ProgressDialog(mFragActivityContext); 
		
		Bundle args = getArguments(); 

		if (args != null) { // load web link received through bundle
			//NOP
			
			try{
				
				DataWrapper dataWrapper = (DataWrapper) args.getSerializable("connection_list");
				ArrayList<LinkedinUser> mConnections = dataWrapper.getList();
				
				listLinkedinUsers = mConnections;
				
				Logger.vLog("MessageFragment", "Receipents : "+listLinkedinUsers.size());
				
				if (listLinkedinUsers.size() > 0) {
					LinkedinUser user = listLinkedinUsers.get(0);
					edtRecipients.setText(user.fname+" "+user.lname);
				}
				
			}catch(Exception ex){
				
			}
			
			callingFrom = args.getString("callingFrom");
		}
		
		if (callingFrom.equals("NavigationDrawer")) {
			btnLeft.setBackgroundResource(R.drawable.btn_list_tap_effect);
		}else{
			btnLeft.setBackgroundResource(R.drawable.btn_back_tap_effect);
		}
		
		typeface = FontUtils.getLatoRegularTypeface(mFragActivityContext);
		
		edtRecipients.setTypeface(typeface);
		edtSubject.setTypeface(typeface);
		edtMessage.setTypeface(typeface);
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		int key = v.getId();
		
		switch (key) {

		case R.id.btn_left:
			
			if (callingFrom.equals("NavigationDrawer")) {
				((BaseActivity) getActivity()).showHideNevigationDrawer();
			}else{
				getActivity().onBackPressed();
			}
			
			
			break;

		case R.id.btn_send:
			
			if (!isEmpty(edtSubject) && !isEmpty(edtMessage)) {
				new LongOperationForSendMessage().execute(listLinkedinUsers);
			}else{
				Toast.makeText(mFragActivityContext, "Please enter the details..", Toast.LENGTH_SHORT).show();
			}
			
			break;
			
		case R.id.btn_cancel:
			edtSubject.setText("");
			edtMessage.setText("");
			break;
			
		}
	}
	
	private boolean isEmpty(EditText etText) {
		return etText.getText().toString().trim().length() == 0;
	}
	
	private class LongOperationForSendMessage extends AsyncTask<Object, Void, String> {

		@Override
		protected String doInBackground(Object... params) {

			HttpResponse response = null;
			String result;
			
			String strSubject = edtSubject.getText().toString();
			String strMessage = edtMessage.getText().toString();
			
			@SuppressWarnings("unchecked")
			List<LinkedinUser> listRecipients = (ArrayList<LinkedinUser>) params[0];
			
			Logger.vLog("LongOperationForSendMessage", "Size of Recipients : "+listRecipients.size());
			
			if (listRecipients.size() < 50) {

				String prepared_url = "https://api.linkedin.com/v1/people/~/mailbox?oauth2_access_token="
						+ EasyLinkedIn.getAccessToken();

				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(prepared_url);

				post.setHeader("content-type", "text/XML");

				StringBuilder builder = new StringBuilder();

				builder.append("<mailbox-item>");
				builder.append("<recipients>");
				
				for (LinkedinUser user : listRecipients) {
					builder.append("<recipient><person path='/people/"+user.id+"' /></recipient>");
				}
				
//				builder.append("<recipient><person path='/people/abcdefg' /></recipient>");
				builder.append("</recipients>");
				
				String mSubject = "<subject>"+strSubject+"</subject>";
				String mMessage = "<body>"+strMessage+"</body>";
				
				
//				builder.append("<subject>Linkedin Locator - Testing</subject>");
//				builder.append("<body>This is a message from Linkedin-Locator Dev Team, Kindly ignore this message. \nSorry For Inconvenience. \n~Dev Team.</body>");
				
				builder.append(mSubject);
				builder.append(mMessage);
				
				builder.append("</mailbox-item>");

				String myEntity = builder.toString();

				try {

					StringEntity str_entity = new StringEntity(myEntity);
					post.setEntity(str_entity);

					response = httpclient.execute(post);
					Log.i("Send Message : ", ""+response.toString());

					result = "Success";

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = "Error.. Message not sent.";

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = "Error.. Message not sent.";

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = "Error.. Message not sent.";

				}
				
			}else{
				result = "Too many recipients in the message. Max allowed to be 50 recipients.";
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if (pDialog.isShowing()) {
				pDialog.dismiss();
			}
			
			if (result.equals("Success")) {
				Toast.makeText(mFragActivityContext, "Message Sent Successfully..", Toast.LENGTH_SHORT).show();
				edtSubject.setText("");
				edtMessage.setText("");
				getActivity().onBackPressed();
				
			}else{
				Toast.makeText(mFragActivityContext, result, Toast.LENGTH_SHORT).show();
			}
						
		}

		@Override
		protected void onPreExecute() {
			pDialog.setMessage("Sending Message..");
			pDialog.show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}
}
