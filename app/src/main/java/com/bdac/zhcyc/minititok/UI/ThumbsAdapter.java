package com.bdac.zhcyc.minititok.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bdac.zhcyc.minititok.Network.beans.Item;
import com.bdac.zhcyc.minititok.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ThumbsAdapter extends RecyclerView.Adapter<ThumbsAdapter.ThumbViewHolder> {

    private List<Item> items = new ArrayList<>();
    private OnThumbClickListener onThumbClickListener;

    public void setOnThumbClickListener(OnThumbClickListener onThumbClickListener) {
        this.onThumbClickListener = onThumbClickListener;
    }

    public void setItems(List<Item> items) {
        if (items != null) {
            this.items = items;
        }
        for (int i = 0; i < items.size(); i++) {
            notifyItemChanged(i);
        }
    }

    public List<Item> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ThumbViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_thumb_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        ThumbsAdapter.ThumbViewHolder viewHolder = new ThumbsAdapter.ThumbViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbViewHolder holder, int position) {
        String imageUrl = items.get(position).getImage_url();

        ImageView imageView = holder.imageView;

        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(new RequestOptions().centerCrop().error(R.drawable.pic_nothing).placeholder(R.drawable.pic_nothing))
                .load(imageUrl).into(imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ThumbViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;

        public ThumbViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_thumb);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Item item = items.get(getAdapterPosition());
            Bundle bundle = new Bundle();
            bundle.putString("imageUrl", item.getImage_url());
            bundle.putString("videoUrl", item.getVideo_url());
            //TODO 播放
            onThumbClickListener.onThumbClick(bundle, imageView);
        }
    }

    public interface OnThumbClickListener {
        void onThumbClick(Bundle bundle, View view);
    }
}
