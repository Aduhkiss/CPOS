package com.thecloudyco.pos.module;

import java.util.HashMap;
import java.util.Map;

import com.thecloudyco.pos.module.impl.SecureTerminal;
import com.thecloudyco.pos.module.impl.ShowBalance;
import com.thecloudyco.pos.module.impl.ShowTransaction;
import com.thecloudyco.pos.module.impl.Signout;
import com.thecloudyco.pos.module.impl.VoidItemRemake;
import com.thecloudyco.pos.module.impl.VoidTotal;
import com.thecloudyco.pos.module.impl.nosale.ActiveSessions;
import com.thecloudyco.pos.module.impl.nosale.CashPickup;
import com.thecloudyco.pos.module.impl.nosale.CheckNoteReport;
import com.thecloudyco.pos.module.impl.nosale.PrintActiveSessions;
import com.thecloudyco.pos.module.impl.nosale.PrintManagerOverride;
import com.thecloudyco.pos.module.impl.nosale.PrintManagerQR;
import com.thecloudyco.pos.module.impl.nosale.ReprintTrans;
import com.thecloudyco.pos.module.impl.nosale.TillContentsReport;
import com.thecloudyco.pos.module.impl.tender.*;

public class ModuleManager {
	private static Map<String, CModule> Modules = new HashMap<>();
	
	public static void registerModules() {
		// Called from the main class when we start everything up
		// this method will be where we add everything to the hashmap for the program to access
		
		// Numeric Modules
		Modules.put("", new ShowBalance());
		Modules.put("NS/DT", new ShowTransaction());
		//Modules.put("0001", new OpenGUI());
		
		// uhh.. not numeric modules
		Modules.put("VOIDTOTAL", new VoidTotal());
		// fuck this bullshit it doesnt fucking work
		//Modules.put("VOID", new VoidItem());
		Modules.put("VOID", new VoidItemRemake());
		Modules.put("SIGNOUT", new Signout());
		Modules.put("SECURE", new SecureTerminal());
		Modules.put("PRINTMO", new PrintManagerOverride());
		Modules.put("PRINTMOQR", new PrintManagerQR());
		
		// Tender Modules
		Modules.put("T/CASH", new CashTender());
		Modules.put("T/CHECK", new CheckTender());
		Modules.put("T/ONLINE", new OnlinePayTender());
		Modules.put("T/BTC", new BTCTender());
		Modules.put("T/EFT", new ElectronicFundsTransferTender());
		
		// Non-Sale Utility Modules
		Modules.put("NS/CN", new CheckNoteReport());
		Modules.put("NS/TCR", new TillContentsReport());
		Modules.put("NS/RT", new ReprintTrans());
		Modules.put("NS/CP", new CashPickup());
		Modules.put("NS/AC", new ActiveSessions());
		Modules.put("NS/PAC", new PrintActiveSessions());
	}
	
	public static CModule getModule(String id) {
		if(Modules.get(id) != null) {
			return Modules.get(id);
		}
		return null;
	}
}