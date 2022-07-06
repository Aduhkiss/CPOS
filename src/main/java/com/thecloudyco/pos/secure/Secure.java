package com.thecloudyco.pos.secure;

import com.thecloudyco.pos.util.ConsoleUtil;

public class Secure {
	
	public static int timer;
	public static boolean isSecured;
	
	public static void secureTerminal() {
		isSecured = true;
		ConsoleUtil.Clear();
	}
	
	public static void unlockTerminal() {
		isSecured = false;
		timer = 0;
	}
	
	public static boolean isSecured() {
		return isSecured;
	}
	
}
