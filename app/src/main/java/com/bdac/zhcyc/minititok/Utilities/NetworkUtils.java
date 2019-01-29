package com.bdac.zhcyc.minititok.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.bdac.zhcyc.minititok.MainActivity;
import com.bdac.zhcyc.minititok.Network.IMiniTikTokService;
import com.bdac.zhcyc.minititok.Network.beans.Feed;
import com.bdac.zhcyc.minititok.Network.beans.FeedResponse;
import com.bdac.zhcyc.minititok.Network.beans.Item;
import com.bdac.zhcyc.minititok.Network.beans.PostVideoResponse;
import com.bdac.zhcyc.minititok.R;
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
    private static String STUDENT_ID = "1120172129";
    private static String USER_NAME = "Cyc and zhc";
    private static final String IMAGE_NAME = "cover_image";
    private static final String VIDEO_NAME = "video";
    private static final String PACKAGE_NAME = "com.bdac.zhcyc.minitiktok";

    private static List<Feed> feeds = null;
    private static Item item =null;

    public static void postVideo(Uri imageUrl, Uri videoUrl, Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        STUDENT_ID = sharedPref.getString(context.getString(R.string.sp_student_id), "1120171065");
        USER_NAME = sharedPref.getString(context.getString(R.string.sp_user_name), "zhcyc");

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
            public void onResponse(@NonNull Call<PostVideoResponse> call, @NonNull Response<PostVideoResponse> response) {
                item = response.body().getItem();
                DatabaseUtils.saveItemToDatabase(item);
                Toast.makeText(context, "Post successfully!", Toast.LENGTH_SHORT).show();
                MainActivity.refreshItems();
            }

            @Override
            public void onFailure(@NonNull Call<PostVideoResponse> call,@NonNull Throwable t) {
                Toast.makeText(context, "Post failed!", Toast.LENGTH_SHORT).show();
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
