package com.example.andrey.funnygame;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;


public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int TYPE_FIRST_ITEM = 0;
    public static final int TYPE_ITEM = 1;

    List<VideoMyClass> videoMyClass;

    RVAdapter(List<VideoMyClass> videoMyClass){
        this.videoMyClass = videoMyClass;
    }
    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView videoTitle;
        ImageView previewImage;

        VideoViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            videoTitle = (TextView)itemView.findViewById(R.id.video_title);
            previewImage = (ImageView)itemView.findViewById(R.id.video_preview);
        }
    }

    public static class BigViewHolder extends RecyclerView.ViewHolder {

        public static CardView cv;
        public static Spinner tags;
        public static Spinner duration;
        public static Button gen;
        public static EditText myTag;

        BigViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);
            tags = (Spinner) itemView.findViewById(R.id.tags);
            duration = (Spinner) itemView.findViewById(R.id.duration);
            gen = (Button) itemView.findViewById(R.id.gen);
            myTag = (EditText) itemView.findViewById(R.id.ownTag);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_FIRST_ITEM:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);
                BigViewHolder bvh = new BigViewHolder(v1);
                return bvh;

            case TYPE_ITEM:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                VideoViewHolder vvh = new VideoViewHolder(v2);
                return vvh;
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_FIRST_ITEM:
                BigViewHolder bigViewHolder = (BigViewHolder) holder;
                // Do what you need for the first item
                break;
            case TYPE_ITEM:
                ((VideoViewHolder)holder).videoTitle.setText(videoMyClass.get(position).title);
                ((VideoViewHolder)holder).previewImage.setImageBitmap(videoMyClass.get(position).preview);
                break;
        }


    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_FIRST_ITEM;
        } else return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return VideoMyClass.counter;
    }
}
