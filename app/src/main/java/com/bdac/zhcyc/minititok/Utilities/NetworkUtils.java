package com.bdac.zhcyc.minititok.Utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.bdac.zhcyc.minititok.Network.IMiniTikTokService;
import com.bdac.zhcyc.minititok.Network.beans.Feed;
import com.bdac.zhcyc.minititok.Network.beans.FeedResponse;
import com.bdac.zhcyc.minititok.Network.beans.Item;
import com.bdac.zhcyc.minititok.Network.beans.PostVideoResponse;
import com.bdac.zhcyc.minititok.UI.FeedsAdapter;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
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
    private static final String IMAGE_NAME = "cover_image";
    private static final String VIDEO_NAME = "video";

    private static List<Feed> feeds = null;
    private static Item item =null;

    public static void postVideo(Uri imageUrl, Uri videoUrl, Context context, final RecyclerView rv) {
        Log.d(TAG,imageUrl.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(IMiniTikTokService.class).createVideo(
                STUDENT_ID,
                USER_NAME,
                getMultipartFromUri(IMAGE_NAME,imageUrl,context),
                getMultipartFromUri(VIDEO_NAME,videoUrl,context)
        ).enqueue(new Callback<PostVideoResponse>(){
            @Override
            public void onResponse(@NonNull Call<PostVideoResponse> call, @NonNull Response<PostVideoResponse> response){
                Log.d(TAG,"post response!");

                item = response.body().getItem();
                Log.d(TAG,item.getStudent_id());

                DatabaseUtils.saveItemToDatabase(item);

                Toast.makeText(context,"post successfully!",Toast.LENGTH_SHORT).show();

                //TODO 刷新个人主页的RecyclerView

                //TODO 更新个人主页的List<Item>
            }

            @Override
            public void onFailure(@NonNull Call<PostVideoResponse> call,@NonNull Throwable t) {
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
                    public void onResponse(@NonNull Call<FeedResponse> call,@NonNull Response<FeedResponse> response) {
                        Log.d(TAG, "get response!");
                        feeds = response.body().getFeeds();

                        if (rv.getAdapter() instanceof FeedsAdapter) {
                            FeedsAdapter feedsAdapter = (FeedsAdapter)rv.getAdapter();
                            feedsAdapter.refresh(feeds);
                        }

                    }
                    @Override
                    public void onFailure(@NonNull Call<FeedResponse> call,@NonNull Throwable t) {
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
