package com.flipkart.rssreader;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.NotificationCompat;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class RSSService extends IntentService {

    RSSParser rssParserHandler = new RSSParser();
    DBHelper dbHelper;
    SQLiteDatabase database;
    NotificationManager notificationManager;
    public static final String REFRESH = "com.flipkart.rssreader.REFRESH";


    public RSSService() {
        super("RSSService");
//        initializing in constructor will result in null pointer exception as its not necessary that context
//        is present in constructor. It will definitely be there in onCreate method of service.
//        notificationManager =
//                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
//            get access to DBHelper from application object
            dbHelper = ((RSSApplication) getApplication()).dbHelper;
            database = dbHelper.getWritableDatabase();
            String url = intent.getStringExtra("RSS_URL");
            getNewsFeed(url);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private boolean alreadyExists(FeedItem item) {
        int count = getCount(item.title);
        return count > 0;
    }

    public int getCount(String title) {
        Cursor c = null;
        String query = "select count(*) from feedItem where title = ?";
        c = database.rawQuery(query, new String[] {title});
        if (c.moveToFirst()) {
            return c.getInt(0);
        }
        return 0;
    }

    public void getNewsFeed(String url) {
        URL link = null;
        try {
            link = new URL(url);
            URLConnection connection = link.openConnection();
            InputStream inStr = connection.getInputStream();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(inStr,rssParserHandler);

//          get array of feeditems fro the parser
            ArrayList<FeedItem> feedItems = rssParserHandler.feedItems;

//            iterate and insert records in the database
            for(FeedItem item: feedItems) {
//                create a content values object
                ContentValues row = new ContentValues();
                row.put("title", item.title);
                row.put("link", item.link);

                if(!alreadyExists(item)) {
                    database.insert(DBHelper.FEED_ITEM_TABLE, null, row);

                    sendNotification(item);
                }
            }
            sendNotification(feedItems.get(0));

//            Sending broadcast
            Intent intent = new Intent(REFRESH);
            sendBroadcast(intent);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(FeedItem item) {
//        create a notification object using the Notification compat builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle("RSSReader");
        builder.setContentText(item.title);
        builder.setSmallIcon(R.mipmap.ic_launcher);


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

//        send notification
        notificationManager.notify(1001, notification);
    }

}
