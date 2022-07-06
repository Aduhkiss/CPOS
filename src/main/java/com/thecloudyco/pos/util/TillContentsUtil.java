package com.thecloudyco.pos.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.thecloudyco.pos.Main;


public class TillContentsUtil {
	
	public static double getCashTillContents(String terminal) throws SQLException {
		ResultSet result = Main.getDB().query("SELECT * FROM `till_contents` WHERE `terminal_number` = '" + terminal + "';");
		boolean f = false;
		while(result.next()) {
			f = true;
			return result.getDouble("cash_amount");
		}
		if(!f) {
			Main.getDB().update("INSERT INTO `till_contents` (`terminal_number`, `cash_amount`, `check_amount`) VALUES ('" + terminal + "', '0.00', '0.00');");
		}
		return 0.00;
	}
	
	public static double getCheckTillContents(String terminal) throws SQLException {
		ResultSet result = Main.getDB().query("SELECT * FROM `till_contents` WHERE `terminal_number` = '" + terminal + "';");
		boolean f = false;
		while(result.next()) {
			f = true;
			return result.getDouble("check_amount");
		}
		if(!f) {
			Main.getDB().update("INSERT INTO `till_contents` (`terminal_number`, `cash_amount`, `check_amount`) VALUES ('" + terminal + "', '0.00', '0.00');");
		}
		return 0.00;
	}
	
	public static void addCashTillContents(String terminal, double cash) throws SQLException {
		double current = getCashTillContents(terminal);
		Main.getDB().update("UPDATE `till_contents` SET `cash_amount` = '" + (current + cash) + "' WHERE `terminal_number` = '" + terminal + "';");
	}
	
	public static void addCheckTillContents(String terminal, double cash) throws SQLException {
		double current = getCashTillContents(terminal);
		Main.getDB().update("UPDATE `till_contents` SET `check_amount` = '" + (current + cash) + "' WHERE `terminal_number` = '" + terminal + "';");
	}
}
