package main.com.dashdriver.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.R;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.constant.MyVehicleCls;
import main.com.dashdriver.pojoclasses.MyvehicleBean;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class MyVehiclsAct extends AppCompatActivity {

    private RelativeLayout addvehiclelay,exit_app_but;
    private ListView vehicllist;
    private MySession mySession;
    private String user_log_data="",user_id="";
    private MyVehicleAdapter myVehicleAdapter;
    private List<MyvehicleBean> myvehicleBeanArrayList;
    private TextView novehicletv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLanguageSession.get(this).setLangRecreate();
                setContentView(R.layout.activity_my_vehicls);
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data != null) {
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
        }
        idinit();
        clickevent();
    }

    private void clickevent() {
        addvehiclelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyVehiclsAct.this,AddVehicle.class);
                startActivity(i);
            }
        });
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });
    }

    private void idinit() {
        novehicletv = findViewById(R.id.novehicletv);
        vehicllist = findViewById(R.id.vehicllist);
        addvehiclelay = findViewById(R.id.addvehiclelay);
        exit_app_but = findViewById(R.id.exit_app_but);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getMyVehicle();
          }

    private void getMyVehicle() {
        HashMap<String,String>param=new HashMap<>();
        param.put("driver_id",user_id);
        myvehicleBeanArrayList = new ArrayList<>();
        ApiCallBuilder.build(this).isShowProgressBar(true)
                .setUrl(BaseUrl.get().getVehicle())
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                Log.e("VehicleList","=======>"+response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("1")) {
                        MyVehicleCls successData = new Gson().fromJson(response, MyVehicleCls.class);
                        myvehicleBeanArrayList.addAll(successData.getResult());
                        myVehicleAdapter = new MyVehicleAdapter(MyVehiclsAct.this, myvehicleBeanArrayList);
                        vehicllist.setAdapter(myVehicleAdapter);
                        myVehicleAdapter.notifyDataSetChanged();
                        novehicletv.setVisibility(View.GONE);
                    }
                    else {
                        novehicletv.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    novehicletv.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }

            @Override
            public void Failed(String error) {

            }
        });
    }

    public class MyVehicleAdapter extends BaseAdapter {

        String[] result;
        Context context;
       List<MyvehicleBean> myvehicleBeanArrayList;
        private LayoutInflater inflater = null;


        public MyVehicleAdapter(Activity activity, List<MyvehicleBean> myvehicleBeanArrayList) {
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
            rowView = inflater.inflate(R.layout.custom_my_car_lay, null);
            LinearLayout updatecar = rowView.findViewById(R.id.updatecar);
            ImageView truckimage = rowView.findViewById(R.id.truckimage);
            TextView truckname = rowView.findViewById(R.id.truckname);
            TextView date = rowView.findViewById(R.id.date);
            TextView category = rowView.findViewById(R.id.category);
            TextView type = rowView.findViewById(R.id.type);

            if (myvehicleBeanArrayList.get(position).getVehicleImage()==null||myvehicleBeanArrayList.get(position).getVehicleImage().equalsIgnoreCase("")||myvehicleBeanArrayList.get(position).getVehicleImage().equalsIgnoreCase(BaseUrl.image_baseurl)){

            }
            else {
                Picasso.with(MyVehiclsAct.this).load(myvehicleBeanArrayList.get(position).getVehicleImage()).placeholder(R.drawable.placeholder).into(truckimage);

            }

            truckname.setText(""+myvehicleBeanArrayList.get(position).getVehicleName());
            category.setText(""+myvehicleBeanArrayList.get(position).getVehicleTypeName());
            date.setText(""+myvehicleBeanArrayList.get(position).getDateTime());
            type.setText(""+myvehicleBeanArrayList.get(position).getVehicleType());

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(MyVehiclsAct.this,UpdateVehicle.class);
                    i.putExtra("vehicle_id",myvehicleBeanArrayList.get(position).getId());
                    i.putExtra("vehicle_name",myvehicleBeanArrayList.get(position).getVehicleName());
                    i.putExtra("vehicle_type_name",myvehicleBeanArrayList.get(position).getVehicleTypeName());
                    i.putExtra("vehicle_type_id",myvehicleBeanArrayList.get(position).getVehicleTypeId());
                    i.putExtra("vehicle_number",myvehicleBeanArrayList.get(position).getVehicleNumber());
                    i.putExtra("vehicle_model_year",myvehicleBeanArrayList.get(position).getVehicleModelYear());
                    i.putExtra("vehicle_color",myvehicleBeanArrayList.get(position).getVehicleColor());
                    i.putExtra("vehicle_size",myvehicleBeanArrayList.get(position).getVehicleSize());
                    i.putExtra("vehicle_size_id",myvehicleBeanArrayList.get(position).getVehicleSizeId());
                    i.putExtra("vehicle_image",myvehicleBeanArrayList.get(position).getVehicleImage());
                    i.putExtra("vehicle_make",myvehicleBeanArrayList.get(position).getVehicle_make());
                    i.putExtra("license_plate",myvehicleBeanArrayList.get(position).getLicense_plate());
                    startActivity(i);



                }
            });

            return rowView;
        }

    }

}
