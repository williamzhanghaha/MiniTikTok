package com.bdac.zhcyc.minititok.Utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.bdac.zhcyc.minititok.Network.IMiniTikTokService;
import com.bdac.zhcyc.minititok.Network.ResourceUtils;
import com.bdac.zhcyc.minititok.Network.beans.Feed;
import com.bdac.zhcyc.minititok.Network.beans.FeedResponse;
import com.bdac.zhcyc.minititok.Network.beans.Item;
import com.bdac.zhcyc.minititok.Network.beans.PostVideoResponse;
import com.bdac.zhcyc.minititok.UI.FeedsAdapter;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
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
 *
 * postVideo(Uri imgUrl,Uri videoUrl,Context context,RecyclerView rv)
 * context是发布视频activity.this
 *
 * fetchFeed(RecyclerView rv)
 */

public class NetworkUtils {
    private static final String TAG = "Seb";

    private static final String BASE_URL = "http://10.108.10.39:8080/";
    private static final String STUDENT_ID = "1120172129";
    private static final String USER_NAME = "Cyc and zhc";
    private static final String IMG_NAME = "image";
    private static final String VIDEO_NAME = "video";

    private static List<Feed>  feeds = null;
    private static List<Item> items = null;

    public static void postVideo(Uri imgUrl, Uri videoUrl, Context context, final RecyclerView rv) {
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

                Item item = items.get(0);
                DatabaseUtils.saveItemToDatabase(item);

                //TODO 刷新个人主页的RecyclerView
                //rv.getAdapter().notifyDataSetChanged();

                //TODO 更新个人主页的List<Item>
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                Log.d(TAG,"post failed!");
            }
        });
    }

    public static void fetchFeed(final RecyclerView rv){

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
                        //TODO 更新Feed流(主页)的List<Feed>
                        if (rv.getAdapter() instanceof FeedsAdapter) {
                            FeedsAdapter feedsAdapter = (FeedsAdapter)rv.getAdapter();
                            feedsAdapter.refresh(feeds);
                        }
//                        rv.getAdapter().refrush(DatabaseUtils.loadItemsFromDatabas());
                    }

                    @Override
                    public void onFailure(Call<FeedResponse> call, Throwable t) {
                        Log.d(TAG,t.getMessage());
                    }
                });
    }

    private static MultipartBody.Part getMultipartFromUri(String name, Uri uri, Context context) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(context, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }
}
