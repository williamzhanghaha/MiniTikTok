package com.bdac.zhcyc.minititok.Network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.bdac.zhcyc.minititok.Network.beans.Feed;
import com.bdac.zhcyc.minititok.Network.beans.FeedResponse;
import com.bdac.zhcyc.minititok.Network.beans.Item;
import com.bdac.zhcyc.minititok.Network.beans.PostVideoResponse;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Sebb,
 * @date 2019/1/25
 */

/**
 * 网络相关：
 * List<Feed> postVideo(Uri imgUrl,Uri videoUrl,Context context)
 * context是发布视频的那个activity.this
 *
 * List<Item> fetchFeed()
 */

public class NetworkUtils {
    private static final String TAG = "Seb";

    private static final String BASE_URL = "http://10.108.10.39:8080/";
    private static final String STUDENT_ID = "1120172129";
    private static final String USER_NAME = "CHENYICHENG";
    private static final String IMG_NAME = "cover_img";
    private static final String VIDEO_NAME = "video";

    private static List<Feed> feeds = null;
    private static List<Item> items = null;

    public static List<Item> postVideo(final Uri imgUrl, Uri videoUrl, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(IMiniTikTokService.class).creatVideo(
                STUDENT_ID,
                USER_NAME,
                getMultipartFromUri(IMG_NAME,imgUrl,context),
                getMultipartFromUri(VIDEO_NAME,videoUrl,context)
        ).enqueue(new Callback<PostVideoResponse>(){
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response){
                Log.d(TAG,"post response!");
                items = response.body().getItems();
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                Log.d(TAG,"post failed!");
            }
        });

        if(items!=null){
            return items;
        }else{
            return null;
        }
    }

    public static List<Feed> fetchFeed(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(IMiniTikTokService.class).feedResponse()
                .enqueue(new Callback<FeedResponse>() {
                    @Override
                    public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                        Log.d(TAG, "get response!");
                        feeds = response.body().getFeeds();
                    }

                    @Override
                    public void onFailure(Call<FeedResponse> call, Throwable t) {
                        Log.d(TAG,"get failed!");
                    }
                });
        if(feeds!=null){
            return feeds;
        }else{
            return null;
        }
    }

    private static MultipartBody.Part getMultipartFromUri(String name, Uri uri, Context context) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(context, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }
}
