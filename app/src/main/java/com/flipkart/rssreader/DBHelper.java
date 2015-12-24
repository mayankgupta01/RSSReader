package com.flipkart.rssreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mayank.gupta on 18/12/15.
 */

//Should have a single instance of DB helper for an App
public class DBHelper extends SQLiteOpenHelper {

    public static final String FEED_ITEM_TABLE = "feedItem";
    public static final String FEED_ITEM_TABLE_TITLE = "title";
    public static final String FEED_ITEM_TABLE_LINK = "link";
    public DBHelper(Context context) {
        super(context, "rssreader.db", null, 3);
    }

//To use a cursor adapter, your table must have a _id column and it should be primary key.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + FEED_ITEM_TABLE + "(  _id integer primary key, " +
                                                "title text not null, "+
                                                "link text not null)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "drop table if exists feedItem";
        db.execSQL(query);
        onCreate(db);
    }
}
