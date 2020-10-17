package main.com.dashdriver.constant;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import main.com.dashdriver.service.TrackingService;

public class MyReceiver extends BroadcastReceiver {
    MySession mySession;
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mySession = new MySession(context);
        if (isMyServiceRunning(TrackingService.class,context)){
            Log.e("Service ... ","running");
        }
        else {
            try {
                if (mySession!=null){
                    if (mySession.IsLoggedIn()){
                        Intent intent1 = new Intent(context, TrackingService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent1);
                        } else {
                            context.startService(intent1);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("EXC BACK >>"," >"+e.getMessage());
            }
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already","running");
                return true;
            }
        }
        Log.i("Service not","running");
        return false;
    }
}
