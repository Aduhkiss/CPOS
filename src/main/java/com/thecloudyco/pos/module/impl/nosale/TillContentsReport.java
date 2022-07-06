package com.thecloudyco.pos.module.impl.nosale;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.print.PrintException;

import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.util.TillContentsUtil;
import com.thecloudyco.printutil.AtticusPrintUtil;

public class TillContentsReport extends CModule {
	
	public TillContentsReport() {
		super("TILL CONTENTS REPORT");
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy   HH:mm:ss");  
	    Date date = new Date();
		DecimalFormat df = new DecimalFormat("##.##");
		
		AtticusPrintUtil.addToQueue("*******************");
		AtticusPrintUtil.addToQueue("TILL CONTENTS REPORT");
		AtticusPrintUtil.addToQueue("*******************");// df.format(price) TillContentsUtil.

		try {
			AtticusPrintUtil.addToQueue("CASH:  " + df.format(TillContentsUtil.getCashTillContents(Main.getConfig().getTerminalNumber())));
			AtticusPrintUtil.addToQueue("CHECK:  " + df.format(TillContentsUtil.getCheckTillContents(Main.getConfig().getTerminalNumber())));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		
		return;
	}
	
}
