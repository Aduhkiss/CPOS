package com.thecloudyco.pos.module;

import java.util.Scanner;

public abstract class CTender {
	
	private String name;
	private boolean mgrOverrideNeeded;
	
	public CTender(String name) {
		this.name = name;
		this.mgrOverrideNeeded = false;
	}
	
	public CTender(String name, boolean mgrOverrideNeeded) {
		this.name = name;
		this.mgrOverrideNeeded = mgrOverrideNeeded;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean requiresMgrOverride() {
		return mgrOverrideNeeded;
	}
	
	public abstract void execute(String[] args, Scanner sc);
}
