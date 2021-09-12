package main.com.dashdriver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.dashdriver.activity.BookingActivity;
import main.com.dashdriver.activity.DashBoardAct;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import main.com.dashdriver.activity.LoginAct;
import main.com.dashdriver.activity.MyVehiclsAct;
import main.com.dashdriver.activity.PaymentAct;
import main.com.dashdriver.activity.ProfileAct;
import main.com.dashdriver.activity.TripStatusAct;
import main.com.dashdriver.activity.WalletAct;
import main.com.dashdriver.app.Config;
import main.com.dashdriver.constant.ACProgressCustom;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.GPSTracker;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.constant.MyVehicleCls;
import main.com.dashdriver.draweractivity.BaseActivity;
import main.com.dashdriver.pojoclasses.MyvehicleBean;
import main.com.dashdriver.restapi.ApiClient;
import main.com.dashdriver.service.TrackingService;
import main.com.dashdriver.utils.NotificationUtils;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    private FrameLayout contentFrameLayout;
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    TextView changecar, user_name, mywalletmoney, online_offline_tv;
    public static int sts = 0;
    MySession mySession;
    private String user_log_data = "";
    public static String amount = "0", promo_code = "", user_id = "", image_url, firstname_str = "", lastname_str = "";
    private CircleImageView user_img;
    Switch switch_driver_sts;
    private String status;
    private String selected_car_id = "";
    ArrayList<MyvehicleBean> myCarBeanArrayList;
    String car_name = "";
    String car_number = "";
    private TextView carname, carnumber;
    private ImageView carimage;
    private LinearLayout addcarlay,carinfolay;
    Dialog canceldialog;
    String status_job = "", request_id_main = "";
    public static int driver_sts = 0;
    ProgressBar progressbar;
    private ProgressBar progressBarCircle;
    boolean dialogsts_show = false;
    MapStyleOptions style;
    Dialog booking_request_dialog;
    private FloatingActionButton book_now;
    private Timer mTimer2;
    private Dialog dialog;
    private MediaPlayer mPlayer;

    private enum TimerStatus { STARTED, STOPPED }

    private long timeCountInMilliSeconds;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private TextView textViewTime;
    private CountDownTimer countDownTimer;
    public static String request_id = "", strDate = "";
    private String diff_second = "";
    Marker drivermarker;
    public static String ACTIVE_CAR_ID = "";
    public static String time_zone = "";
    ACProgressCustom ac_dialog;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView todaytripcount, ratetv, tripcount, todaytipsamount, todayearning, driver_name, car_nametv, cartypename, car_numbertv, lasttrip_time, lasttripamount, lasttripdate;
    String currentVersion;
    private boolean min_sts = true;
    private String language = "";
    MyLanguageSession myLanguageSession;
    boolean isOnce=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        mPlayer = MediaPlayer.create(this, R.raw.driver);
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        contentFrameLayout = (FrameLayout) findViewById(R.id.contentFrame);

        try {
            final Window win= getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            // unlock screen
        } catch (Exception e) {}

        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        Log.e("TIME ZONE >>", tz.getDisplayName());
        Log.e("TIME ZONE ID>>", tz.getID());
        time_zone = tz.getID();


        //Remember this is the FrameLayout area within your activity_main.xml

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sts = 0;
        min_sts = true;
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
                    user_name = findViewById(R.id.user_name);
                    user_name.setText("" + jsonObject1.getString("first_name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        idinits();
        checkGps();


        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");

                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        Log.e("KEY MSG MAIN ACT=", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is Now")) {

                            if (booking_request_dialog == null) {
                                Log.e("COME ", "null");
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                request_id = String.valueOf(data.getInt("request_id"));
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = data.getString("rating");
                                String favorite_ride = "";
                                String payment_type = data.getString("payment_type");

                                String est_pickup_distance = data.getString("estimate_distance");
                                String est_pickup_time = data.getString("estimate_time");

                                diff_second = data.getString("diff_second");
                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,payment_type,est_pickup_distance,est_pickup_time);

                            } else if (booking_request_dialog.isShowing()) {
                                Log.e("COME ", "show");
                            } else {
                                Log.e("COME ", "else");
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                request_id = String.valueOf(data.getInt("request_id"));
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = data.getString("rating");
                                diff_second = data.getString("diff_second");
                                String favorite_ride = "";
                                String payment_type = data.getString("payment_type");
                                String est_pickup_distance = data.getString("estimate_distance");
                                String est_pickup_time = data.getString("estimate_time");
                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,payment_type, est_pickup_distance, est_pickup_time);

                            }
                        } else if (keyMessage.equalsIgnoreCase("your booking request is Letter")) {
                            if (booking_request_dialog == null) {
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = data.getString("rating");
                                String favorite_ride = "";
                                String payment_type = data.getString("payment_type");
                                request_id = String.valueOf(data.getInt("request_id"));
                                diff_second = data.getString("diff_second");
                                String est_pickup_distance = data.getString("estimate_distance");
                                String est_pickup_time = data.getString("estimate_time");
                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,payment_type, est_pickup_distance, est_pickup_time);

                            } else if (booking_request_dialog.isShowing()) {

                            } else {
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                request_id = String.valueOf(data.getInt("request_id"));
                                diff_second = data.getString("diff_second");
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = data.getString("rating");
                                String favorite_ride = "";
                                String payment_type = data.getString("payment_type");
                                String est_pickup_distance = data.getString("estimate_distance");
                                String est_pickup_time = data.getString("estimate_time");
                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,payment_type, est_pickup_distance, est_pickup_time);

                            }


                        } else if (keyMessage.equalsIgnoreCase("your booking request is cancel by user")) {
                            stopCountDownTimer();
                            if (booking_request_dialog == null) {

                            } else {
                                if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                                    booking_request_dialog.cancel();
                                    booking_request_dialog.dismiss();
                                    diff_second = "";
                                }

                            }
                             reideAllreadyCanceled();


                        } else if (keyMessage.equalsIgnoreCase("arriving latter booking request")) {
                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");
//                              bookedRequestAlert(picklaterdate,picklatertime);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("New Current Booking")){
                GetCurrentBooking();
            }
            try {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String result = intent.getExtras().getString("result");
                    Log.e("dddd"," >>"+result);
                    if (result.equalsIgnoreCase("notmach")) {
                        sessionexp();
                    }
                }
            } catch (Exception e) {
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter("New Request Driver"));

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(MainActivity.this.getApplicationContext());
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
        getMyVehicle();
        new GetDriverProfile().execute();
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
               new CurrentBooking().execute();
            }
        };
        mTimer2 = new Timer();
        mTimer2.schedule(task, 5000, 10000);
        GetCurrentBooking();
        new CurrentBooking().execute();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void idinits() {
        book_now =  findViewById(R.id.book_now);
        todaytripcount =  findViewById(R.id.todaytripcount);
        todaytipsamount =  findViewById(R.id.todaytipsamount);
        tripcount =  findViewById(R.id.tripcount);
        lasttrip_time =  findViewById(R.id.lasttrip_time);
        todayearning =  findViewById(R.id.todayearning);
        todaytipsamount =  findViewById(R.id.todaytipsamount);
        lasttripamount =  findViewById(R.id.lasttripamount);
        lasttripdate =  findViewById(R.id.lasttripdate);
        mywalletmoney = findViewById(R.id.mywalletmoney);
        user_img = findViewById(R.id.user_img);
        online_offline_tv =  findViewById(R.id.online_offline_tv);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        carinfolay = (LinearLayout) findViewById(R.id.carinfolay);
        addcarlay = (LinearLayout) findViewById(R.id.addcarlay);
        carname =  findViewById(R.id.carname);
        carnumber =  findViewById(R.id.carnumber);
        changecar =  findViewById(R.id.changecar);
        carimage = (ImageView) findViewById(R.id.carimage);
        switch_driver_sts = (Switch) findViewById(R.id.switch_driver_sts);
        switch_driver_sts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    status = "ONLINE";
                    new ChgStatus().execute(status);
                }
                else {
                    status = "OFFLINE";
                    new ChgStatus().execute(status);
                }
            }
        });


        addcarlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyVehiclsAct.class);
                startActivity(i);
            }
        });
        changecar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCarBeanArrayList == null || myCarBeanArrayList.isEmpty()) {

                } else {
                    carSelectionPop(myCarBeanArrayList);
                }
            }
        });
        book_now.setOnClickListener(v -> {
            startActivity(new Intent(this, BookingActivity.class));
        });
    }


    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        MainActivity.this, R.raw.stylemap_3));

        gMap.setBuildingsEnabled(false);
        gMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
        drivermarker = gMap.addMarker(marker);
        drivermarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));
        gMap.animateCamera(cameraUpdate);

        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (drivermarker != null) {
                    if (isOnce) {
                        isOnce=false;
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, 18);
                        gMap.animateCamera(cameraUpdate);
                    }
                    drivermarker.setPosition(latLng1);
                    drivermarker.setRotation(location.getBearing());
                }
            }
        });


    }

    private void checkGps() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }


    }
private void DismissDialog(){
        if (dialog!=null){
            if (dialog.isShowing())dialog.dismiss();
        }
}
    private void carSelectionPop(ArrayList<MyvehicleBean> myCarBeanArrayList) {
        DismissDialog();
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.selectcar_lay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final RadioGroup radioGroup_cartype = (RadioGroup) dialog.findViewById(R.id.radioGroup_mycar);
        TextView cancel =  dialog.findViewById(R.id.cancel);
        TextView select =  dialog.findViewById(R.id.select);
        for (int i = 0; i < myCarBeanArrayList.size(); i++) {
            RadioButton rbn = new RadioButton(this);
            if (myCarBeanArrayList.get(i).getActive_car()!=null&&myCarBeanArrayList.get(i).getActive_car().equalsIgnoreCase("Yes")){
                rbn.setChecked(true);
            }
            else {
                rbn.setChecked(false);
            }
            rbn.setId(Integer.parseInt(myCarBeanArrayList.get(i).getId()));
            rbn.setText(myCarBeanArrayList.get(i).getVehicleName());
            radioGroup_cartype.addView(rbn);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                int selectedId = radioGroup_cartype.getCheckedRadioButtonId();

                selected_car_id = String.valueOf(selectedId);
                // find the radiobutton by returned id
                if (selectedId == -1) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.selectcar), Toast.LENGTH_LONG).show();
                } else {
                    RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);
                    String loadtype = radioButton.getText().toString();


                    new ChangeCar().execute(selected_car_id);
                }


            }
        });
        dialog.show();
    }

    private void showNewRequest(String firstname, String lastname, String picuplocation, String dropofflocation, final String request_id, String picklaterdate, String picklatertime, String booktype, String rating, String favorite_ride, String payment_type, String est_pickup_distance, String est_pickup_time) {
        DismissDialog();
        mPlayer.start();
        mPlayer.setLooping(true);
        dialogsts_show = true;
        request_id_main = request_id;
        // Log.e("War Msg in dialog", war_msg);
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_new_job_lay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView decline =  dialog.findViewById(R.id.decline);
        TextView datetimetv =  dialog.findViewById(R.id.datetimetv);
        TextView rating_tv =  dialog.findViewById(R.id.rating_tv);
        TextView payment_type_tv =  dialog.findViewById(R.id.payment_type_tv);
        TextView pickup_distnace_tv =  dialog.findViewById(R.id.pickup_distnace_tv);
        TextView pickup_time_tv =  dialog.findViewById(R.id.pickup_time_tv);
        rating_tv.setText("" + rating+"%");
        payment_type_tv.setText(getResources().getString(R.string.paymenttype_txt)+" " + payment_type);
        pickup_distnace_tv.setText(getResources().getString(R.string.distance)+" " + est_pickup_distance+" mile");
        pickup_time_tv.setText(getResources().getString(R.string.time)+" " + est_pickup_time+" min");

        if (booktype == null || booktype.equalsIgnoreCase("")) {
            datetimetv.setVisibility(View.GONE);
        } else {
            datetimetv.setVisibility(View.VISIBLE);
        }
        ImageView favroiteride = dialog.findViewById(R.id.favroiteride);
        if (favorite_ride.equalsIgnoreCase("yes")) {
            favroiteride.setVisibility(View.VISIBLE);
        } else {
            favroiteride.setVisibility(View.GONE);
        }

        TextView accept = dialog.findViewById(R.id.accept);
        TextView pick_location = dialog.findViewById(R.id.pick_location);
        TextView drop_location = dialog.findViewById(R.id.drop_location);
        textViewTime = dialog.findViewById(R.id.textViewTime);
        TextView username = dialog.findViewById(R.id.username);
        final ProgressBar progressBarCircle = (ProgressBar) dialog.findViewById(R.id.progressBarCircle);
        username.setText("" + firstname + " " + lastname);
        pick_location.setText("Pickup : " + picuplocation);
        drop_location.setText("Destination : " + dropofflocation);
        //int sec = 60;
        int sec = 60;
        int mili = 1000;
        int newsec = 1;
        Log.e("diff_second_popup", "POPUP " + diff_second);
        if (diff_second == null || diff_second.equalsIgnoreCase("")) {
        } else {
            int difernce = 0;
            try {
                difernce = Integer.parseInt(diff_second);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            ;
            newsec = sec - difernce;
        }
        Log.e("newsec >>", "dd " + newsec);
        timeCountInMilliSeconds = 1 * newsec * mili;
        Log.e("Count Timer", "gg " + timeCountInMilliSeconds);
        timerStatus = TimerStatus.STOPPED;
        progressBarCircle.setMax((int) 60);
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                mPlayer.stop();
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                if (dialog != null || dialog.isShowing()) {
                    dialog.cancel();
                    dialog.dismiss();
                    diff_second = "";
                }

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                stopCountDownTimer();
                timerStatus = TimerStatus.STOPPED;
            }
        }.start();
        countDownTimer.start();


        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.stop();
                if (dialog != null || dialog.isShowing()) {
                    dialog.cancel();
                    dialog.dismiss();
                    diff_second = "";
                }
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                Calendar c = Calendar.getInstance();
                TimeZone tz = c.getTimeZone();
                Log.e("TIME ZONE >>", tz.getDisplayName());
                Log.e("TIME ZONE ID>>", tz.getID());
                time_zone = tz.getID();

                status_job = "Cancel";
                new ResponseToRequest().execute(request_id, status_job);
                stopCountDownTimer();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.stop();
                if (dialog != null || dialog.isShowing()) {
                    dialog.cancel();
                    dialog.dismiss();
                    diff_second = "";
                }

                Calendar c = Calendar.getInstance();
                TimeZone tz = c.getTimeZone();
                Log.e("TIME ZONE >>", tz.getDisplayName());
                Log.e("TIME ZONE ID>>", tz.getID());
                time_zone = tz.getID();
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }


                status_job = "Accept";
                new ResponseToRequest().execute(request_id, status_job);
                stopCountDownTimer();
            }
        });
        if (!dialog.isShowing()&&dialog!=null) {
                dialog.show();
        }


    }
private void StopTime(){
    mPlayer.stop();
    if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
        NotificationUtils.r.stop();
    }

    if (dialog != null) {
        dialog.cancel();
        dialog.dismiss();
        diff_second = "";
    }
    stopCountDownTimer();
    timerStatus = TimerStatus.STOPPED;
}
    private class ResponseToRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            if (ac_dialog != null) {
                ac_dialog.show();
            }

            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String postReceiverUrl = BaseUrl.baseurl + "driver_accept_and_Cancel_request?";

                URL url = new URL(postReceiverUrl);
                Map<String,Object> params = new LinkedHashMap<>();

                params.put("request_id", strings[0]);
                params.put("status", strings[1]);
                params.put("timezone", time_zone);
                params.put("driver_id", user_id);

                Log.e("dsfafdasfsaf","param = " + params.toString());

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
            Log.e("driver_accept","" + result);
            super.onPostExecute(result);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                        reideAllreadyCanceled();
                    } else if(jsonObject.getString("status").equalsIgnoreCase("3")){
                        Toast.makeText(MainActivity.this, "Your account is not approved by admin", Toast.LENGTH_LONG).show();
                        sessionexp();
                    } else {
                        if (status_job.equalsIgnoreCase("Cancel")) {
                        } else {
                            Intent i = new Intent(MainActivity.this, TripStatusAct.class);
                            i.putExtra("request_id", request_id_main);
                            startActivity(i);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    private void reideAllreadyCanceled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d",
               /* TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
               */ TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    private void stopCountDownTimer() {
        if (countDownTimer == null) {
        } else {
            countDownTimer.cancel();
        }

    }

    private class GetDriverProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_profile?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("type", "DRIVER");
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
                Log.e("Json Driver Profile", ">>>>>>>>>>>>" + response);
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
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        String online_status = jsonObject1.getString("online_status");
                        if (jsonObject1.getString("vehicle_name")==null||jsonObject1.getString("vehicle_name").equalsIgnoreCase("")||jsonObject1.getString("vehicle_name").equalsIgnoreCase("null")){
                            carnumber.setText("");
                            carname.setText("");
                            Log.e("CAR_NUMBER"," >> "+jsonObject1.getString("vehicle_name"));

                        }
                        else {
                            Log.e("CAR_NUMBER else"," >> "+jsonObject1.getString("vehicle_name"));

                            carnumber.setText("" + jsonObject1.getString("license_plate").trim());
                            carname.setText("" + jsonObject1.getString("vehicle_name"));
                        }


                        mywalletmoney.setText("Wallet Balance $" + jsonObject1.getString("amount"));
                        if (jsonObject1.getString("amount")!=null){
                            MainActivity.amount = jsonObject1.getString("amount").replace(",","");

                        }
                        String car_image = jsonObject1.getString("car_image");
                        promo_code = jsonObject1.getString("promo_code");
                        image_url = jsonObject1.getString("image");
                        Log.e("image_url >> "," >> "+image_url);
                        if (image_url == null || image_url.equalsIgnoreCase("") || image_url.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else {
                            Glide.with(MainActivity.this)
                                    .load(image_url)
                                    .into(user_img);

                        }
                        if (car_image == null || car_image.equalsIgnoreCase("") || car_image.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else {
                            Glide.with(MainActivity.this)
                                    .load(car_image)
                                    .into(carimage);

                        }

                        if (online_status.equalsIgnoreCase("ONLINE")) {
                            switch_driver_sts.setChecked(true);
                            online_offline_tv.setText(getResources().getString(R.string.gooffline));
                        } else {
                            switch_driver_sts.setChecked(false);
                            online_offline_tv.setText(getResources().getString(R.string.goonline));

                        }
                        JSONObject jsonObject3 = jsonObject1.getJSONObject("last_trip");
                        JSONObject jsonObject4 = jsonObject1.getJSONObject("today_earn");
                        //   JSONObject jsonObject5 = jsonObject1.getJSONObject("today_tips");
                        if (jsonObject3.getString("return").equalsIgnoreCase("success")) {
                            lasttripamount.setText("$" + jsonObject3.getString("earning"));
                            String lasttripdates = jsonObject3.getString("date_time");
                            try {
                                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lasttripdates);
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm aa");
                                SimpleDateFormat formatter2 = new SimpleDateFormat("dd MMM, yyyy");
                                String time = formatter.format(date1);

                                String mainDate = formatter2.format(date1);
                                lasttrip_time.setText("" + time);
                                lasttripdate.setText("" + mainDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        if (jsonObject4.getString("return").equalsIgnoreCase("success")) {
                            todayearning.setText("$" + jsonObject4.getString("amount"));
                            todaytripcount.setText("" + getResources().getString(R.string.trip) + " " + jsonObject4.getString("trip"));

                        }
                        SharedPreferences prefd = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                        String firebase_regids = prefd.getString("regId", null);

                        Log.e("rddof", " >> " + firebase_regids);

                        if (jsonObject1.getString("register_id") == null || jsonObject1.getString("register_id").equalsIgnoreCase("") || jsonObject1.getString("register_id").equalsIgnoreCase("0") || jsonObject1.getString("register_id").equalsIgnoreCase("null")) {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                            String firebase_regid = pref.getString("regId", null);

                            Log.e("firebase_regid >>>prof", " >> " + firebase_regid);
                            if (firebase_regid != null) {
                                new UpdateRegistrationid().execute(firebase_regid);
                            }
                        }
                        if (min_sts) {
                            min_sts = false;
                           // new MinimumWalletCheck().execute();
                        }

                        if (jsonObject1.getString("state")==null||jsonObject1.getString("state").equalsIgnoreCase("")||jsonObject1.getString("state").equalsIgnoreCase("0")){
                            profileUpdate();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }
    private void profileUpdate() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv =  canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv =  canceldialog.findViewById(R.id.body_tv);
        body_tv.setText(""+getResources().getString(R.string.updateprofile));
        no_tv.setText(""+getResources().getString(R.string.later));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                Intent i = new Intent(MainActivity.this, ProfileAct.class);
                startActivity(i);
            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }
    public class CurrentBooking extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_current_booking?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("type", "DRIVER");
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
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                Log.e("CURRENT_BOOKING >>>", "" + response);
                if (response==null){
                    return;
                }
                JSONObject jsonObject = new JSONObject(response);
                String msg = jsonObject.getString("message");
                if (msg.equalsIgnoreCase("successfull")) {
                    mTimer2.cancel();
                    diff_second = jsonObject.getString("diff_second");
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        request_id = jsonObject1.getString("id");
                        try {
                            Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject1.getString("req_datetime"));
                            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                            strDate = formatter.format(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String status = jsonObject1.getString("status");
                        if (status.equalsIgnoreCase("Pending")) {
                            String firstname = "";
                            String lastname = "";
                            String rating = "";


                            String picuplocation = jsonObject1.getString("picuplocation");
                            String dropofflocation = jsonObject1.getString("dropofflocation");
                            request_id = String.valueOf(jsonObject1.getString("id"));
                            String picklaterdate = jsonObject1.getString("picklaterdate");
                            String picklatertime = jsonObject1.getString("picklatertime");
                            String booktype = jsonObject1.getString("booktype");
                            String favorite_ride = jsonObject1.getString("favorite_ride");
                            String payment_type = jsonObject1.getString("payment_type");
                            String est_pickup_distance = jsonObject1.getString("estimate_distance");
                            String est_pickup_time = jsonObject1.getString("estimate_time");
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("user_details");
                            for (int k = 0; k < jsonArray1.length(); k++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(k);
                                firstname = jsonObject2.getString("first_name");
                                lastname = jsonObject2.getString("last_name");
                                rating = jsonObject2.getString("rating");
                            }
                            if (dialog == null) {
                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride, payment_type, est_pickup_distance, est_pickup_time);
                            } else if (dialog.isShowing()) {

                            } else {
                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride, payment_type, est_pickup_distance, est_pickup_time);
                            }
                        } /*else if (status.equalsIgnoreCase("Accept")) {
                            Intent k = new Intent(MainActivity.this, TripStatusAct.class);
                            startActivity(k);
                        } else if (status.equalsIgnoreCase("Arrived")) {
                            Intent j = new Intent(MainActivity.this, TripStatusAct.class);
                            startActivity(j);
                        } else if (status.equalsIgnoreCase("Start")) {
                            Intent j = new Intent(MainActivity.this, TripStatusAct.class);
                            startActivity(j);
                        } else if (status.equalsIgnoreCase("End")) {
                            Intent j = new Intent(MainActivity.this, PaymentAct.class);
                            startActivity(j);
                        }*/
                    }
                }else {
                    StopTime();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void GetCurrentBooking(){
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id", user_id);
        param.put("type", "DRIVER");
        ApiCallBuilder.build(this).setUrl(BaseUrl.baseurl + "get_current_booking")
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    Log.e("CURRENT BOOKING >>>", "" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {
                        mTimer2.cancel();
                        diff_second = jsonObject.getString("diff_second");
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            request_id = jsonObject1.getString("id");
                            try {
                                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject1.getString("req_datetime"));
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                strDate = formatter.format(date1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String status = jsonObject1.getString("status");
                             if (status.equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(MainActivity.this, TripStatusAct.class);
                                startActivity(k);
                            } else if (status.equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(MainActivity.this, TripStatusAct.class);
                                startActivity(j);
                            } else if (status.equalsIgnoreCase("Start")) {
                                Intent j = new Intent(MainActivity.this, TripStatusAct.class);
                                startActivity(j);
                            } else if (status.equalsIgnoreCase("End")) {
                                Intent j = new Intent(MainActivity.this, PaymentAct.class);
                                startActivity(j);
                            }
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void Failed(String error) {

            }
        });
    }

    private class ChgStatus extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_online_status?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("status", strings[0]);
                params.put("type", "DRIVER");
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
            if (ac_dialog != null && ac_dialog.isShowing()) {
                ac_dialog.dismiss();
            }
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                mySession.onlineuser(false);
                Intent i = new Intent(getApplicationContext(), DashBoardAct.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }


        }
    }

    private class MinimumWalletCheck extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://hitchride.net/webservice/get_driver_setting
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_driver_setting?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
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
            if (ac_dialog != null && ac_dialog.isShowing()) {
                ac_dialog.dismiss();
            }
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    Log.e("Driver Setting", " >" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        String cash_ride_minimum_amt = jsonObject1.getString("cash_ride_minimum_amt");
                        if (cash_ride_minimum_amt == null || cash_ride_minimum_amt.equalsIgnoreCase("")) {

                        } else {
                            double min_req = Double.parseDouble(cash_ride_minimum_amt);
                            double my_amount = Double.parseDouble(amount);
                            Log.e("Driver Setting Amt", " >" + min_req + " >> " + my_amount);

                            if (min_req > my_amount) {
                                updateWalletAmount();
                            }


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private class ChangeCar extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/change_car?driver_id=21&car_id=3
            try {
                String postReceiverUrl = BaseUrl.baseurl + "change_car?";
                Log.e("CHANGECAR"," >> "+postReceiverUrl+"driver_id="+user_id+"&vehicle_id="+strings[0]);

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("driver_id", user_id);
                params.put("vehicle_id", strings[0]);



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
            if (result == null) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();

            } else {
                new GetDriverProfile().execute();
                //getMyVehicle();
            }


        }
    }


    private class UpdateRegistrationid extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            //http://mobileappdevelop.co/NAXCAN/webservice/update_register_id?user_id=31&register_id=1234
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_register_id?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("register_id", strings[0]);

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
                Log.e("Update Register id ", ">>>>>>>>>>>>" + response);
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
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
            }
        }
    }

    private void appUpdate() {
        //   Log.e("War Msg in dialog", war_msg);
         canceldialog = new Dialog(MainActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv =  canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv =  canceldialog.findViewById(R.id.body_tv);
        body_tv.setText("" + getResources().getString(R.string.appupdateneed));
        no_tv.setText("" + getResources().getString(R.string.remindlater));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                final String appPackageName = BuildConfig.APPLICATION_ID; // package name of the app
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySession.setAppUpdate("later");
                canceldialog.dismiss();

            }
        });
        if (canceldialog==null){
            canceldialog.show();
        }
        else if (canceldialog.isShowing()){

        }
        else {
            canceldialog.show();

        }



    }

    private void updateWalletAmount() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv =  canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv =  canceldialog.findViewById(R.id.body_tv);
        body_tv.setText("" + getResources().getString(R.string.updatewallet));
        no_tv.setText("" + getResources().getString(R.string.later));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                Intent i = new Intent(MainActivity.this, WalletAct.class);
                startActivity(i);
            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

    private void sessionexp() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_one_button);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView body_tv =  canceldialog.findViewById(R.id.body_tv);
        body_tv.setText("" + getResources().getString(R.string.sessionexpired));
        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                mySession.logoutUser();
                Intent ie = new Intent(MainActivity.this, TrackingService.class);
                stopService(ie);
                Intent i = new Intent(MainActivity.this, LoginAct.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });
        canceldialog.show();


    }
    private void getMyVehicle() {
        myCarBeanArrayList = new ArrayList<>();
        Call<ResponseBody> call = ApiClient.getApiInterface().getMyVehicleList(user_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject object = new JSONObject(responseData);
                        Log.e("My Vehicle >", " >" + responseData);
                        if (object.getString("status").equals("1")) {
                            carinfolay.setVisibility(View.VISIBLE);
                            addcarlay.setVisibility(View.GONE);
                            MyVehicleCls successData = new Gson().fromJson(responseData, MyVehicleCls.class);
                            myCarBeanArrayList.addAll(successData.getResult());

                        }
                        else {
                            carinfolay.setVisibility(View.GONE);
                            addcarlay.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        carinfolay.setVisibility(View.GONE);
                        addcarlay.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                }
                else {
                    carinfolay.setVisibility(View.GONE);
                    addcarlay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();


            }
        });
    }

}

