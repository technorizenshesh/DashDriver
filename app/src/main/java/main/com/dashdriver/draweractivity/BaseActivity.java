package main.com.dashdriver.draweractivity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.util.LinkedHashMap;
import java.util.Map;

import main.com.dashdriver.Fragments.FragmentLanguage;
import main.com.dashdriver.R;
import main.com.dashdriver.activity.AboutUsAct;
import main.com.dashdriver.activity.AddBankAccountAct;
import main.com.dashdriver.activity.EmergencyActivity;
import main.com.dashdriver.activity.InviteEarnAct;
import main.com.dashdriver.activity.LoginAct;
import main.com.dashdriver.activity.ManageDocumentAct;
import main.com.dashdriver.activity.MyVehiclsAct;
import main.com.dashdriver.activity.NotificationAct;
import main.com.dashdriver.activity.PayoutActivity;
import main.com.dashdriver.activity.PerformanceAct;
import main.com.dashdriver.activity.PrivacyPolicyAct;
import main.com.dashdriver.activity.ProfileAct;
import main.com.dashdriver.activity.RideHistory;
import main.com.dashdriver.activity.RiderFeedbackAct;
import main.com.dashdriver.activity.SupportAct;
import main.com.dashdriver.activity.TermsConditions;
import main.com.dashdriver.activity.TransectionHistory;
import main.com.dashdriver.activity.WalletAct;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.service.TrackingService;


public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationview;
    boolean exit = false;
    MySession mySession;
    private TextView user_name;
    private LinearLayout payout_lay,aboutuslay,privacylay,termslay,riderfeedback,bankdetails,emergencylay,invitefriendlay,managedocuments,managevehicles,notificationlay,supportlay, logout,myprofile,mywallet,ridehistory,performance_lay,trasaction_lay;

    private GoogleApiClient mGoogleApiClient;
private String user_log_data="",user_id="";
    private String language = "";
    MyLanguageSession myLanguageSession;
    private LinearLayout language_lay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, TrackingService.class));
        } else {
            startService(new Intent(this, TrackingService.class));
        }
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data != null) {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    Log.e("USER NAME"," >>"+jsonObject1.getString("first_name"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        adddrawer();
        idinit();
        clickevent();


    }

    private void clickevent() {
        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, ProfileAct.class);
                startActivity(i);
            }
        });
        supportlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, SupportAct.class);
                startActivity(i);
            }
        });
        mywallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, WalletAct.class);
                startActivity(i);
            }
        });
        ridehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, RideHistory.class);
                startActivity(i);
            }
        });
        performance_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, PerformanceAct.class);
                startActivity(i);
            }
        });
        trasaction_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, TransectionHistory.class);
                startActivity(i);
            }
        });
        notificationlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, NotificationAct.class);
                startActivity(i);
            }
        });
        managevehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, MyVehiclsAct.class);
                startActivity(i);
            }
        });
        managedocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, ManageDocumentAct.class);
                startActivity(i);
            }
        });
        invitefriendlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, InviteEarnAct.class);
                startActivity(i);
            }
        });
        bankdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, AddBankAccountAct.class);
                startActivity(i);
            }
        });
        emergencylay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, EmergencyActivity.class);
                startActivity(i);
            }
        });
        riderfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, RiderFeedbackAct.class);
                startActivity(i);
            }
        });
        termslay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, TermsConditions.class);
                startActivity(i);
            }
        });
        privacylay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, PrivacyPolicyAct.class);
                startActivity(i);
            }
        });
        aboutuslay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, AboutUsAct.class);
                startActivity(i);
            }
        });
        payout_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, PayoutActivity.class);
                startActivity(i);
            }
        });
        language_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FragmentLanguage().show(getSupportFragmentManager(),"");
            }
        });
    }

    private void idinit() {
        aboutuslay = findViewById(R.id.aboutuslay);
        privacylay = findViewById(R.id.privacylay);
        termslay = findViewById(R.id.termslay);
        riderfeedback = findViewById(R.id.riderfeedback);
        bankdetails = findViewById(R.id.bankdetails);
        invitefriendlay = findViewById(R.id.invitefriendlay);
        emergencylay = findViewById(R.id.emergencylay);
        managedocuments = findViewById(R.id.managedocuments);
        managevehicles = findViewById(R.id.managevehicles);
        supportlay = findViewById(R.id.supportlay);
        notificationlay = findViewById(R.id.notificationlay);
        performance_lay = findViewById(R.id.performance_lay);
        trasaction_lay = findViewById(R.id.trasaction_lay);
        ridehistory = findViewById(R.id.ridehistory);
        payout_lay = findViewById(R.id.payout_lay);
        language_lay = findViewById(R.id.language_lay);

        mywallet = findViewById(R.id.mywallet);
        myprofile = findViewById(R.id.myprofile);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BaseActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                Intent k = new Intent(BaseActivity.this, TrackingService.class);
                stopService(k);
                new ChgStatus().execute();
            }
        });


    }


    private void adddrawer() {
        setSupportActionBar(toolbar);
        navigationview = (NavigationView) findViewById(R.id.navigationview);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawer_layout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

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
    }
    public void onRefreshLanguage(){
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private class ChgStatus extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_online_status?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("status", "OFFLINE");
                params.put("type", "DRIVER");
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                Log.e("Logout",postReceiverUrl+urlParameters);
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
            Log.e("LogoutResponse","=====>"+result);
            try {
                JSONObject object=new JSONObject(result);
                if (object.getString("status").contains("1")){
                    mySession.logoutUser();
                    Intent i = new Intent(BaseActivity.this, LoginAct.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

