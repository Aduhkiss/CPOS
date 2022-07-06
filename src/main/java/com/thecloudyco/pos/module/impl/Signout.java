package com.thecloudyco.pos.module.impl;

import java.sql.SQLException;
import java.util.Scanner;

import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.util.ConsoleUtil;

public class Signout extends CModule {
	
	public Signout() {
		super("SIGNOUT REGISTER");
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		
		
		
		if(Register.access().getTransaction() != null) {
			ConsoleUtil.Print("ERROR", "Cannot Signout while a transaction is in progress");
			return;
		}
		
		try {
			Main.getDB().update("DELETE FROM `active_sessions` WHERE `operator_number` = '" + Register.access().getLoggedIn().getOperatorId() + "' AND `terminal_number` = '" + Main.getConfig().getTerminalNumber() + "';");
		} catch (SQLException e) {
		}
		
		Register.access().setBalance(0.00);
		Register.access().loggedInOperator = null;
		
		ConsoleUtil.Clear();
	}

}
