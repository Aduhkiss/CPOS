package com.thecloudyco.pos.module.impl.nosale;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import javax.print.PrintException;

import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.util.ConsoleUtil;
import com.thecloudyco.pos.util.IdiotUtil;
import com.thecloudyco.printutil.AtticusPrintUtil;


public class CheckNoteReport extends CModule {
	
	public CheckNoteReport() {
		super("CHECK NOTE REPORT");
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		ConsoleUtil.Print("PLEASE ENTER", "NOTE SERIAL NUMBER");
		String serial_number = sc.nextLine();
		
		try {
			ResultSet result = Main.getDB().query("SELECT * FROM `cash_paper` WHERE `serial` = '" + serial_number + "';");
			boolean anything = false;
			
			while(result.next()) {
				anything = true;
				System.out.println("===============================================");
				System.out.println("            CASH LOOKUP INFORMATION");
				System.out.println("===============================================");
				System.out.println("NOTE FINDER NAME:  " + result.getString("finder"));
				System.out.println("");
				System.out.println("NOTE SERIAL NUMBER: " + result.getString("serial"));
				System.out.println("");
				System.out.println("NOTE CASH VALUE: " + result.getString("value"));
				System.out.println("");
				System.out.println("IS THIS FAKE?: " + IdiotUtil.idiotConvert(result.getInt("fake")));
				System.out.println("");
				System.out.println("===============================================");
				
				AtticusPrintUtil.addToQueue("*******************");
				AtticusPrintUtil.addToQueue("CASH LOOKUP INFORMATION");
				AtticusPrintUtil.addToQueue("*******************");
				AtticusPrintUtil.addToQueue("FINDER NAME:  " + result.getString("finder"));
				AtticusPrintUtil.addToQueue("");
				AtticusPrintUtil.addToQueue("SERIAL NUMBER: " + result.getString("serial"));
				AtticusPrintUtil.addToQueue("");
				AtticusPrintUtil.addToQueue("CASH VALUE: " + result.getString("value"));
				AtticusPrintUtil.addToQueue("");
				AtticusPrintUtil.addToQueue("IS FAKE?: " + IdiotUtil.idiotConvert(result.getInt("fake")));
				AtticusPrintUtil.addToQueue("");
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
				
				return;
			}
			
			if(!anything) {
				System.out.println("===============================================");
				System.out.println("      NOTE SERIAL NUMBER NOT FOUND!");
				System.out.println("===============================================");
				return;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
