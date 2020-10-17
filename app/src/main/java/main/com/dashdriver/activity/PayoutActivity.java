package main.com.dashdriver.activity;

import android.app.Activity;
import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.com.dashdriver.Fragments.FragmentAddPayout;
import main.com.dashdriver.R;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.constant.Payout;
import main.com.dashdriver.constant.PayoutBeen;
import main.com.dashdriver.databinding.ActivityPayoutBinding;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class PayoutActivity extends AppCompatActivity {
    private ActivityPayoutBinding binding;
    private MySession mySession;
    private String user_log_data;
    private String user_id;
    private payoutBeens myVehicleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLanguageSession.get(this).setLangRecreate();
        binding= DataBindingUtil.setContentView(this,R.layout.activity_payout);
        bindView();
    }

    private void bindView() {
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data != null) {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    Log.e("USER NAME"," >>"+jsonObject1.getString("first_name"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        binding.exitApp.setOnClickListener(v->finish());
        binding.addPayout.setOnClickListener(v->{
            new FragmentAddPayout().CallBack(this::getPayout).show(getSupportFragmentManager(),"");
        });
        getPayout();
        binding.swipeRefresh.setOnRefreshListener(this::getPayout);
    }
    private void getPayout(){
        binding.swipeRefresh.setRefreshing(true);
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id",user_id);
        ApiCallBuilder.build(this).setUrl(BaseUrl.get().getExpanse())
                .setParam(param)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        binding.swipeRefresh.setRefreshing(false);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equals("1")) {
                                ArrayList<PayoutBeen>payoutBeens=new ArrayList<>();
                                Payout successData = new Gson().fromJson(response, Payout.class);
                                payoutBeens.addAll(successData.getResult());
                                myVehicleAdapter = new payoutBeens(PayoutActivity.this, payoutBeens);
                                binding.list.setAdapter(myVehicleAdapter);
                                myVehicleAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void Failed(String error) {
                        binding.swipeRefresh.setRefreshing(false);
                    }
                });
    }
    public class payoutBeens extends BaseAdapter {

        String[] result;
        Context context;
        List<PayoutBeen> myvehicleBeanArrayList;
        private LayoutInflater inflater = null;


        public payoutBeens(Activity activity, List<PayoutBeen> myvehicleBeanArrayList) {
            this.myvehicleBeanArrayList = myvehicleBeanArrayList;
            this.context = activity;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            //return 4;
            return myvehicleBeanArrayList == null ? 0 : myvehicleBeanArrayList.size();
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
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View rowView;
            rowView = inflater.inflate(R.layout.custom_payout_lay, null);
            TextView amount_tv = rowView.findViewById(R.id.amount_tv);
            TextView date_tv = rowView.findViewById(R.id.date_tv);
            TextView payment_desc = rowView.findViewById(R.id.payment_desc);
            TextView payment_title = rowView.findViewById(R.id.payment_title);
            amount_tv.setText("$"+myvehicleBeanArrayList.get(position).getAmount());
            date_tv.setText(myvehicleBeanArrayList.get(position).getDate());
            payment_desc.setText(myvehicleBeanArrayList.get(position).getDescription());
            payment_title.setText(myvehicleBeanArrayList.get(position).getTitle());
            return rowView;
        }

    }
}
