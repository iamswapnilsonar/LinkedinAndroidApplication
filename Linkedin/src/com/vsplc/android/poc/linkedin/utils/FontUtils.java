package com.vsplc.android.poc.linkedin.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * This class gives the typeface (font) which used in the development wherever it wants.
 * @Date 22 Dec, 2014
 * @author VSPLC
 */
public class FontUtils {

	public static Typeface getLatoLightTypeface(Context context){
		// created custom font "lato_light.ttf"
		return Typeface.createFromAsset(context.getAssets(),"fonts/lato_light.ttf");
	}
	
	public static Typeface getLatoRegularTypeface(Context context){
		// created custom font "lato_regular.ttf"
		return Typeface.createFromAsset(context.getAssets(),"fonts/lato_regular.ttf");
	}

	public static Typeface getLatoBlackTypeface(Context context){
		// created custom font "lato_black.ttf"
		return Typeface.createFromAsset(context.getAssets(),"fonts/lato_black.ttf");
	}
}
