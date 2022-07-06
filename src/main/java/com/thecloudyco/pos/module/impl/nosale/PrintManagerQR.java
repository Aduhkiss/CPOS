package com.thecloudyco.pos.module.impl.nosale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;

import com.thecloudyco.pos.Main;
import com.thecloudyco.printutil.*;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.user.Operator;

public class PrintManagerQR extends CModule {
	
	public PrintManagerQR() {
		super("MANAGER OVERRIDE QR", false);
	}

	@Override
	public void execute(String[] args, Scanner sc) {
		//TODO: Ask for operator ID and password to print that user
		
		
		// Calculate the expiration time (4 Hours Active Override time)
		long expires = System.currentTimeMillis() + (3600000 * 10);
		Operator operator = Register.access().getLoggedIn();
		
		//System.out.println("TESTING: Override '" + ("999002" + operator.getOperatorId() + "" + expires) + "'");
		//System.out.println("INSERT INTO `paper_overrides` (`operator_id`, `override_number`, `level`, `expiration_time`) VALUES ('" + operator.getOperatorId() + "', '" + ("999002" + operator.getOperatorId() + "" + expires) + "', 0, '" + expires + "');");
		
		try {
			Main.getDB().update("INSERT INTO `paper_overrides` (`operator_id`, `override_number`, `level`, `expiration_time`) VALUES ('" + operator.getOperatorId() + "', '" + ("999002" + operator.getOperatorId() + "" + expires) + "', 0, '" + expires + "');");
		} catch (SQLException e) {
			e.printStackTrace();
			// What do we do if this code fails??
		}
		
		DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
		PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
		patts.add(Sides.DUPLEX);

		PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor, patts);
		if (ps.length == 0) {
			throw new IllegalStateException("No Printer found");
		}

		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		PrintService myService = PrintServiceLookup.lookupDefaultPrintService();
		
		if (myService == null) {
			throw new IllegalStateException("Printer not found");
		}
		
		ByteArrayOutputStream expected = new ByteArrayOutputStream();
		
		try {
			expected.write(POS.POSPrinter.Justification(POS.Justifications.Center));
			
			expected.write(POS.POSPrinter.CharSize.DoubleHeight3());
			expected.write("Dynamic Manager Override\n".getBytes());
			
			expected.write(POS.POSPrinter.CharSize.Normal());
			expected.write(POS.POSPrinter.SetStyles(POS.PrintStyle.None));
			
//			expected.write(POS.POSPrinter.BarCode.SetBarcodeHeightInDots(600));
//			expected.write(POS.POSPrinter.BarCode.SetBarWidth(POS.BarWidth.Thinnest));
//			expected.write(POS.POSPrinter.FontSelect.FontA());// ("999002" + operator.getOperatorId() + "" + expires)
//			expected.write(POS.POSPrinter.BarCode.PrintBarcode(POS.BarcodeType.ITF, ("999002" + operator.getOperatorId() + "" + expires)));
			
			expected.write(POS.POSPrinter.QrCode.Print(("999002" + operator.getOperatorId() + "" + expires)));
			expected.write((operator.getFullName() + "\n").getBytes());
			expected.write(POS.POSPrinter.Justification(POS.Justifications.Left));
			
			expected.write(POS.POSPrinter.CutPage());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		DocPrintJob job = myService.createPrintJob();
		Doc doc = new SimpleDoc(expected.toByteArray(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
		try {
			job.print(doc, new HashPrintRequestAttributeSet());
		} catch (PrintException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
