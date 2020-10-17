package main.com.dashdriver.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.dashdriver.MainActivity;
import main.com.dashdriver.R;
import main.com.dashdriver.app.Config;
import main.com.dashdriver.constant.ACProgressCustom;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.DataParser;
import main.com.dashdriver.constant.GPSTracker;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.utils.NotificationUtils;

public class TripStatusAct extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    Toolbar toolbar;
    private double longitude = 0.0, latitude = 0.0;
    MySession mySession;
    //new code location chekc
    protected static final String TAG = "MainActivity";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected Boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;
    Marker drivermarker;
    GPSTracker gps;
    private RelativeLayout exit_app_but;
    private double pic_lat = 0.0, pick_lon = 0.0, drop_lat = 0.0, drop_lon = 0.0, driver_lat = 0.0, driver_lon = 0.0;
    ACProgressCustom ac_dialog;
    private String dropofflocation_str = "", pickup_location_str = "", Status_Chk = "", booking_user_id = "", usermobile_str = "", userimage_str = "", username_str = "", Status = "", user_log_data = "", user_id = "", time_zone = "", request_id = "";
    private TextView location_tv, sts_text, tripsts_but, payment_type_tv;
    LatLng startlatlong;
    final Timer timer = new Timer();
    BroadcastReceiver mRegistrationBroadcastReceiver;
    private LinearLayout botumlay;
    private MarkerOptions options = new MarkerOptions();
    Marker marker;
    private ImageView navigate, message_lay, calllay;
    private String language = "";
    MyLanguageSession myLanguageSession;
    private CircleImageView user_image;
    private TextView user_name, tv_estimate_time,tv_amount;
    private LatLng newLatLng;
    ArrayList<LatLng> points = new ArrayList<>();
    private MarkerOptions DirverMarker;
    private String UserName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_trip_status);
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
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data != null) {
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mySession = new MySession(this);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw new Error("Can't find tool bar, did you forget to add it in Activity layout file?");
        }

        setSupportActionBar(toolbar);
        idinits();
        clcickevent();
        checkGps();

        try {
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
                    Log.e("Push notification: ", "" + message);
                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        Log.e("KEY MSG =", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                            request_id = data.getString("request_id");
                            MainActivity.request_id = request_id;
                            usercancelRide();
                        } else if (keyMessage.equalsIgnoreCase("your ride is update")) {
                            if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                NotificationUtils.r.stop();
                            }
                            new GetCurrentBooking().execute();
                        } else if (keyMessage.equalsIgnoreCase("drop point is added")) {
                            if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                NotificationUtils.r.stop();
                            }

                            newPointAdded();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (getIntent().getExtras()!=null) {
            UserName = getIntent().getExtras().getString("user_name");
        }
        new GetCurrentBooking().execute();
    }

    private void clcickevent() {
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naviGationWith();
            }
        });
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCancelRide();
            }
        });
        tripsts_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Status >>", ">" + Status);
                if (Status.equalsIgnoreCase("Accept")) {
                    areUsureEnd("arrived");
                } else if (Status.equalsIgnoreCase("Arrived")) {
                    areUsureEnd("start");
                } else if (Status.equalsIgnoreCase("Start")) {
                    double distance_difference = distFrom(latitude, longitude, drop_lat, drop_lon);
                    if (distance_difference > 1) {
                        distanceWarningPop();
                    } else {
                        areUsureEnd("end");
                    }
                }
            }
        });
        message_lay.setOnClickListener(v -> {
            Intent i = new Intent(this, ChatingAct.class);
            i.putExtra("receiver_id", booking_user_id);
            i.putExtra("request_id", request_id);
            i.putExtra("receiver_img", userimage_str);
            i.putExtra("receiver_name", username_str);
            i.putExtra("block_status", "");
            startActivity(i);
        });
        calllay.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + usermobile_str));
            startActivity(callIntent);
        });
    }

    private void naviGationWith() {


        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_navigate_option);
        //  dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.back_pop_col)));
        TextView waze = (TextView) dialogSts.findViewById(R.id.waze);
        TextView googlemap = (TextView) dialogSts.findViewById(R.id.googlemap);
        TextView cancel = (TextView) dialogSts.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  sts = 1;
                dialogSts.dismiss();

                //employerAccept();

            }
        });
        googlemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                if (Status != null && Status.equalsIgnoreCase("accept")) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + pickup_location_str));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + dropofflocation_str));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        });
        waze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();

                if (Status != null && Status.equalsIgnoreCase("ACCept")) {
                    String uri = "https://waze.com/ul?ll=" + pic_lat + "," + pick_lon + "&navigate=yes";
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                } else {
                    String uri = "https://waze.com/ul?ll=" + drop_lat + "," + drop_lon + "&navigate=yes";
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                }


                //String uri = "geo: latitude,longtitude";

            }
        });

        dialogSts.show();


    }

    @Override
    public void onBackPressed() {
    }

    private void areUsureEnd(final String status) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.bookig_cancel_me_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        if (status.equalsIgnoreCase("end")) {
            message_tv.setText("" + getResources().getString(R.string.toendtrip));
        } else if (status.equalsIgnoreCase("arrived")) {
            message_tv.setText("" + getResources().getString(R.string.toarrived));
        } else {
            message_tv.setText("" + getResources().getString(R.string.tostarttrip));
        }

        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equalsIgnoreCase("end")) {
                    Status_Chk = "End";
                } else if (status.equalsIgnoreCase("arrived")) {
                    Status_Chk = "Arrived";
                } else {
                    Status_Chk = "Start";
                }
                Calendar c = Calendar.getInstance();
                TimeZone tz = c.getTimeZone();
                Log.e("TIME ZONE >>", tz.getDisplayName());
                Log.e("TIME ZONE ID>>", tz.getID());
                time_zone = tz.getID();


                new ResponseToRequest().execute(Status_Chk);
                canceldialog.dismiss();

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

    private void distanceWarningPop() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        final TextView heading_tv = (TextView) canceldialog.findViewById(R.id.heading_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);

        heading_tv.setText("" + getResources().getString(R.string.warning));
        message_tv.setText("" + getResources().getString(R.string.notonpoint));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        no_tv.setText("" + getResources().getString(R.string.arrived));
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
                Status_Chk = "End";
                Calendar c = Calendar.getInstance();
                TimeZone tz = c.getTimeZone();
                Log.e("TIME ZONE >>", tz.getDisplayName());
                Log.e("TIME ZONE ID>>", tz.getID());
                time_zone = tz.getID();
                new ResponseToRequest().execute(Status_Chk);
            }
        });
        canceldialog.show();


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
        LocalBroadcastManager.getInstance(TripStatusAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(TripStatusAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(TripStatusAct.this.getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(TripStatusAct.this).unregisterReceiver(mRegistrationBroadcastReceiver);

    }

    private void idinits() {
        payment_type_tv = findViewById(R.id.payment_type_tv);
        navigate = findViewById(R.id.navigate);
        botumlay = findViewById(R.id.botumlay);
        sts_text = findViewById(R.id.sts_text);
        exit_app_but = findViewById(R.id.exit_app_but);
        location_tv = findViewById(R.id.location_tv);
        tripsts_but = findViewById(R.id.tripsts_but);
        message_lay = findViewById(R.id.message_lay);
        calllay = findViewById(R.id.calllay);
        user_image = findViewById(R.id.user_image);
        user_name = findViewById(R.id.user_name);
        tv_estimate_time = findViewById(R.id.tv_estimate_time);
        tv_amount = findViewById(R.id.tv_amount);
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist * 1.60934;
    }

    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog != null) {
                ac_dialog.show();
            }
            if (gMap != null) {
                gMap.clear();
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
                String postReceiverUrl = BaseUrl.baseurl + "get_current_booking?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                Log.e("User Sta id,", "" + user_id);
                params.put("user_id", user_id);
                params.put("type", "DRIVER");
                params.put("timezone", time_zone);

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
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    Log.e("ResposneBooking", "====>" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {
                        String sub_total=jsonObject.getString("sub_total");
                        tv_amount.setText("ETA:$"+sub_total);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            request_id = jsonObject1.getString("id");
                            booking_user_id = jsonObject1.getString("user_id");
                            MainActivity.request_id = request_id;
                            if (jsonObject1.getString("user_details").length()>5){
                                JSONArray jsonArray3 = jsonObject1.getJSONArray("user_details");
                                for (int user = 0; user < jsonArray3.length(); user++) {
                                JSONObject jsonObject2 = jsonArray3.getJSONObject(user);
                                username_str = jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name");
                                usermobile_str = jsonObject2.getString("mobile");
                                userimage_str = jsonObject2.getString("profile_image");
                                Picasso.with(TripStatusAct.this).load(userimage_str).placeholder(R.drawable.user).into(user_image);
                                user_name.setText(username_str);
                            }}else {
                                user_name.setText(UserName);
                            }
                            dropofflocation_str = jsonObject1.getString("dropofflocation");
                            pickup_location_str = jsonObject1.getString("picuplocation");
                            location_tv.setText("" + jsonObject1.getString("picuplocation"));
                            payment_type_tv.setText("Payment Type : " + jsonObject1.getString("payment_type"));
                            Status = jsonObject1.getString("status");

                            if (Status.equalsIgnoreCase("Accept")) {
                                sts_text.setText(getResources().getString(R.string.picpassenger) + " " + username_str);
                                tripsts_but.setText(getResources().getString(R.string.arrived));
                                botumlay.setBackgroundResource(R.color.buttoncol);
                            } else if (Status.equalsIgnoreCase("Arrived")) {
                                sts_text.setText(getResources().getString(R.string.picpassenger) + " " + username_str);

                                tripsts_but.setText(getResources().getString(R.string.slidebegintrip));
                                botumlay.setBackgroundResource(R.color.buttoncol);
                            } else if (Status.equalsIgnoreCase("Start")) {
                                location_tv.setText("" + jsonObject1.getString("dropofflocation"));

                                sts_text.setText(getResources().getString(R.string.inroute));
                                tripsts_but.setText(getResources().getString(R.string.slideendtrip));
                                botumlay.setBackgroundResource(R.color.red);
                            } else if (Status.equalsIgnoreCase("End")) {
                                Intent j = new Intent(TripStatusAct.this, PaymentAct.class);
                                startActivity(j);
                                finish();

                            }

                            if (jsonObject1.getString("picuplat") == null || jsonObject1.getString("picuplat").equalsIgnoreCase("")) {
                            } else {
                                pic_lat = Double.parseDouble(jsonObject1.getString("picuplat"));
                                pick_lon = Double.parseDouble(jsonObject1.getString("pickuplon"));
                                startlatlong = new LatLng(pic_lat, pick_lon);
                                drop_lat = Double.parseDouble(jsonObject1.getString("droplat"));
                                drop_lon = Double.parseDouble(jsonObject1.getString("droplon"));
                                if (gMap == null) {
                                    Log.e("Come Map Null", "");
                                } else {
                                    //  MarkerOptions markers1 = new MarkerOptions().position(new LatLng(driver_lat, driver_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_marker)).flat(true).anchor(0.5f, 0.5f);

                                    JSONArray jsonArray2 = jsonObject.getJSONArray("booking_dropoff");
                                    for (int ii = 0; ii < jsonArray2.length(); ii++) {
                                        JSONObject jsonObject2 = jsonArray2.getJSONObject(ii);
                                        if (jsonObject2.getString("droplon") != null && !jsonObject2.getString("droplon").equalsIgnoreCase("")) {
                                            double droppoint_lat = Double.parseDouble(jsonObject2.getString("droplat"));
                                            double droppoint_lon = Double.parseDouble(jsonObject2.getString("droplon"));
                                            LatLng latLng = new LatLng(droppoint_lat, droppoint_lon);
                                            options.position(latLng);
                                            options.title("" + jsonObject2.getString("dropofflocation"));
                                            marker = gMap.addMarker(options);
                                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                        }
                                    }
                                    MarkerOptions markers = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_marker)).flat(true).anchor(0.5f, 0.5f);
                                    MarkerOptions marker2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)).flat(true).anchor(0.5f, 0.5f);
                                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
                                    gMap.animateCamera(zoom);
                                    gMap.moveCamera(center);
                                    gMap.addMarker(markers);
                                    gMap.addMarker(marker2);
                                    gMap.addMarker(DirverMarker);
                                    Log.e("Come Map True", "" + pic_lat);

                                    String url = getUrl(new LatLng(pic_lat, pick_lon), new LatLng(drop_lat, drop_lon));
                                    FetchUrl FetchUrl = new FetchUrl();
                                    FetchUrl.execute(url, "first");
                                }
                            }


                        }
                        timer.schedule(new TimerTask() {
                            public void run() {
                                System.out.println("-------------runing-------------");
                                String driver_lat_sr = String.valueOf(driver_lat);
                                String driver_lon_sr = String.valueOf(driver_lon);
                                if (driver_lat != 0) {
                                    new GetDriverLat().execute(driver_lat_sr, driver_lon_sr);
                                }
                            }
                        }, 1000, 12000);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pessengerdetail:
                pessengerDetail();
                return true;
            case R.id.cancelbook:
                toCancelRide();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void pessengerDetail() {
        final Dialog carselection = new Dialog(TripStatusAct.this);
        carselection.requestWindowFeature(Window.FEATURE_NO_TITLE);
        carselection.setCancelable(false);
        //carselection.setContentView(R.layout.pessengerdetail);
        carselection.setContentView(R.layout.passenger_lay);
        // carselection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        carselection.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cancel = (ImageView) carselection.findViewById(R.id.cancel);
        CircleImageView userimage = (CircleImageView) carselection.findViewById(R.id.userimage);
        TextView username = carselection.findViewById(R.id.username);
        TextView numberofrate = carselection.findViewById(R.id.numberofrate);
        LinearLayout msglay = carselection.findViewById(R.id.msglay);
        LinearLayout call = carselection.findViewById(R.id.call);
        username.setText("" + username_str);
        if (userimage_str == null || userimage_str.equalsIgnoreCase("") || userimage_str.equalsIgnoreCase(BaseUrl.image_baseurl)) {

        } else {
            Picasso.with(TripStatusAct.this).load(userimage_str).into(userimage);

        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carselection.dismiss();
            }
        });
        msglay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carselection.dismiss();
                Log.e("booking_user_id", "=====>" + booking_user_id);
                Intent i = new Intent(TripStatusAct.this, ChatingAct.class);
                i.putExtra("receiver_id", booking_user_id);
                i.putExtra("request_id", request_id);
                i.putExtra("receiver_img", userimage_str);
                i.putExtra("receiver_name", username_str);
                startActivity(i);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carselection.dismiss();
                if (usermobile_str == null || usermobile_str.equalsIgnoreCase("")) {

                } else {
                    if (ActivityCompat.checkSelfPermission(TripStatusAct.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + usermobile_str));
                    startActivity(callIntent);

                }
            }
        });
        carselection.show();
    }

    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void toCancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.sure_to_cancle);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Status_Chk = "Cancel";
                new ResponseToRequest().execute(Status_Chk);
                canceldialog.dismiss();

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(TripStatusAct.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TripStatusAct.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        TripStatusAct.this, R.raw.stylemap_3));

        gMap.setBuildingsEnabled(false);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        DirverMarker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);
        gMap.setMyLocationEnabled(true);
        driver_lat = latitude;
        driver_lon = longitude;
        drivermarker = gMap.addMarker(DirverMarker);
        drivermarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));

        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                driver_lat = location.getLatitude();
                driver_lon = location.getLongitude();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (drivermarker != null) {
                    drivermarker.setPosition(newLatLng);
                    rotateMarker(drivermarker, location.getBearing());
                    if (points.size() > 0 && Status.equals("Start")) {
                        Log.e("InSideCondition", "====>" + true);
                        if (!PolyUtil.isLocationOnPath(newLatLng, points, true, 100)) {
                            Log.e("isLocationOnPath", "====>" + true);
                            String url = getUrl(newLatLng, new LatLng(drop_lat, drop_lon));
                            FetchUrl FetchUrl = new FetchUrl();
                            FetchUrl.execute(url, "second");
                        }
                    }
                }
            }
        });
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });

    }

    private void checkGps() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            Log.e("FindTruck Latitude", "" + latitude);
            Log.e("FindTruck longitude", "" + longitude);
        } else {
            gpsTracker.showSettingsAlert();
        }


    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + getResources().getString(R.string.googlekey_other);
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {
        String col = "";

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                col = url[1];
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result, col);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        String cols = "";

        @SuppressLint("WrongThread")
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                cols = jsonData[1];
                DataParser parser = new DataParser();
                routes = parser.parse(jObject);
                tv_estimate_time.setText("Estimate ride time: " + parser.getTime(jObject));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            PolylineOptions lineOptions = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                if (cols.equalsIgnoreCase("second")) {
                    lineOptions.color(Color.GRAY);
                } else {
                    lineOptions.color(Color.BLACK);
                }

            }
            LatLngBounds latLngBounds;
            if (cols.equalsIgnoreCase("second")) {
                latLngBounds = new LatLngBounds.Builder()
                        .include(newLatLng)
                        .include(new LatLng(drop_lat, drop_lon))
                        .build();
            } else {
                latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(pic_lat, pick_lon))
                        .include(new LatLng(drop_lat, drop_lon))
                        .build();
            }
            gMap.clear();
            int width = getResources().getDisplayMetrics().widthPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen
            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));

            if (lineOptions != null) {
                gMap.addPolyline(lineOptions);
            }
            MarkerOptions markers = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_marker)).flat(true).anchor(0.5f, 0.5f);
            MarkerOptions marker2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)).flat(true).anchor(0.5f, 0.5f);
            gMap.addMarker(markers);
            gMap.addMarker(marker2);
            drivermarker = gMap.addMarker(DirverMarker);
            drivermarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));
        }
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final com.google.android.gms.common.api.Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                try {
                    status.startResolutionForResult(TripStatusAct.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        finish();
                        break;

                }
                break;
            case 2:
                break;

        }

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
                //  setButtonsEnabledState();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private class GetDriverLat extends AsyncTask<String, String, String> {
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
//http://mobileappdevelop.co/NAXCAN/webservice/update_lat_lon?lat=123&lon=321&user_id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_lat_lon?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("lat", strings[0]);
                params.put("lon", strings[1]);
                Log.e("strings[0]>>", "" + strings[0]);
                Log.e("strings[1]>>", "" + strings[1]);


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
                Log.e("Update Trip onchange ", ">>>>>>>>>>>>" + response);
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

    private void usercancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                canceldialog.dismiss();
                finish();

            }
        });
        canceldialog.show();


    }

    private void newPointAdded() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        message_tv.setText("" + getResources().getString(R.string.dropadd));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                new GetCurrentBooking().execute();
                canceldialog.dismiss();


            }
        });
        canceldialog.show();


    }

    private class ResponseToRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (ac_dialog != null) {
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
                String postReceiverUrl = BaseUrl.baseurl + "driver_accept_and_Cancel_request?";
                Log.e("EndLat", "========>" + gpsTracker.getLatitude() + " Lon==>" + gpsTracker.getLongitude());
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                // Log.e("Status >.", "" + strings[0]);

                params.put("request_id", request_id);
                params.put("status", strings[0]);
                params.put("timezone", time_zone);
                params.put("driver_id", user_id);
                params.put("droplat", "" + gpsTracker.getLatitude());
                params.put("droplon", "" + gpsTracker.getLongitude());


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
                // Log.e("Json Start End", ">>>>>>>>>>>>" + response);
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
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(TripStatusAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(TripStatusAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();

            } else {
                if (Status_Chk.equalsIgnoreCase("Arrived")) {
                    Status = "Arrived";
                    sts_text.setText(getResources().getString(R.string.pickuppesanger) + " " + username_str);
                    tripsts_but.setText(getResources().getString(R.string.slidebegintrip));


                } else if (Status_Chk.equalsIgnoreCase("Start")) {
                    location_tv.setText("" + dropofflocation_str);

                    Status = "Start";
                    sts_text.setText(getResources().getString(R.string.inroute));
                    tripsts_but.setText(getResources().getString(R.string.slideendtrip));
                    botumlay.setBackgroundResource(R.color.red);


                } else if (Status_Chk.equalsIgnoreCase("End")) {
                    Status = "End";
                    // Toast.makeText(TripStatusAct.this,"In working..",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(TripStatusAct.this, PaymentAct.class);
                    startActivity(i);
                    finish();
                } else if (Status_Chk.equalsIgnoreCase("Cancel")) {
                    finish();
                }
            }
        }
    }

}
