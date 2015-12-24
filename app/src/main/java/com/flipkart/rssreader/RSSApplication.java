package com.flipkart.rssreader;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mayank.gupta on 18/12/15.
 */
public class RSSApplication extends Application {

    private String feed_url = "http://www.engadget.com/rss.xml";
    public DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DBHelper(getApplicationContext());

        Intent intent = new Intent(getApplicationContext(), RSSService.class);
        intent.putExtra("RSS_URL", feed_url);

//        wrap the intent inside a pending intent
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),1001,intent,
                                                                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC,System.currentTimeMillis(),60000,pendingIntent);
    }
}
