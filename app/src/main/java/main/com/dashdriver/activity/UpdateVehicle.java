package main.com.dashdriver.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import main.com.dashdriver.R;
import main.com.dashdriver.constant.ACProgressCustom;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MultipartUtility;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.pojoclasses.VehicleType;
import main.com.dashdriver.pojoclasses.ViechleBean;
import main.com.dashdriver.restapi.ApiClient;

public class UpdateVehicle extends AppCompatActivity {

    private TextView addvehicle;
    private RelativeLayout vehiclomglay;
    private ImageView carimage;
    private String VEHICLE_IMG_PATH = "";
    private EditText vehiclename, vehicleplatenumber, vehiclemodelyear, vehiclecolor,licenseplate,vehiclemake;
    private Spinner vehicletype, vehiclesize;
    private String vehicle_name_str = "", vehicle_plate_number_str = "", vehicle_model_str = "", vehicle_col_str = "", vehicle_type_str = "", vehicle_type_id_str, vehicle_size_str = "", vehicle_size_id_str = "";
    ACProgressCustom ac_dialog;
    private ArrayList<ViechleBean> viechleBeanArrayList;
    private SelectViechleAdapter selectViechleAdapter;
    private MySession mySession;
    private String user_log_data = "", user_id = "",vehicle_make_str="",license_plate_str="",vehicle_id="",vehicle_img_str="",refrigirated="No";
    private RelativeLayout exit_app_but;


    private ImageView galleryphoto,crphoto;
    private LinearLayout imglay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_vehicle);

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

                    Log.e("user_id >>>>", "" + user_id);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null&&!bundle.isEmpty())
        {

            vehicle_id = bundle.getString("vehicle_id");
            vehicle_name_str = bundle.getString("vehicle_name");
            vehicle_type_str = bundle.getString("vehicle_type_name");
            vehicle_type_id_str = bundle.getString("vehicle_type_id");
            vehicle_plate_number_str = bundle.getString("vehicle_number");
            vehicle_model_str = bundle.getString("vehicle_model_year");
            vehicle_col_str = bundle.getString("vehicle_color");
            vehicle_size_str = bundle.getString("vehicle_size");
            vehicle_size_id_str = bundle.getString("vehicle_size_id");
            vehicle_img_str = bundle.getString("vehicle_image");

            vehicle_make_str = bundle.getString("vehicle_make");
            license_plate_str = bundle.getString("license_plate");





        }

        idinti();
        clickevent();
        getVehicleType();


    }


    private void clickevent() {
        addvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicle_name_str = vehiclename.getText().toString();
                vehicle_col_str = vehiclecolor.getText().toString();
                vehicle_plate_number_str = vehicleplatenumber.getText().toString();
                vehicle_model_str = vehiclemodelyear.getText().toString();
                vehicle_make_str = vehiclemake.getText().toString();
                license_plate_str = licenseplate.getText().toString();


                if (vehicle_name_str == null || vehicle_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(UpdateVehicle.this, getResources().getString(R.string.enteryourvehiclename), Toast.LENGTH_LONG).show();

                }
                else if (vehicle_make_str == null || vehicle_make_str.equalsIgnoreCase("")) {
                    Toast.makeText(UpdateVehicle.this, getResources().getString(R.string.entervehiclemake), Toast.LENGTH_LONG).show();

                }
                else if (vehicle_type_id_str == null || vehicle_type_id_str.equalsIgnoreCase("")) {
                    Toast.makeText(UpdateVehicle.this, getResources().getString(R.string.selectvehiclecategory), Toast.LENGTH_LONG).show();

                } /*else if (vehicle_size_id_str == null || vehicle_size_id_str.equalsIgnoreCase("")) {
                    Toast.makeText(UpdateVehicle.this, getResources().getString(R.string.selectvehiclesize), Toast.LENGTH_LONG).show();

                }*/ else if (vehicle_plate_number_str == null || vehicle_plate_number_str.equalsIgnoreCase("")) {
                    Toast.makeText(UpdateVehicle.this, getResources().getString(R.string.enteryourvehicleregnumber), Toast.LENGTH_LONG).show();

                } else if (vehicle_model_str == null || vehicle_model_str.equalsIgnoreCase("")) {
                    Toast.makeText(UpdateVehicle.this, getResources().getString(R.string.selectvehiclemodel), Toast.LENGTH_LONG).show();

                }
                else if (license_plate_str == null || license_plate_str.equalsIgnoreCase("")) {
                    Toast.makeText(UpdateVehicle.this, getResources().getString(R.string.enterlicennumber), Toast.LENGTH_LONG).show();

                }
                else if (vehicle_col_str == null || vehicle_col_str.equalsIgnoreCase("")) {
                    Toast.makeText(UpdateVehicle.this, getResources().getString(R.string.enteryourvehiclecol), Toast.LENGTH_LONG).show();

                } else {

                     new UpdateVehicleAsc().execute();
                }

            }
        });
/*
        vehiclomglay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
*/
        galleryphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });
        crphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 2);
            }
        });
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idinti() {
        imglay = findViewById(R.id.imglay);
        carimage = findViewById(R.id.carimage);
        galleryphoto = findViewById(R.id.galleryphoto);

        exit_app_but = findViewById(R.id.exit_app_but);
        vehiclecolor = findViewById(R.id.vehiclecolor);
        vehiclemodelyear = findViewById(R.id.vehiclemodelyear);
        vehicleplatenumber = findViewById(R.id.vehicleplatenumber);
        vehiclesize = findViewById(R.id.vehiclesize);
        vehiclename = findViewById(R.id.vehiclename);
        crphoto = findViewById(R.id.crphoto);
        vehiclomglay = findViewById(R.id.vehiclomglay);
        addvehicle = findViewById(R.id.addvehicle);
        vehicletype = findViewById(R.id.vehicletype);
        licenseplate = findViewById(R.id.licenseplate);
        vehiclemake = findViewById(R.id.vehiclemake);

        vehiclecolor.setText(""+vehicle_col_str);
        vehiclename.setText(""+vehicle_name_str);
        vehicleplatenumber.setText(""+vehicle_plate_number_str);
        vehiclemodelyear.setText(""+vehicle_model_str);
        vehiclemake.setText(""+vehicle_make_str);
        licenseplate.setText(""+license_plate_str);

        if (vehicle_img_str==null||vehicle_img_str.equalsIgnoreCase("")||vehicle_img_str.equalsIgnoreCase(BaseUrl.image_baseurl)){

        }
        else {
            Picasso.with(UpdateVehicle.this).load(vehicle_img_str).placeholder(R.drawable.placeholder).into(carimage);
           // galleryphoto.setVisibility(View.GONE);
           // crphoto.setVisibility(View.GONE);
            imglay.setVisibility(View.GONE);
        }





        vehicletype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (viechleBeanArrayList == null) {

                } else {
                    if (viechleBeanArrayList.size() > 0) {
                        if (viechleBeanArrayList.get(position).getId().equalsIgnoreCase("0")) {
                            vehicle_type_id_str = "";
                            vehicle_type_str = "";
                        } else {
                            vehicle_type_id_str = viechleBeanArrayList.get(position).getId();
                            vehicle_type_str = viechleBeanArrayList.get(position).getVehicleName();

                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        ContextWrapper cw = new ContextWrapper(UpdateVehicle.this);
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
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        final int REQUIRED_SIZE = 1024;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        VEHICLE_IMG_PATH = saveToInternalStorage(bitmap);
        Log.e("DECODE PATH", "ff " + VEHICLE_IMG_PATH);
        carimage.setImageBitmap(bitmap);
       // galleryphoto.setVisibility(View.GONE);
        //crphoto.setVisibility(View.GONE);
        imglay.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri selectedImage = data.getData();
                    String ImagePath = getPath(selectedImage);
                    decodeFile(ImagePath);

                    break;
                case 2:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    String cameraPath = saveToInternalStorage(photo);
                    decodeFile(cameraPath);
                    break;

            }
        }


    }


    private void getVehicleType() {
        if (ac_dialog != null) {
            ac_dialog.show();
        }
        viechleBeanArrayList = new ArrayList<>();
        ViechleBean viechleBean = new ViechleBean();
        viechleBean.setId("0");
        viechleBean.setVehicleName("Select Vehicle Category");
        viechleBeanArrayList.add(viechleBean);
        Call<ResponseBody> call = ApiClient.getApiInterface().getVehicleType();
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
                        Log.e("Vehicle Type >", " >" + responseData);
                        if (object.getString("status").equals("1")) {

                            VehicleType successData = new Gson().fromJson(responseData, VehicleType.class);
                            viechleBeanArrayList.addAll(successData.getResult());
                            selectViechleAdapter = new SelectViechleAdapter(UpdateVehicle.this, viechleBeanArrayList);
                            vehicletype.setAdapter(selectViechleAdapter);
                            if (vehicle_type_id_str!=null&&!vehicle_type_id_str.isEmpty()){
                                for (int i=0;i<viechleBeanArrayList.size();i++){
                                    if (vehicle_type_id_str!=null&&!vehicle_type_id_str.equalsIgnoreCase("")){
                                        if (vehicle_type_id_str.equalsIgnoreCase(viechleBeanArrayList.get(i).getId())){
                                            vehicletype.setSelection(i);
                                        }
                                    }
                                }

                            }

                        }
                    } catch (Exception e) {
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

            }
        });
    }


    public class SelectViechleAdapter extends BaseAdapter {

        String[] result;
        Context context;
        String type = "";

        ArrayList<ViechleBean> viechleBeanArrayList;
        private LayoutInflater inflater = null;


        public SelectViechleAdapter(Activity activity, ArrayList<ViechleBean> viechleBeanArrayList) {
            this.viechleBeanArrayList = viechleBeanArrayList;
            this.context = activity;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            //return 10;
            return viechleBeanArrayList == null ? 0 : viechleBeanArrayList.size();


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

            rowView = inflater.inflate(R.layout.custom_spinner_lay, null);
            TextView itemname = rowView.findViewById(R.id.itemname);
            itemname.setText("" + viechleBeanArrayList.get(position).getVehicleName());


            return rowView;
        }

    }

    public class UpdateVehicleAsc extends AsyncTask<String, String, String> {

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
//http://technorizen.com/transport/webservice/update_vehicle?driver_id=5&vehicle_type_id=
            String charset = "UTF-8";
            String requestURL = BaseUrl.baseurl + "update_vehicle?";
            Log.e("UpdateUrl >>", requestURL+"&driver_id="+user_id+"&vehicle_id="+vehicle_id+"&vehicle_name="+vehicle_name_str+"&vehicle_type_str="+vehicle_type_str+"&vehicle_type_id="+vehicle_type_id_str+"&vehicle_number="+vehicle_plate_number_str+"&vehicle_model_year="+vehicle_model_str+"&vehicle_color="+vehicle_col_str+"&vehicle_size="+vehicle_size_str+"&vehicle_size_id="+vehicle_size_id_str);

            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("driver_id", user_id);
                multipart.addFormField("vehicle_id", vehicle_id);
                multipart.addFormField("vehicle_name", vehicle_name_str);
                multipart.addFormField("vehicle_type_name", vehicle_type_str);
                multipart.addFormField("vehicle_type_id", vehicle_type_id_str);
                multipart.addFormField("vehicle_number", vehicle_plate_number_str);
                multipart.addFormField("vehicle_model_year", vehicle_model_str);
                multipart.addFormField("vehicle_color", vehicle_col_str);
                multipart.addFormField("vehicle_size", vehicle_size_str);
                multipart.addFormField("vehicle_size_id", vehicle_size_id_str);
                multipart.addFormField("refrigirated", refrigirated.trim());
                multipart.addFormField("vehicle_make", vehicle_make_str);
                multipart.addFormField("license_plate", license_plate_str);

                multipart.addFormField("vehicle_model", "");

                if (VEHICLE_IMG_PATH.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(VEHICLE_IMG_PATH);
                    multipart.addFilePart("vehicle_image", ImageFile);
                }
                List<String> response = multipart.finish();
                for (String line : response) {
                    Jsondata = line;
                    Log.e("Update Vehicle ====", Jsondata);

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
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        Toast.makeText(UpdateVehicle.this, getResources().getString(R.string.yourvehicleupdateedsucc), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }


    }


    private void carReq() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(UpdateVehicle.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        // canceldialog.setContentView(R.layout.car_request_add);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                finish();
            }
        });
        canceldialog.show();
    }

}
