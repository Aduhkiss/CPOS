package com.thecloudyco.printutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;

public class AtticusPrintUtil {
	
	private static String payload = "";
	private static String last_payload = "";
	
	public static void addToQueue(String str) {
		payload = payload + str + System.getProperty("line.separator");
	}
	
	public static void print() throws PrintException, IOException {
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		InputStream is = new ByteArrayInputStream((payload + "").getBytes("UTF-8"));
		PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(1));
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	    Doc doc = new SimpleDoc(is, flavor, null);
	    Doc cut = new SimpleDoc(new ByteArrayInputStream(CutPage()), flavor, null);
	    
	    DocPrintJob job = service.createPrintJob();
	    PrintJobWatcher pjw = new PrintJobWatcher(job);
	    
	    job.print(doc, pras);
	    
	    pjw.waitForDone();
	    is.close();
	    
	    last_payload = payload;
	    payload = "";
	    
	    ByteArrayOutputStream expected = new ByteArrayOutputStream();
	    
	    expected.write(POS.POSPrinter.CutPage());
	    
	    DocPrintJob jobb = service.createPrintJob();
		Doc docc = new SimpleDoc(expected.toByteArray(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
		try {
			jobb.print(docc, new HashPrintRequestAttributeSet());
		} catch (PrintException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void print_R(String transaction) throws PrintException, IOException {
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		InputStream is = new ByteArrayInputStream((payload + "").getBytes("UTF-8"));
		PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(1));
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	    Doc doc = new SimpleDoc(is, flavor, null);
	    Doc cut = new SimpleDoc(new ByteArrayInputStream(CutPage()), flavor, null);
	    
	    DocPrintJob job = service.createPrintJob();
	    PrintJobWatcher pjw = new PrintJobWatcher(job);
	    
	    job.print(doc, pras);
	    
	    pjw.waitForDone();
	    is.close();
	    
	    last_payload = payload;
	    payload = "";
	    
	    ByteArrayOutputStream expected = new ByteArrayOutputStream();
	    
	    expected.write(POS.POSPrinter.Justification(POS.Justifications.Center));
		
		expected.write(POS.POSPrinter.CharSize.DoubleHeight3());
		expected.write(" \n".getBytes());
		
		expected.write(POS.POSPrinter.CharSize.Normal());
		expected.write(POS.POSPrinter.SetStyles(POS.PrintStyle.None));
		
		expected.write(POS.POSPrinter.BarCode.SetBarcodeHeightInDots(300));
		expected.write(POS.POSPrinter.BarCode.SetBarWidth(POS.BarWidth.Thin));
		
		expected.write(POS.POSPrinter.FontSelect.FontA());
		expected.write(POS.POSPrinter.BarCode.PrintBarcode(POS.BarcodeType.CODABAR_NW_7, ("" + transaction)));
		expected.write(("" + "\n").getBytes());
		
		expected.write(POS.POSPrinter.Justification(POS.Justifications.Left));
	    
	    expected.write(POS.POSPrinter.CutPage());
	    
	    DocPrintJob jobb = service.createPrintJob();
		Doc docc = new SimpleDoc(expected.toByteArray(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
		try {
			jobb.print(docc, new HashPrintRequestAttributeSet());
		} catch (PrintException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getPayload() {
		return payload;
	}
	
	public static String getLastPayload() {
		return last_payload;
	}
	
	public static void setPayload(String data) {
		payload = data;
	}
	
	private static byte[] CutPage() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bytes.write((byte) ((char) (0x1D)));
		bytes.write((byte) ('V'));
		bytes.write((byte) 66);
		bytes.write((byte) 3);

		return bytes.toByteArray();
	}
	/*


string ESC = "\u001B";
string GS = "\u001D";
string InitializePrinter = ESC + "@";
string BoldOn = ESC + "E" + "\u0001";
string BoldOff = ESC + "E" + "\0";
string DoubleOn = GS + "!" + "\u0011";  // 2x sized text (double-high + double-wide)
string DoubleOff = GS + "!" + "\0";

printJob.Print(InitializePrinter);
printJob.PrintLine("Here is some normal text.");
printJob.PrintLine(BoldOn + "Here is some bold text." + BoldOff);
printJob.PrintLine(DoubleOn + "Here is some large text." + DoubleOff);

	 */
}
