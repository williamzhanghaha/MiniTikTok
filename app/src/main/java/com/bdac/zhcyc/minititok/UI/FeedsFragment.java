package com.bdac.zhcyc.minititok.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bdac.zhcyc.minititok.Network.beans.Feed;
import com.bdac.zhcyc.minititok.R;
import com.bdac.zhcyc.minititok.Utilities.NetworkUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FeedsFragment extends Fragment implements FeedsAdapter.FeedListItemClickListener {
    public static final String TAG = "FeedsFragment";

    private RecyclerView recyclerView;
    private FeedsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        recyclerView = view.findViewById(R.id.rv_feeds);
        adapter = new FeedsAdapter();
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FeedsAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.header_feeds, recyclerView, false));
        adapter.setFooterView(LayoutInflater.from(getContext()).inflate(R.layout.footer_feeds, recyclerView, false));

        adapter.setFeedListItemClickListener(this);
        NetworkUtils.fetchFeed(recyclerView);

        return view;
    }

    @Override
    public void onFeedListItemClicked(int clickedItemIndex, Feed feed) {
        Log.d(TAG, "onFeedListItemClicked: Index: " + clickedItemIndex + " Feed:" + feed.getVideo_url());
        //TODO 点击feed
    }
}
