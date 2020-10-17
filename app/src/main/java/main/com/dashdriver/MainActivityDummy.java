package main.com.dashdriver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import main.com.dashdriver.activity.SetLocation;
import main.com.dashdriver.activity.SplashActivity;
import main.com.dashdriver.constant.GPSTracker;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.draglocation.MyTask;
import main.com.dashdriver.draglocation.WebOperations;
import main.com.dashdriver.draweractivity.BaseActivity;

public class MainActivityDummy extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {
    private Integer THRESHOLD = 2;
    private int count = 0, countDrop = 0;
    private FrameLayout contentFrameLayout;
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private AutoCompleteTextView pickuplocation, dropofflocation;
    private MySession mySession;
    public static double longitude = 0.0, latitude = 0.0;
    public static String time_zone="";
    int initial_flag = 0;
    public static boolean mylocset = true;
    String address_complete = "";
    Marker googlemarker_pos, my_job_location_marker;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    Location location;
    Location location_ar;
    LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    boolean sts;
    ImageView gpslocator,clear_pick_ic,clear_drop_ic,map_ic;
    ProgressBar progressbar;
    private TextView booknow,bookletter;
    private RecyclerView cartypelist;
    CarHoriZontalLay carHoriZontalLay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maindummy);
        contentFrameLayout = (FrameLayout) findViewById(R.id.contentFrame); //Remember this is the FrameLayout area within your activity_maindummydummy.xml
        getLayoutInflater().inflate(R.layout.activity_maindummy, contentFrameLayout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        time_zone = tz.getID();
        gpsTracker = new GPSTracker(MainActivityDummy.this);
        mySession = new MySession(this);
        checklocation();
        idinits();
        getCurrentLocation();
        autocompleteView();
        // checkGps();

        clickevetn();
        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void idinits() {
        map_ic = (ImageView) findViewById(R.id.map_ic);
        gpslocator = (ImageView) findViewById(R.id.gpslocator);
        clear_pick_ic = (ImageView) findViewById(R.id.clear_pick_ic);
        clear_drop_ic = (ImageView) findViewById(R.id.clear_drop_ic);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        booknow = (TextView) findViewById(R.id.booknow);
        bookletter = (TextView) findViewById(R.id.bookletter);
        pickuplocation = (AutoCompleteTextView) findViewById(R.id.pickuplocation);
        dropofflocation = (AutoCompleteTextView) findViewById(R.id.dropofflocation);
        cartypelist = (RecyclerView) findViewById(R.id.cartypelist);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(MainActivityDummy.this, LinearLayoutManager.HORIZONTAL, false);
        cartypelist.setLayoutManager(horizontalLayoutManagaer);
        carHoriZontalLay = new CarHoriZontalLay();
        cartypelist.setAdapter(carHoriZontalLay);
        //cartypelist.scrollToPosition(listPosition);
        carHoriZontalLay.notifyDataSetChanged();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(MainActivityDummy.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivityDummy.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        MainActivityDummy.this, R.raw.stylemap_3));

        gMap.setMyLocationEnabled(true);
        gMap.setBuildingsEnabled(false);
        // gMap.setMyLocationEnabled(true);
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

        gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                if (initial_flag != 0) {

                    if (mylocset) {
                        LatLng latLng = gMap.getCameraPosition().target;
                        address_complete = loadAddress(latLng.latitude, latLng.longitude);
                        pickuplocation.setText(address_complete);

                    }}
                initial_flag++;

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SetLocation.pickuplocation_str!=null&&!SetLocation.pickuplocation_str.equalsIgnoreCase("")){
            dropofflocation.setText(""+SetLocation.pickuplocation_str);
            SetLocation.pickuplocation_str="";
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
        LatLng latLng = null;
        if (googlemarker_pos == null) {

        } else {
            if (gMap == null) {

            } else {
                if (location == null) {

                } else {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                }
                if (latLng == null) {

                } else {
                    //  googlemarker_pos.setPosition(latLng);
                }


            }
        }
        //    updateLocationUI();

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
                Intent i = new Intent(MainActivityDummy.this, SetLocation.class);
                i.putExtra("setLoc", "dropoff");
                startActivity(i);
            }
        });
        bookletter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



               /* Intent i = new Intent(MainActivityDummy.this, TripStatusAct.class);
                startActivity(i);*/


            }
        });

        booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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
                if (gMap == null) {

                } else {
                    Location loc = gMap.getMyLocation();
                    if (loc != null) {
                        LatLng latLang = new LatLng(loc.getLatitude(), loc
                                .getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 17);
                        gMap.animateCamera(cameraUpdate);
                        address_complete = loadAddress(latitude, longitude);
                        pickuplocation.setText(address_complete);

                    }

                }
            }
        });
    }

    private String loadAddress(double latitude, double longitude) {
        try {
            WebOperations wo = new WebOperations(MainActivityDummy.this.getApplicationContext());
            wo.setUrl("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + getResources().getString(R.string.googlekey_other));
            String str = new MyTask(wo, 3).execute().get();
            JSONObject jk = new JSONObject(str);
            JSONArray results = jk.getJSONArray("results");
            JSONObject jk1 = results.getJSONObject(0);
            String add1 = jk1.getString("formatted_address");
            return add1;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadData(String s) {
        try {
            if (count == 0) {
                List<String> l1 = new ArrayList<>();
                if (s == null) {

                } else {
                    l1.add(s);
                    sts = true;
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(MainActivityDummy.this, l1, "" + latitude, "" + longitude);
                    pickuplocation.setAdapter(ga);

                }

            }
            count++;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataDrop(String s) {
        try {
            if (countDrop == 0) {
                List<String> l1 = new ArrayList<>();
                if (s == null) {

                } else {
                    l1.add(s);
                    sts = false;
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(MainActivityDummy.this, l1, "" + latitude, "" + longitude);
                    dropofflocation.setAdapter(ga);

                }

            }
            countDrop++;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void autocompleteView() {
        pickuplocation.setThreshold(THRESHOLD);
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
                  //  clear_pick_ic.setVisibility(View.VISIBLE);
                    //  picmap_ic.setVisibility(View.GONE);
                    loadData(pickuplocation.getText().toString());
                } else {
                    //clear_pick_ic.setVisibility(View.INVISIBLE);
                    //  picmap_ic.setVisibility(View.VISIBLE);

                }
            }
        });
        dropofflocation.setThreshold(THRESHOLD);
        dropofflocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sts = false;
                mylocset = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.length() > 0) {
                   // clear_drop_ic.setVisibility(View.VISIBLE);
                    //map_ic.setVisibility(View.GONE);
                    loadDataDrop(dropofflocation.getText().toString());
                } else {
                    //clear_drop_ic.setVisibility(View.INVISIBLE);
                    //map_ic.setVisibility(View.VISIBLE);
                }
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
                            if (l2 == null || l2.isEmpty()) {

                            } else {
                                pickuplocation.setText("" + l2.get(i));
                                pickuplocation.dismissDropDown();
                                /*dropoffloc_str = dropofflocation.getText().toString();
                                pickuploc_str = pickuplocation.getText().toString();
                                if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("") || pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
                                } else {
                                    pinmarimg.setVisibility(View.GONE);
                                    new GetPickRoute().execute();
                                }*/
                            }


                        } else {
                            if (l2 == null || l2.isEmpty()) {

                            } else {
                                dropofflocation.setText("" + l2.get(i));
                                dropofflocation.dismissDropDown();
                                /*dropoffloc_str = dropofflocation.getText().toString();
                                pickuploc_str = pickuplocation.getText().toString();
                                if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("") || pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
                                } else {
                                    pinmarimg.setVisibility(View.GONE);
                                    new GetPickRoute().execute();
                                }*/

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
                        wo.setUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyDQhXBxYiOPm-aGspwuKueT3CfBOIY3SJs&input=" + constraint.toString().trim().replaceAll(" ", "+") + "&location=" + lat + "," + lon + "+&radius=20000&types=geocode&sensor=true");
                        String result = null;
                        try {
                            result = new MyTask(wo, 3).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        parseJson(result);


                        // Assign the data to the FilterResults
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
    class CarHoriZontalLay extends RecyclerView.Adapter<CarHoriZontalLay.MyViewHolder> {
        String[] nameArray;
        Integer[] drawableArray;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView carname, total_dis, total_amt, eta_min;
            ImageView carimage;
            View viewLine, small_verview;
            RelativeLayout backview;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.viewLine = (View) itemView.findViewById(R.id.viewLine);

                this.carname = (TextView) itemView.findViewById(R.id.carname);

            }
        }

        public CarHoriZontalLay() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_car_lay, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
            if (listPosition == 3) {
                holder.viewLine.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = listPosition;




                    //  selected_service_fare.setVisibility(View.VISIBLE);

                }
            });


        }

        @Override
        public int getItemCount() {
            return 4;
            //return myCarBeanArrayList == null ? 0 : myCarBeanArrayList.size();
        }
    }

}
