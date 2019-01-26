package com.bdac.zhcyc.minititok;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;

import com.bdac.zhcyc.minititok.Network.NetworkUtils;
import com.bdac.zhcyc.minititok.Network.beans.Feed;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Seb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
