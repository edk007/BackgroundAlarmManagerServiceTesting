package com.edtest.backgroundalarmmanagerservicetesting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "BACKGROUND_ALARM_MANAGER_SERVICE_TESTING";
    public static final String TAG2 = "MAIN_ACTIVITY: ";

    public static final String BAMST_SERVICE_STATUS = "com.edtest.backgroundalarmmanagerservicetesting.STATUS";
    public static final String BAMST_BROADCAST_ACTION = "com.edtest.backgroundalarmmanagerservicetesting.BROADCAST";

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        //start the service
        TestJobIntentService.enqueueWork(this);

        //start a receiver to listen for the updates
        IntentFilter statusIntentFilter = new IntentFilter(BAMST_BROADCAST_ACTION);
        ServiceStateReceiver serviceStateReceiver = new ServiceStateReceiver();
        registerReceiver(serviceStateReceiver, statusIntentFilter);

        //unable to automatically remove battery optimization from an application
        //to have users do this, there are 3 paths:

        /*
        Samsung Path
        Device Care -> Battery -> make sure this app is listed under apps that wont be put to sleep
         */

        /*
        Path 1 - use this code - no manifest required
        Intent myIntent = new Intent();
        myIntent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        startActivity(myIntent);
         */

        /*
        path 2 - more risky for play store apps to get blocked for using this

        use this Manifest XML plus this code
        <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"/>

        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName))
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
        }
        startActivity(intent);
        */

    }

    private class ServiceStateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (BAMST_BROADCAST_ACTION.equals(intent.getAction())) {
                //handle
                textView.setText(intent.getStringExtra(BAMST_SERVICE_STATUS));
            }

        }
    }
}