package com.example.lcout.mymovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lcout.mymovies.model.Review;

import java.util.ArrayList;

/**
 * Created by lcout on 19/04/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private final LayoutInflater inflater;
    private final ArrayList<Review> mListReviews;

    public ReviewAdapter(ArrayList<Review> listReviews, Context context) {
        inflater = LayoutInflater.from(context);
        mListReviews = listReviews;
    }


    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reviewView = inflater.inflate(R.layout.review_item,parent,false);
        return new ReviewViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return mListReviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{
        private final TextView author;
        private final TextView review;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.tv_author_name);
            review = itemView.findViewById(R.id.tv_review);
        }

        public void bind(int position) {
            author.setText(mListReviews.get(position).author);
            review.setText(mListReviews.get(position).review);
        }
    }
}
