package com.thecloudyco.pos.module.impl.tender;

import java.util.Scanner;

import com.thecloudyco.override.api.ManagerAPI;
import com.thecloudyco.override.common.OverrideType;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.transaction.Tender;
import com.thecloudyco.pos.util.ConsoleUtil;
import com.thecloudyco.pos.util.SoundUtils;

public class BTCTender extends CModule {
	
	public BTCTender() {
		super("BITCOIN", 0.00);
	}
	
	@Override
	public void execute(String[] args, Scanner sc) {
		if(Register.access().getTransaction() != null) {
		} else {
			ConsoleUtil.Print("ERROR", "There is no active transaction");
			return;
		}
		
		ConsoleUtil.Print("PLEASE ENTER", "BITCOIN TENDER AMOUNT");
		double tender = 0.00;
		try {
			tender = Double.valueOf(sc.nextLine());
		} catch(IllegalArgumentException ex) {
			tender = Register.access().getTransaction().getFinalPrice();
			// Quick bug fix while im looking through this code
			if(tender < 0.00) {
				tender = 0.00;
			}
		}
		
		if(tender >= this.getTenderLimitCheck()) {
			SoundUtils.beep();
			ManagerAPI mAPI = new ManagerAPI();
			// Ask for a managers override
			boolean flag = false;
			SoundUtils.beep();
			ConsoleUtil.Print("MO", "TENDER AMOUNT LIMIT CHECK");
			System.out.println("\n");
			String override = sc.nextLine();
			
			try {
				flag = mAPI.isAuthorized(OverrideType.CASH_OFFICE_FES, override);
			} catch (Exception e) {}
			
			if(!flag) {
				ConsoleUtil.Print("ERROR", "Not Authorized");
				return;
			} else {
				
				if(tender < 0.00) {
					ConsoleUtil.Print("ERROR", "Tender Amount must be positive number");
					return;
				}
				
				Register.access().getTransaction().voidLimitCheckBypassed = true;
				
				// TODO: Process and log the tender
				Register.access().removeBalance(tender);
				Register.access().getTransaction().addTender(new Tender("BITCOIN", (tender * -1), true));
				ConsoleUtil.Print("BITCOIN TENDER PROCESSED", "$" + Register.access().getReadableBalance() + " REMAINING");				
				
				if(Register.access().getBalance() <= 0) {
					// Finish the transaction, and tell the cashier how much change to give the customer
					ConsoleUtil.Print("** THANK YOU **", "** COME AGAIN **");
					// There should never be change owed with online pay, but whatever, just incase
					System.out.println("CHANGE OWED: " + Register.access().getReadableBalance());
					//TODO: Print Recipt
					//TODO: Clear the list of items that the customer is purchasing (or just completely reset it)
					Register.access().setBalance(0.00);
					Register.access().getTransaction().finish(true);
					Register.access().voidTransaction();
					return;
				}
			}
		}
	}
	
}
