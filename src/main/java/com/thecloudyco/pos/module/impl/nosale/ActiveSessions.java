package com.thecloudyco.pos.module.impl.nosale;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.database.AtticusDB;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.util.ConsoleUtil;

public class ActiveSessions extends CModule {
	
	public ActiveSessions() {
		super("ACTIVE SESSIONS", false);
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		ConsoleUtil.Print("NOW DISPLAYING", "ACTIVE SESSIONS...");
		
		System.out.println("=======================================");
		
		AtticusDB db = Main.getDB();
		
		ResultSet result;
		try {
			result = db.query("SELECT * FROM `active_sessions`;");
			while(result.next()) {
				System.out.println("OPERATOR: " + result.getString("operator_number"));
				System.out.println("TERMINAL: " + result.getString("terminal_number"));
				System.out.println("=======================================");
				
			}
		} catch (SQLException e) {
			//ConsoleUtil.Print("FUCK THERE", "WAS AN ERROR");
		}
		return;
	}

}
