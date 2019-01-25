package com.bdac.zhcyc.minititok.Network.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Sebb,
 * @date 2019/1/25
 */

public class FeedResponse {
    @SerializedName("feeds")
    List<Feed> feeds;
    @SerializedName("success")
    boolean isSuccess;

    public List<Feed> getFeeds() {
        return feeds;
    }

    public boolean isSuccess() {
        return isSuccess;
    }


}
