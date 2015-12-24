package com.flipkart.rssreader;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {
    private String feed_url = "http://www.engadget.com/rss.xml";
    private static final String TAG = "BootCompleteReceviver";

    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent i) {
        Log.i(TAG, "RSS Service Starting after reboot");
        Intent intent = new Intent(context, RSSService.class);
        intent.putExtra("RSS_URL", feed_url);

//        wrap the intent inside a pending intent
        PendingIntent pendingIntent = PendingIntent.getService(context,1001,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC,System.currentTimeMillis(),60000,pendingIntent);

    }
}
