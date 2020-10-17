package main.com.dashdriver.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.dashdriver.R;
import main.com.dashdriver.app.Config;
import main.com.dashdriver.constant.ACProgressCustom;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.utils.NotificationUtils;

public class InvoiceActivity extends AppCompatActivity {

    private ProgressBar progressbar;
    private RelativeLayout exit_app_but;
    private TextView servicetax,nightcharge,tipamount,discount_type,paymentmessage,time_tv,date_tv, total_fare, payment_type, carcharge, cullectpayment, basefare, timefare, distancefare, distance;
    private String comment_str = "";
    float rating = 0;
    private EditText comment_et;
    private RatingBar ratingbar;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    public String pay_status = "",payment_type_str="";
    String time_zone = "",amount_str="",car_charge_str="",amount_str_main="";
    ACProgressCustom ac_dialog;
    ProgressDialog progressDialog;
    String strexpiry_date ="",request_id="",stryear="",cardnumber="",cvv_number="",token_id="",rider_id="";
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());


        setContentView(R.layout.activity_invoice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }
        progressDialog = new ProgressDialog(this);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }

        idinit();
        clickevent();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.e("Push notification: ", "" + message);
                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        Log.e("KEY MSG =", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your payment is successfull")) {
                            pay_status = "Processing";
                            PaymentSendByDriver(data.getString("payment_type").trim());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null&&!bundle.isEmpty()){
            request_id = bundle.getString("request_id");
            Log.e("request_id >> "," >"+request_id);
            new GetPayment().execute();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(InvoiceActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(InvoiceActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(InvoiceActivity.this.getApplicationContext());
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(InvoiceActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }





    private void idinit() {
        ratingbar = (RatingBar) findViewById(R.id.ratingbar);
        comment_et = (EditText) findViewById(R.id.comment_et);
        discount_type =  findViewById(R.id.discount_type);
        nightcharge =  findViewById(R.id.nightcharge);
        servicetax =  findViewById(R.id.servicetax);
        tipamount =  findViewById(R.id.tipamount);
        carcharge =  findViewById(R.id.carcharge);
        paymentmessage =  findViewById(R.id.paymentmessage);
        distance =  findViewById(R.id.distance);
        basefare =  findViewById(R.id.basefare);
        timefare =  findViewById(R.id.timefare);
        time_tv =  findViewById(R.id.time_tv);
        distancefare =  findViewById(R.id.distancefare);
        cullectpayment =  findViewById(R.id.cullectpayment);
        payment_type =  findViewById(R.id.payment_type);
        total_fare =  findViewById(R.id.total_fare);
        date_tv =  findViewById(R.id.date_tv);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
    }

    private class GetPayment extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);
/*
            if (ac_dialog!=null){
                ac_dialog.show();
            }
*/


            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//http://mobileappdevelop.co/NAXCAN/webservice/get_payment?request_id=1
                String postReceiverUrl = BaseUrl.baseurl + "get_payment?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", request_id);
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(urlParameters);
                writer.flush();
                String response = "";
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                writer.close();
                reader.close();
                return response;
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressbar.setVisibility(View.GONE);
/*
            if (ac_dialog!=null){
                ac_dialog.dismiss();
            }
*/


            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    Log.e("GET PAYMENT RESPONSE", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successful")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            pay_status = jsonObject1.getString("pay_status");
                            if (jsonObject1.getString("discount")==null||jsonObject1.getString("discount").equalsIgnoreCase("")||jsonObject1.getString("discount").equalsIgnoreCase("0")){
                                discount_type.setText(getResources().getString(R.string.discountapp) + "$ 0");
                            }
                            else {
                                discount_type.setText(getResources().getString(R.string.discountapp) + "$ " + jsonObject1.getString("discount")+" will be added in your wallet");
                            }

                            amount_str = jsonObject1.getString("total");
                            amount_str_main = jsonObject1.getString("total");
                            car_charge_str = String.valueOf(jsonObject1.getString("car_charge"));
                            nightcharge.setText("$" + jsonObject1.getString("night_charge_amount"));
                            servicetax.setText("$" + jsonObject1.getString("service_tax_amount"));

                            distancefare.setText("$" + jsonObject1.getString("per_miles_charge"));
                            timefare.setText("$" + jsonObject1.getString("per_min_charge"));
                           // basefare.setText("" + "$ " + jsonObject1.getInt("base_fare"));
                            carcharge.setText("" + "$ " + jsonObject1.getInt("car_charge"));
                            distance.setText("Distance(" + jsonObject1.getString("miles") + " km)");
                            time_tv.setText("Time(" + jsonObject1.getString("perMin") + " min)");
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("booking_detail");

                            String tip_amount_str = jsonObject2.getString("tip_amount");
                            if (tip_amount_str == null || tip_amount_str.equalsIgnoreCase("") || tip_amount_str.equalsIgnoreCase("0")) {
                                tip_amount_str = "0";
                                tipamount.setText("$0.00");
                                total_fare.setText("Total :" + "$" + jsonObject1.getString("total"));
                            } else {
                                tipamount.setText("$" + tip_amount_str);
                                double tipamt = Double.parseDouble(tip_amount_str);
                                total_fare.setText("Total :" + "$" + jsonObject1.getString("total"));

                                if (jsonObject1.getString("total") != null && !jsonObject1.getString("total").equalsIgnoreCase("")) {
                                    double totalrideamt = Double.parseDouble(jsonObject1.getString("total"));
                                    double total = tipamt + totalrideamt;
                                    total_fare.setText("Total :" + "$" + total);
                                }
                            }
                          //  total_fare.setText("Total :" + "$" + jsonObject1.getString("total"));

                            payment_type_str=jsonObject2.getString("payment_type");
                            rider_id=jsonObject2.getString("user_id");
                            payment_type.setText("Payment Type : "+jsonObject2.getString("payment_type"));
                            if (jsonObject2.getString("payment_type").equalsIgnoreCase("Card")){
                                if (jsonObject1.getString("driver_amount_j")==null||jsonObject1.getString("driver_amount_j").equalsIgnoreCase("")){
                                    paymentmessage.setText(getResources().getString(R.string.cardrideamountaddwallet));

                                }
                                else {
                                    paymentmessage.setText(""+"$" + jsonObject1.getString("driver_amount_j")+" "+getResources().getString(R.string.cardrideamountaddwallet));

                                }
                                cullectpayment.setText(""+getResources().getString(R.string.submit));
                            }
                            else if (jsonObject2.getString("payment_type").equalsIgnoreCase("Wallet")){
                                if (jsonObject1.getString("driver_amount_j")==null||jsonObject1.getString("driver_amount_j").equalsIgnoreCase("")){
                                    paymentmessage.setText(getResources().getString(R.string.amountaddwallet));

                                }
                                else {
                                    paymentmessage.setText(""+"$" + jsonObject1.getString("driver_amount_j")+" "+getResources().getString(R.string.cardrideamountaddwallet));

                                }
                                cullectpayment.setText(""+getResources().getString(R.string.submit));


                            }
                            else {
                                cullectpayment.setText(""+getResources().getString(R.string.collectmoney));
                                paymentmessage.setText(getResources().getString(R.string.colectmoneyfromrider));

                            }
                            JSONArray cardarray = jsonObject1.getJSONArray("card_details");
                            for (int k=0;k<cardarray.length();k++){
                                JSONObject cardobj = cardarray.getJSONObject(i);
                                strexpiry_date = cardobj.getString("expiry_month");
                                stryear = cardobj.getString("expiry_year");
                                cardnumber = cardobj.getString("card_number");
                                cvv_number = cardobj.getString("cvv");
                                   }


                            try {
                                Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject2.getString("req_datetime"));
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                String strDate = formatter.format(date1);
                                date_tv.setText(""+strDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void PaymentSendByDriver1(String payment_typesss) {
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(InvoiceActivity.this);
        alertDialog.setTitle("Payment");
        alertDialog.setMessage("Payment give by passenger");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
            }
        });
        alertDialog.show();
    }

    private void PaymentSendByDriver(String payment_types) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(InvoiceActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.payment_recieved_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv =  canceldialog.findViewById(R.id.no_tv);
        TextView message_tv =  canceldialog.findViewById(R.id.message_tv);
        if (payment_type_str==null){
            yes_tv.setText(getResources().getString(R.string.yes));
            message_tv.setText("" + getResources().getString(R.string.recivecash));

        }
        else {
            if (payment_type_str.equalsIgnoreCase("Cash")) {
                yes_tv.setText(getResources().getString(R.string.yes));
                message_tv.setText("" + getResources().getString(R.string.recivecash));

            } else if (payment_type_str.equalsIgnoreCase("Wallet")) {
                message_tv.setText("" + getResources().getString(R.string.givebywallet));
            } else if (payment_type_str.equalsIgnoreCase("CreditCard")) {
                message_tv.setText("" + getResources().getString(R.string.givebycreditcard));
            }
        }

        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
             //   new ConfirmPayment().execute("Processing");
                canceldialog.dismiss();
            }
        });
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
               // new ConfirmPayment().execute("Confirm");
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

}
