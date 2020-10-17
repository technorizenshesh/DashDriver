package main.com.dashdriver.paymentclasses;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

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
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import main.com.dashdriver.R;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MySession;


public class CreateCustomerFromCard extends AppCompatActivity {
    Button sending,package_money,edit_button_here;
    EditText namecard,edt_cardnumber,expiry_date,year,security_code,postalcode;
    private static final String TAG = "SetupActivity";
    ProgressDialog progressDialog;
    String strnamecard,cardnumber,strexpiry_date,cvv_number,stryear;
    String str_token="",pack_price="",email_str="",pack_name="",pack_id="",user_log_data="",user_id="";
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    TextView demoplus_name;
    CardInputWidget mCardInputWidget;
    Dialog dialog;
    private CreditCardFormatTextWatcher tv;
    int month, year_int;
    private String token_id,time_zone="";
    private MySession mySession;
    private RelativeLayout exit_app_but;

    /**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);
        progressDialog = new ProgressDialog(this);
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    email_str = jsonObject1.getString("email");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
      Bundle  b = getIntent().getExtras();
        if (b != null&&!b.isEmpty()) {
            pack_price = b.getString("amount_str");

            Log.d("payment", pack_price);
            System.out.println("payment" + pack_price);
        }else
        {
            Toast.makeText(this,"payment is null",Toast.LENGTH_SHORT).show();
        }
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        Log.e("TIME ZONE >>",tz.getDisplayName());
        Log.e("TIME ZONE ID>>",tz.getID());
        time_zone = tz.getID();
        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
        //   mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        namecard = (EditText) findViewById(R.id.namecard);
        edt_cardnumber = (EditText) findViewById(R.id.edt_cardnumber);
        expiry_date = (EditText) findViewById(R.id.expiry_date);
        year = (EditText) findViewById(R.id.year);
        security_code = (EditText) findViewById(R.id.security_code);
        postalcode = (EditText) findViewById(R.id.postalcode);
        package_money = (Button) findViewById(R.id.package_money);

        demoplus_name = (TextView) findViewById(R.id.demoplus_name);
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sending = (Button) findViewById(R.id.sending_payment);

        dialog = new Dialog(CreateCustomerFromCard.this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        tv = new CreditCardFormatTextWatcher(edt_cardnumber);
        edt_cardnumber.addTextChangedListener(tv);
        sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strnamecard = namecard.getText().toString().trim();
                cardnumber = edt_cardnumber.getText().toString().trim();
                strexpiry_date = expiry_date.getText().toString().trim();
                stryear = year.getText().toString().trim();
                cvv_number = security_code.getText().toString().trim();
                //Validate();
                if (strnamecard.equals(""))
                {
                    namecard.setError("Card Name can't be empty");
                }
                if (cardnumber.equals("")){
                    edt_cardnumber.setError("Card Number can't be empty");
                }if (strexpiry_date.equals(""))
                {
                    expiry_date.setError("Exp Date can't be empty");
                }if (stryear.equals(""))
                {
                    year.setError("Year can't be empty");
                }if (cvv_number.equals(""))
                {
                    security_code.setError("Security Code can't be empty");
                }else {
                    showDialog();
                    month = Integer.parseInt(strexpiry_date);
                    year_int = Integer.parseInt(stryear);

                    onClickSomething(cardnumber, month, year_int, cvv_number);
                    mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
                    Card card = new Card(cardnumber, month, year_int, cvv_number);  // pk_test_2khGozRubEhBZxFXj3TnxrkO
                    card.setCurrency("usd");

                    Stripe stripe = new Stripe(CreateCustomerFromCard.this, BaseUrl.stripe_publish);  //pk_test_DpAdEXE4slfMy2FR7vsSj0ya
                   //Stripe stripe = new Stripe(CreateCustomerFromCard.this, "pk_test_tuLF7lx5bPZpsfwM4OzqE0HJ");  //pk_test_DpAdEXE4slfMy2FR7vsSj0ya

                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server

                                    Log.e("Token",">>" + token.getId());
                                    hideDialog();
                                    token_id = token.getId();
                                    paymentwithcard();

                             }
                                public void onError(Exception error) {
                                    Log.e("Eeeeeeeeeeeeeeerrrrr",">>" + error.toString());
                                    // Show localized error message
                                    Toast.makeText(CreateCustomerFromCard.this, "\n" + "The expiration year or the security code of your card is not valid",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    hideDialog();
                                }
                            });
                }

            }
        });
        edit_button_here=(Button)findViewById(R.id.button2);
        edit_button_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    startActivity(new Intent(ConfirmPayment.this,WalletActivity.class));


                finish();
            }
        });

        package_money.setText(pack_price);
        demoplus_name.setText(pack_name);
    }

    private void paymentwithcard() {
        // Tag used to cancel the request
        if(Utils.isConnected(getApplicationContext())){
          new CreateCardCustomer().execute();
        //  new CreateCustomer().execute();

        }

        else{
            Toast.makeText(getApplicationContext(), "Please Cheeck network conection..", Toast.LENGTH_SHORT).show();
        }
    }
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

    }
    public void onClickSomething(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC) {
        Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);
        card.validateNumber();
        card.validateCVC();
    }

    private class CreateCustomer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://hitchride.net/webservice/create_customer?token=tok_1DItTTGKRcHzfO4O4yKL9xCb&description=create%20my%20account
            try {

                String postReceiverUrl = BaseUrl.baseurl + "create_customer?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("token", token_id);
                params.put("description", email_str);
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
            // prgressbar.setVisibility(View.GONE);

            Log.e("Create Customer Res", ">>>>>>>>>>>>" + result);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                       JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                       String customer_id =  jsonObject1.getString("id");
                       Log.e("customer_id >> ", " >> "+customer_id);
                        new TransferAmount().execute(customer_id);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // BaseActivity.Card_Added_Sts="Added";

            }


        }
    }
    private class CreateCardCustomer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://hitchride.net/webservice/create_customer?token=tok_1DItTTGKRcHzfO4O4yKL9xCb&description=create%20my%20account
            try {

                String postReceiverUrl = BaseUrl.baseurl + "external_accounts?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("token", token_id);
                params.put("account_id", "acct_1DNy6iC6uB7VXr0E");
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
            // prgressbar.setVisibility(View.GONE);

            Log.e("Create Customer Res", ">>>>>>>>>>>>" + result);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                       JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                       String customer_id =  jsonObject1.getString("id");
                       Log.e("customer_id >> ", " >> "+customer_id);
                      //  new TransferAmount().execute(customer_id);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // BaseActivity.Card_Added_Sts="Added";

            }


        }
    }
    private class TransferAmount extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://hitchride.net/webservice/transfer_amount?amount=120&currency=usd&account_id=acc_usdd123
            try {

                String postReceiverUrl = BaseUrl.baseurl + "transfer_amount?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("amount", "10");
                params.put("account_id", strings[0]);
                params.put("currency", "usd");
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
            // prgressbar.setVisibility(View.GONE);

            Log.e("Create Customer Res", ">>>>>>>>>>>>" + result);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // BaseActivity.Card_Added_Sts="Added";

            }


        }
    }


}



//https://github.com/twilio/voice-quickstart-android