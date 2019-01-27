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

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;


    private List<Feed> feeds = new ArrayList<>();
    private FeedListItemClickListener feedListItemClickListener;
    private View mHeaderView;
    private View mFooterView;

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

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyDataSetChanged();
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getFooterView () {
        return mFooterView;
    }

    @Override
    public int getItemViewType(int position) {
//        if(mHeaderView == null) return TYPE_NORMAL;
//        if(position == 0) return TYPE_HEADER;
//        return TYPE_NORMAL;

        int dataSize = feeds.size();
        if (mHeaderView == null) {
            if (position >= 0 && position < dataSize) {
                return TYPE_NORMAL;
            } else {
                if (mFooterView != null) {
                    return TYPE_FOOTER;
                }
            }
        } else {
            if (position == 0) {
                return TYPE_HEADER;
            } else if (position > 0 && position <= dataSize) {
                return TYPE_NORMAL;
            } else {
                if (mFooterView != null) {
                    return TYPE_FOOTER;
                }
            }
        }
        return TYPE_NORMAL;

    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER) return new FeedViewHolder(mHeaderView);
        if (mFooterView != null && viewType == TYPE_FOOTER) return new FeedViewHolder(mFooterView);
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
        if(getItemViewType(position) == TYPE_HEADER) return;
        if (getItemViewType(position) == TYPE_FOOTER) return;
        final int realPosition = mHeaderView == null ? position : position - 1;;
        Feed feed = feeds.get(realPosition);
        //TODO 更改bind操作
        holder.textView.setText(feed.getVideo_url());
    }

    @Override
    public int getItemCount() {
        int count =  mHeaderView == null ? feeds.size() : feeds.size() + 1;
        count = mFooterView == null ? count : count + 1;
        return count;
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //TODO 添加所有控件
        private TextView textView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;
            if (itemView == mFooterView) return;
            textView = itemView.findViewById(R.id.feed_url_view);
            itemView.setOnClickListener(this);
        }

        public int getRealPosition(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return mHeaderView == null ? position : position - 1;
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getRealPosition(this);
            if (feedListItemClickListener != null) {
                feedListItemClickListener.onFeedListItemClicked(clickedPosition, feeds.get(clickedPosition));
            }
        }
    }

    public interface FeedListItemClickListener {
        void onFeedListItemClicked(int clickedItemIndex, Feed feed);
    }
}
