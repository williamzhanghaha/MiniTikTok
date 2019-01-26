package com.bdac.zhcyc.minititok;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bdac.zhcyc.minititok.Utilities.DatabaseUtils;
import com.bdac.zhcyc.minititok.Network.beans.Item;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Seb";
    private static List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseUtils.dbInit(this);
    }

    @Override
    protected void onDestroy(){
        DatabaseUtils.dbDestory();
        super.onDestroy();
    }
}
