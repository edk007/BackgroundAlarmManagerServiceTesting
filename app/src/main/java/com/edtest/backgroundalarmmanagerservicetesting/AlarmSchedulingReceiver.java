package com.edtest.backgroundalarmmanagerservicetesting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmSchedulingReceiver extends BroadcastReceiver {
    public static final String TAG = "BACKGROUND_ALARM_MANAGER_SERVICE_TESTING";
    public static final String TAG2 = "ALARM_SCHEDULING_RECEIVER: ";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG, TAG2 + "onReceive");

        //this alarm is fired in order for us to start the service again
        //start service
        TestJobIntentService.enqueueWork(context);
    }
}
