package com.thecloudyco.pos.util;

import java.io.Console;
import java.util.Scanner;

public class PasswordUtil {

    private static boolean isConsole;
    private static Console console;

    public PasswordUtil() {

        console = System.console();

        if (console == null) {
            //System.out.println("Couldn't get Console instance");
            isConsole = false;
        } else { isConsole = true; }
    }

    public static boolean isConsole() {
        return isConsole;
    }

    public static String getText(String prompt) {
        Scanner sc = new Scanner(System.in);
        System.out.println(prompt);
        return sc.nextLine();
    }

    public static String getPassword(String prompt) {
        if(isConsole()) {

            char[] passwordArray = console.readPassword(prompt);
            return new String(passwordArray);

        } else {

            Scanner sc = new Scanner(System.in);
            System.out.println(prompt);
            return sc.nextLine();

        }
    }
}
