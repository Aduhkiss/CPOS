package com.thecloudyco.pos.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ConsoleUtil {
	public static void Print(String line_one, String line_two) {
		// First Clear the entire page
		Clear();
		if(line_one.equals("MO")) {
			System.out.println("M	" + "\n" + line_two);
		} else {
			System.out.println(line_one + "\n" + line_two);
		}
		//System.out.println(line_two);
	}
	
	public static void Clear() {
		for(int i = 0; i < 100; i++) {
			System.out.println("\n");
		}
	}
	
	/*
	 * Thank you Joachim :)
	 */
	public static void open(String webpage) throws IOException, URISyntaxException {
		Desktop.getDesktop().browse(new URI(webpage));
	}
}
