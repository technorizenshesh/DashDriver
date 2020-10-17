package main.com.dashdriver.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;

import main.com.dashdriver.MainActivity;
import main.com.dashdriver.R;
import main.com.dashdriver.app.Config;
import main.com.dashdriver.constant.GPSTracker;
import main.com.dashdriver.constant.MyReceiver;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.utils.NotificationUtils;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1500;
    MySession mySession;
    public static final int RequestPermissionCode = 1;
    protected static final String TAG = "MainActivityDummy";
    public static double latitude = 0.0, longitude = 0.0;
    GPSTracker gpsTracker;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    boolean isAccepted=false;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        gpsTracker = new GPSTracker(SplashActivity.this);
        mySession = new MySession(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };
        requestPermission();
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String firebase_regid = pref.getString("regId", null);
        Log.e("Splash", "Firebase reg id: " + firebase_regid);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]
                {

                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CALL_PHONE
                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
                for (int i:grantResults){
                    isAccepted=i==0;
                    if (!isAccepted){
                        break;
                    }
                }
                if (isAccepted){
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (Settings.canDrawOverlays(this)) {
                                    omHandler();
                                }else {
                                    OverDrowPermission();
                                }
                            }else {
                                omHandler();
                            }
                        }else {
                            nesePermission();
                        }

                }else {
                    requestPermission();
                }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
        if (isAccepted){
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        omHandler();
                    }else {
                        OverDrowPermission();
                    }
                }else {
                    omHandler();
                }
            }else {
                nesePermission();
            }
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

    }


    private void omHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mySession.IsLoggedIn()) {
                    setStartTime();
                        if (gpsTracker.canGetLocation()) {
                            latitude = gpsTracker.getLatitude();
                            longitude = gpsTracker.getLongitude();
                        }
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else {
                        if (gpsTracker.canGetLocation()) {
                            latitude = gpsTracker.getLatitude();
                            longitude = gpsTracker.getLongitude();
                        }
                            Intent i = new Intent(SplashActivity.this, WelcomeAct.class);
                            startActivity(i);
                            finish();
                }
            }
        }, 1500);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setStartTime() {
        AlarmManager alarmMgr = (AlarmManager) (SplashActivity.this).getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 *60*2, alarmIntent);
    }
    public void nesePermission() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(SplashActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.allowpermission));
        alertDialog.setMessage(getResources().getString(R.string.nessper));
        alertDialog.setPositiveButton(getResources().getString(R.string.allow), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.closeapp), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }
    public void OverDrowPermission() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(SplashActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.allowpermission));
        alertDialog.setMessage(getResources().getString(R.string.nessper));
        alertDialog.setPositiveButton(getResources().getString(R.string.allow), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.closeapp), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }
}