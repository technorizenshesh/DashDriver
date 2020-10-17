package main.com.dashdriver.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.dashdriver.Interfaces.onAutocompleteAddressListener;
import main.com.dashdriver.MainActivity;
import main.com.dashdriver.R;
import main.com.dashdriver.constant.ACProgressCustom;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.DataParser;
import main.com.dashdriver.constant.GPSTracker;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.draglocation.MyTask;
import main.com.dashdriver.draglocation.WebOperations;
import main.com.dashdriver.draweractivity.BaseActivity;
import main.com.dashdriver.utils.AutoCompleteAdapter;
import main.com.dashdriver.utils.ModelAutoAddress;
import main.com.dashdriver.utils.Tools;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class BookingActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult>, onAutocompleteAddressListener {
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
    private FrameLayout contentFrameLayout;
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private AutoCompleteTextView dropofflocation;
    private AutoCompleteTextView pickuplocation;
    private MySession mySession;
    public static double longitude = 0.0, latitude = 0.0, pickup_lat_str = 0, pickup_lon_str = 0, drop_lat_str = 0, drop_lon_str = 0;
    public static String time_zone = "";
    String address_complete = "";
    Marker googlemarker_pos;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    Location location;
    Location location_ar;
    LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    boolean sts;
    ImageView gpslocator, clear_pick_ic, clear_drop_ic, map_ic, pinmarimg;
    ProgressBar progressbar;
    private TextView booknow, bookletter;
    public static String selected_car_id = "";
    public static String user_id = "", amount = "", state_str = "", identity = "", date_str = "", time_str = "", ride_status = "", booktype = "", pickuploc_str = "", dropoffloc_str = "";
    ACProgressCustom ac_dialog;
    LatLng picklatLng, droplatlong;
    private String user_log_data = "";
    ScheduledExecutorService scheduleTaskExecutor;
    private String language = "";
    MyLanguageSession myLanguageSession;
    private PlacesClient placesClient;
    private EditText et_user_name, et_mobile;
    private Integer THRESHOLD = 2;
    private int count = 0, countDrop = 0;
    private AutoCompleteAdapter adapterFilter,adapterFilter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.googlekey_other), Locale.US);
        }
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        contentFrameLayout = (FrameLayout) findViewById(R.id.contentFrame);
        getLayoutInflater().inflate(R.layout.activity_booking, contentFrameLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        time_zone = tz.getID();
        gpsTracker = new GPSTracker(BookingActivity.this);
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data != null) {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    state_str = jsonObject1.getString("state");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        checklocation();
        idinits();
        getCurrentLocation();
        clickevetn();
        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            if (requestCode == 1) {
                pickuplocation.setText(place.getAddress());
                pickup_lat_str = place.getLatLng().latitude;
                pickup_lon_str = place.getLatLng().longitude;
                dropoffloc_str = dropofflocation.getText().toString();
                pickuploc_str = pickuplocation.getText().toString();
            } else if (requestCode == 2) {
                dropofflocation.setText(place.getAddress());
                drop_lat_str = place.getLatLng().latitude;
                drop_lon_str = place.getLatLng().longitude;
                dropoffloc_str = dropofflocation.getText().toString();
                pickuploc_str = pickuplocation.getText().toString();
            }
            if (!dropoffloc_str.equalsIgnoreCase("") && !pickuploc_str.equalsIgnoreCase("")) {
                pinmarimg.setVisibility(View.GONE);
                PickRoute();
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i("TAG", status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }

    }

    private void PickRoute() {
        if (gMap != null) {
            gMap.clear();
            if (latitude != 0) {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);
            }
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(pickup_lat_str, pickup_lon_str));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            gMap.animateCamera(zoom);
            gMap.moveCamera(center);
            String url = getUrl(new LatLng(pickup_lat_str, pickup_lon_str), new LatLng(drop_lat_str, drop_lon_str));
            FetchUrl FetchUrl = new FetchUrl();
            FetchUrl.execute(url);
            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(new LatLng(pickup_lat_str, pickup_lon_str))
                    .include(new LatLng(drop_lat_str, drop_lon_str))
                    .build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int padding = (int) (width * 0.20); // offset from edges of the map 12% of screen
            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));

        }
    }

    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void idinits() {
        placesClient = Places.createClient(this);
        pinmarimg = (ImageView) findViewById(R.id.pinmarimg);
        map_ic = (ImageView) findViewById(R.id.map_ic);
        gpslocator = (ImageView) findViewById(R.id.gpslocator);
        clear_pick_ic = (ImageView) findViewById(R.id.clear_pick_ic);
        clear_drop_ic = (ImageView) findViewById(R.id.clear_drop_ic);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        booknow = (TextView) findViewById(R.id.booknow);
        bookletter = (TextView) findViewById(R.id.bookletter);
        pickuplocation = findViewById(R.id.pickuplocation);
        dropofflocation = findViewById(R.id.dropofflocation);
        et_user_name = findViewById(R.id.et_user_name);
        et_mobile = findViewById(R.id.et_mobile);
      /*  pickuplocation.setOnClickListener(v->{
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, placeFields)
                    .build(this);
            startActivityForResult(intent, 1);
        });
        dropofflocation.setOnClickListener(v->{
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, placeFields)
                    .build(this);
            startActivityForResult(intent, 2);
        });*/
        autocompleteView();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(BookingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BookingActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        BookingActivity.this, R.raw.stylemap_3));

        gMap.setMyLocationEnabled(true);
        gMap.setBuildingsEnabled(false);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        gMap.animateCamera(cameraUpdate);

        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (googlemarker_pos != null) {
                    googlemarker_pos.setPosition(latLng1);
                }
            }
        });



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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
    }

    private void checklocation() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location_ar = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (location_ar != null) {
            //Getting longitude and latitude
            longitude = location_ar.getLongitude();
            latitude = location_ar.getLatitude();
            if (latitude == 0.0) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;

            }
            address_complete = loadAddress(latitude, longitude);
            pickuplocation.setText(address_complete);


        } else {
            System.out.println("----------------geting Location from GPS----------------");
            GPSTracker tracker = new GPSTracker(this);
            location_ar = tracker.getLocation();
            if (location_ar == null) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;
                address_complete = loadAddress(latitude, longitude);
                pickuplocation.setText(address_complete);


            } else {
                longitude = location_ar.getLongitude();
                latitude = location_ar.getLatitude();

                if (latitude == 0.0) {
                    latitude = SplashActivity.latitude;
                    longitude = SplashActivity.longitude;

                }
                address_complete = loadAddress(latitude, longitude);
                pickuplocation.setText(address_complete);


            }
        }
    }

    private void clickevetn() {
        map_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BookingActivity.this, SetLocation.class);
                i.putExtra("setLoc", "dropoff");
                startActivity(i);
            }
        });
        bookletter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickuploc_str = pickuplocation.getText().toString();
                dropoffloc_str = dropofflocation.getText().toString();
                if (pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
                    Toast.makeText(BookingActivity.this, getResources().getString(R.string.plsselpicklocation), Toast.LENGTH_LONG).show();
                } else if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("")) {
                    Toast.makeText(BookingActivity.this, getResources().getString(R.string.seldroploc), Toast.LENGTH_LONG).show();
                } else {
                    booktype = "Letter";
                    selectDateTime();
                }
            }
        });
        booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickuploc_str = pickuplocation.getText().toString();
                dropoffloc_str = dropofflocation.getText().toString();
                if (pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
                    Toast.makeText(BookingActivity.this, getResources().getString(R.string.plsselpicklocation), Toast.LENGTH_LONG).show();
                } else if (dropoffloc_str.equalsIgnoreCase("")) {
                    Toast.makeText(BookingActivity.this, getResources().getString(R.string.seldroploc), Toast.LENGTH_LONG).show();
                } else if (et_user_name.getText().toString().isEmpty()) {
                    et_user_name.setError(getString(R.string.required));
                    et_user_name.requestFocus();
                } else if (et_mobile.getText().toString().isEmpty()) {
                    et_mobile.setError(getString(R.string.required));
                    et_mobile.requestFocus();
                } else {
                    booktype = "Now";
                    BookNow();
                }
            }
        });
        clear_pick_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickuplocation.setText("");
            }
        });
        clear_drop_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropofflocation.setText("");
            }
        });
        gpslocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMap != null) {
                    Location loc = gMap.getMyLocation();
                    if (loc != null) {
                        LatLng latLang = new LatLng(loc.getLatitude(), loc
                                .getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 17);
                        gMap.animateCamera(cameraUpdate);
                        if (sts) {
                            address_complete = loadAddress(latitude, longitude);
                            pickuplocation.setText(address_complete);
                        }

                    }

                }
            }
        });
    }

    private String loadAddress(double latitude, double longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current", strReturnedAddress.toString());
            } else {
                Log.w("My Current", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onSelectAddress(int type, ModelAutoAddress address) {
        InputMethodManager inputManager = (InputMethodManager)
                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if (type == 1) {
            pickuplocation.dismissDropDown();
            pickuplocation.setText(address.getDescription());
            dropoffloc_str = dropofflocation.getText().toString();
            pickuploc_str = pickuplocation.getText().toString();
            if (!dropoffloc_str.equalsIgnoreCase("") && !pickuploc_str.equalsIgnoreCase("")) {
                new GetPickRoute().execute();
            }
        } else {
            dropofflocation.dismissDropDown();
            dropofflocation.setText(address.getDescription());
            dropoffloc_str = dropofflocation.getText().toString();
            pickuploc_str = pickuplocation.getText().toString();
            if (!dropoffloc_str.equalsIgnoreCase("") && !pickuploc_str.equalsIgnoreCase("")) {
                new GetPickRoute().execute();
            }
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

            longitude = location.getLongitude();
            latitude = location.getLatitude();


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + getResources().getString(R.string.googlekey_other);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            ArrayList<LatLng> animation_list = null;
            if (result == null) {


            } else {

            }

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                animation_list = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    animation_list.add(position);
                    points.add(position);
                    if (j == 0) {
                        picklatLng = position;
                    }
                    if (j == (path.size() - 1)) {
                        droplatlong = position;
                    }

                }
                Log.e("SIZE POINT", " True >> " + points.size());
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

            }
            if (lineOptions != null) {
                gMap.clear();
                gMap.addPolyline(lineOptions);
                MarkerOptions pick = new MarkerOptions().position(picklatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_marker)).flat(true).anchor(0.5f, 0.5f);
                MarkerOptions drop = new MarkerOptions().position(droplatlong).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)).flat(true).anchor(0.5f, 0.5f);
                gMap.addMarker(pick);
                gMap.addMarker(drop);
            }
        }
    }


    private void selectDateTime() {
        final Dialog dialogSts = new Dialog(BookingActivity.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.selectdate_newlay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView date_tv = (TextView) dialogSts.findViewById(R.id.date_tv);
        final TextView time_tv = (TextView) dialogSts.findViewById(R.id.time_tv);
        TextView cancel = (TextView) dialogSts.findViewById(R.id.cancel);
        TextView ok = (TextView) dialogSts.findViewById(R.id.ok);
        LinearLayout time_lay = (LinearLayout) dialogSts.findViewById(R.id.time_lay);
        LinearLayout date_lay = (LinearLayout) dialogSts.findViewById(R.id.date_lay);
        date_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(BookingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                                String mon = MONTHS[monthOfYear];
                                int mot = monthOfYear + 1;
                                String month = "";
                                if (mot >= 10) {
                                    month = String.valueOf(mot);
                                } else {
                                    month = "0" + String.valueOf(mot);
                                }
                                String daysss = "";
                                if (dayOfMonth >= 10) {
                                    daysss = String.valueOf(dayOfMonth);
                                } else {
                                    daysss = "0" + String.valueOf(dayOfMonth);
                                }
                                date_tv.setText(dayOfMonth + "-" + mon + "-" + year);
                                date_str = "" + year + "-" + month + "-" + daysss;

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date_str == null || date_str.equalsIgnoreCase("")) {
                    Toast.makeText(BookingActivity.this, getResources().getString(R.string.seldate), Toast.LENGTH_SHORT).show();
                } else if (time_str == null || time_str.equalsIgnoreCase("")) {
                    Toast.makeText(BookingActivity.this, getResources().getString(R.string.seltime), Toast.LENGTH_SHORT).show();
                } else {
                    dialogSts.dismiss();
                    booktype = "Letter";
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
            }
        });
        time_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(BookingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                int hour = hourOfDay;
                                int fullhour = hourOfDay;
                                int minutes = minute;
                                String timeSet = "";
                                if (hour > 12) {
                                    hour -= 12;
                                    timeSet = "PM";
                                } else if (hour == 0) {
                                    hour += 12;
                                    timeSet = "AM";
                                } else if (hour == 12) {
                                    timeSet = "PM";
                                } else {
                                    timeSet = "AM";
                                }

                                String min = "";
                                if (minutes < 10)
                                    min = "0" + minutes;
                                else
                                    min = String.valueOf(minutes);
                                time_str = "" + hourOfDay + " : " + min + " " + timeSet;
                                time_tv.setText("" + time_str);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        dialogSts.show();
    }

    private HashMap<String, String> getParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("device_type", "android");
        param.put("driver_id", user_id);
        param.put("picuplocation", pickuploc_str);
        param.put("dropofflocation", dropoffloc_str);
        param.put("picuplat", "" + pickup_lat_str);
        param.put("pickuplon", "" + pickup_lon_str);
        param.put("droplat", "" + drop_lat_str);
        param.put("droplon", "" + drop_lon_str);
        param.put("shareride_type", "no");
        param.put("booktype", booktype);
        param.put("passenger", "1");
        param.put("current_time", "" + Tools.get().CurrentDate());
        param.put("timezone", "" + time_zone);
        param.put("payment_type", "Cash");
        param.put("status", "" + booktype);
        param.put("apply_code", "");
        param.put("picklatertime", "");
        param.put("picklaterdate", "");
        param.put("user_name", et_user_name.getText().toString());
        param.put("mobile", et_mobile.getText().toString());
        param.put("vehical_type", "Reqular");
        return param;
    }

    private void BookNow() {
        StringBuilder param=new StringBuilder();
        param.append(BaseUrl.get().BookNow()+"?");
        for (Map.Entry<String, String> entry : getParam().entrySet()) {
            param.append("&"+entry.getKey()+"="+entry.getValue());
        }
        Log.e("BookingUrl","======>"+param.toString());
        ApiCallBuilder.build(this).isShowProgressBar(true).setUrl(BaseUrl.get().BookNow())
                .setParam(getParam()).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                Log.e("BookingResponse", "=======>" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getString("status").contains("1");
                    MainActivity.request_id = object.getString("request_id");
                    if (status) {
                        Intent i = new Intent(BookingActivity.this, TripStatusAct.class);
                        i.putExtra("user_name", et_user_name.getText().toString());
                        startActivity(i);
                    } else {
                        Toast.makeText(BookingActivity.this, "" + object.getString("result"), Toast.LENGTH_SHORT).show();
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

    class GeoAutoCompleteAdapter extends BaseAdapter implements Filterable {

        private Activity context;
        private List<String> l2 = new ArrayList<>();
        private LayoutInflater layoutInflater;
        private WebOperations wo;
        private String lat, lon;

        public GeoAutoCompleteAdapter(Activity context, List<String> l2, String lat, String lon) {
            this.context = context;
            this.l2 = l2;
            this.lat = lat;
            this.lon = lon;
            layoutInflater = LayoutInflater.from(context);
            wo = new WebOperations(context);
        }

        @Override
        public int getCount() {

            return l2.size();
        }

        @Override
        public Object getItem(int i) {
            return l2.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = layoutInflater.inflate(R.layout.geo_search_result, viewGroup, false);
            TextView geo_search_result_text = (TextView) view.findViewById(R.id.geo_search_result_text);
            try {
                geo_search_result_text.setText(l2.get(i));
                geo_search_result_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        if (sts) {
                            pickuplocation.setText("" + l2.get(i));
                            pickuplocation.dismissDropDown();
                            dropoffloc_str = dropofflocation.getText().toString();
                            pickuploc_str = pickuplocation.getText().toString();
                            if (!dropoffloc_str.equalsIgnoreCase("") && !pickuploc_str.equalsIgnoreCase("")) {
                                new GetPickRoute().execute();
                            }

                        } else {
                            dropofflocation.setText("" + l2.get(i));
                            dropofflocation.dismissDropDown();
                            dropoffloc_str = dropofflocation.getText().toString();
                            pickuploc_str = pickuplocation.getText().toString();
                            if (!dropoffloc_str.equalsIgnoreCase("") && !pickuploc_str.equalsIgnoreCase("")) {
                                new GetPickRoute().execute();
                            }

                        }

                    }
                });

            } catch (Exception e) {

            }

            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        wo.setUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyBXvrm0wKFaamcHvScRaQ2_Oi9lZw8if6k&input=" + constraint.toString().trim().replaceAll(" ", "+") + "&location=" + lat + "," + lon + "+&radius=1000&types=geocode&sensor=true");
                        String result = null;
                        try {
                            result = new MyTask(wo, 3).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        parseJson(result);
                        filterResults.values = l2;
                        filterResults.count = l2.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count != 0) {
                        l2 = (List) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        private void parseJson(String result) {
            try {
                l2 = new ArrayList<>();
                JSONObject jk = new JSONObject(result);

                JSONArray predictions = jk.getJSONArray("predictions");
                for (int i = 0; i < predictions.length(); i++) {
                    JSONObject js = predictions.getJSONObject(i);
                    l2.add(js.getString("description"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetPickRoute extends AsyncTask<String, String, String> {
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
            String address1 = pickuploc_str.trim().replaceAll(" ", "+");
            String address = address1.trim().replaceAll(",", "+");
            String postReceiverUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + getResources().getString(R.string.googlekey_other);

            try {
                //  String postReceiverUrl = "https://api.ctlf.co.uk/";
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

            if (result == null) {

            } else if (result.equalsIgnoreCase("")) {

            } else {
                JSONObject location = null;
                try {
                    location = new JSONObject(result).getJSONArray("results")
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location");


                    //    pickup_lat_str,pickup_lon_str,drop_lat_str,drop_lon_str,
                    pickup_lat_str = location.getDouble("lat");
                    pickup_lon_str = location.getDouble("lng");
                    new GetDropOffLatRoute().execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private class GetDropOffLatRoute extends AsyncTask<String, String, String> {
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
            String address1 = dropoffloc_str.trim().replaceAll(" ", "+");
            String address = address1.trim().replaceAll(",", "+");
            String postReceiverUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + getResources().getString(R.string.googlekey_other);
            try {
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
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {

            } else if (result.equalsIgnoreCase("")) {

            } else {
                JSONObject location = null;
                try {
                    location = new JSONObject(result).getJSONArray("results")
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location");


                    drop_lat_str = location.getDouble("lat");
                    drop_lon_str = location.getDouble("lng");
                    if (gMap != null) {
                        gMap.clear();
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(pickup_lat_str, pickup_lon_str));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                        gMap.animateCamera(zoom);
                        gMap.moveCamera(center);

                        String url = getUrl(new LatLng(pickup_lat_str, pickup_lon_str), new LatLng(drop_lat_str, drop_lon_str));
                        FetchUrl FetchUrl = new FetchUrl();
                        FetchUrl.execute(url);
                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .include(new LatLng(pickup_lat_str, pickup_lon_str))
                                .include(new LatLng(drop_lat_str, drop_lon_str))
                                .build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int padding = (int) (width * 0.20); // offset from edges of the map 12% of screen
                        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void autocompleteView() {
        pickuplocation.setThreshold(THRESHOLD);
        adapterFilter=new AutoCompleteAdapter(this,location).setCallback(1,this);
        adapterFilter2=new AutoCompleteAdapter(this,location).setCallback(2,this);
        pickuplocation.setAdapter(adapterFilter);
        dropofflocation.setAdapter(adapterFilter2);
        pickuplocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sts = true;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    clear_pick_ic.setVisibility(View.VISIBLE);
//                    loadData(pickuplocation.getText().toString());
                    adapterFilter.getFilter().filter(pickuplocation.getText().toString());
                } else {
                    clear_pick_ic.setVisibility(View.GONE);
                }
            }
        });
        dropofflocation.setThreshold(THRESHOLD);
        dropofflocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sts = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    clear_drop_ic.setVisibility(View.VISIBLE);
                    map_ic.setVisibility(View.GONE);
//                    loadDataDrop(dropofflocation.getText().toString());
                    adapterFilter2.getFilter().filter(dropofflocation.getText().toString());
                } else {
                    clear_drop_ic.setVisibility(View.GONE);
                    map_ic.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}

