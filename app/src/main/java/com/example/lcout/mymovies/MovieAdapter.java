package com.example.lcout.mymovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lcout.mymovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private final ItemClickListener mOnClickListener;
    public List<Movie> mMovies;
    private final Context mContext;
    private final String baseURL = "http://image.tmdb.org/t/p/";
    private final String imgSize = "w185/";

    interface ItemClickListener {
        void onCursorItemClick(int clickedIndex);

    }

    public MovieAdapter(ItemClickListener clickListener, Context context) {
        mContext = context;
        mOnClickListener = clickListener;
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdListItem, parent, shouldAttachToParentImmediately);

        return new MovieAdapter.MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {
        String posterPath = mMovies.get(position).poster_path;

        String fullImageURL = baseURL + imgSize + posterPath;
        Picasso.with(mContext).load(fullImageURL).into(holder.image);
        Log.d(TAG, fullImageURL);
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        }
        return mMovies.size();
    }

    public List<Movie> swapCursor(List<Movie> newMovies) {
        if (mMovies == newMovies)
            return null;

        List<Movie> temp = mMovies;
        this.mMovies = newMovies;

        if (newMovies != null)
            this.notifyDataSetChanged();

        return temp;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView image;

        public MovieViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.iv_image);
            image.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mOnClickListener.onCursorItemClick(getAdapterPosition());
        }

    }
}
