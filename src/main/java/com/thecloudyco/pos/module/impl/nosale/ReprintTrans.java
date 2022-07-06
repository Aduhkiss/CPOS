package com.thecloudyco.pos.module.impl.nosale;

import java.io.IOException;
import java.util.Scanner;

import javax.print.PrintException;

import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.printutil.AtticusPrintUtil;

public class ReprintTrans extends CModule {
	
	public ReprintTrans() {
		super("REPRINT LAST TRANSACTION");
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		AtticusPrintUtil.setPayload(AtticusPrintUtil.getLastPayload());
		try {
			AtticusPrintUtil.print();
		} catch (PrintException | IOException e) {
		}
		return;
	}

}
