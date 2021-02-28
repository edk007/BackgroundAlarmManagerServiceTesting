package com.edtest.backgroundalarmmanagerservicetesting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestJobIntentService extends JobIntentService {
    public static final String TAG = "BACKGROUND_ALARM_MANAGER_SERVICE_TESTING";
    public static final String TAG2 = "TEST_JOB_INTENT_SERVICE: ";

    public static final String BAMST_SERVICE_STATUS = "com.edtest.backgroundalarmmanagerservicetesting.STATUS";
    public static final String BAMST_BROADCAST_ACTION = "com.edtest.backgroundalarmmanagerservicetesting.BROADCAST";

    static final int JOB_ID = 1000;
    static final int REQUEST_CODE = 1001;

    public static void enqueueWork(Context context) {
        Intent intent = new Intent(context, com.edtest.backgroundalarmmanagerservicetesting.TestJobIntentService.class);
        enqueueWork(context, com.edtest.backgroundalarmmanagerservicetesting.TestJobIntentService.class, JOB_ID, intent);
    }

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, com.edtest.backgroundalarmmanagerservicetesting.TestJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        //schedule alarm manager
        Intent alarmSchedulingReceiver = new Intent(TestJobIntentService.this, AlarmSchedulingReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TestJobIntentService.this, REQUEST_CODE, alarmSchedulingReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) TestJobIntentService.this.getSystemService(ALARM_SERVICE);

        //make the time 60 seconds in the future
        long nextTime = System.currentTimeMillis() + 60000; //add 60 seconds to time

        //this app will show errors here becuase the gradle is set to target above these codes - but this is for reference anyway
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            //below kitkat / SDK19
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        }
        else if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //kitkat (19) -> Marshmallow (23)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //above marshmallow
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        }

        //write teh time stamp to the file
        String ts = DateFormat.getDateTimeInstance().format(new Date());
        Log.w(TAG, TAG2 + "DO_WORK: " + ts);
        writeToFile("TIME_STAMP: " + ts + ", " + System.currentTimeMillis() + "\n");

        //broadcast status
        String status = "Last Update: " + ts;
        Intent statusIntent = new Intent(BAMST_BROADCAST_ACTION)
                // Puts the status into the Intent
                .putExtra(BAMST_SERVICE_STATUS, status);
        // Broadcasts the Intent to receivers in this app.
        sendBroadcast(statusIntent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void writeToFile(String data) {
        String TAG3 = "WRITE_FILE_OUTPUT: ";
        String fileName = "BACKGROUND_ALARM_MANAGER_SERVICE_TESTING_LOG.txt";
        File file;
        File saveFilePath;

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            saveFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        } else  {
            saveFilePath = com.edtest.backgroundalarmmanagerservicetesting.TestJobIntentService.this.getExternalFilesDir(null);
        }
        Log.w(TAG, TAG2 + TAG3 + "LOG_FILE_PATH: " + saveFilePath.toString());
        file = new File(saveFilePath, fileName);

        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            try {
                stream.write(data.getBytes());
            } finally {
                Log.w(TAG, TAG2 + TAG3 + "WRITE_SUCCESS");
                stream.close();
            }
        } catch (IOException e) {
            Log.w(TAG, TAG2 + TAG3 + "WRITE_FAIL");
            e.printStackTrace();
        }
    }
}
