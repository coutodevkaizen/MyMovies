package com.example.lcout.mymovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lcout.mymovies.data.FavouriteContract;
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavouriteMovie();
            }
        });
    }

    private void addFavouriteMovie() {
        if (mMovie == null || mMovie.id < 1)
            return;

        ContentValues contentValues = new ContentValues();

        contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_MOVIE_ID, mMovie.id);
        contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_TITLE, mMovie.title);

        Uri uri = getContentResolver().insert(FavouriteContract.FavouriteEntry.CONTENT_URI, contentValues);

        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }


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
