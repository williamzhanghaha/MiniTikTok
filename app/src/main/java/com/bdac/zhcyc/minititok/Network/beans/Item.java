package com.bdac.zhcyc.minititok.Network.beans;

import com.google.gson.annotations.SerializedName;

/**
 * @author Sebb,
 * @date 2019/1/25
 */

/**
 * post方法取回的java beans
 * 也是数据库的beans
 */

public class Item {
    long id;

    @SerializedName("student_id")
    String student_id;

    @SerializedName("user_name")
    String user_name;

    @SerializedName("image_url")
    String image_url;

    @SerializedName("video_url")
    String video_url;





    public long getId() {
        return id;
    }

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

    public void setId(long id) {
        this.id = id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
