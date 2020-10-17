package main.com.dashdriver.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import main.com.dashdriver.Interfaces.onSelectDate;

public class Tools {
    String vehicle_model_str="";
    public static Tools get() {
        return new Tools();
    }
    public void DatePicker(Context context, onSelectDate date){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
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
                        date.Success(dayOfMonth + "-" + mon + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    public void DatePicker(Context context, onSelectDate date,String format){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date.Success(ChangeDateFormat(year+"-" + (monthOfYear+1) + "-" +dayOfMonth ,format));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    public String ChangeDateToMonth(String date){
        SimpleDateFormat in_format=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat out_format=new SimpleDateFormat("MMMM");
        try {
            Date in_date=in_format.parse(date);
            return out_format.format(in_date);
        } catch (ParseException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }

    }
    public String ChangeDateFormat(String date,String format){
        SimpleDateFormat in_format=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat out_format=new SimpleDateFormat(format);
        try {
            Date in_date=in_format.parse(date);
            return out_format.format(in_date);
        } catch (ParseException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }

    }
    public String CurrentDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        return format;
    }
}
