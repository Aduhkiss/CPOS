package com.thecloudyco.override.common;

public enum OverrideType {
	
	TRAINING 		  (1, "Price Verify / Training"),
	CASHIER 		  (2, "Cashier"),
	FUEL_CENTER 	  (3, "Fuel Center"),
	CSD      		  (4, "Customer Service Desk Associate"),
	FILE_MAINT 		  (5, "File Maintanance"),
	CASH_OFFICE_FES   (6, "Cash Office / Front End Supervisor"),
	FE_MGMT_STR_MGMT  (7, "Front End / Store Management"),
	DIVISIONAL_MGMT	  (8, "Divisional Management"),
	MAIN_OFFICE		  (9, "Main Office Use");
	
	private int power;
	private String name;
	
	OverrideType(int power, String name) {
		this.power = power;
		this.name = name;
	}
	
	public int getPower() {
		return power;
	}
	
	public String getName() {
		return name;
	}
	
	public static OverrideType getType(int power) {
		for(OverrideType ot : OverrideType.values()) {
			if(ot.getPower() == power) {
				return ot;
			}
		}
		return null;
	}
}
 