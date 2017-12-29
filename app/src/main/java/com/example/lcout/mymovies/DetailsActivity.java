package com.example.lcout.mymovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private final String TAG = DetailsActivity.class.getSimpleName();
    private Movie mMovie;
    private final String baseURL = "http://image.tmdb.org/t/p/";
    private final String imgSize = "w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getMovieFromExtra();
        pupulateScreenInfo();
    }

    private void pupulateScreenInfo() {
        TextView title = findViewById(R.id.tv_title);
        ImageView thumbnail = findViewById(R.id.iv_thumbnail);
        TextView overview = findViewById(R.id.tv_overview);
        TextView rating = findViewById(R.id.tv_rating);
        TextView release_date = findViewById(R.id.tv_release);

        title.setText(mMovie.title);
        String fullImageURL = baseURL + imgSize + mMovie.poster_path;
        Picasso.with(this).load(fullImageURL).into(thumbnail);
        overview.setText(mMovie.overview);
        overview.setMovementMethod(new ScrollingMovementMethod());
        rating.setText(mMovie.vote_average.toString());
        release_date.setText(mMovie.release_date);
    }

    private void getMovieFromExtra() {
        Intent intent = getIntent();
        if(!intent.hasExtra(Intent.EXTRA_TEXT))
            finish();

        String extra = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (TextUtils.isEmpty(extra))
            finish();

        Log.d(TAG + " LUAN", extra);
        Gson gson = new Gson();
        mMovie = gson.fromJson(extra, Movie.class);
    }
}
