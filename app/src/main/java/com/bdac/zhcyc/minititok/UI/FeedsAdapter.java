package com.bdac.zhcyc.minititok.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bdac.zhcyc.minititok.Network.beans.Feed;
import com.bdac.zhcyc.minititok.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.FeedViewHolder>{

    List<Feed> feeds = new ArrayList<>();
    private FeedListItemClickListener feedListItemClickListener;

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public void setFeedListItemClickListener(FeedListItemClickListener feedListItemClickListener) {
        this.feedListItemClickListener = feedListItemClickListener;
    }

    public void refresh (List<Feed> feeds) {
        if (feeds != null) {
            this.feeds = feeds;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_feeds_feed;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        FeedViewHolder viewHolder = new FeedViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        Feed feed = feeds.get(position);
        //TODO 更改bind操作
        holder.textView.setText(feed.getVideo_url());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //TODO 添加所有控件
        private TextView textView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.feed_url_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            if (feedListItemClickListener != null) {
                feedListItemClickListener.onFeedListItemClicked(clickedPosition);
            }
        }
    }

    public interface FeedListItemClickListener {
        void onFeedListItemClicked(int clickedItemIndex);
    }
}
