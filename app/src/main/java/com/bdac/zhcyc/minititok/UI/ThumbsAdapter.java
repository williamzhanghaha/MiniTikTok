package com.bdac.zhcyc.minititok.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bdac.zhcyc.minititok.Network.beans.Item;
import com.bdac.zhcyc.minititok.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ThumbsAdapter extends RecyclerView.Adapter<ThumbsAdapter.ThumbViewHolder> {

    private List<Item> items = new ArrayList<>();

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

        Glide.with(imageView.getContext()).load(imageUrl).into(imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ThumbViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ThumbViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_thumb);
        }
    }
}
