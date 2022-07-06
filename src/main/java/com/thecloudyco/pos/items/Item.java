package com.thecloudyco.pos.items;

import com.thecloudyco.taxes.ItemTaxablility;

public class Item {
	
	private String upc;
	private String name;
	private double price;
	private int custom_price;
	private int weight;
	private int quantity;
	private double price_per_pound;
	private int transaction_limit;
	private int restricted;
	private int minimumLevelNeeded;

	private String taxPlan;
	private ItemTaxablility taxStatus;
	
	private double myPrice;
	private int quantityBuying;
	private double weightBuying;
	private double priceBuying;
	
	public Item(String upc, String name, double price, int custom_price, int weight, int quantity, double price_per_pound, int transaction_limit, int restricted, int minimumLevelNeeded, String taxPlan, String taxStatus) {
		this.upc = upc;
		this.name = name;
		this.price = price;
		this.custom_price = custom_price;
		this.weight = weight;
		this.quantity = quantity;
		this.price_per_pound = price_per_pound;
		this.transaction_limit = transaction_limit;
		this.restricted = restricted;
		this.minimumLevelNeeded = minimumLevelNeeded;

		this.taxPlan = taxPlan;
		try {
			this.taxStatus = ItemTaxablility.valueOf(taxStatus);
		} catch(IllegalArgumentException ex) {
			this.taxStatus = ItemTaxablility.NONE;
		}
	}

	@Deprecated
//	public Item(String upc, String name, double price, int custom_price, int weight, int quantity, double price_per_pound, int transaction_limit, boolean restricted, int minimumLevelNeeded) {
//		this.upc = upc;
//		this.name = name;
//		this.price = price;
//		this.custom_price = custom_price;
//		this.weight = weight;
//		this.quantity = quantity;
//		this.price_per_pound = price_per_pound;
//		this.transaction_limit = transaction_limit;
//		if(restricted) {
//			this.restricted = 1;
//		} else {
//			this.restricted = 0;
//		}
//		this.minimumLevelNeeded = minimumLevelNeeded;
//	}
	
	public String getUpc() {
		return upc;
	}
	public String getName() {
		return name;
	}
	public double getPrice() {
		return price;
	}
	public int isCustom_price() {
		return custom_price;
	}
	public int isWeight() {
		return weight;
	}
	public int isQuantity() {
		return quantity;
	}
	public double getPrice_per_pound() {
		return price_per_pound;
	}
	public int getTransaction_limit() {
		return transaction_limit;
	}
	public int isRestricted() {
		return restricted;
	}
	public void setMyPrice(double a) {
		myPrice = a;
	}
	public void setMyPrice(String a) {
		myPrice = Double.valueOf(a);
	}
	public double getMyPrice() {
		return myPrice;
	}
	public void setQuantityBuying(int a, double price) {
		quantityBuying = a;
		priceBuying = price;
	}
	public void setWeightBuying(double a, double price) {
		weightBuying = a;
		priceBuying = price;
	}
	public int getQuantityBuying() {
		return quantityBuying;
	}
	public double getPriceBuying() {
		return priceBuying;
	}
	public double getWeightBuying() {
		return weightBuying;
	}
	public void setRestricted(int i) {
		restricted = i;
	}
	public void setMinimumLevelNeeded(int a) {
		minimumLevelNeeded = a;
	}
	public int getMinimumLevelNeeded() {
		return minimumLevelNeeded;
	}
	public String getTaxPlan() {
		return taxPlan;
	}
	public ItemTaxablility getTaxStatus() {
		return taxStatus;
	}
}
