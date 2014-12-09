package com.vsplc.android.social_poc.logger;

import android.util.Log;

public class Logger {
	private static boolean isDebug = true;

	public static void vLog(String tag, String msg) {
		if (isDebug)
			Log.v("Linkedin : ", tag + " => " + msg);
	}

	public static void wLog(String tag, String msg) {
		if (isDebug)
			Log.w("Linkedin : ", tag + " => " + msg);
	}

	public static void eLog(String tag, String msg) {
		if (isDebug)
			Log.e("Linkedin : ", tag + " => " + msg);
	}

	public static void sLog(String msg) {
		if (isDebug)
			System.out.println("Linkedin : " + msg);
	}
}
