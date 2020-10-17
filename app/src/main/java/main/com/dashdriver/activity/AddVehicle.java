package main.com.dashdriver.activity;
//TODO: user app login. 03322619097 Driver 03322619098 password. 123456
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.R;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.pojoclasses.VehicleType;
import main.com.dashdriver.pojoclasses.ViechleBean;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class AddVehicle extends AppCompatActivity {
    private TextView addvehicle,vehiclemodelyear;
    private RelativeLayout vehiclomglay;
    private ImageView crphoto,carimage,galleryphoto;
    private String VEHICLE_IMG_PATH = "";
    private EditText vehiclename, vehicleplatenumber, vehiclecolor,licenseplate,vehiclemake;
    private Spinner vehicletype, vehiclesize;
    private String vehicle_name_str = "",vehicle_make_str="",license_plate_str="", vehicle_plate_number_str = "", vehicle_model_str = "", vehicle_col_str = "", vehicle_type_str = "", vehicle_type_id_str, vehicle_size_str = "", vehicle_size_id_str = "";
    private ArrayList<ViechleBean> viechleBeanArrayList;
    private SelectViechleAdapter selectViechleAdapter;
    private MySession mySession;
    private String user_log_data = "", user_id = "";
    private RelativeLayout exit_app_but;


    private LinearLayout imglay;
    private RadioButton rd_regular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLanguageSession.get(this).setLangRecreate();
        setContentView(R.layout.activity_add_vehicle);
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
                if (VEHICLE_IMG_PATH == null || VEHICLE_IMG_PATH.equalsIgnoreCase("")) {
                    Toast.makeText(AddVehicle.this, getResources().getString(R.string.selectvehicleimg), Toast.LENGTH_LONG).show();
                } else if (vehicle_name_str == null || vehicle_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddVehicle.this, getResources().getString(R.string.enteryourvehiclename), Toast.LENGTH_LONG).show();
                }
                else if (vehicle_make_str == null || vehicle_make_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddVehicle.this, getResources().getString(R.string.entervehiclemake), Toast.LENGTH_LONG).show();
                }
                else if (vehicle_type_id_str == null || vehicle_type_id_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddVehicle.this, getResources().getString(R.string.selectvehiclecategory), Toast.LENGTH_LONG).show();

                } else if (vehicle_plate_number_str == null || vehicle_plate_number_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddVehicle.this, getResources().getString(R.string.enteryourvehicleregnumber), Toast.LENGTH_LONG).show();

                } else if (vehicle_model_str == null || vehicle_model_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddVehicle.this, getResources().getString(R.string.selectvehiclemodel), Toast.LENGTH_LONG).show();

                }
                else if (license_plate_str == null || license_plate_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddVehicle.this, getResources().getString(R.string.enterlicennumber), Toast.LENGTH_LONG).show();

                }
                else if (vehicle_col_str == null || vehicle_col_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddVehicle.this, getResources().getString(R.string.enteryourvehiclecol), Toast.LENGTH_LONG).show();

                } else {
                    AddNewVehicle();
                }
            }
        });
        galleryphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
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


        vehiclemodelyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddVehicle.this,
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
                                vehiclemodelyear.setText(dayOfMonth + "-" + mon + "-" + year);
                                vehicle_model_str = "" + year + "-" + month + "-" + daysss;

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

    }

    private void idinti() {
        imglay = findViewById(R.id.imglay);
        carimage = findViewById(R.id.carimage);
        galleryphoto = findViewById(R.id.galleryphoto);
        exit_app_but = findViewById(R.id.exit_app_but);
        licenseplate = findViewById(R.id.licenseplate);
        vehiclemake = findViewById(R.id.vehiclemake);
        vehiclecolor = findViewById(R.id.vehiclecolor);

        vehiclemodelyear = findViewById(R.id.vehiclemodelyear);
        vehicleplatenumber = findViewById(R.id.vehicleplatenumber);
        vehiclesize = findViewById(R.id.vehiclesize);
        vehiclename = findViewById(R.id.vehiclename);
        crphoto = findViewById(R.id.crphoto);
        vehiclomglay = findViewById(R.id.vehiclomglay);
        addvehicle = findViewById(R.id.addvehicle);
        vehicletype = findViewById(R.id.vehicletype);
        rd_regular = findViewById(R.id.rd_regular);


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
        }
        cursor.close();
        return path;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String dateToStr = format.format(today);
        ContextWrapper cw = new ContextWrapper(AddVehicle.this);
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
        galleryphoto.setVisibility(View.GONE);
        crphoto.setVisibility(View.GONE);
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
        viechleBeanArrayList = new ArrayList<>();
        ViechleBean viechleBean = new ViechleBean();
        viechleBean.setId("0");
        viechleBean.setVehicleName("Select Vehicle Category");
        viechleBeanArrayList.add(viechleBean);
        ApiCallBuilder.build(this).setUrl(BaseUrl.get().getCar())
                .isShowProgressBar(true).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    Log.e("Vehicle Type >", " >" + response);
                    if (object.getString("status").equals("1")) {
                        VehicleType successData = new Gson().fromJson(response, VehicleType.class);
                        viechleBeanArrayList.addAll(successData.getResult());
                        selectViechleAdapter = new SelectViechleAdapter(AddVehicle.this, viechleBeanArrayList);
                        vehicletype.setAdapter(selectViechleAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void Failed(String error) {

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
    private HashMap<String,String>getParam(){
        HashMap<String,String>param=new HashMap<>();
        param.put("driver_id", user_id);
        param.put("vehicle_name", vehicle_name_str);
        param.put("vehicle_type_name", vehicle_type_str);
        param.put("vehicle_type_id", vehicle_type_id_str);
        param.put("vehicle_number", vehicle_plate_number_str);
        param.put("vehicle_model_year", vehicle_model_str);
        param.put("vehicle_color", vehicle_col_str);
        param.put("vehicle_size", vehicle_size_str);
        param.put("vehicle_size_id", vehicle_size_id_str);
        param.put("vehicle_make", vehicle_make_str);
        param.put("license_plate", license_plate_str);
        param.put("vehicle_model", "");
        param.put("vehicle_type", rd_regular.isChecked()?"Reqular":"VIP");
        return param;
    }
    private void AddNewVehicle(){
        ApiCallBuilder.build(this).setUrl(BaseUrl.get().AddVehicle())
                .isShowProgressBar(true)
                .setFile("vehicle_image",VEHICLE_IMG_PATH)
                .setParam(getParam()).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        Toast.makeText(AddVehicle.this, getResources().getString(R.string.yourvehicleaddedsucc), Toast.LENGTH_LONG).show();
                        finish();
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
