package com.thecloudyco.pos.util;

import java.util.regex.PatternSyntaxException;

public class StringUtil {
	public static String[] toArray(String string) {
		String[] splitArray = null;
		try {
			splitArray = string.split("\\\\s+");
		} catch(PatternSyntaxException ex) {
		}
		return splitArray;
	}
	
	public static String combine(String[] arr, int startPos) {
        StringBuilder str = new StringBuilder();

        for(int i = startPos; i < arr.length; ++i) {
           str = str.append(arr[i] + " ");
        }
        return str.toString();
	}
	
	public static String realBalance(String value) {
		int v = value.indexOf(".");
		if(value.substring(v, value.length()).length() > 2) {
			return value.substring(0, v + 3);
		}
		//System.out.println(value.substring(v).length());
		if(v == 3) {
			value = value + "0";
		}
		return value;
	}
	
	public static String realBalance(double value) {
		String valuee = String.valueOf(value);
		int v = valuee.indexOf(".");
		if(valuee.substring(v, valuee.length()).length() > 2) {
			return valuee.substring(0, v + 3);
		}
		if(v == 3) {
			valuee = valuee + "0";
		}
		return valuee;
	}
	
	public static String removeLeadingZeroes(String str) {
	      String strPattern = "^0+(?!$)";
	      str = str.replaceAll(strPattern, "");
	      return str;
	}
}
