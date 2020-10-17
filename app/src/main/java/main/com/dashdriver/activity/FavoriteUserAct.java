package main.com.dashdriver.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.dashdriver.R;
import main.com.dashdriver.constant.ACProgressCustom;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.constant.UserDetailBean;

public class FavoriteUserAct extends AppCompatActivity {
    ACProgressCustom ac_dialog;
    private ArrayList<UserDetailBean> userDetailBeanArrayList;
    private MySession mySession;
    private String user_log_data="",user_id="";
    private FavdriverAdp favdriverAdp;
    private ListView favuserlist;
    private RelativeLayout exit_app_but;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_favorite_user);
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
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");


                    // amount = jsonObject1.getString("amount");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        idinit();
    }

    private void idinit() {
        exit_app_but = findViewById(R.id.exit_app_but);
        favuserlist = findViewById(R.id.favuserlist);
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new GetFavUserAsc().execute();
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

    public class FavdriverAdp extends BaseAdapter {
        String[] result;
        Context context;
        private LayoutInflater inflater = null;
        ArrayList<UserDetailBean> userDetailBeanArrayList;

        public FavdriverAdp(Activity activity, ArrayList<UserDetailBean> userDetailBeanArrayList) {
            this.context = activity;
            this.userDetailBeanArrayList = userDetailBeanArrayList;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return userDetailBeanArrayList == null ? 0 : userDetailBeanArrayList.size();
        }
        // return driverDetailBeanArrayList == null ? 0 : driverDetailBeanArrayList.size();        }

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

            rowView = inflater.inflate(R.layout.custom_fav_lay, null);
            TextView cardetail = rowView.findViewById(R.id.cardetail);
            TextView driver_name = rowView.findViewById(R.id.driver_name);
            Switch switch_my_sts = rowView.findViewById(R.id.switch_my_sts);
            CircleImageView driver_img = rowView.findViewById(R.id.driver_img);
            if (userDetailBeanArrayList.get(position).getFav_status().equalsIgnoreCase("Available")){
                switch_my_sts.setChecked(true);
            }
            else {
                switch_my_sts.setChecked(false);
            }

            switch_my_sts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Unavailable
                    if (isChecked){
                        new UpdateAvbSts().execute(userDetailBeanArrayList.get(position).getId(),"Available");
                    }
                    else {
                        new UpdateAvbSts().execute(userDetailBeanArrayList.get(position).getId(),"Unavailable");
                    }
                }
            });

            driver_name.setText(userDetailBeanArrayList.get(position).getFirst_name()+" "+userDetailBeanArrayList.get(position).getLast_name());
            //cardetail.setText("" + userDetailBeanArrayList.get(position).getCar_model() + "\n" + userDetailBeanArrayList.get(position).getCar_number().trim() + " , " + userDetailBeanArrayList.get(position).getCar_color());
            String driver_img_str=userDetailBeanArrayList.get(position).getImage();
            if (driver_img_str == null || driver_img_str.equalsIgnoreCase("") || driver_img_str.equalsIgnoreCase(BaseUrl.image_baseurl)) {

            } else {
                Picasso.with(FavoriteUserAct.this).load(BaseUrl.image_baseurl + driver_img_str).placeholder(R.drawable.user).into(driver_img);

            }
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
            return rowView;
        }

    }


    private class GetFavUserAsc extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressbar.setVisibility(View.VISIBLE);
            userDetailBeanArrayList = new ArrayList<>();
            if (ac_dialog != null) {
                ac_dialog.show();
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//http://technorizen.com/ZIPPI/webservice/get_favoriting_users?driver_id=1000
                String postReceiverUrl = BaseUrl.baseurl + "get_favoriting_users?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
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
            // progressbar.setVisibility(View.GONE);
            Log.e("Fav User Response","> "+result);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            UserDetailBean driverDetailBean = new UserDetailBean();
                            driverDetailBean.setId(jsonObject1.getString("id"));
                            driverDetailBean.setFirst_name(jsonObject1.getString("first_name"));
                            driverDetailBean.setLast_name(jsonObject1.getString("last_name"));
                            driverDetailBean.setMobile(jsonObject1.getString("mobile"));
                            driverDetailBean.setEmail(jsonObject1.getString("email"));
                            driverDetailBean.setImage(jsonObject1.getString("image"));
                            driverDetailBean.setFav_status(jsonObject1.getString("fav_status"));
//                            driverDetailBean.setDistance(jsonObject1.getString("distance"));
                            driverDetailBean.setStatus(jsonObject1.getString("status"));
                            driverDetailBean.setOnline_status(jsonObject1.getString("online_status"));
                            //driverDetailBean.setCar_number(jsonObject1.getString("car_number"));
                           // driverDetailBean.setCar_color(jsonObject1.getString("car_color"));
                           // driverDetailBean.setCar_image(jsonObject1.getString("car_image"));
                           // driverDetailBean.setCar_model(jsonObject1.getString("car_model"));
                           // driverDetailBean.setYear_of_manufacture(jsonObject1.getString("year_of_manufacture"));
                            userDetailBeanArrayList.add(driverDetailBean);
                        }

                        favdriverAdp = new FavdriverAdp(FavoriteUserAct.this,userDetailBeanArrayList);
                        favuserlist.setAdapter(favdriverAdp);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class UpdateAvbSts extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressbar.setVisibility(View.VISIBLE);
            if (ac_dialog != null) {
                ac_dialog.show();
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//http://technorizen.com/ZIPPI/webservice/update_favorite_status?driver_id=1000&user_id=999&status=Unavailable
                String postReceiverUrl = BaseUrl.baseurl + "update_favorite_status?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("driver_id", user_id);
                params.put("user_id", strings[0]);
                params.put("status", strings[1]);


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
            // progressbar.setVisibility(View.GONE);
            Log.e("Fav Update Response","> "+result);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }


        }
    }



}
