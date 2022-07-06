package com.thecloudyco.pos.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StripeHelper {

    /***
     * StripeHelper
     * @Author Atticus Zambrana
     *
     * Easy to use API for interfacing with the Stripe API
     * Helpful when writing other point of sale functions
     */

    public static StripeHelper me;
    public static StripeHelper get() {
        if(me == null) {
            me = new StripeHelper();
        }
        return me;
    }

    public StripeHelper() {
        Stripe.apiKey = "sk_test_Bs9Ch6TpuBMsDSSIU2sPjgjQ";
        // Secret Key   sk_test_Bs9Ch6TpuBMsDSSIU2sPjgjQ
    }

    public void chargeCard(int amount) throws StripeException {
        List<Object> paymentMethodTypes =
                new ArrayList<>();
        paymentMethodTypes.add("card");
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", "usd");
        params.put(
                "payment_method_types",
                paymentMethodTypes
        );

        PaymentIntent paymentIntent = PaymentIntent.create(params);
    }
}
