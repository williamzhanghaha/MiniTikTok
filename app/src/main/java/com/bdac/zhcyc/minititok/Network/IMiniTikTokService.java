package com.bdac.zhcyc.minititok.Network;

import com.bdac.zhcyc.minititok.Network.beans.FeedResponse;
import com.bdac.zhcyc.minititok.Network.beans.PostVideoResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * @author Sebb,
 * @date 2019/1/25
 */

public interface IMiniTikTokService {
//POST    url: http://10.108.10.39:8080/minidouyin/video
//GET     url: http://10.108.10.39:8080/minidouyin/feed


    @Multipart

    @POST("minidouyin/video")
    Call<PostVideoResponse> creatVideo(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Part MultipartBody.Part img,
            @Part MultipartBody.Part video
            );

    @GET("minidouyin/feed")
    Call<FeedResponse> feedResponse();


}
