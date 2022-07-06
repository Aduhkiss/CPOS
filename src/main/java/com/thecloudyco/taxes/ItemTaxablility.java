package com.thecloudyco.taxes;

public enum ItemTaxablility {

    FOODSTAMPABLE("F"), // The Item is FOOD and CANNOT be taxed
    TAXABLE("T"),       // The item is NOT food, and will have taxes applied
    TAX_FOOD("B"),      // The item IS food, but can be taxed

    ANYTHING_ELSE("A"), // The Item is NOT taxed, however is NOT food either
    NONE(" ");          // The default value of an item if its not programmed by the controller

    String letter;

    ItemTaxablility(String letter) {
        this.letter = letter;
    }

    public String getLetter() {
        return letter;
    }
}
