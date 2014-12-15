package com.vsplc.android.social_poc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.vsplc.android.social_poc.logger.Logger;
import com.vsplc.android.social_poc.model.LinkedinUser;

public class MethodUtils {

	public static void printSet(Set<String> set) {

		// Get an iterator
		Iterator<String> iterator = set.iterator();

		// Display elements
		while (iterator.hasNext()) {
			Logger.vLog("printSet", "Value : " + iterator.next());
		}

	}

	public static Map<String, List<LinkedinUser>> getCountrywiseConnections(
			List<LinkedinUser> listConnections) {

		Map<String, List<LinkedinUser>> mapCountrywiseConnections = new HashMap<String, List<LinkedinUser>>();
		Iterator<String> iterator = LinkedinApplication.setOfGlobalCountries
				.iterator();

		while (iterator.hasNext()) {
			String code = iterator.next();

			List<LinkedinUser> list = new ArrayList<LinkedinUser>();

			for (int i = 0; i < listConnections.size(); i++) {
				LinkedinUser user = listConnections.get(i);

				if (user.country_code != null && user.country_code.equals(code)) {
					// NOP
					list.add(user);
				}
			}

			Logger.vLog("Users", "" + list.size());
			mapCountrywiseConnections.put(code, list);
		}

		// printMap(mapCountrywiseConnections);
		return mapCountrywiseConnections;
	}

	public static List<LinkedinUser> getTotalCountrywiseConnections(String country,
			List<LinkedinUser> listConnections) {

		List<LinkedinUser> list = new ArrayList<LinkedinUser>();

		for (int i = 0; i < listConnections.size(); i++) {
			LinkedinUser user = listConnections.get(i);

			if (user.country_code != null && user.country_code.equals(country)) {
				// NOP
				list.add(user);
			}
		}

		Logger.vLog("Users list : ", "" + list.size());
		return list;
	}
	
	public static List<LinkedinUser> getIndustrywiseConnections(
			String industry, List<LinkedinUser> connections) {

		List<LinkedinUser> filteredConnections = new ArrayList<LinkedinUser>();

		for (int i = 0; i < connections.size(); i++) {
			LinkedinUser user = connections.get(i);

			if (user.industry != null && user.industry.equals(industry)) {
				// NOP
				filteredConnections.add(user);
			}
		}

		Logger.vLog(industry + "Users", "" + filteredConnections.size());

		for (LinkedinUser user : filteredConnections)
			Logger.vLog("Print", "Value : " + user.toString());
		return filteredConnections;
	}

	public static List<LinkedinUser> getCitywiseConnections(String city, String country) {

		List<LinkedinUser> connections = LinkedinApplication.mapCountrywiseConnections.get(country);
		Logger.vLog("City : ", city);
		
		List<LinkedinUser> filteredConnections = new ArrayList<LinkedinUser>();

		for (int i = 0; i < connections.size(); i++) {
			LinkedinUser user = connections.get(i);

			if (user.location != null && user.location.contains(city)) {
				// NOP
				filteredConnections.add(user);
			}
		}

//		Logger.vLog(city + "Users", "" + filteredConnections.size());

//		for (LinkedinUser user : filteredConnections)
//			Logger.vLog("Print", "Value : " + user.toString());
		
		return filteredConnections;
	}
	
	public static void printMap(Map<String, List<LinkedinUser>> map) {

		for (Entry<String, List<LinkedinUser>> entry : map.entrySet()) {

			String country_code = entry.getKey();
			Logger.vLog("CC", country_code);

			List<LinkedinUser> list = entry.getValue();
			Logger.vLog("Users", "" + list.size());

			for (LinkedinUser user : list)
				Logger.vLog("Print", "Value : " + user.toString());
		}
	}

	/**
	 * get city from location area.
	 * 
	 * @param location
	 * @param countryCode
	 * @return
	 */
	public static String getCityNameFromLocation(String location,
			String countryCode) {

		String str = location;

		String strWordArea = "Area";
		String strWordCountry = getISOCountryNameFromCC(countryCode);
		String strWordSemiColon = ",";

		if (str.equalsIgnoreCase(strWordCountry)) {
			// NOP
			str = "NA";
		} else {
			if (str.contains(strWordArea)) {
				str = str.replace(" " + strWordArea, "");
				if (str.contains(strWordCountry)) {
					str = str.replace(" " + strWordCountry, "");
					if (str.contains(strWordSemiColon)) {
						str = str.replace(strWordSemiColon, "");

					}
				}
			}
		}

		// System.out.println(str);
		return str;
	}

	/**
	 * get ISO Country name from Country Code.
	 * 
	 * @param countryCode
	 * @return
	 */
	public static String getISOCountryNameFromCC(String countryCode) {
		Locale obj = new Locale("", countryCode.toUpperCase());
		String strCountryName = obj.getDisplayCountry();
		return strCountryName;
	}
}