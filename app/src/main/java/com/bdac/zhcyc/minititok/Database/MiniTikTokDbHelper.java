package com.bdac.zhcyc.minititok.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Sebb,
 * @date 2019/1/26
 */

public class MiniTikTokDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "minitiktok.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE "+MiniTikTokContract.MiniTikTokEntry.TABLE_NAME+" ("+
                    MiniTikTokContract.MiniTikTokEntry._ID+" INTEGER PRIMARY KEY,"+
                    MiniTikTokContract.MiniTikTokEntry.COLUMN_STUDENT_ID+" TEXT,"+
                    MiniTikTokContract.MiniTikTokEntry.COLUMN_USER_NAME+" TEXT,"+
                    MiniTikTokContract.MiniTikTokEntry.COLUMN_IMAGE_URL+" TEXT,"+
                    MiniTikTokContract.MiniTikTokEntry.COLUMN_VIDEO_URL+" TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MiniTikTokContract.MiniTikTokEntry.TABLE_NAME;

    public MiniTikTokDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
