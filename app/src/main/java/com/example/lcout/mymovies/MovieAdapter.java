package com.example.lcout.mymovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ListItemClickListener mOnClickListener;
    private int mNumberItems;
    ArrayList<Movie> mListMovies;
    Context mContext;
    final String baseURL = "http://image.tmdb.org/t/p/";
    final String imgSize = "w185/";

    interface ListItemClickListener {
        void onListItemClick(int clickedIndex);

    }

    public MovieAdapter(ArrayList<Movie> listMovies, ListItemClickListener clickListener, Context context) {
        mContext = context;
        mNumberItems = listMovies.size();
        mListMovies = listMovies;
        mOnClickListener = clickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdListItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mListMovies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;

        public MovieViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_image);
            image.setOnClickListener(this);
        }

        void bind(int position) {
            String fullImageURL = baseURL + imgSize + mListMovies.get(position).poster_path;
            Picasso.with(mContext).load(fullImageURL).into(image);
            Log.d(TAG, mListMovies.get(position).poster_path);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }

    }
}
