package com.thecloudyco.pos.module.impl.nosale;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.print.PrintException;

import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.database.AtticusDB;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.util.ConsoleUtil;
import com.thecloudyco.printutil.AtticusPrintUtil;

public class PrintActiveSessions extends CModule {
	
	public PrintActiveSessions() {
		super("PRINT ACTIVE SESSIONS", false);
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		ConsoleUtil.Print("NOW PRINTING", "ACTIVE SESSIONS...");
		
		AtticusPrintUtil.addToQueue("*******************");
		AtticusPrintUtil.addToQueue("ACTIVE SESSIONS REPORT");
		AtticusPrintUtil.addToQueue("*******************");
		
		AtticusDB db = Main.getDB();
		
		ResultSet result;
		try {
			result = db.query("SELECT * FROM `active_sessions`;");
			while(result.next()) {
				AtticusPrintUtil.addToQueue("OPERATOR: " + result.getString("operator_number"));
				AtticusPrintUtil.addToQueue("TERMINAL: " + result.getString("terminal_number"));
				AtticusPrintUtil.addToQueue("*******************");
				
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy   HH:mm:ss");  
		    Date date = new Date();
			DecimalFormat df = new DecimalFormat("##.##");
			
			AtticusPrintUtil.addToQueue("");
			AtticusPrintUtil.addToQueue("*******************");
			AtticusPrintUtil.addToQueue("  " + formatter.format(date));
			AtticusPrintUtil.addToQueue("  " + Register.access().getLoggedIn().getOperatorId() + "   " + Main.getConfig().getTerminalNumber());
			AtticusPrintUtil.addToQueue("*******************");
			
			AtticusPrintUtil.addToQueue("              ");
			AtticusPrintUtil.addToQueue("              ");
			AtticusPrintUtil.addToQueue("              ");
			AtticusPrintUtil.addToQueue("              ");
			AtticusPrintUtil.addToQueue("              ");
			AtticusPrintUtil.addToQueue("              ");
			AtticusPrintUtil.addToQueue("              ");
			AtticusPrintUtil.addToQueue("              ");
			
			try {
				AtticusPrintUtil.print();
			} catch (PrintException | IOException e) {
			}
		} catch (SQLException e) {
			//ConsoleUtil.Print("FUCK THERE", "WAS AN ERROR");
		}
		return;
	}

}
