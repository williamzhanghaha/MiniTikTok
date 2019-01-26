package com.bdac.zhcyc.minititok.Database;


import android.provider.BaseColumns;

/**
 * @author Sebb,
 * @date 2019/1/26
 */

public final class MiniTikTokContract {
    private MiniTikTokContract(){}

    public static class MiniTikTokEntry implements BaseColumns{
        public static final String TABLE_NAME = "miniTikTok";
        public static final String COLUMN_STUDENT_ID = "student_id";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_VIDEO_URL = "video_url";
    }
}
