package com.vsplc.android.poc.linkedin.utils;

import java.io.Serializable;
import java.util.ArrayList;

import com.vsplc.android.poc.linkedin.model.LinkedinUser;

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
