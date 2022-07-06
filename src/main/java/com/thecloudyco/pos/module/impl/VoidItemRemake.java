package com.thecloudyco.pos.module.impl;

import java.util.ConcurrentModificationException;
import java.util.Scanner;

import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.items.Item;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.util.ConsoleUtil;
import com.thecloudyco.pos.util.StringUtil;

public class VoidItemRemake extends CModule {
	
	public VoidItemRemake() {
		super("VOID ITEM REMAKE");
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		ConsoleUtil.Print("PLEASE SCAN OR KEY", "ITEM TO BE VOIDED");
		System.out.println("\n");
		String item_upc = sc.nextLine();
		
		if(item_upc == null || item_upc.equals("")) {
			ConsoleUtil.Print("ERR", "NO ITEM FOUND");
			System.out.println("\n");
			return;
		}
		
		boolean found_it = false;
		
		try {
			for(Item it : Register.access().getTransaction().getItems()) {
				if(it.getUpc().equals(StringUtil.removeLeadingZeroes(item_upc))) {
					found_it = true;
					// Remove this item from the transaction entirely
					
					Register.access().getTransaction().voidItem(it);
					Register.access().removeBalance(it.getMyPrice());
					//System.out.println("[DEBUG] Removed: " + it.getMyPrice() + " from balance.");

					double taxAmount = 0.00;
					if(it.getTaxPlan().equals("A"))
						taxAmount = Register.access().TaxPlanA;
					if(it.getTaxPlan().equals("B"))
						taxAmount = Register.access().TaxPlanB;
					if(it.getTaxPlan().equals("C"))
						taxAmount = Register.access().TaxPlanC;
					if(it.getTaxPlan().equals("D"))
						taxAmount = Register.access().TaxPlanD;

					double salesTax = ((it.getMyPrice() * (taxAmount / 100)) * 100) / 100.0;
					Register.access().removeBalance(salesTax);
					
					System.out.println("[VOID] " + it.getName() + " | -$" + it.getMyPrice());
				}
			}
		} catch(ConcurrentModificationException ex) {}
		catch(NullPointerException ex) {
			ConsoleUtil.Print("", "NO TRANSACTION FOUND");
			System.out.println("\n");
		}
		
		if(!found_it) {
			ConsoleUtil.Print("", "NO ITEM FOUND");
			System.out.println("\n");
		}
		
		return;
	}

}
