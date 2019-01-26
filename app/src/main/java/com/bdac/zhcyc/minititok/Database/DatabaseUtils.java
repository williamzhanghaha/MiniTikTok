package com.bdac.zhcyc.minititok.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.bdac.zhcyc.minititok.Network.beans.Feed;
import com.bdac.zhcyc.minititok.Network.beans.Item;

import java.util.Collections;
import java.util.List;

/**
 * @author Sebb,
 * @date 2019/1/26
 */

public class DatabaseUtils {
    private static MiniTikTokDbHelper mDbHelper = null;
    private static SQLiteDatabase mDb = null;

    /**
     * context 是想要打开数据库的activity.this
     * @param context
     */
    public static void dbInit(Context context) {
        mDbHelper = new MiniTikTokDbHelper(context);
        mDb = mDbHelper.getWritableDatabase();
    }

    public static void dbDestory() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public static List<Item> loadItemsFromDatabas(){
        if(mDb==null){
            return Collections.emptyList();
        }

        List<Item> items = null;
        Cursor cursor = null;

        try{
            String [] projection = {
                    BaseColumns._ID,
                    MiniTikTokContract.MiniTikTokEntry.COLUMN_STUDENT_ID,
                    MiniTikTokContract.MiniTikTokEntry.COLUMN_USER_NAME,
                    MiniTikTokContract.MiniTikTokEntry.COLUMN_IMAGE_URL,
                    MiniTikTokContract.MiniTikTokEntry.COLUMN_VIDEO_URL,
            };

            cursor = mDb.query(
                    MiniTikTokContract.MiniTikTokEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    MiniTikTokContract.MiniTikTokEntry._ID+" DESC"
            );

            while(cursor.moveToNext()){
                long id = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MiniTikTokContract.MiniTikTokEntry._ID)
                );
                String studentId = cursor.getString(
                        cursor.getColumnIndexOrThrow(MiniTikTokContract.MiniTikTokEntry.COLUMN_STUDENT_ID)
                );
                String userName = cursor.getString(
                        cursor.getColumnIndexOrThrow(MiniTikTokContract.MiniTikTokEntry.COLUMN_USER_NAME)
                );
                String imageUrl = cursor.getString(
                        cursor.getColumnIndexOrThrow(MiniTikTokContract.MiniTikTokEntry.COLUMN_IMAGE_URL)
                );
                String videoUrl = cursor.getString(
                        cursor.getColumnIndexOrThrow(MiniTikTokContract.MiniTikTokEntry.COLUMN_VIDEO_URL)
                );
                Item item = new Item();
                item.setStudent_id(studentId);
                item.setUser_name(userName);
                item.setImage_url(imageUrl);
                item.setVideo_url(videoUrl);

                items.add(item);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return items;
    }

    public static void saveItem2Database(){
        try{

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

