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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import main.com.dashdriver.R;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MySession;


public class ConfirmPayment extends AppCompatActivity {
    Button sending,package_money,edit_button_here;
    EditText namecard,edt_cardnumber,expiry_date,year,security_code,postalcode;
    private static final String TAG = "SetupActivity";
    ProgressDialog progressDialog;
    String strnamecard,cardnumber,strexpiry_date,cvv_number,stryear;
    String str_token="",pack_price="",pack_name="",pack_id="",user_log_data="",user_id="";
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

        dialog = new Dialog(ConfirmPayment.this);
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

                    if (BaseUrl.stripe_publish!=null&&!BaseUrl.stripe_publish.equalsIgnoreCase("")){
                        showDialog();
                        month = Integer.parseInt(strexpiry_date);
                        year_int = Integer.parseInt(stryear);

                        onClickSomething(cardnumber, month, year_int, cvv_number);
                        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
                        Card card = new Card(cardnumber, month, year_int, cvv_number);  // pk_test_2khGozRubEhBZxFXj3TnxrkO
                        Stripe stripe = new Stripe(ConfirmPayment.this, BaseUrl.stripe_publish);  //pk_test_DpAdEXE4slfMy2FR7vsSj0ya
                        //  Stripe stripe = new Stripe(ConfirmPayment.this, "pk_test_tuLF7lx5bPZpsfwM4OzqE0HJ");  //pk_test_DpAdEXE4slfMy2FR7vsSj0ya

                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        // Send token to your server
                                        System.out.println("----------------Token" + token.getId());
                                        hideDialog();
                                        token_id = token.getId();
                                        paymentwithcard();

                                    }
                                    public void onError(Exception error) {
                                        // Show localized error message
                                        Toast.makeText(ConfirmPayment.this, "\n" + "The expiration year or the security code of your card is not valid",
                                                Toast.LENGTH_LONG
                                        ).show();
                                        System.out.println("Eeeeeeeeeeeeeeerrrrr" + error.toString());
                                        hideDialog();
                                    }
                                });

                    }

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
            Paymentjsontask task = new Paymentjsontask();
            task.execute();

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



    public class Paymentjsontask extends AsyncTask<String, Void, String> {
        boolean iserror = false;
        String result = "";
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            String cancel_req_tag = "paymentin";
        progressDialog.setMessage("Payment you in...");
        showDialog();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(String... params) {
            //HttpClient client = new DefaultHttpClient();


            HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            DefaultHttpClient client = new DefaultHttpClient();
            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
           // HttpPost post = new HttpPost(BaseUrl.baseurl+"strips_payment?payment_type=Card&currency=USD&amount="+pack_price+"&user_id="+user_id+"&token="+token_id);
            HttpPost post = new HttpPost(BaseUrl.baseurl+"strips_payment?transaction_type=add_to_wallet&payment_type=Card&currency=USD&amount="+pack_price+"&user_id="+user_id+"&token="+token_id+"&request_id=&rating=&review=&car_charge=&tip=&time_zone="+time_zone);

            try {
                HttpResponse response = client.execute(post);
                String object = EntityUtils.toString(response.getEntity());
                System.out.println("#####object=" + object);
                //JSONArray js = new JSONArray(object);
                JSONObject jobect1 = new JSONObject(object);
                result = jobect1.getString("message");
                if(result.equalsIgnoreCase("payment successfull")){
                    String details = jobect1.getString("result");

                }else{

                }
            }

            catch (Exception e) {
                Log.v("22", "22" + e.getMessage());
                e.printStackTrace();
                iserror = true;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result1) {
            // TODO Auto-generated method stub
            super.onPostExecute(result1);
            hideDialog();
            if(iserror== false){
                if (result.equalsIgnoreCase("payment successfull")){

                    Toast.makeText(getApplicationContext(),"Payment Added Successfully.",Toast.LENGTH_SHORT).show();

                    finish();

                } else {
                    Toast.makeText(ConfirmPayment.this,"Card information is wrong.",Toast.LENGTH_SHORT).show();

                }
            }else{
                Toast.makeText(getApplicationContext(),"Oops!! Please check server connection .",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}
//https://github.com/twilio/voice-quickstart-android