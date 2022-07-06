package com.thecloudyco.pos.module.impl.tender;

import java.sql.SQLException;
import java.util.Scanner;

import com.thecloudyco.override.api.ManagerAPI;
import com.thecloudyco.override.common.OverrideType;
import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.transaction.Tender;
import com.thecloudyco.pos.util.ConsoleUtil;
import com.thecloudyco.pos.util.QuickMessage;
import com.thecloudyco.pos.util.SoundUtils;
import com.thecloudyco.pos.util.TillContentsUtil;

public class CheckTender extends CModule {
	
	public CheckTender() {
		super("PERSONAL CHECK");
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		
		if(Register.access().getTransaction() != null) {
		} else {
			ConsoleUtil.Print("ERROR", "There is no active transaction");
			return;
		}
		
		ConsoleUtil.Print("PLEASE ENTER", "CHECK TENDER AMOUNT");
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
		ManagerAPI mAPI = new ManagerAPI();
		
		// If the tender is larger then $600, we're going to need a Manager Override
		if(tender >= 600.00 || Register.access().getTransaction().getVoidedMoney() >= 50.00) {
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
				
				Register.access().getTransaction().voidLimitCheckBypassed = true;
				
				if(tender < 0.00) {
					ConsoleUtil.Print("ERROR", "TENDER AMOUNT MUST BE POSITIVE");
					return;
				}
				
				// TODO: Process and log the tender
				Register.access().removeBalance(tender);
				Register.access().getTransaction().addTender(new Tender("PERSONAL CHECK", (tender * -1), true));
				try {
					TillContentsUtil.addCheckTillContents(Main.getConfig().getTerminalNumber(), tender);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ConsoleUtil.Print("PERSONAL CHECK PROCESSED", "$" + Register.access().getReadableBalance() + " REMAINING");				
				
				if(Register.access().getBalance() <= 0) {
					// Finish the transaction, and tell the cashier how much change to give the customer
					ConsoleUtil.Print("** THANK YOU **", "** COME AGAIN **");
					System.out.println("CHANGE OWED: " + (Register.access().getReadableBalance() * -1));
					
					if((Register.access().getReadableBalance() * -1) >= 0.00) {
						try {
							TillContentsUtil.addCashTillContents(Main.getConfig().getTerminalNumber(), Register.access().getReadableBalance());
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					//TODO: Print Recipt
					//TODO: Clear the list of items that the customer is purchasing (or just completely reset it)
					Register.access().getTransaction().finish(true);
					Register.access().setBalance(0.00);
					Register.access().voidTransaction();
					return;
				}
			}
		} else {
			
			if(tender < 0.00) {
				ConsoleUtil.Print("ERROR", "TENDER AMOUNT MUST BE POSITIVE");
				return;
			}
			
			// TODO: Process and log the tender
			Register.access().removeBalance(tender);
			Register.access().getTransaction().addTender(new Tender("PERSONAL CHECK", (tender * -1), false));
			try {
				TillContentsUtil.addCheckTillContents(Main.getConfig().getTerminalNumber(), tender);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ConsoleUtil.Print("PERSONAL CHECK PROCESSED", "$" + Register.access().getReadableBalance() + " REMAINING");
			
			
			if(Register.access().getBalance() <= 0) {
				// Finish the transaction, and tell the cashier how much change to give the customer
				ConsoleUtil.Print("** THANK YOU **", "** COME AGAIN **");
				System.out.println("CHANGE OWED: " + (Register.access().getReadableBalance() * -1));
				if((Register.access().getReadableBalance() * -1) >= 0.00) {
					try {
						TillContentsUtil.addCashTillContents(Main.getConfig().getTerminalNumber(), Register.access().getReadableBalance());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//TODO: Print Recipt
				//TODO: Clear the list of items that the customer is purchasing (or just completely reset it)
				Register.access().getTransaction().finish(true);
				Register.access().setBalance(0.00);
				Register.access().voidTransaction();
				
				return;
			}
		}
	}

}
