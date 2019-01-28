package com.bdac.zhcyc.minititok;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.VideoView;

import com.bdac.zhcyc.minititok.Utilities.DatabaseUtils;
import com.bdac.zhcyc.minititok.Utilities.NetworkUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Sebb,
 * @date 2019/1/28
 */

public class PostingActivity extends AppCompatActivity {

    private VideoView videoView;
    private EditText editText;
    private FloatingActionButton btnSend;

    public void setPostSuccess(boolean postSuccess) {
        isPostSuccess = postSuccess;
    }

    private boolean isPostSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posting);

        videoView = findViewById(R.id.video_view);
        editText = findViewById(R.id.editview);
        btnSend = findViewById(R.id.btn_send);

        Bundle bundle = getIntent().getExtras();

        String stringImageUri = bundle.getString("imageUri");
        String stringVideoUri = bundle.getString("videoUri");

        Uri imageUri = Uri.parse(stringImageUri);
        Uri videoUri = Uri.parse(stringVideoUri);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkUtils.postVideo(imageUri,videoUri,PostingActivity.this,null);
            }
        });

        videoView.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        videoView.setVideoURI(videoUri);
        videoView.start();
    }
}
