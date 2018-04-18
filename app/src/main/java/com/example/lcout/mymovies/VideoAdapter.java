package com.example.lcout.mymovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lcout on 18/04/2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<com.example.lcout.mymovies.VideoAdapter.VideoViewHolder> {

    private Context mContext;
    private ArrayList<Video> mListVideos;
    private VideoAdapter.ListItemClickListener mOnClickListener;

    interface ListItemClickListener {
        void onListItemClick(int clickedIndex);
    }

    public VideoAdapter(ArrayList<Video> listVideos, VideoAdapter.ListItemClickListener clickListener, Context context) {
        mContext = context;
        mListVideos = listVideos;
        mOnClickListener = clickListener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdListItem = R.layout.video_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdListItem, parent, shouldAttachToParentImmediately);
        VideoAdapter.VideoViewHolder viewHolder = new VideoAdapter.VideoViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mListVideos.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton ibPlay;
        TextView tvName;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ibPlay = itemView.findViewById(R.id.ib_play);
            ibPlay.setOnClickListener(this);
            tvName = itemView.findViewById(R.id.tv_name);
        }

        void bind(int position) {
            tvName.setText(mListVideos.get(position).name);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }

    }
}

