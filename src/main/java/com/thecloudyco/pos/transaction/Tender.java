package com.thecloudyco.pos.transaction;

public class Tender {
	
	public String type;
	public double amount;
	public boolean mo;
	
	public Tender(String type, double amount, boolean neededMO) {
		this.mo = neededMO;
		this.type = type;
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public double getAmount() {
		return amount;
	}
	
	public void neededMO(boolean v) {
		mo = v;
	}
}
