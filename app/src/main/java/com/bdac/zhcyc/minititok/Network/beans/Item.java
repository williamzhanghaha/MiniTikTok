package com.bdac.zhcyc.minititok.Network.beans;

import com.google.gson.annotations.SerializedName;

/**
 * @author Sebb,
 * @date 2019/1/25
 */

/**
 * post方法取回的java beans
 */

public class Item {
    @SerializedName("student_id")
    String student_id;

    @SerializedName("user_name")
    String user_name;

    @SerializedName("image_url")
    String image_url;

    @SerializedName("video_url")
    String video_url;

    public String getStudent_id() {
        return student_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getVideo_url() {
        return video_url;
    }

}
