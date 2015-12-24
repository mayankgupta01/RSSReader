package com.flipkart.rssreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "Main Activity";

    static class RefreshBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received refresh from service");
        }
    }

    private String url = "http://www.engadget.com/rss.xml";
    private ListView feedItemListView;
    //    Cursor Adapter needs a cursor object to query the database
    private SimpleCursorAdapter simpleCursorAdapter;
    private Cursor cursor;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private RefreshBroadCastReceiver receiver = new RefreshBroadCastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feedItemListView = (ListView) findViewById(R.id.listView);
        feedItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] args = {Long.toString(id)};
                Cursor c = database.query(DBHelper.FEED_ITEM_TABLE, null, "_id = ?", args, null, null, null, null);

//                read the item details from cursor
                c.moveToFirst();
                int linkIndex = c.getColumnIndex(dbHelper.FEED_ITEM_TABLE_LINK);
                String link = c.getString(linkIndex);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                startActivity(intent);
            }
        });

        dbHelper = ((RSSApplication) getApplication()).dbHelper;
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.FEED_ITEM_TABLE, null, null, null, null, null, null);
        String[] from = {dbHelper.FEED_ITEM_TABLE_TITLE, dbHelper.FEED_ITEM_TABLE_LINK};
        int[] to = {android.R.id.text1, android.R.id.text2};

        simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);

        feedItemListView.setAdapter(simpleCursorAdapter);
    }
//  Using BroadCast receiver when RSS feed is refreshed
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RSSService.REFRESH);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    // Day 5 : Pass 1
    public void refresh(View view) {
        Intent intent = new Intent(this, RSSService.class);
        intent.putExtra("RSS_URL", url);
        startService(intent);

    }
}
