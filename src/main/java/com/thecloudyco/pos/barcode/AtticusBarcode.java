package com.thecloudyco.pos.barcode;

import java.awt.image.BufferedImage;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;

public class AtticusBarcode {
	
	private String data;
	private Barcode barcode;
	private BufferedImage image;
	
	public AtticusBarcode(String data) {
		this.data = data;
		try {
			barcode = BarcodeFactory.createCode128(data);
		} catch (BarcodeException e) {
			e.printStackTrace();
		}
		try {
			this.image = BarcodeImageHandler.getImage(barcode);
		} catch (OutputException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public String getData() {
		return data;
	}
}
