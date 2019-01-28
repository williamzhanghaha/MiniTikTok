package com.bdac.zhcyc.minititok.UI;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bdac.zhcyc.minititok.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.bdac.zhcyc.minititok.Utilities.DatabaseUtils.loadItemsFromDatabase;

public class MeFragment extends Fragment implements ThumbsAdapter.OnThumbClickListener {

    private RecyclerView recyclerView;
    private ThumbsAdapter adapter;
    private GridLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        recyclerView = view.findViewById(R.id.rv_thumbs);
        adapter = new ThumbsAdapter();
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ThumbsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        refreshData();

        swipeRefreshLayout = view.findViewById(R.id.srl_thumbs);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 :
                                recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        adapter.setOnThumbClickListener(this);

        return view;
    }

    public void refreshData () {
        adapter.setItems(loadItemsFromDatabase());
    }

    @Override
    public void onThumbClick(Bundle bundle, View view) {
        //TODO 跳转
        Intent intent = new Intent(getActivity(), MePlayerActivity.class);
        intent.putExtras(bundle);

        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(), view, MePlayerActivity.VIEW_NAME_MAIN);

        ActivityCompat.startActivity(getContext(), intent, activityOptionsCompat.toBundle());
    }
}
