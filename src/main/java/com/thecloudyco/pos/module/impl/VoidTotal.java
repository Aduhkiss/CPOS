package com.thecloudyco.pos.module.impl;

import java.util.Scanner;

import com.thecloudyco.override.api.ManagerAPI;
import com.thecloudyco.override.common.OverrideType;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.util.ConsoleUtil;
import com.thecloudyco.pos.util.QuickMessage;
import com.thecloudyco.pos.util.SoundUtils;

public class VoidTotal extends CModule {
	public VoidTotal() {
		super("VOID TRANSACTION");
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		
		if(Register.access().getTransaction() != null) {
		} else {
			ConsoleUtil.Print("ERROR", "There is no active transaction");
			return;
		}
		
		double balance = Register.access().getBalance();
		
		// If the balance is over $60, we require a manager override
		if(balance >= 60.00 || Register.access().getTransaction().getVoidedMoney() >= 60.00) {
			SoundUtils.beep();
			
			ConsoleUtil.Print("MO", "VT TRANSACTION LIMIT CHECK");
			System.out.println("\n");
			
			String override = sc.nextLine();
			ManagerAPI mAPI = new ManagerAPI();
			
			boolean flag = false;
			try {
				flag = mAPI.isAuthorized(OverrideType.CASH_OFFICE_FES, override);
			} catch (Exception e) {}
			
			if(!flag) {
				ConsoleUtil.Print("ERROR", "Not Authorized");
				return;
			} else {
				Register.access().getTransaction().voidLimitCheckBypassed = true;
				ConsoleUtil.Print("** VOID TRANSACTION **", "** THANK YOU **");
				Register.access().setBalance(0.00);
				Register.access().getTransaction().finish(false);
				Register.access().voidTransaction();
				return;
			}
			
		} else {
			ConsoleUtil.Print("** VOID TRANSACTION **", "** THANK YOU **");
			Register.access().setBalance(0.00);
			Register.access().getTransaction().finish(false);
			Register.access().voidTransaction();
			return;
		}
	}

}
