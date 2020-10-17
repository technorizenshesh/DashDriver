package main.com.dashdriver.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.dashdriver.MainActivity;
import main.com.dashdriver.R;
import main.com.dashdriver.app.Config;
import main.com.dashdriver.constant.ACProgressCustom;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.GPSTracker;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.draweractivity.BaseActivity;
import main.com.dashdriver.service.MyFirebaseMessagingService;
import main.com.dashdriver.service.TrackingService;
import main.com.dashdriver.utils.NotificationUtils;

public class DashBoardAct extends BaseActivity implements OnMapReadyCallback {
    private FrameLayout contentFrameLayout;
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    TextView changecar, user_name, mywallettv;
    public static int sts = 0;
    MySession mySession;
    private String user_log_data = "";
    public static String car_number="",amount = "", promo_code = "", user_id = "", image_url, firstname_str = "", lastname_str = "";
    private CircleImageView user_img;

    private String status,time_zone="";


    String car_name = "",status_str="";
    private TextView mywalletmoney,carname, carnumber, goonline;

    BroadcastReceiver mRegistrationBroadcastReceiver;
    String status_job = "", request_id_main = "";
    public static int driver_sts = 0;
    CountDownTimer yourCountDownTimer;
    private ProgressBar progressBarCircle;
    boolean dialogsts_show = false;
    MapStyleOptions style;
    Dialog booking_request_dialog;
    RatingBar rating;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }
    ACProgressCustom ac_dialog;
    private long timeCountInMilliSeconds;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private TextView textViewTime;
    private CountDownTimer countDownTimer;
    public static String request_id = "",password="", strDate = "";
    private TextView todaytripcount,ratetv, tripcount, todaytipsamount, todayearning, driver_name, car_nametv, cartypename, car_numbertv, lasttrip_time, lasttripamount, lasttripdate;
    CircleImageView driverimg;

    Marker drivermarker;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        contentFrameLayout = (FrameLayout) findViewById(R.id.contentFrame);
        getLayoutInflater().inflate(R.layout.activity_dash_board, contentFrameLayout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();

        time_zone = tz.getID();
        sts = 0;
        mySession = new MySession(this);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

        user_log_data = mySession.getKeyAlldata();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    password = jsonObject1.getString("password");
                    promo_code = jsonObject1.getString("promo_code");
                    // amount = jsonObject1.getString("amount");
                  //  mywallettv = (TextView) findViewById(R.id.mywallettv);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mapStyle();
        idinits();
        checkGps();
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {

        } else {
            String message = bundle.getString("message");
            if (message == null) {

            } else {
                JSONObject data = null;
                try {
                    data = new JSONObject(message);
                    String keyMessage = data.getString("key").trim();
                    Log.e("KEY MSG =", "" + keyMessage);
                    if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                        NotificationUtils.r.stop();
                    }

                    if (keyMessage.equalsIgnoreCase("your booking request is Now")) {
                        String firstname = data.getString("first_name");
                        String lastname = data.getString("last_name");
                        String picuplocation = data.getString("picuplocation");
                        String dropofflocation = data.getString("dropofflocation");
                        request_id = String.valueOf(data.getInt("request_id"));
                        //showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id);
                    } else if (keyMessage.equalsIgnoreCase("your booking request is Letter")) {
                        String firstname = data.getString("first_name");
                        String lastname = data.getString("last_name");
                        String picuplocation = data.getString("picuplocation");
                        String dropofflocation = data.getString("dropofflocation");
                        request_id = String.valueOf(data.getInt("request_id"));
                        //showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


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
                        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying())
                        {
                            NotificationUtils.r.stop();
                        }

                        if (keyMessage.equalsIgnoreCase("your booking request is Now")) {

                            String firstname = data.getString("first_name");
                            String lastname = data.getString("last_name");
                            String picuplocation = data.getString("picuplocation");
                            String dropofflocation = data.getString("dropofflocation");
                            request_id = String.valueOf(data.getInt("request_id"));
                            // showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id);
                        } else if (keyMessage.equalsIgnoreCase("your booking request is Letter")) {

                            String firstname = data.getString("first_name");
                            String lastname = data.getString("last_name");
                            String picuplocation = data.getString("picuplocation");
                            String dropofflocation = data.getString("dropofflocation");
                            request_id = String.valueOf(data.getInt("request_id"));

                            // showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id);

                        } else if (keyMessage.equalsIgnoreCase("your booking request is cancel by user")) {

                            if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                                booking_request_dialog.dismiss();
                            }
                            reideAllreadyCanceled();


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void mapStyle()
    {
        style = new MapStyleOptions("https://maps.googleapis.com/maps/api/staticmap?key=" + getResources().getString(R.string.googlekey) + "&center=-33.9,151.14999999999998&zoom=12&format=png&maptype=roadmap&style=element:geometry%7Ccolor:0xf5f5f5&style=element:labels.icon%7Cvisibility:off&style=element:labels.text.fill%7Ccolor:0x616161&style=element:labels.text.stroke%7Ccolor:0xf5f5f5&style=feature:administrative.land_parcel%7Celement:labels.text.fill%7Ccolor:0xbdbdbd&style=feature:poi%7Celement:geometry%7Ccolor:0xeeeeee&style=feature:poi%7Celement:labels.text.fill%7Ccolor:0x757575&style=feature:poi.park%7Celement:geometry%7Ccolor:0xe5e5e5&style=feature:poi.park%7Celement:labels.text.fill%7Ccolor:0x9e9e9e&style=feature:road%7Celement:geometry%7Ccolor:0xffffff&style=feature:road.arterial%7Celement:labels.text.fill%7Ccolor:0x757575&style=feature:road.highway%7Celement:geometry%7Ccolor:0xdadada&style=feature:road.highway%7Celement:labels.text.fill%7Ccolor:0x616161&style=feature:road.local%7Celement:labels.text.fill%7Ccolor:0x9e9e9e&style=feature:transit.line%7Celement:geometry%7Ccolor:0xe5e5e5&style=feature:transit.station%7Celement:geometry%7Ccolor:0xeeeeee&style=feature:water%7Celement:geometry%7Ccolor:0xc9c9c9&style=feature:water%7Celement:labels.text.fill%7Ccolor:0x9e9e9e&size=480x360");
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String result = intent.getExtras().getString("result");
                    if (result.equalsIgnoreCase("notmach")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardAct.this);

                        builder.setMessage("Your Session is expire,Please login again")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                               /* Intent serviceIntent = new Intent(BaseActivity.this, TrackingService.class);
                                stopService(serviceIntent);*/
                                        mySession.logoutUser();
                                        Intent ie = new Intent(DashBoardAct.this, TrackingService.class);
                                        stopService(ie);
                                    Intent i = new Intent(DashBoardAct.this, LoginAct.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);

                                    }
                                });

                        AlertDialog alert = builder.create();
                        if (alert != null && alert.isShowing()) {

                        } else {
                            alert.show();
                        }


                    }
                }

            } catch (Exception e) {

            }


        }
    };

    @Override
    protected void onResume() {
        if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
            NotificationUtils.r.stop();
        }
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }

        Log.e("notification_data >>", "" + MyFirebaseMessagingService.notification_data);
        super.onResume();
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }
        new GetDriverProfile().execute();
        new GetCurrentBooking().execute();
        registerReceiver(broadcastReceiver, new IntentFilter("New Request Driver"));
        LocalBroadcastManager.getInstance(DashBoardAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(DashBoardAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(DashBoardAct.this.getApplicationContext());
    }

    private void idinits() {

        mywalletmoney = (TextView) findViewById(R.id.mywalletmoney);
        ratetv = (TextView) findViewById(R.id.ratetv);
        rating = (RatingBar) findViewById(R.id.rating);
        todaytripcount = (TextView) findViewById(R.id.todaytripcount);
        todaytipsamount = (TextView) findViewById(R.id.todaytipsamount);
        tripcount = (TextView) findViewById(R.id.tripcount);
        lasttrip_time = (TextView) findViewById(R.id.lasttrip_time);
        todayearning = (TextView) findViewById(R.id.todayearning);
        todaytipsamount = (TextView) findViewById(R.id.todaytipsamount);
        lasttripamount = (TextView) findViewById(R.id.lasttripamount);
        lasttripdate = (TextView) findViewById(R.id.lasttripdate);
        car_numbertv = (TextView) findViewById(R.id.car_numbertv);
        cartypename = (TextView) findViewById(R.id.cartypename);
        car_nametv = (TextView) findViewById(R.id.car_nametv);
        driver_name = (TextView) findViewById(R.id.driver_name);
        driverimg = (CircleImageView) findViewById(R.id.driverimg);
        goonline = (TextView) findViewById(R.id.goonline);
        goonline.setVisibility(View.VISIBLE);
        goonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status_str.equalsIgnoreCase("Deactive")){

                    waitDocumentApproval();
                  // Toast.makeText(DashBoardAct.this,getResources().getString(R.string.notactive),Toast.LENGTH_LONG).show();
                }
                else {
                    new ChgStatus().execute();
                }

            }
        });



        carname = (TextView) findViewById(R.id.carname);
        carnumber = (TextView) findViewById(R.id.carnumber);
    }
    private void waitDocumentApproval() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DashBoardAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_one_button);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv = (TextView) canceldialog.findViewById(R.id.body_tv);
        body_tv.setText(""+getResources().getString(R.string.notactive));
      //  no_tv.setText(""+getResources().getString(R.string.remindlater));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();

            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canceldialog.dismiss();

            }
        });
       // if (canceldialog==null&&!canceldialog.isShowing())
        canceldialog.show();


    }


    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(DashBoardAct.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DashBoardAct.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        DashBoardAct.this, R.raw.stylemap_3));

        gMap.setMyLocationEnabled(true);
        gMap.setBuildingsEnabled(false);
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 19);
        //  gMap.addMarker(marker).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));

        drivermarker = gMap.addMarker(marker);
        drivermarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));
        gMap.animateCamera(cameraUpdate);

/*
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.stylemap));
*/
        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                Log.e("my Loc  on change >>", "kk " + location.getLatitude());
                LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());

                if (drivermarker != null) {
                    String latitudes = String.valueOf(location.getLatitude());
                    String longitudes = String.valueOf(location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, 19);
                    gMap.animateCamera(cameraUpdate);
                    drivermarker.setPosition(latLng1);

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
            // if gps off get lat long from network
            //   locationfromnetwork();
            gpsTracker.showSettingsAlert();
        }


    }


    private void showNewRequest(String firstname, String lastname, String picuplocation, String dropofflocation, final String request_id) {
        dialogsts_show = true;
        request_id_main = request_id;
        //   Log.e("War Msg in dialog", war_msg);
        booking_request_dialog = new Dialog(DashBoardAct.this);
        booking_request_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        booking_request_dialog.setCancelable(false);
        booking_request_dialog.setContentView(R.layout.custom_new_job_lay);
        booking_request_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView decline = (TextView) booking_request_dialog.findViewById(R.id.decline);
        TextView accept = (TextView) booking_request_dialog.findViewById(R.id.accept);
        TextView pick_location = (TextView) booking_request_dialog.findViewById(R.id.pick_location);
        TextView drop_location = (TextView) booking_request_dialog.findViewById(R.id.drop_location);
        textViewTime = (TextView) booking_request_dialog.findViewById(R.id.textViewTime);
        TextView username = (TextView) booking_request_dialog.findViewById(R.id.username);
        progressBarCircle = (ProgressBar) booking_request_dialog.findViewById(R.id.progressBarCircle);
        username.setText("" + firstname + " " + lastname);
        pick_location.setText("" + picuplocation);
        drop_location.setText("" + dropofflocation);
        timeCountInMilliSeconds = 1 * 59000;
        timerStatus = TimerStatus.STOPPED;
        startStop();
        yourCountDownTimer = new CountDownTimer(59000, 1000) {

            public void onTick(long millisUntilFinished) {


            }

            public void onFinish() {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                if (booking_request_dialog != null && booking_request_dialog.isShowing()) {
                    booking_request_dialog.dismiss();
                }


            }
        }.start();


        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                booking_request_dialog.dismiss();
                status_job = "Cancel";
                new ResponseToRequest().execute(request_id, status_job);
                stopCountDownTimer();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                booking_request_dialog.dismiss();
                status_job = "Accept";
                new ResponseToRequest().execute(request_id, status_job);
                stopCountDownTimer();
            }
        });
        if (booking_request_dialog.isShowing()) {

        } else {
            booking_request_dialog.show();
        }


    }

    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            startCountDownTimer();

        } else {

            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    private void setTimerValues() {
        int time = 1;

        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 59 * 1000;
    }

    private void startCountDownTimer()
    {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
            }
            @Override
            public void onFinish() {
                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                setTimerValues();
                stopCountDownTimer();
                timerStatus = TimerStatus.STOPPED;
            }
        }.start();
        countDownTimer.start();
    }
    private String hmsTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    private void setProgressBarValues() {
        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    private class GetDriverProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog!=null){
                ac_dialog.show();
            }

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://technorizen.com/WORKSPACE1/shipper/webservice/get_driver?driver_id=36
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
            Log.e("Json Driver Profile", ">>>>>>>>>>>>" + result);
            if (ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        user_id = jsonObject1.getString("id");
                        image_url = jsonObject1.getString("image");
                        status_str=jsonObject1.getString("status");
                        mywalletmoney.setText("$"+jsonObject1.getString("amount"));
                        MainActivity.amount = jsonObject1.getString("amount");
                        firstname_str = jsonObject1.getString("first_name");
                        lastname_str = jsonObject1.getString("last_name");
                        cartypename.setText(""+jsonObject1.getString("car_type_name"));
                        car_number = jsonObject1.getString("car_number").trim();
                        car_name = jsonObject1.getString("vehicle_name");
                        car_nametv.setText("" + car_name);
                        car_numbertv.setText("" + car_number.trim());
                        String ratings = jsonObject1.getString("rating");
                        if (ratings!=null){
                            rating.setRating(Float.parseFloat(ratings));
                            ratetv.setText("( "+ratings+"/5 )");
                        }
                        user_img = (CircleImageView) findViewById(R.id.user_img);
                        user_name = (TextView) findViewById(R.id.user_name);
                        user_name.setText("" + firstname_str + " " + lastname_str);
                        driver_name.setText("" + firstname_str + " " + lastname_str);



                        if (image_url == null) {

                        } else if (image_url.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else if (image_url.equalsIgnoreCase("")) {

                        } else {
                            Picasso.with(DashBoardAct.this).load(image_url).into(user_img);
                            Picasso.with(DashBoardAct.this).load(image_url).into(driverimg);

                        }

                        String online_status = jsonObject1.getString("online_status");
                        if (online_status.equalsIgnoreCase("ONLINE")) {
                            mySession.onlineuser(true);
                            Intent i = new Intent(DashBoardAct.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else {
                            mySession.onlineuser(false);
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
                            todaytripcount.setText(""+getResources().getString(R.string.trip)+" " + jsonObject4.getString("trip"));

                        }

/*
                        if (jsonObject5.getString("return").equalsIgnoreCase("success")) {
                            todaytipsamount.setText("$ " + jsonObject5.getString("amount"));
                            tripcount.setText(""+getResources().getString(R.string.trip)+" " + jsonObject5.getString("trip"));
                        }
*/



                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }
    private class ChgStatus extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog!=null){
                ac_dialog.show();
            }

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/update_online_status?status=OFFLINE&user_id=1&type=DRIVER
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_online_status?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();


                params.put("user_id", user_id);
                params.put("status", "ONLINE");
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
            if (ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                mySession.onlineuser(true);
                Log.e("Change STATUS", " res " + result);
                Intent i = new Intent(DashBoardAct.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(DashBoardAct.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        if (yourCountDownTimer!=null){
            yourCountDownTimer.cancel();
        }
    }

    private class ResponseToRequest extends AsyncTask<String, String, String> {
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
//http://mobileappdevelop.co/NAXCAN/webservice/driver_accept_and_Cancel_request?request_id=1&status=Accept
            //http://mobileappdevelop.co/NAXCAN/webservice/
            try {
                String postReceiverUrl = BaseUrl.baseurl + "driver_accept_and_Cancel_request?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();


                params.put("request_id", strings[0]);
                params.put("status", strings[1]);

                params.put("timezone", time_zone);
                params.put("driver_id", user_id);
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
                Toast.makeText(DashBoardAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(DashBoardAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                        reideAllreadyCanceled();
                    } else {
                        if (status_job.equalsIgnoreCase("Cancel")) {

                        } else {
                            Intent i = new Intent(DashBoardAct.this, TripStatusAct.class);
                            i.putExtra("request_id", request_id_main);
                            startActivity(i);
                            //finish();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog!=null){
                ac_dialog.show();
            }

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//http://mobileappdevelop.co/NAXCAN/webservice/get_current_booking?user_id=1
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    Log.e("CURRENT BOOKING >>>", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {
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
                                String picuplocation = jsonObject1.getString("picuplocation");
                                String dropofflocation = jsonObject1.getString("dropofflocation");
                                String request_id = String.valueOf(jsonObject1.getString("id"));
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("user_details");
                                for (int k = 0; k < jsonArray1.length(); k++) {
                                    JSONObject jsonObject2 = jsonArray1.getJSONObject(k);
                                    firstname = jsonObject2.getString("first_name");
                                    lastname = jsonObject2.getString("last_name");

                                }
                                if (dialogsts_show) {
                                    dialogsts_show = false;
                                } else {
                                    //  showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id);
                                }


                            } else if (status.equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(DashBoardAct.this, TripStatusAct.class);
                                startActivity(k);
                                // finish();

                            } else if (status.equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(DashBoardAct.this, TripStatusAct.class);
                                startActivity(j);
                                // finish();

                            } else if (status.equalsIgnoreCase("Start")) {
                                Intent j = new Intent(DashBoardAct.this, TripStatusAct.class);
                                startActivity(j);
                                // finish();


                            } else if (status.equalsIgnoreCase("End")) {
                                Intent j = new Intent(DashBoardAct.this, TripStatusAct.class);
                                startActivity(j);
                                //finish();
                            }



/*
                            else {
                                Intent k = new Intent(MainActivity.this, MyBookingAct.class);
                                startActivity(k);
                                finish();

                            }
*/

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
        final Dialog canceldialog = new Dialog(DashBoardAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
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


}