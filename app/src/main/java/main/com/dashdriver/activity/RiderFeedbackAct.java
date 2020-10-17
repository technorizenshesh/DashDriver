package main.com.dashdriver.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import main.com.dashdriver.R;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.constant.ReaderFeedbackbean;

public class RiderFeedbackAct extends  AppCompatActivity {

    ProgressBar prgressbar;
    private ReaderFeedAdp tranjsadp;
    private ListView tranjectionhistory;
    private RelativeLayout exit_app_but;
    ArrayList<ReaderFeedbackbean> readedFeedbackArrayList;
    ACProgressFlower ac_dialog;
    private TextView nofeedbacktv;
    private MySession mySession;
    private String user_log_data="",user_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLanguageSession.get(this).setLangRecreate();
        setContentView(R.layout.activity_rider_feedback);
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
        ac_dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();


        idinit();
        clickevt();
        new ReaderFeedbackAsc().execute();
    }

    private void clickevt() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void idinit() {
        nofeedbacktv = findViewById(R.id.nofeedbacktv);
        prgressbar = (ProgressBar) findViewById(R.id.prgressbar);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        tranjectionhistory = (ListView) findViewById(R.id.tranjectionhistory);
    }
    public class ReaderFeedAdp extends BaseAdapter {

        String[] result;
        Context context;
        ArrayList<ReaderFeedbackbean> readedFeedbackArrayList;

        private LayoutInflater inflater = null;


        public ReaderFeedAdp(Activity activity, ArrayList<ReaderFeedbackbean> readedFeedbackArrayList) {
            this.context = activity;
            this.readedFeedbackArrayList = readedFeedbackArrayList;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return readedFeedbackArrayList==null?0:readedFeedbackArrayList.size();
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

            rowView = inflater.inflate(R.layout.custom_reader_feedback, null);
            TextView review_tv = (TextView) rowView.findViewById(R.id.review_tv);
            RatingBar rating = (RatingBar) rowView.findViewById(R.id.rating);
            TextView date_tv = (TextView) rowView.findViewById(R.id.date_tv);
            review_tv.setText(""+readedFeedbackArrayList.get(position).getReview());
            date_tv.setText(""+readedFeedbackArrayList.get(position).getDate());
            String rat = readedFeedbackArrayList.get(position).getRating();
            if (rat==null||rat.equalsIgnoreCase("")){

            }
            else {

                rating.setRating(Float.parseFloat(rat));
            }
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            return rowView;
        }

    }
    private class ReaderFeedbackAsc extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  prgressbar.setVisibility(View.VISIBLE);
            if (ac_dialog!=null){
                ac_dialog.show();
            }

            readedFeedbackArrayList = new ArrayList<>();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/get_user_feedback?driver_id=22
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_user_feedback?";
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
            // prgressbar.setVisibility(View.GONE);
            if (ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(RiderFeedbackAct.this, "Server Error,Please Try again..", Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(RiderFeedbackAct.this, "Server Error,Please Try again..", Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")){
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int k=0;k<jsonArray.length();k++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(k);
                            ReaderFeedbackbean readerFeedbackbean = new ReaderFeedbackbean();
                            readerFeedbackbean.setRating(jsonObject1.getString("rating"));
                            readerFeedbackbean.setReview(jsonObject1.getString("review"));
                            try {
                                Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject1.getString("date_time"));
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                String strDate = formatter.format(date1);
                                readerFeedbackbean.setDate(strDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            readedFeedbackArrayList.add(readerFeedbackbean);
                        }
                        tranjsadp = new ReaderFeedAdp(RiderFeedbackAct.this,readedFeedbackArrayList);
                        tranjectionhistory.setAdapter(tranjsadp);

                    }

                    if (readedFeedbackArrayList==null||readedFeedbackArrayList.isEmpty()){
                        nofeedbacktv.setVisibility(View.VISIBLE);
                    }
                    else {
                        nofeedbacktv.setVisibility(View.GONE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

}
