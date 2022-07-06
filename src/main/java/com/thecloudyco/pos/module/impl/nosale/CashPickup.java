package com.thecloudyco.pos.module.impl.nosale;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.print.PrintException;

import com.thecloudyco.override.api.ManagerAPI;
import com.thecloudyco.override.common.OverrideType;
import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.util.ConsoleUtil;
import com.thecloudyco.pos.util.TillContentsUtil;
import com.thecloudyco.printutil.AtticusPrintUtil;

public class CashPickup extends CModule {
	public CashPickup() {
		super("CASHIER PICKUP", true);
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		ManagerAPI mAPI = new ManagerAPI();
		if (Register.access().getTransaction() != null) {
			boolean flag = false;
			ConsoleUtil.Print("AUTHORIZATION REQUIRED", "ENTER MANAGER OVERRIDE PASSWORD");
			String override = sc.nextLine();
			try {
				flag = mAPI.isAuthorized(OverrideType.CASH_OFFICE_FES, override);
			} catch (Exception e) {
				ConsoleUtil.Print("ERR", "Not Authorized");
				return;
			}
			if (!flag) {
				ConsoleUtil.Print("ERR", "Not Authorized");
				return;
			}
		}

		ConsoleUtil.Print("PLEASE ENTER", "AMOUNT TO PICKUP");
		double pickup = sc.nextDouble();

		// Take this cash from the till and print a receipt
		try {
			TillContentsUtil.addCashTillContents(Main.getConfig().getTerminalNumber(), (pickup * -1));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy   HH:mm:ss");
		Date date = new Date();
		DecimalFormat df = new DecimalFormat("##.##");

		AtticusPrintUtil.addToQueue("*******************");
		AtticusPrintUtil.addToQueue("CASH PICKUP REPORT");
		AtticusPrintUtil.addToQueue("*******************");
		AtticusPrintUtil.addToQueue("");
		AtticusPrintUtil.addToQueue("PICKUP AMOUNT: $" + pickup);
		AtticusPrintUtil.addToQueue("");
		AtticusPrintUtil.addToQueue("*******************");
		AtticusPrintUtil.addToQueue("MANAGER SIGNATURE: ");
		AtticusPrintUtil.addToQueue("");
		AtticusPrintUtil.addToQueue("");
		AtticusPrintUtil.addToQueue("");
		AtticusPrintUtil.addToQueue("");
		AtticusPrintUtil.addToQueue("");
		AtticusPrintUtil.addToQueue("*******************");
		AtticusPrintUtil.addToQueue("  " + formatter.format(date));
		AtticusPrintUtil.addToQueue(
				"  " + Register.access().getLoggedIn().getOperatorId() + "   " + Main.getConfig().getTerminalNumber());
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
