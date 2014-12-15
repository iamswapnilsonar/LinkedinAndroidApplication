package com.vsplc.android.social_poc.utils;

import java.io.Serializable;
import java.util.ArrayList;

import com.vsplc.android.social_poc.model.LinkedinUser;

@SuppressWarnings("serial")
public class DataWrapper implements Serializable {

	private ArrayList<LinkedinUser> list;

	public DataWrapper(ArrayList<LinkedinUser> list) {
		this.list = list;
	}

	public ArrayList<LinkedinUser> getList() {
		return this.list;
	}

}
