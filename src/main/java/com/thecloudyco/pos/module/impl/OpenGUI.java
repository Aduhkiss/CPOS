package com.thecloudyco.pos.module.impl;

import java.util.Scanner;

import com.thecloudyco.pos.gui.util.POSGui;
import com.thecloudyco.pos.module.CModule;

public class OpenGUI extends CModule {
	public OpenGUI() {
		super("OPEN TERMINAL GUI", true);
	}
	
	@Override
	public void execute(String[] args, Scanner sc) {
		POSGui.main();
	}
}
