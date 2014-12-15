package com.vsplc.android.social_poc.model;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class City {
	
	public String name;
	public List<LinkedinUser> connections;
	public String country;
	public LatLng latLng;
	
	public City() {
		// TODO Auto-generated constructor stub
	}
	
	public City(String name){
		this.name = name;
	}
	
	public City getCityObject(){
		return this;
	}
	
	@Override
	public String toString() {
		String divider = "\n -----------------\n";
		String str = " Name : " + this.name + "\n  Country : " + this.country;
		
		if (this.latLng != null) {
			str += " Lat : " + this.latLng.latitude + " Long : " + this.latLng.longitude;
		}
		
		return divider + str + divider;
	}
}
