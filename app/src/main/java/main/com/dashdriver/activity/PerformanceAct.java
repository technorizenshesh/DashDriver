package main.com.dashdriver.activity;

import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import main.com.dashdriver.R;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.databinding.ActivityPerformanceBinding;
import main.com.dashdriver.databinding.MonthlyViewBinding;
import main.com.dashdriver.utils.Tools;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class PerformanceAct extends AppCompatActivity {
    MySession mySession;
    private String user_log_data="",user_id="";
    private String language = "";
    MyLanguageSession myLanguageSession;
    MonthlyViewBinding binding;
    private ActivityPerformanceBinding mbinding;
    private JSONObject dailyobj,weekobj;
    private JSONArray Month_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        mbinding=DataBindingUtil.setContentView(this,R.layout.activity_performance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        clickevent();
        GetPerformance();
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

    private void clickevent() {
        mbinding.exitAppBut.setOnClickListener(v->finish());
        mbinding.rdGroup.setOnCheckedChangeListener((group, checkedId) -> {
            mbinding.tvNoRecord.setVisibility(View.GONE);
            switch (checkedId){
                case R.id.rd_today:
                    BindDay(getString(R.string.today),dailyobj);
                    break;
                case R.id.rd_week:
                    BindDay(getString(R.string.week),weekobj);
                    break;
                case R.id.rd_month:
                    BindMonth(Month_array);
                    break;
            }
        });
        mbinding.tvFromDate.setOnClickListener(v->Tools.get().DatePicker(this,mbinding.tvFromDate::setText,"yyyy-MM-dd"));
        mbinding.tvToDate.setOnClickListener(v->Tools.get().DatePicker(this,mbinding.tvToDate::setText,"yyyy-MM-dd"));
        mbinding.imgSearch.setOnClickListener(v->GetPerformanceFilter());
    }

    private void BindDay(String type,JSONObject jsonObject){
        mbinding.monthDataLay.removeAllViews();
        try {
        binding= DataBindingUtil.inflate(getLayoutInflater(),R.layout.monthly_view,mbinding.monthDataLay,false);
        binding.monthAcceptrideTv.setText(""+jsonObject.getString("accepted_rides")+" %");
        binding.monthTotalEarningTv.setText("$ "+String. format("%.2f", jsonObject.getDouble("earning")));
        binding.monthTotalRideCount.setText(""+jsonObject.getString("total"));
        binding.monthcanceledRide.setText(""+jsonObject.getString("cancel_rides")+" %");
        binding.tvMonth.setText(type);
        }catch (JSONException e){
            binding.tvMonth.setText("No data found");
        }
        mbinding.monthDataLay.addView(binding.getRoot());
    }
    private void BindMonth(JSONArray array){
        mbinding.monthDataLay.removeAllViews();
        try {
            String LS_MONTH="";
            double earning=0;
            for (int i=0;i<array.length();i++){
                JSONObject monthobj=array.getJSONObject(i);
                String MONTH=Tools.get().ChangeDateToMonth(monthobj.getString("date"));
                if (MONTH.equals(LS_MONTH)){
                    earning=earning+Double.valueOf(monthobj.getString("earning"));
                    Log.e("earning","===>"+earning);
                    binding.monthAcceptrideTv.setText(""+monthobj.getString("accepted_rides")+" %");
                    binding.monthTotalEarningTv.setText("$ "+String. format("%.2f", earning));
                    binding.monthTotalRideCount.setText(""+monthobj.getString("total"));
                    binding.monthcanceledRide.setText(""+monthobj.getString("cancel_rides")+" %");
                    binding.tvMonth.setText(MONTH);
                }else {
                    earning=0;
                    binding= DataBindingUtil.inflate(getLayoutInflater(),R.layout.monthly_view,binding.monthDataLay,false);
                    binding.monthAcceptrideTv.setText(""+monthobj.getString("accepted_rides")+" %");
                    binding.monthTotalEarningTv.setText("$ "+String. format("%.2f",monthobj.getDouble("earning")));
                    binding.monthTotalRideCount.setText(""+monthobj.getString("total"));
                    binding.monthcanceledRide.setText(""+monthobj.getString("cancel_rides")+" %");
                    binding.tvMonth.setText(MONTH);
                    mbinding.monthDataLay.addView(binding.getRoot());
                    earning=earning+Double.valueOf(monthobj.getString("earning"));
                }
                LS_MONTH=MONTH;
            }
        }catch (JSONException e){

        }
    }
    private void GetPerformance(){
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id", user_id);
        ApiCallBuilder.build(this).setUrl(BaseUrl.get().getStatement())
                .isShowProgressBar(true)
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                Log.e("EarningReport","=========>"+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("status");
                    if (msg.equalsIgnoreCase("1")) {
                        JSONObject jsonArray = jsonObject.getJSONObject("result");
                        dailyobj = jsonArray.getJSONObject("daily");
                        weekobj = jsonArray.getJSONObject("week");
                        Month_array = jsonArray.getJSONArray("month");
                        BindDay(getString(R.string.today),dailyobj);
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
    private void GetPerformanceFilter(){
        if (mbinding.tvFromDate.getText().toString().isEmpty()){
            Toast.makeText(this, "Select From date", Toast.LENGTH_SHORT).show();
            return;
        } if (mbinding.tvToDate.getText().toString().isEmpty()){
            Toast.makeText(this, "Select To date", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id", user_id);
        param.put("start_date", mbinding.tvFromDate.getText().toString());
        param.put("end_date", mbinding.tvToDate.getText().toString());
        Log.e("URL_EARNING","====>"+BaseUrl.get().filterEarning()+"?user_id="+user_id+
                "&start_date="+mbinding.tvFromDate.getText().toString()+"&end_date="+mbinding.tvToDate.getText().toString());
        ApiCallBuilder.build(this).setUrl(BaseUrl.get().filterEarning())
                .isShowProgressBar(true)
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                Log.e("EarningReportFilter","=========>"+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("status");
                    if (msg.equalsIgnoreCase("1")) {
                        JSONObject jsonArray = jsonObject.getJSONObject("result");
                        String month=jsonArray.getString("month");
                        if (month.equals("null")) {
                            mbinding.rdGroup.clearCheck();
                            mbinding.monthDataLay.removeAllViews();
                            mbinding.tvNoRecord.setVisibility(View.VISIBLE);
                        }else{
                            mbinding.rdGroup.clearCheck();
                            mbinding.tvNoRecord.setVisibility(View.GONE);
                            JSONArray array = jsonArray.getJSONArray("month");
                            BindMonth(array);
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
}
