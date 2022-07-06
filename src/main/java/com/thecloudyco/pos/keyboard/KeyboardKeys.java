package com.thecloudyco.pos.keyboard;

public enum KeyboardKeys {
	
	REFUND(""),
	SMARTCARD_WIC(""),
	WIC(""),
	FS_CVV(""),
	MISC(""),
	CASH(""),
	ONLINE_PAY(""),
	CHECK(""),
	EFT(""),
	TOTAL(""),
	SUSPEND_RETRIEVE(""),
	CLEAR(""),
	NO_SALE(""),
	
	SIGN_OUT_IN(""),
	OVERRIDE(""),
	
	VOID(""),
	FS_NO_FS(""),
	POSTAGE_STAMPS_BOOK(""),
	PRICE_OVERRIDE(""),
	TAX_NO_TAX(""),
	QUANTITY(""),
	DISCOUNT_TAX_EXEMPT(""),
	PICKUP_MERCH(""),
	ENTER(""),
	ALT_ID(""),
	GIFT_RECEIPT(""),
	SPECIAL_PRINT(""),
	MFR_COUPON(""),
	GIFT_ITEM(""),
	SKU("");
	
	private String key;
	
	KeyboardKeys(String key) {
		this.key = key;
	}
}
