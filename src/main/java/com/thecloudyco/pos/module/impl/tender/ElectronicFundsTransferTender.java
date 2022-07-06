package com.thecloudyco.pos.module.impl.tender;


import java.sql.SQLException;
import java.util.Scanner;

import com.stripe.exception.StripeException;
import com.thecloudyco.override.api.ManagerAPI;
import com.thecloudyco.override.common.OverrideType;
import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.stripe.StripeHelper;
import com.thecloudyco.pos.transaction.Tender;
import com.thecloudyco.pos.util.ConsoleUtil;
import com.thecloudyco.pos.util.PasswordUtil;
import com.thecloudyco.pos.util.QuickMessage;
import com.thecloudyco.pos.util.TillContentsUtil;

public class ElectronicFundsTransferTender extends CModule {

    public ElectronicFundsTransferTender() {
        super("CREDIT/DEBIT");
    }

    @Override
    public void execute(String[] args, Scanner sc) {

        if(Register.access().getTransaction() != null) {
        } else {
            ConsoleUtil.Print("ERR", "There is no active transaction");
            return;
        }

        ConsoleUtil.Print("PLEASE ENTER", "TENDER AMOUNT");
        double tender = 0.00;
        try {
            tender = Double.valueOf(sc.nextLine());
        } catch(IllegalArgumentException ex) {
            tender = Register.access().getTransaction().getFinalPrice();
            // Quick bug fix while im looking through this code
            if(tender < 0.00) {
                tender = 0.00;
            }
        }
        ManagerAPI mAPI = new ManagerAPI();

        // Attempt to charge the card
        String account_number = PasswordUtil.getPassword("ENTER ACCOUNT NUMBER       OR CLEAR TO CANCEL");
        if(account_number.equals("0")) {  System.out.println("Cancelled."); return; }
        String expiration = PasswordUtil.getText("ENTER EXPIRE DATE        OR CLEAR TO CANCEL");
        if(expiration.equals("0")) {  System.out.println("Cancelled."); return; } // Because expiration dates can have 0's in them

        String security_code = PasswordUtil.getPassword("ENTER SECURITY CODE      OR CLEAR TO CANCEL");
        if(security_code.equals("0")) {  System.out.println("Cancelled."); return; }

        try {
            StripeHelper.get().chargeCard((int) tender);
        } catch (StripeException e) {
            System.out.println(e.getMessage());
        }
    }

}
