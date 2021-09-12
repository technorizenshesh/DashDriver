package main.com.dashdriver.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.stripe.android.model.Card;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.dashdriver.MainActivity;
import main.com.dashdriver.R;
import main.com.dashdriver.app.Config;
import main.com.dashdriver.constant.ACProgressCustom;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.paymentclasses.Utils;
import main.com.dashdriver.utils.NotificationUtils;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class PaymentAct extends AppCompatActivity {

    private ProgressBar progressbar;
    private RelativeLayout exit_app_but;
    private TextView servicetax,nightcharge,tipamount, discount_type, paymentmessage, time_tv, date_tv, total_fare, payment_type, carcharge, cullectpayment, basefare, timefare, distancefare, distance;
    private String comment_str = "";
    float rating = 0;
    private EditText comment_et;
    private RatingBar ratingbar;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    public String pay_status = "", payment_type_str = "";
    String time_zone = "", amount_str = "", car_charge_str = "",discount_str="0", amount_str_main = "";
    ACProgressCustom ac_dialog;
    ProgressDialog progressDialog;
    String strexpiry_date = "", tip_amount_str = "0",customer_id="", stryear = "", cardnumber = "", cvv_number = "", token_id = "", rider_id = "";
    ScheduledExecutorService scheduleTaskExecutor;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();

        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_payment);
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

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
        Log.e("RequestID","=======>"+MainActivity.request_id);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                }
                else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
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
                        } else if (keyMessage.equalsIgnoreCase("Tip amount is updated")) {
                           new GetPayment().execute();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new GetPayment().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
        LocalBroadcastManager.getInstance(PaymentAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(PaymentAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(PaymentAct.this.getApplicationContext());
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                new GetPaymentTwo().execute();
            }
      }, 5, 7, TimeUnit.SECONDS);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(PaymentAct.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        if (scheduleTaskExecutor == null) {
        } else {
            scheduleTaskExecutor.shutdown();
        }
    }

    private void clickevent() {

        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cullectpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = ratingbar.getRating();
                comment_str = comment_et.getText().toString();
                Calendar c = Calendar.getInstance();
                TimeZone tz = c.getTimeZone();
                time_zone = tz.getID();
                SubmitPayment();
//                if (comment_str.equalsIgnoreCase("")) {
//                    Toast.makeText(PaymentAct.this, getResources().getString(R.string.plsraterider), Toast.LENGTH_LONG).show();
//                } else {
//
//                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    private void ResponseToRequest(){
        HashMap<String, String> params = new HashMap<>();
        params.put("request_id", MainActivity.request_id);
        params.put("status", "Finish");
        params.put("review", comment_str);
        params.put("rating", String.valueOf(rating));
        params.put("timezone", time_zone);
        params.put("driver_id", MainActivity.user_id);
        ApiCallBuilder.build(this).setUrl(BaseUrl.baseurl + "driver_accept_and_Cancel_request")
                .setParam(params).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                Log.e("Payment","======>"+response);
                finish();
            }

            @Override
            public void Failed(String error) {

            }
        });
    }



    private void SubmitPayment(){
        HashMap<String,String>params=new HashMap<>();
        params.put("request_id", MainActivity.request_id);
        params.put("amount", amount_str);
        params.put("car_charge", ""+car_charge_str);
        params.put("rating", "" + rating);
        params.put("discount", discount_str);
        params.put("tip", tip_amount_str);
        params.put("timezone", ""+time_zone);
        params.put("review", ""+comment_str);
        params.put("pay_type", payment_type_str);
        ApiCallBuilder.build(this)
                .setUrl(BaseUrl.baseurl + "add_payment?")
                .isShowProgressBar(true)
                .setParam(params)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        try {
                            ResponseToRequest();
                            Log.e("adadasdasd","ResponseToRequest");
//                            JSONObject object = new JSONObject(response);
//                            if (object.getString("status").equals("1")) {
//                                ResponseToRequest();
//                            } else {
//                                ResponseToRequest();
//                                // Toast.makeText(PaymentAct.this, "User Payment not done.. wait a moment", Toast.LENGTH_SHORT).show();
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void Failed(String error) {

                    }
                });
    }
    private class SubmitPayment extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "add_payment?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", MainActivity.request_id);
                params.put("amount", amount_str);
                params.put("car_charge", car_charge_str);
                params.put("rating", ""+rating);
                params.put("discount", discount_str);
                params.put("tip", tip_amount_str);
                params.put("timezone", time_zone);

                if (comment_str == null) {
                    params.put("review", "");
                } else {
                    params.put("review", comment_str);
                }

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                Log.e("addPayment","=====>"+urlParameters);
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
            Log.e("SUBMITPAYMENTRESULT", " >> " + result);

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                ResponseToRequest();
                     }
        }
    }

    private void idinit() {
        nightcharge =findViewById(R.id.nightcharge);
        servicetax =findViewById(R.id.servicetax);
        ratingbar = findViewById(R.id.ratingbar);
        comment_et =  findViewById(R.id.comment_et);
        discount_type =  findViewById(R.id.discount_type);
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
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_payment?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", MainActivity.request_id);
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


                            if (jsonObject1.getString("discount") == null || jsonObject1.getString("discount").equalsIgnoreCase("") || jsonObject1.getString("discount").equalsIgnoreCase("0")) {
                                discount_type.setText(getResources().getString(R.string.discountapp) + "$ 0");
                                discount_str ="0";
                                Log.e("discount_str if >>"," >> "+discount_str);

                            } else {
                                discount_str =  jsonObject1.getString("discount");
                                discount_type.setText(getResources().getString(R.string.discountapp) + "$ " + jsonObject1.getString("discount") + " will be added in your wallet");
                                Log.e("discount_str else >>"," >> "+discount_str);
                            }


                            amount_str = jsonObject1.getString("total");
                            amount_str_main = jsonObject1.getString("total");
                            car_charge_str = String.valueOf(jsonObject1.getString("car_charge"));


                            nightcharge.setText("$" + jsonObject1.getString("night_charge_amount"));
                            servicetax.setText("$" + jsonObject1.getString("service_tax_amount"));

                            distancefare.setText("$" + jsonObject1.getString("per_miles_charge"));
                            timefare.setText("$" + jsonObject1.getString("per_min_charge"));
                            // basefare.setText("" + "$ " + jsonObject1.getInt("base_fare"));
                            carcharge.setText("" + "$" + jsonObject1.getString("car_charge"));
                            distance.setText("Distance(" + jsonObject1.getString("miles") + " km)");
                            time_tv.setText("Time(" + jsonObject1.getString("perMin") + " min)");

                            JSONArray jsonArray3 = jsonObject1.getJSONArray("user_detail");
                            for (int user = 0; user < jsonArray3.length(); user++) {
                                JSONObject jsonObject21 = jsonArray3.getJSONObject(user);
                                token_id = jsonObject21.getString("card_id");
                                customer_id = jsonObject21.getString("cust_id");

                            }
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("booking_detail");

                            payment_type_str = jsonObject2.getString("payment_type");
                            tip_amount_str = jsonObject2.getString("tip_amount");
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
                                    DecimalFormat df2 = new DecimalFormat(".##");
                                    total_fare.setText("Total :" + "$" + df2.format(total));

                                }

                            }


                            rider_id = jsonObject2.getString("user_id");
                            payment_type.setText("Payment Type : " + jsonObject2.getString("payment_type"));
                            if (jsonObject2.getString("payment_type").equalsIgnoreCase("Card")) {
                                if (jsonObject1.getString("driver_amount_j") == null || jsonObject1.getString("driver_amount_j").equalsIgnoreCase("")) {
                                    paymentmessage.setText(getResources().getString(R.string.cardrideamountaddwallet));

                                } else {
                                    paymentmessage.setText("" + "$" + jsonObject1.getString("driver_amount_j") + " " + getResources().getString(R.string.cardrideamountaddwallet));

                                }
                                cullectpayment.setText("" + getResources().getString(R.string.submit));
                            } else if (jsonObject2.getString("payment_type").equalsIgnoreCase("Wallet")) {
                                if (jsonObject1.getString("driver_amount_j") == null || jsonObject1.getString("driver_amount_j").equalsIgnoreCase("")) {
                                    paymentmessage.setText(getResources().getString(R.string.amountaddwallet));

                                } else {
                                    paymentmessage.setText("" + "$" + jsonObject1.getString("driver_amount_j") + " " + getResources().getString(R.string.cardrideamountaddwallet));

                                }
                                cullectpayment.setText("" + getResources().getString(R.string.submit));


                            } else {
                                cullectpayment.setText("" + getResources().getString(R.string.collectmoney));
                                paymentmessage.setText(getResources().getString(R.string.colectmoneyfromrider));

                            }


                            JSONArray cardarray = jsonObject1.getJSONArray("card_details");
                            for (int k = 0; k < cardarray.length(); k++) {
                                JSONObject cardobj = cardarray.getJSONObject(i);
                                strexpiry_date = cardobj.getString("expiry_month");
                                stryear = cardobj.getString("expiry_year");
                                cardnumber = cardobj.getString("card_number");
                                cvv_number = cardobj.getString("cvv");
                            }


                            try {
                                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject2.getString("req_datetime"));
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                String strDate = formatter.format(date1);
                                date_tv.setText("" + strDate);

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

    private class GetPaymentTwo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_payment?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", MainActivity.request_id);
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
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("booking_detail");

                            if (!tip_amount_str.equalsIgnoreCase(jsonObject2.getString("tip_amount"))) {
                                tip_amount_str = jsonObject2.getString("tip_amount");
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
                            }


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void PaymentSendByDriver(String payment_types) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(PaymentAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.payment_recieved_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv =  canceldialog.findViewById(R.id.no_tv);
        TextView message_tv = canceldialog.findViewById(R.id.message_tv);
        if (payment_type_str == null) {
            yes_tv.setText(getResources().getString(R.string.yes));
            message_tv.setText("" + getResources().getString(R.string.recivecash));

        } else {
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