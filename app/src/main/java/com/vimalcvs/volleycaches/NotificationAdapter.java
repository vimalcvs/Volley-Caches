package com.vimalcvs.volleycaches;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    private final Context context;
    private final List<NotificationModel> list;

    public NotificationAdapter(Context context, List<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_notification, parent, false);
        return new NotificationHolder(listItem);
    }

    @Override
    public void onBindViewHolder(NotificationHolder holder, int position) {
        NotificationModel notificationModel = list.get(position);

        holder.title.setText(notificationModel.getTitle());
        holder.body.setText(notificationModel.getBody());

        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getImage())
                .placeholder(R.drawable.ic_placeholder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.img_poster);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder {

        public TextView title,body;
        public ImageView img_poster;
        public ProgressBar progress;

        public NotificationHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            progress = itemView.findViewById(R.id.progress);
            img_poster = itemView.findViewById(R.id.img_poster);
        }
    }
}
