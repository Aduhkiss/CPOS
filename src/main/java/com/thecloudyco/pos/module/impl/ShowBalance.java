package com.thecloudyco.pos.module.impl;
import java.util.Scanner;

import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.util.StringUtil;

public class ShowBalance extends CModule {
	public ShowBalance() {
		super("SHOW BALANCE", false);
	}
	
	@Override
	public void execute(String[] args, Scanner sc) {
		double bal = Register.access().getReadableBalance();
		//double salesTax = Register.access().getTransaction().getCurrentTaxAmount();
		double salesTax = 0.00;
		if(Register.access().getTransaction() != null) {
			salesTax = Register.access().getTransaction().getCurrentTaxAmount();
		}
		
		if(bal >= 0.00) {
			try {
				System.out.println("TAX: $" + StringUtil.realBalance(String.valueOf(salesTax)));
			} catch(NullPointerException ex) {
				System.out.println("TAX: " + "$0.00");
			}
			System.out.println("BALANCE: $" + StringUtil.realBalance(String.valueOf(bal)));
		} else {
			System.out.println("REFUND: $" + StringUtil.realBalance(String.valueOf(Math.abs(bal))));
		}
		return;
	}
}
