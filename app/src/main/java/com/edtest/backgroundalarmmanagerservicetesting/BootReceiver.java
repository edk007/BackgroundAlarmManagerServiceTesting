package com.edtest.backgroundalarmmanagerservicetesting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    public static final String TAG = "BACKGROUND_ALARM_MANAGER_SERVICE_TESTING";
    public static final String TAG2 = "BOOT_RECEIVER: ";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.w(TAG, TAG2 + "onReceive");

            //start the service
            TestJobIntentService.enqueueWork(context);
        }
    }
}
