package com.thecloudyco.pos.module;

import java.util.Scanner;

public abstract class CModule {
	
	protected String name;
	protected boolean mgrOverrideNeeded;
	protected double tenderLimitCheck;
	
	public CModule(String name) {
		this.name = name;
		this.mgrOverrideNeeded = false;
	}
	
	public CModule(String name, boolean mgrOverrideNeeded) {
		this.name = name;
		this.mgrOverrideNeeded = mgrOverrideNeeded;
	}
	
	public CModule(String name, double tenderLimitCheck) {
		this.name = name;
		this.mgrOverrideNeeded = false;
		this.tenderLimitCheck = tenderLimitCheck;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean requiresMgrOverride() {
		return mgrOverrideNeeded;
	}
	
	public double getTenderLimitCheck() {
		return tenderLimitCheck;
	}
	
	public abstract void execute(String[] args, Scanner sc);
}
