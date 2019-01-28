package com.bdac.zhcyc.minititok.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bdac.zhcyc.minititok.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MePlayerActivity extends AppCompatActivity {

    private FeedsListVideoPlayer videoPlayer;
    public static final String VIEW_NAME_MAIN = "player:name:main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_player);
        videoPlayer = findViewById(R.id.me_video_player);
        videoPlayer.setThumbPlay(true);
        videoPlayer.setLooping(true);


        ViewCompat.setTransitionName(videoPlayer, VIEW_NAME_MAIN);

        Intent intent = getIntent();
        Bundle bundle;
        String imageUrl = "";
        String videoUrl = "";

        if (intent != null) {
            bundle = intent.getExtras();
            if (bundle != null) {
                imageUrl = bundle.getString("imageUrl");
                videoUrl = bundle.getString("videoUrl");
            }
        }

        ImageView imageView = new ImageView(this);
        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(new RequestOptions().centerCrop().error(R.drawable.pic_nothing).placeholder(R.drawable.pic_nothing))
                .load(imageUrl).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        videoPlayer.setUp(videoUrl, true, "");
        videoPlayer.setThumbImageView(imageView);

        videoPlayer.startPlayLogic();
    }
}
