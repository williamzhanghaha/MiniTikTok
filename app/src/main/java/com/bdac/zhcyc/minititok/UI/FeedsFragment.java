package com.bdac.zhcyc.minititok.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bdac.zhcyc.minititok.Network.beans.Feed;
import com.bdac.zhcyc.minititok.R;
import com.bdac.zhcyc.minititok.Utilities.NetworkUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class FeedsFragment extends Fragment implements FeedsAdapter.FeedListItemClickListener, FeedsAdapter.FeedListRefreshedListener {
    public static final String TAG = "FeedsFragment";

    private RecyclerView recyclerView;
    private FeedsAdapter adapter;
    private SmoothScrollLayoutManager layoutManager;

    private Toast toast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        recyclerView = view.findViewById(R.id.rv_feeds);
        adapter = new FeedsAdapter();
        layoutManager = new SmoothScrollLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FeedsAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.header_feeds, recyclerView, false));
        adapter.setFooterView(LayoutInflater.from(getContext()).inflate(R.layout.footer_feeds, recyclerView, false));

        adapter.setFeedListItemClickListener(this);
        adapter.setFeedListRefreshedListener(this);
        NetworkUtils.fetchFeed(recyclerView);

        recyclerView.setItemAnimator(new DefaultItemAnimator());


        return view;
    }

    public void refreshFeeds () {
        Log.d(TAG, "refreshFeeds: In");
        NetworkUtils.fetchFeed(recyclerView);
        if (toast != null) toast.cancel();
        toast = Toast.makeText(getContext(), getString(R.string.processing_refreshing), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onFeedListItemClicked(int clickedItemIndex, Feed feed) {
        Log.d(TAG, "onFeedListItemClicked: Index: " + clickedItemIndex + " Feed:" + feed.getVideo_url());
        //TODO 点击feed
    }

    @Override
    public void onFeedListItemRefreshed() {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(getContext(), getString(R.string.processing_refreshed), Toast.LENGTH_SHORT);
        toast.show();
        recyclerView.smoothScrollToPosition(0);
    }
}
