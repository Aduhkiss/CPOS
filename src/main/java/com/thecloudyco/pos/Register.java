package com.thecloudyco.pos;

import com.thecloudyco.pos.transaction.Transaction;
import com.thecloudyco.pos.user.Operator;
import com.thecloudyco.pos.util.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Register {
	public static Register me;
	
	public static Register access() {
		if(me == null) {
			me = new Register();
		}
		return me;
	}
	
	// All the variables that we need to access in other parts of the program
	private double BALANCE;
	public Operator loggedInOperator;
	private Transaction transaction;
	private double tillContents;

	// Tax plans
	public double TaxPlanA = 0.00;
	public double TaxPlanB = 0.00;
	public double TaxPlanC = 0.00;
	public double TaxPlanD = 0.00;

	public Register() {
	}
	
	public Transaction getTransaction() {
		return transaction;
	}
	
	public void createTransaction() {
		transaction = new Transaction(loggedInOperator);
	}
	
	public void voidTransaction() {
		transaction = null;
	}
	
	public double getBalance() {
		return BALANCE;
	}
	
	public double getTillContents() {
		return tillContents;
	}
	
	public void setTillContents(double a) {
		tillContents = a;
	}
	
	public double getReadableBalance() {
		return Double.valueOf(StringUtil.realBalance(String.valueOf(BALANCE)));
	}
	
	public void removeBalance(double money) {
		BALANCE = BALANCE - money;
	}
	
	public void addBalance(double money) {
		BALANCE = BALANCE + money;
	}
	
	public void addBalance(String money) {
		BALANCE = BALANCE + Double.valueOf(money);
	}
	
	public void setBalance(double money) {
		BALANCE = money;
	}
	
	public void setLoggedIn(Operator operator) {
		loggedInOperator = operator;
	}
	
	public boolean isLoggedIn() {
		if(loggedInOperator != null) {
			return true;
		}
		return false;
	}
	
	public Operator getLoggedIn() {
		return loggedInOperator;
	}
}
