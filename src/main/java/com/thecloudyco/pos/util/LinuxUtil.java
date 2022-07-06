package com.thecloudyco.pos.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LinuxUtil {
	/*
	 * All of the Cloudy Co. Cash Registers should be running on linux machines when they are in PRODUCTION enviroments.
	 * This class allows for easy access to mess with the Linux operating system through Java
	 * 
	 * It is part of a security patch I will be releasing to better keep our machines safe
	 */
	
	public static void lockSystem() {
		bash("trap '' 2");
	}
	
	public static void unlockSystem() {
		bash("trap 2");
	}
	
	private static boolean bash(String command) {
	    boolean success = false;
	    Runtime r = Runtime.getRuntime();
	    // Use bash -c so we can handle things like multi commands separated by ; and
	    // things like quotes, $, |, and \. My tests show that command comes as
	    // one argument to bash, so we do not need to quote it to make it one thing.
	    // Also, exec may object if it does not have an executable file as the first thing,
	    // so having bash here makes it happy provided bash is installed and in path.
	    String[] commands = {"bash", "-c", command};
	    try {
	        Process p = r.exec(commands);

	        p.waitFor();
	        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line = "";

	        while ((line = b.readLine()) != null) {
	            System.out.println(line);
	        }

	        b.close();
	        success = true;
	    } catch (Exception e) {
	        System.err.println("Failed to execute bash with command: " + command);
	        e.printStackTrace();
	    }
	    return success;
	}
}
