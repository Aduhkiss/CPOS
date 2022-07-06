package com.thecloudyco.pos.transaction;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.print.PrintException;

import com.google.gson.Gson;
import com.thecloudyco.override.api.ManagerAPI;
import com.thecloudyco.override.common.OverrideType;
import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.barcode.AtticusBarcode;
import com.thecloudyco.pos.items.Item;
import com.thecloudyco.pos.user.Operator;
import com.thecloudyco.pos.util.ConsoleUtil;
import com.thecloudyco.pos.util.SoundUtils;
import com.thecloudyco.pos.util.StringUtil;
import com.thecloudyco.pos.util.TillContentsUtil;
import com.thecloudyco.printutil.AtticusPrintUtil;

public class Transaction {
	
	private Operator operator;
	private String id;
	private List<Item> Items;
	private List<Tender> Tenders;
	
	private BufferedImage barcodeImage;
	
	public boolean voidLimitCheckBypassed;
	
	private double voidedMoney;
	private double currentTaxAmount = 0.00;
	
	public Transaction(Operator operator) {
		this.operator = operator;
		this.Items = new ArrayList<>();
		this.Tenders = new ArrayList<>();
	}
	
	public List<Tender> getTenders() {
		return Tenders;
	}
	
	public void addTender(Tender t) {
		Tenders.add(t);
	}

	public Operator getOperator() {
		return operator;
	}

	public List<Item> getItems() {
		return Items;
	}
	
	public void addItem(Item item) {
		
		Items.add(item);

		// Check if the item is taxed
		double taxAmount = 0.00;
		if(item.getTaxPlan().equals("A"))
			taxAmount = Register.access().TaxPlanA;
		if(item.getTaxPlan().equals("B"))
			taxAmount = Register.access().TaxPlanB;
		if(item.getTaxPlan().equals("C"))
			taxAmount = Register.access().TaxPlanC;
		if(item.getTaxPlan().equals("D"))
			taxAmount = Register.access().TaxPlanD;

		double salesTax = ((item.getMyPrice() * (taxAmount / 100)) * 100) / 100.0;
		currentTaxAmount = currentTaxAmount + salesTax;
		Register.access().addBalance(salesTax);
	}
	
	public void voidItem(Item item) {
		Items.remove(item);
		voidedMoney = voidedMoney + item.getMyPrice();
	}
	
	public double getVoidedMoney() {
		return voidedMoney;
	}

	public double getCurrentTaxAmount() {
		return currentTaxAmount;
	}
	
	public void finish(boolean success) {
		
		// Gets called when the transaction is finished
		Gson gson = new Gson();
		String item_json = gson.toJson(Items);
		String tender_json = gson.toJson(Tenders);
		
		// Check to see if we've voided too much money
		if(voidedMoney >= 60.00 && (voidLimitCheckBypassed != true)) {
			SoundUtils.beep();
			
			ConsoleUtil.Print("MO", "VOID TRANSACTION LIMIT CHECK");
			System.out.println("\n");
			Scanner sc = new Scanner(System.in);
			
			String override = sc.nextLine();
			ManagerAPI mAPI = new ManagerAPI();
			
			boolean flag = false;
			try {
				flag = mAPI.isAuthorized(OverrideType.CASH_OFFICE_FES, override);
			} catch (Exception e) {}
			
			if(!flag) {
				ConsoleUtil.Print("ERROR", "Not Authorized");
				return;
			} else {
				try {
					run(success, item_json, tender_json);
				} catch (PrintException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				voidLimitCheckBypassed = true;
			}
		} else {
			try {
				run(success, item_json, tender_json);
			} catch (PrintException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	String ESC = "\u001B";
	String BoldOn = ESC + "E" + "\u0001";
	String BoldOff = ESC + "E" + "\0";
	
	private void run(boolean success, String item_json, String tender_json) throws PrintException, IOException {
		StringBuilder tId = new StringBuilder();
		for(int i = 0; i < 10; i++) {
			tId.append((int)(Math.random() * (9 - 0 + 1) + 0));
		}
		
		AtticusBarcode transBarcode = new AtticusBarcode("44532" + tId.toString());

		AtticusPrintUtil.addToQueue(Main.CON_RECIPT_HEADER);
		AtticusPrintUtil.addToQueue("  YOUR CASHIER WAS " + operator.getFirstName());
		AtticusPrintUtil.addToQueue("*******************");
		AtticusPrintUtil.addToQueue("");
		
		double price = 0.00;
		
		for(Item i : getItems()) {
			String mo = "";
			if(i.isRestricted() == 1) {
				mo = BoldOn + "MO   ";
			}
			if(i.isQuantity() == 1) {
				AtticusPrintUtil.addToQueue("[" + i.getUpc() + "] (" + i.getQuantityBuying() + " @ " + i.getPriceBuying() + ") ");
				AtticusPrintUtil.addToQueue(mo + "" + i.getName() + " | " + BoldOff + i.getTaxStatus().getLetter());
				AtticusPrintUtil.addToQueue("$" + StringUtil.realBalance(String.valueOf(i.getMyPrice())) + "");
			}
			else if(i.isWeight() == 1) {
				AtticusPrintUtil.addToQueue("[" + i.getUpc() + "] (" + i.getWeightBuying() + " lb @ " + i.getPriceBuying() + ") ");
				AtticusPrintUtil.addToQueue(mo + "" + i.getName() + " | " + BoldOff + i.getTaxStatus().getLetter());
				AtticusPrintUtil.addToQueue("$" + StringUtil.realBalance(String.valueOf(i.getMyPrice())) + "");
			} else {
				AtticusPrintUtil.addToQueue("[" + i.getUpc() + "] ");
				AtticusPrintUtil.addToQueue(mo + "" + i.getName() + " | " + BoldOff + i.getTaxStatus().getLetter());
				AtticusPrintUtil.addToQueue("$" + StringUtil.realBalance(String.valueOf(i.getMyPrice())) + "");
			}
			price = price + i.getMyPrice();
		}
		
		DecimalFormat df = new DecimalFormat("##.##");
		
		AtticusPrintUtil.addToQueue("");
		AtticusPrintUtil.addToQueue("  TAX: " + df.format(getCurrentTaxAmount()));
		AtticusPrintUtil.addToQueue("  BALANCE: " + df.format(price + getCurrentTaxAmount()));
		
		for(Tender t : getTenders()) {
			if(t.mo) {
				AtticusPrintUtil.addToQueue(BoldOn + "  [MO " + t.getType().toUpperCase() + "] " + BoldOff);
			} else { AtticusPrintUtil.addToQueue("  [" + t.getType().toUpperCase() + "] "); }
			AtticusPrintUtil.addToQueue("   $" + t.getAmount());
			AtticusPrintUtil.addToQueue("  ");
		}
		
		AtticusPrintUtil.addToQueue(""); // Change this kinda thing to Math.abs() to get the absolute value of the number
		AtticusPrintUtil.addToQueue("  CHANGE: $" + Math.abs(Register.access().getReadableBalance()));
		
		// Print transaction and operator information
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy   HH:mm:ss");  
	    Date date = new Date();
		
		AtticusPrintUtil.addToQueue("*******************");
		AtticusPrintUtil.addToQueue("  " + formatter.format(date));
		AtticusPrintUtil.addToQueue("  " + operator.getOperatorId() + "   " + Main.getConfig().getStoreNumber() + "   " + Main.getConfig().getTerminalNumber());
		AtticusPrintUtil.addToQueue("*******************");

		double till = 0.00;
		try {
			till = TillContentsUtil.getCashTillContents(Main.getConfig().getTerminalNumber());
			if(till >= 1200.00) {
				System.out.println("CASH PICKUP");
				System.out.println("NEEDED SOON.");
				System.out.println("");
//				System.out.println("PLEASE ALERT A PIC");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(!success) {
			if(voidLimitCheckBypassed) {
				AtticusPrintUtil.addToQueue(BoldOn + "** MO  VOID TRANSACTION **" + BoldOff);
			} else {
				AtticusPrintUtil.addToQueue(BoldOn + "** VOID TRANSACTION **" + BoldOff);
			}
		} else {
			AtticusPrintUtil.addToQueue("*******************");
			AtticusPrintUtil.addToQueue("  SAVE THIS RECEIPT FOR ");
			AtticusPrintUtil.addToQueue(  "REFUNDS OR ADJUSTMENTS!");
			AtticusPrintUtil.addToQueue("  ");
			AtticusPrintUtil.addToQueue("  THANK YOU!");
			AtticusPrintUtil.addToQueue("*******************");
			AtticusPrintUtil.addToQueue("TRANSACTION ID: ");
			AtticusPrintUtil.addToQueue("44532" + tId.toString());
			AtticusPrintUtil.addToQueue("*******************");
		}
		
		// Create the barcode
//		
//		try {
//			Code128Bean code128 = new Code128Bean();
//			code128.setHeight(15f);
//			code128.setModuleWidth(0.3);
//			code128.setQuietZone(10);
//			code128.doQuietZone(true);
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
//			//code128.generateBarcode(canvas, "44532" + tId.toString());
//			canvas.finish();
//			
//			barcodeImage = canvas.getBufferedImage();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
		
		AtticusPrintUtil.addToQueue("              ");
		AtticusPrintUtil.addToQueue("              ");
		AtticusPrintUtil.addToQueue("              ");
		AtticusPrintUtil.addToQueue("              ");
		
		if(success) {
			String reciept = AtticusPrintUtil.getPayload();
			
			
			// Upload the reciept to the database
			try {
				Main.getDB().update("INSERT INTO `receipts` (`transaction`, `receipt`) VALUES ('" + "44532" + tId.toString() + "', '" + reciept + "');");
			} catch (SQLException e) {
			}
		}
		
		//new PrintActionListener(transBarcode.getImage());
		
		// Print the fucker
		AtticusPrintUtil.print_R("44532" + tId.toString());
		
//		if(success) {
//			// Also print the barcode for the transaction ID
//			PrinterJob printJob = PrinterJob.getPrinterJob();
//			printJob.setPrintable(new Printable() {
//			        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
//			                if (pageIndex != 0) {
//			                    return NO_SUCH_PAGE;
//			                }
//			                graphics.drawImage(barcodeImage, 0, 0, barcodeImage.getWidth(), barcodeImage.getHeight(), null);
//			                return PAGE_EXISTS;
//			        }
//			});     
//			try {
//			    printJob.print();
//			} catch (PrinterException e1) {             
//			    e1.printStackTrace();
//			}
//		}
	}
	
	public double getFinalPrice() {
		double p = 0.00;
		for(Item i : getItems()) {
			p = p + i.getMyPrice();
		}
		return p;
	}
	
	public boolean exists(String UPC) {
		for(Item item : Items) {
			if(item.getUpc().equals(UPC)) {
				return true;
			}
		}
		return false;
	}
}
