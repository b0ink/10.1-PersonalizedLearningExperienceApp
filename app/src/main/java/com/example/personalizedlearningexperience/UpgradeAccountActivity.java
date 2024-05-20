package com.example.personalizedlearningexperience;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

//import com.paypal.android.corepayments.CoreConfig;
//import com.paypal.android.corepayments.Environment;
//import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutClient;
//

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Locale;

import com.example.personalizedlearningexperience.PaymentsUtil;
import com.google.android.gms.wallet.button.ButtonOptions;
import com.google.android.gms.wallet.button.PayButton;
import com.google.android.gms.wallet.contract.TaskResultContracts;

public class UpgradeAccountActivity extends AppCompatActivity {


    private CheckoutViewModel model;
    public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;

    private PayButton btnPurchaseStarter;
    private PayButton btnPurchaseIntermediate;
    private PayButton btnPurchaseAdvanced;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


//        startService(new Intent(this, PayPalService.class).putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config));

        setContentView(R.layout.activity_upgrade_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnPurchaseStarter = findViewById(R.id.btnPurchaseStarter);
        btnPurchaseIntermediate = findViewById(R.id.btnPurchaseIntermediate);
        btnPurchaseAdvanced = findViewById(R.id.btnPurchaseAdvanced);

        initializeUi(btnPurchaseStarter, 5);
        initializeUi(btnPurchaseIntermediate, 10);
        initializeUi(btnPurchaseAdvanced, 15);

        // Check Google Pay availability
        model = new ViewModelProvider(this).get(CheckoutViewModel.class);
        model.canUseGooglePay.observe(this, this::setGooglePayAvailable);

        ((ImageButton) findViewById(R.id.btnGoBack)).setOnClickListener(view -> {
            finish();
        });

    }

    /**
     * If isReadyToPay returned {@code true}, show the button and hide the "checking" text.
     * Otherwise, notify the user that Google Pay is not available. Please adjust to fit in with
     * your current user flow. You are not required to explicitly let the user know if isReadyToPay
     * returns {@code false}.
     *
     * @param available isReadyToPay API response.
     */
    private void setGooglePayAvailable(boolean available) {
        if (available) {
            btnPurchaseStarter.setVisibility(View.VISIBLE);
            btnPurchaseIntermediate.setVisibility(View.VISIBLE);
            btnPurchaseAdvanced.setVisibility(View.VISIBLE);
        } else {
//            Toast.makeText(this, R.string.google_pay_status_unavailable, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "gpay unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private void processPayment(String title, String amount) {
//        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount), "USD", title, PayPalPayment.PAYMENT_INTENT_SALE);
//        Intent intent = new Intent(this, PaymentActivity.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
//        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

//        PayPalNativeCheckoutClient payPalNativeCheckoutClient = new PayPalNativeCheckoutClient(getApplication(), config, "returnurlz")


    }


    private void initializeUi(PayButton button, int price) {
        // The Google Pay button is a layout file – take the root view
        try {
            button.initialize(
                    ButtonOptions.newBuilder()
                            .setAllowedPaymentMethods(PaymentsUtil.getAllowedPaymentMethods().toString()).build()
            );
            button.setOnClickListener(view -> {
                requestPayment(view, price);
            });
        } catch (JSONException e) {
            // Keep Google Pay button hidden (consider logging this to your app analytics service)
        }
    }

//    public Task<PaymentData> getLoadPaymentDataTask(final long priceCents) {
//        JSONObject paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents);
//        if (paymentDataRequestJson == null) {
//            return null;
//        }
//
//        PaymentDataRequest request =
//                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());
//        return paymentsClient.loadPaymentData(request);
//    }


    private final ActivityResultLauncher<Task<PaymentData>> paymentDataLauncher =
            registerForActivityResult(new TaskResultContracts.GetPaymentDataResult(), result -> {
                int statusCode = result.getStatus().getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.SUCCESS:
                        handlePaymentSuccess(result.getResult());
                        break;
                    //case CommonStatusCodes.CANCELED: The user canceled
                    case AutoResolveHelper.RESULT_ERROR:
                        handleError(statusCode, result.getStatus().getStatusMessage());
                        break;
                    case CommonStatusCodes.INTERNAL_ERROR:
                        handleError(statusCode, "Unexpected non API" +
                                " exception when trying to deliver the task result to an activity!");
                        break;
                }
            });

    public void requestPayment(View view, int price) {
        // The price provided to the API should include taxes and shipping.
        final Task<PaymentData> task = model.getLoadPaymentDataTask((long)price*1000);
        task.addOnCompleteListener(paymentDataLauncher::launch);
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a href="https://developers.google.com/pay/api/android/reference/
     * object#PaymentData">PaymentData</a>
     */
    private void handlePaymentSuccess(PaymentData paymentData) {
        final String paymentInfo = paymentData.toJson();

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");

            Toast.makeText(
//                    this, getString(R.string.payments_show_name, billingName),
                    this, "Successfully purchased!",
                    Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d("Google Pay token", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token"));

            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e);
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode holds the value of any constant from CommonStatusCode or one of the
     *                   WalletConstants.ERROR_CODE_* constants.
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/wallet/
     * WalletConstants#constant-summary">Wallet Constants Library</a>
     */
    private void handleError(int statusCode, @Nullable String message) {
        Log.e("loadPaymentData failed",
                String.format(Locale.getDefault(), "Error code: %d, Message: %s", statusCode, message));
    }


}