package com.bdac.zhcyc.minititok.Network.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Sebb,
 * @date 2019/1/25
 */

public class PostVideoResponse {
    @SerializedName("success")
    boolean isSuccess;

    @SerializedName("item")
    Item item;

    public boolean isSuccess() {
        return isSuccess;
    }

    public Item getItem() {
        return item;
    }
}
