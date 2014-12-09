package com.vsplc.android.social_poc.model;

public class LinkedinUser {

	public String id;
	public String fname;
	public String lname;
	public String industry;
	public String country_code;
	public String location;
	public String profilepicture;
	public String profileurl;
	public String headline;

	public LinkedinUser(String id, String fname, String lname, String industry, String country_code, 
			String location, String profilepicture, String profileurl, String headline){
		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.industry = industry;
		this.country_code = country_code;
		this.location = location;
		this.profilepicture = profilepicture;
		this.profileurl = profileurl;
		this.headline = headline;
	}
	
	@Override
	public String toString() {
		String divider = "\n -----------------\n";
		String str = " ID : "+ this.id+
					 "\n  FName : "+ this.fname+
					 "\n  LName : "+ this.lname+
					 "\n  Industry : "+ this.industry+
					 "\n  Country Code : "+ this.country_code+
					 "\n  Location : "+ this.location+
					 "\n  Profile Picture : "+ this.profilepicture+
					 "\n  Profile URL : "+ this.profileurl+
					  "\n Headline : "+ this.headline;		
		return divider + str + divider;
	}

}
