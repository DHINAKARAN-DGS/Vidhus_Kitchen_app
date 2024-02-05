package com.daatstudios.vidhus_kitchen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PaymentsActivity extends AppCompatActivity {


    String orderID, products, price,docid;

    Button mp;
    final int UPI_PAYMENT = 0;

    TextView PRODUCTSTV, PRICETV, ADDRESSTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        mp = findViewById(R.id.make_paymentTV);

        orderID = getIntent().getStringExtra("OID");
        products = getIntent().getStringExtra("Products");
        price = getIntent().getStringExtra("Price");
        docid = getIntent().getStringExtra("DOCID");

        PRODUCTSTV = findViewById(R.id.productsTv);
        PRICETV = findViewById(R.id.amountTV);
        ADDRESSTV = findViewById(R.id.deliveryTv);

        PRICETV.setText("₹ "+price);
        PRODUCTSTV.setText("Products List: "+products);
        ADDRESSTV.setText("Order ID: "+orderID);


        Uri uri = new Uri.Builder().scheme("upi").authority("pay").appendQueryParameter("pa", "daatstudios@ybl")       // virtual ID
                .appendQueryParameter("pn", "Daat Studios")          // name
                .appendQueryParameter("tn", "Nithya Medical Test Payments")       // any note about payment
                .appendQueryParameter("am", price)           // amount
                .appendQueryParameter("cu", "INR")                         // currency
//                .appendQueryParameter("url", "your-transaction-url")       // optional
                .build();

        mp.setOnClickListener(v -> {
            Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
            upiPayIntent.setData(uri);

            Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
            if (null != chooser.resolveActivity(getPackageManager())) {
                startActivityForResult(chooser, UPI_PAYMENT);
            } else {
                Toast.makeText(PaymentsActivity.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response " + resultCode);
        if (requestCode == UPI_PAYMENT) {
            if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                if (data != null) {
                    String trxt = data.getStringExtra("response");
                    Log.e("UPI", "onActivityResult: " + trxt);
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add(trxt);
                    upiPaymentDataOperation(dataList);
                } else {
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
            } else {
                //when user simply back without payment
                Log.e("UPI", "onActivityResult: " + "Return data is null");
                ArrayList<String> dataList = new ArrayList<>();
                dataList.add("nothing");
                upiPaymentDataOperation(dataList);
            }
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(PaymentsActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(PaymentsActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("Orders").document(docid).update("Status","Dispatch Pending");
                finish();
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(PaymentsActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: " + approvalRefNo);
            } else {
                Toast.makeText(PaymentsActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: " + approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(PaymentsActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected() && netInfo.isConnectedOrConnecting() && netInfo.isAvailable();
        }
        return false;
    }
}