package main.com.dashdriver.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import main.com.dashdriver.MainActivity;
import main.com.dashdriver.R;
import main.com.dashdriver.app.Config;
import main.com.dashdriver.constant.ACProgressCustom;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.BasicCustomAdp;
import main.com.dashdriver.constant.CountryBean;
import main.com.dashdriver.constant.ExpandableHeightListView;
import main.com.dashdriver.constant.GPSTracker;
import main.com.dashdriver.constant.MultipartUtility;
import main.com.dashdriver.constant.MyCarBean;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.pojoclasses.Language;
import main.com.dashdriver.pojoclasses.LanguageSetter;
import main.com.dashdriver.restapi.ApiClient;


public class ManageDocumentAct extends AppCompatActivity {
    private CircleImageView user_img;
    private RelativeLayout exit_app_but, image_lay;
    ProgressBar prgressbar;
    BasicCustomAdp basicCustomAdp;
    private String ImagePath = "", LicImagePath = "";
    MySession mySession;
    private Button save_profile;
    private String image_url = "", user_log_data = "";
    String user_id = "";
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    private ImageView license_img;
    ACProgressCustom ac_dialog;

    CustomSpinnerAdapter customSpinnerAdapter;
    private ImageView lic_img, insu_img, car_insp_img, car_img;
    private ImageView insu_img_show, car_insp_show, car_img_show, lic_img_show;
    private EditText address,email_id, password_et, mobile_et, username, car_model_et, car_registrationnum;
    private String email_str = "",address_str="", password_str = "", mobile_str = "", username_str = "", car_model_str = "", car_registrationnum_str = "";

    private RelativeLayout driver_licens_lay, insurance_copy, driver_car_ispe_lay, driver_car_lay;
    private String IMAGEPATH = "",INS_PATH = "", DRIVER_LIC_PATH = "", firebase_regid = "", CAR_IMG_PATH = "", CAR_INS_PATH = "";
    private String click_on = "", car_col_str = "", car_manuyear = "", cartype_id_str = "";
    private ArrayList<String> modellist, carcolorlist;
    private Spinner carmanuyear, carcolspn, servicetype;
    ArrayList<MyCarBean> myCarBeanArrayList;
    ArrayList<LanguageSetter> languageSetterArrayList, mslanguageSetterArrayList;
    MylangAdp mylangAdp;
    ExpandableHeightListView languagelist;
    private TextView sellang,city_et,state_et;
    private RelativeLayout backbut;
    private Spinner country_spn;
private ArrayList<CountryBean> countryBeanArrayList;
    String country_str="",state_str="",city_str="";
    CountryListAdapter countryListAdapter;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_manage_document);
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

        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        modellist = new ArrayList<>();
        carcolorlist = new ArrayList<>();

        modellist.add(getResources().getString(R.string.selmanyyear));
        carcolorlist.add(getResources().getString(R.string.selectcarcol));

        carcolorlist.add("White");
        carcolorlist.add("Black");
        carcolorlist.add("Blue");
        carcolorlist.add("Red");
        carcolorlist.add("Other");


        int year = getLastModelYear();
        int thisyear = getThisYear();
        for (int i = year; i <= thisyear; i++) {
            modellist.add("" + i);
        }
        modellist.add("" + thisyear);


        Log.e("user_log_data", "" + user_log_data);

        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");

                    Log.e("user_id >>>>", "" + user_id);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            idint();
            checkGps();
            clickevetn();
           // new GetCountryList().execute();
            new GetUserProfile().execute();
        }
    }

    private static int getLastModelYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -10);
        return prevYear.get(Calendar.YEAR);
    }

    private static int getThisYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, 0);
        return prevYear.get(Calendar.YEAR);
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

    private void checkGps() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {

            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            Log.e("Login Latitude", "" + latitude);
            Log.e("Login longitude", "" + longitude);
        } else {
            // if gps off get lat long from network
            //   locationfromnetwork();
            gpsTracker.showSettingsAlert();
        }
     //   new GetCarLists().execute();
    }

    private void clickevetn() {
        sellang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguages();
            }
        });
        insurance_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "insurance";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

            }
        });
        driver_licens_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "driverlicens";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

            }
        });
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();


            }
        });
        image_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "proimg";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);


            }
        });
        driver_car_ispe_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "carinsp";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

            }
        });
        driver_car_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "carimg";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

            }
        });

        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                firebase_regid = pref.getString("regId", null);
                email_str = email_id.getText().toString();
                password_str = password_et.getText().toString();
                mobile_str = mobile_et.getText().toString();
                username_str = username.getText().toString();
                car_model_str = car_model_et.getText().toString();
                car_registrationnum_str = car_registrationnum.getText().toString();
                address_str = address.getText().toString();
                state_str = state_et.getText().toString();
                city_str = city_et.getText().toString();


                /*if (email_str == null || email_str.equalsIgnoreCase("")) {
                    Toast.makeText(ManageDocumentAct.this, getResources().getString(R.string.pleaseenteremailid), Toast.LENGTH_LONG).show();
                } *//*else if (password_str == null || password_str.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileAct.this, getResources().getString(R.string.plsentpass), Toast.LENGTH_LONG).show();
                }*//* else if (mobile_str == null || mobile_str.equalsIgnoreCase("")) {
                    Toast.makeText(ManageDocumentAct.this, getResources().getString(R.string.plsmobilestr), Toast.LENGTH_LONG).show();
                } else if (username_str == null || username_str.equalsIgnoreCase("")) {
                    Toast.makeText(ManageDocumentAct.this, getResources().getString(R.string.plsentusername), Toast.LENGTH_LONG).show();

                }else if (address_str == null || address_str.equalsIgnoreCase("")) {
                    Toast.makeText(ManageDocumentAct.this, getResources().getString(R.string.address), Toast.LENGTH_LONG).show();

                }
                else if (country_str == null || country_str.equalsIgnoreCase("")) {
                    Toast.makeText(ManageDocumentAct.this, getResources().getString(R.string.select_country), Toast.LENGTH_LONG).show();

                }else if (state_str == null || state_str.equalsIgnoreCase("")) {
                    Toast.makeText(ManageDocumentAct.this, getResources().getString(R.string.enterstatename), Toast.LENGTH_LONG).show();

                }else if (city_str == null || city_str.equalsIgnoreCase("")) {
                    Toast.makeText(ManageDocumentAct.this, getResources().getString(R.string.entercityname), Toast.LENGTH_LONG).show();

                }*/
                /*else if (car_manuyear == null || car_manuyear.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileAct.this, getResources().getString(R.string.plsselyear), Toast.LENGTH_LONG).show();

                } else if (car_model_str == null || car_model_str.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileAct.this, getResources().getString(R.string.entercarmodel), Toast.LENGTH_LONG).show();

                } else if (car_col_str == null || car_col_str.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileAct.this, getResources().getString(R.string.selcarcol), Toast.LENGTH_LONG).show();

                } else if (car_registrationnum_str == null || car_registrationnum_str.equalsIgnoreCase("")) {
                    Toast.makeText(ManageDocumentAct.this, getResources().getString(R.string.plsenterregi), Toast.LENGTH_LONG).show();
                }  else {
                    new JsonUpdateProfile().execute();
                }*/
                new JsonUpdateProfile().execute();
            }
        });
    }

    private class GetCarLists extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressbar.setVisibility(View.VISIBLE);
            if (ac_dialog != null) {
                ac_dialog.show();
            }

            MyCarBean myCarBean = new MyCarBean();
            myCarBean.setId("0");
            myCarBean.setCarname("Service Type");
            myCarBeanArrayList = new ArrayList<>();
            myCarBeanArrayList.add(myCarBean);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/car_list
            try {
                String postReceiverUrl = BaseUrl.baseurl + "car_list?";
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
                Log.e("Json Login Response", ">>>>>>>>>>>>" + response);
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
            // progressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            MyCarBean myCarBean = new MyCarBean();
                            myCarBean.setId(jsonObject1.getString("id"));
                            myCarBean.setCarname(jsonObject1.getString("car_name"));
                            //myCarBean.setCarname(jsonObject1.getString("car_name"));
                            myCarBeanArrayList.add(myCarBean);

                        }

                        customSpinnerAdapter = new CustomSpinnerAdapter(ManageDocumentAct.this, android.R.layout.simple_spinner_item, myCarBeanArrayList);
                        servicetype.setAdapter(customSpinnerAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
              //  callLanguage();
            }


        }
    }

    private void callLanguage() {
//http://testing.bigclicki.com/webservice/loginapp?email=0&password=0
        languageSetterArrayList = new ArrayList<>();
        mslanguageSetterArrayList = new ArrayList<>();
        if (ac_dialog != null) {
            ac_dialog.show();
        }

        Call<ResponseBody> call = ApiClient.getApiInterface().languageCall();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (ac_dialog != null) {
                    ac_dialog.dismiss();
                }

                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject object = new JSONObject(responseData);
                        Log.e("language >", " >" + responseData);
                        if (object.getString("status").equals("1")) {
                            Language successData = new Gson().fromJson(responseData, Language.class);
                            languageSetterArrayList.addAll(successData.getResult());
                            mslanguageSetterArrayList.addAll(successData.getResult());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                if (ac_dialog != null) {
                    ac_dialog.dismiss();
                }

                Log.e("TAG", t.toString());
            }
        });
    }

    private void selectLanguages() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(ManageDocumentAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_sel_lang);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView donetv = (TextView) dialogSts.findViewById(R.id.donetv);
        TextView cancel_tv = dialogSts.findViewById(R.id.cancel_tv);
        languagelist = dialogSts.findViewById(R.id.languagelist);
        mylangAdp = new MylangAdp(ManageDocumentAct.this, languageSetterArrayList);
        languagelist.setAdapter(mylangAdp);
        mylangAdp.notifyDataSetChanged();

        donetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();

            }
        });
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();

            }
        });
        dialogSts.show();


    }

    private void idint() {
        country_spn = findViewById(R.id.country_spn);
        state_et = findViewById(R.id.state_et);
        city_et = findViewById(R.id.city_et);
        address = findViewById(R.id.address);
        insu_img_show = findViewById(R.id.insu_img_show);
        car_insp_show = findViewById(R.id.car_insp_show);
        car_img_show = findViewById(R.id.car_img_show);
        lic_img_show = findViewById(R.id.lic_img_show);
        driver_car_lay = findViewById(R.id.driver_car_lay);
        driver_car_ispe_lay = findViewById(R.id.driver_car_ispe_lay);
        car_insp_img = findViewById(R.id.car_insp_img);
        car_img = findViewById(R.id.car_img);
        save_profile = findViewById(R.id.save_profile);
        user_img = findViewById(R.id.user_img);
        image_lay = findViewById(R.id.image_lay);
        exit_app_but = findViewById(R.id.exit_app_but);
        servicetype = findViewById(R.id.servicetype);
        carmanuyear = findViewById(R.id.carmanuyear);
        carcolspn = findViewById(R.id.carcolspn);
        insu_img = findViewById(R.id.insu_img);
        insurance_copy = findViewById(R.id.insurance_copy);
        car_registrationnum = findViewById(R.id.car_registrationnum);
        car_model_et = findViewById(R.id.car_model_et);
        lic_img = findViewById(R.id.lic_img);
        username = findViewById(R.id.username);
        driver_licens_lay = findViewById(R.id.driver_licens_lay);
        mobile_et = findViewById(R.id.mobile_et);
        password_et = findViewById(R.id.password_et);
        email_id = findViewById(R.id.email_id);
        backbut = findViewById(R.id.backbut);
        sellang = findViewById(R.id.sellang);

        country_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (countryBeanArrayList != null && !countryBeanArrayList.isEmpty()) {
                }
                if (countryBeanArrayList.get(position).getId() == null || countryBeanArrayList.get(position).getId().equalsIgnoreCase("0")) {

                } else {
                    country_str = countryBeanArrayList.get(position).getCountry();


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        basicCustomAdp = new BasicCustomAdp(ManageDocumentAct.this, android.R.layout.simple_spinner_item, modellist);
        carmanuyear.setAdapter(basicCustomAdp);
        basicCustomAdp = new BasicCustomAdp(ManageDocumentAct.this, android.R.layout.simple_spinner_item, carcolorlist);
        carcolspn.setAdapter(basicCustomAdp);
        carcolspn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                car_col_str = carcolorlist.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        carmanuyear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                car_manuyear = modellist.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        servicetype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (myCarBeanArrayList == null) {

                } else {
                    if (myCarBeanArrayList.size() > 0) {
                        if (myCarBeanArrayList.get(position).getId().equalsIgnoreCase("0")) {

                        } else {
                            cartype_id_str = myCarBeanArrayList.get(position).getId();

                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public class JsonUpdateProfile extends AsyncTask<String, String, String> {

        String Jsondata;
        private boolean checkdata = false;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                // prgressbar.setVisibility(View.VISIBLE);
                if (ac_dialog != null) {
                    ac_dialog.show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                checkdata = true;

            }

        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/user_update?user_id=21&first_name=er&last_name=vijay&mobile=8889994272&email=v@gmail.com&lang=en&currency=inr&place=indore&country=india&register_id=123&ios_register_id=321&lat=22.123456&lon=75.123456
            String charset = "UTF-8";
            String requestURL = BaseUrl.baseurl + "update_profile?";
            Log.e("requestURL >>", requestURL);

            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);


                multipart.addFormField("user_id", user_id);
                multipart.addFormField("first_name", username_str);
                multipart.addFormField("last_name", "");
                multipart.addFormField("email", email_str);
                multipart.addFormField("password", password_str);
                multipart.addFormField("mobile", mobile_str);
                multipart.addFormField("lang", "");
                multipart.addFormField("lat", "" + latitude);
                multipart.addFormField("lon", "" + longitude);
                multipart.addFormField("register_id", firebase_regid);
                multipart.addFormField("type", "DRIVER");
                multipart.addFormField("car_model", car_model_str);
                multipart.addFormField("year_of_manufacture", car_manuyear);
                multipart.addFormField("car_color", car_col_str);
                multipart.addFormField("address", address_str);
                multipart.addFormField("car_number", car_registrationnum_str);
                multipart.addFormField("country", country_str);
                multipart.addFormField("state", state_str);
                multipart.addFormField("city", city_str);
                multipart.addFormField("car_type_id", cartype_id_str);
                if (ImagePath.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(ImagePath);
                    multipart.addFilePart("image", ImageFile);
                } if (INS_PATH.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(INS_PATH);
                    multipart.addFilePart("insurance", ImageFile);
                }
                if (CAR_IMG_PATH.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(CAR_IMG_PATH);
                    multipart.addFilePart("car_image", ImageFile);
                }if (CAR_INS_PATH.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(CAR_INS_PATH);
                    multipart.addFilePart("car_document", ImageFile);
                }
                if (DRIVER_LIC_PATH.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(DRIVER_LIC_PATH);
                    multipart.addFilePart("license", ImageFile);
                }
                List<String> response = multipart.finish();
                for (String line : response) {
                    Jsondata = line;
                    Log.e("Update Response ====", Jsondata);

                }
                JSONObject object = new JSONObject(Jsondata);
                return Jsondata;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // prgressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                mySession.setlogindata(result);
                mySession.signinusers(true);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")){
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
/*                        if (jsonObject1.getString("status").equalsIgnoreCase("Deactive")){
                            Intent i = new Intent(ProfileAct.this, DashBoardAct.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                        }
                        else {
                            Intent i = new Intent(ProfileAct.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                        }*/

                        Intent i = new Intent(ManageDocumentAct.this, MainActivity.class);
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

    private class GetUserProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
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
//http://technorizen.com/WORKSPACE1/shipper/webservice/get_user?user_id=61
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_profile?";
                Log.e("Base Url=", ">>" + postReceiverUrl + "user_id=" + user_id);

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
                Log.e("Profile Response", ">>>>>>>>>>>>" + response);
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
            //  prgressbar.setVisibility(View.GONE);
            if(ac_dialog!=null){
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
                        username.setText("" + jsonObject1.getString("first_name"));
                        email_id.setText("" + jsonObject1.getString("email"));
                        if (jsonObject1.getString("email") == null || jsonObject1.getString("email").equalsIgnoreCase("") || jsonObject1.getString("email").equalsIgnoreCase("0")) {
                            email_id.setEnabled(true);
                        }
                        email_str = jsonObject1.getString("email");
                        password_str = jsonObject1.getString("password");

                        mobile_et.setText("" + jsonObject1.getString("mobile"));

                        password_et.setText("" + jsonObject1.getString("password"));
                        image_url = jsonObject1.getString("image");
                        car_manuyear = jsonObject1.getString("year_of_manufacture");
                        car_col_str = jsonObject1.getString("car_color");

                        country_str = jsonObject1.getString("country");
                        address.setText("" + jsonObject1.getString("address"));
                        state_et.setText("" + jsonObject1.getString("state"));
                        city_et.setText("" + jsonObject1.getString("city"));

                        /*phone_et.setText(""+jsonObject.getString("image"));*/
                        /*phone_et.setText(""+jsonObject.getString("id"));*/
                        if (countryBeanArrayList!=null&&!countryBeanArrayList.isEmpty()){
                            for (int i=0;i<countryBeanArrayList.size();i++){
                                if (country_str!=null&&!country_str.equalsIgnoreCase("")){
                                    if (country_str.equalsIgnoreCase(countryBeanArrayList.get(i).getCountry())){
                                        country_spn.setSelection(i);
                                    }
                                }
                            }

                        }
                        if (myCarBeanArrayList!=null&&!myCarBeanArrayList.isEmpty()){
                            for (int i=0;i<myCarBeanArrayList.size();i++){
                                if (cartype_id_str!=null&&!cartype_id_str.equalsIgnoreCase("")){
                                    if (cartype_id_str.equalsIgnoreCase(myCarBeanArrayList.get(i).getId())){
                                        servicetype.setSelection(i);
                                    }
                                }
                            }

                        }

                        //later added param
                        car_model_et.setText("" + jsonObject1.getString("car_model"));
                        car_registrationnum.setText("" + jsonObject1.getString("car_number"));
                        String license_str = jsonObject1.getString("license");
                        if (license_str == null || license_str.equalsIgnoreCase("") || license_str.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else {
                            Picasso.with(ManageDocumentAct.this).load(license_str).into(lic_img_show);
                            lic_img.setImageResource(R.drawable.checkimg);
                        }
                        String driver_insstr = jsonObject1.getString("insurance");
                        Log.e("driver_insstr >>"," > "+driver_insstr);
                        if (driver_insstr == null || driver_insstr.equalsIgnoreCase("") || driver_insstr.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else {
                            Picasso.with(ManageDocumentAct.this).load(driver_insstr).into(insu_img_show);

                            insu_img.setImageResource(R.drawable.checkimg);
                        }
                        String car_imgs = jsonObject1.getString("car_image");
                        if (car_imgs == null || car_imgs.equalsIgnoreCase("") || car_imgs.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else {
                            Picasso.with(ManageDocumentAct.this).load(car_imgs).into(car_img_show);
                            car_img.setImageResource(R.drawable.checkimg);
                        } String car_ins_img = jsonObject1.getString("car_document");
                        if (car_ins_img == null || car_ins_img.equalsIgnoreCase("") || car_ins_img.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else {
                            Picasso.with(ManageDocumentAct.this).load(car_ins_img).into(car_insp_show);
                            car_insp_img.setImageResource(R.drawable.checkimg);
                        }
                        if (image_url == null) {

                        } else if (image_url.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else if (image_url.equalsIgnoreCase("")) {

                        } else {
                            //  Picasso.with(ProfileAct.this).load(image_url).into(user_img);
                            Glide.with(ManageDocumentAct.this)
                                    .load(image_url)
                                    .thumbnail(0.5f)
                                    .override(200, 200)
                                    .centerCrop()
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                            return false;
                                        }
                                    })
                                    .into(user_img);

                        }
                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    private class GetCountryList extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
            if(ac_dialog!=null){
                ac_dialog.show();
            }

            countryBeanArrayList = new ArrayList<>();


            CountryBean countryListBean = new CountryBean();
            countryListBean.setId("0");
            countryListBean.setCountry("Country");
            countryListBean.setCurrency("");
            countryBeanArrayList.add(countryListBean);

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/country_list
            try {
                String postReceiverUrl = BaseUrl.baseurl + "countrys";
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
                Log.e("Json Country Response", ">>>>>>>>>>>>" + response);
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
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("status");
                    if (message.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CountryBean countryBean = new CountryBean();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            countryBean.setId(jsonObject1.getString("id"));
                            countryBean.setCountry(jsonObject1.getString("name"));
                            countryBean.setCurrency("");
                            countryBeanArrayList.add(countryBean);
                        }

                        countryListAdapter = new CountryListAdapter(ManageDocumentAct.this, countryBeanArrayList);
                        country_spn.setAdapter(countryListAdapter);

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    public class CountryListAdapter extends BaseAdapter {
        Context context;

        LayoutInflater inflter;
        private ArrayList<CountryBean> values;

        public CountryListAdapter(Context applicationContext, ArrayList<CountryBean> values) {
            this.context = applicationContext;
            this.values = values;

            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {

            return values == null ? 0 : values.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.spinner_lay_profile, null);

            TextView names = (TextView) view.findViewById(R.id.pname);
            //  TextView countryname = (TextView) view.findViewById(R.id.countryname);


            names.setText(values.get(i).getCountry());


            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            switch (requestCode) {
                case 1:
                    Uri selectedImage = data.getData();
                    getPath(selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String FinalPath = cursor.getString(columnIndex);
                    cursor.close();
                    String ImagePath = getPath(selectedImage);
                    decodeFile(ImagePath);
                    break;
                case 2:

                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    // File file = new File(photo);
                    //  save(file.getAbsolutePath());
                    ImagePath = saveToInternalStorage(photo);
                    Log.e("PATH Camera", "" + ImagePath);

                    //  avt_imag.setImageBitmap(photo);
                    break;


            }
        }
    }

    public String getPath(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //  Log.e("image_path.===..", "" + path);
        }
        cursor.close();
        return path;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String dateToStr = format.format(today);
        ContextWrapper cw = new ContextWrapper(ManageDocumentAct.this);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile_" + dateToStr + ".JPEG");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }


    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        if (click_on.equalsIgnoreCase("driverlicens")) {
            Log.e("COME", "Yes1");
            DRIVER_LIC_PATH = saveToInternalStorage(bitmap);
            lic_img.setImageResource(R.drawable.checkimg);
        } else if (click_on.equalsIgnoreCase("proimg")) {
            Log.e("COME", "Yes2");
            ImagePath = saveToInternalStorage(bitmap);
            user_img.setImageBitmap(bitmap);
        }else if (click_on.equalsIgnoreCase("carimg")) {
            CAR_IMG_PATH = saveToInternalStorage(bitmap);
            car_img.setImageResource(R.drawable.checkimg);
        }else if (click_on.equalsIgnoreCase("carinsp")) {
            CAR_INS_PATH = saveToInternalStorage(bitmap);
            car_insp_img.setImageResource(R.drawable.checkimg);
        } else {
            Log.e("COME", "Yes3");
            INS_PATH = saveToInternalStorage(bitmap);
            insu_img.setImageResource(R.drawable.checkimg);
        }

    }


    public class MylangAdp extends BaseAdapter {

        String[] result;
        Context context;
        ArrayList<LanguageSetter> languageSetterArrayList;
        private LayoutInflater inflater = null;


        public MylangAdp(Activity activity, ArrayList<LanguageSetter> languageSetterArrayList) {
            this.context = activity;
            this.languageSetterArrayList = languageSetterArrayList;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            // return 8;
            return languageSetterArrayList == null ? 0 : languageSetterArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final Holder holder;
            holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.custom_service_lay, null);
            ImageView check_img = (ImageView) rowView.findViewById(R.id.check_img);
            TextView service_name = (TextView) rowView.findViewById(R.id.service_name);
            service_name.setText("" + languageSetterArrayList.get(position).getName());
            if (languageSetterArrayList.get(position).isSelected()) {
                check_img.setImageResource(R.drawable.checkimg);

            } else {
                check_img.setImageResource(R.drawable.oval);

            }
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = position;
                    for (int k = 0; k < languageSetterArrayList.size(); k++) {
                        if (pos == k) {
                            if (languageSetterArrayList.get(k).isSelected()) {
                                languageSetterArrayList.get(k).setSelected(false);
                                mslanguageSetterArrayList.get(k).setSelected(false);
                            } else {
                                languageSetterArrayList.get(k).setSelected(true);
                                mslanguageSetterArrayList.get(k).setSelected(true);
                            }
                        }
/*
                        else {
                            mskillBeanArrayList.get(k).setSelected(false);
                        }
*/
                    }
                    if (languagelist != null) {
                        mylangAdp = new MylangAdp(ManageDocumentAct.this, languageSetterArrayList);
                        languagelist.setAdapter(mylangAdp);
                        mylangAdp.notifyDataSetChanged();
                    }


                }
            });

            return rowView;
        }

    }

    public class CustomSpinnerAdapter extends ArrayAdapter<MyCarBean> {
        Context context;
        Activity activity;
        private ArrayList<MyCarBean> items;

        public CustomSpinnerAdapter(Context context, int resourceId, ArrayList<MyCarBean> aritems) {
            super(context, resourceId, aritems);
            this.context = context;

            this.items = aritems;
        }

        private class ViewHolder {
            TextView headername;
            TextView cartype;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public MyCarBean getItem(int position) {
//		Log.v("", "items.get("+position+")= "+items.get(position));
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            CustomSpinnerAdapter.ViewHolder holder = null;
//		final BloodGroupPojo mytempojo = getItem(position);
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.spn_head_lay, null);
                holder = new CustomSpinnerAdapter.ViewHolder();
                holder.headername = (TextView) convertView.findViewById(R.id.heading);
                convertView.setTag(holder);

            } else {
                holder = (CustomSpinnerAdapter.ViewHolder) convertView.getTag();
            }
            holder.headername.setText(items.get(position).getCarname());
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            CustomSpinnerAdapter.ViewHolder holder = null;
//			final BloodGroupPojo mytempojo = getItem(position);
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.loc_spn_lay, null);
                holder = new CustomSpinnerAdapter.ViewHolder();
                holder.cartype = (TextView) convertView.findViewById(R.id.cartype);

                convertView.setTag(holder);

            } else {
                holder = (CustomSpinnerAdapter.ViewHolder) convertView.getTag();
            }

            holder.cartype.setText(items.get(position).getCarname());


            return convertView;
        }
    }

}
