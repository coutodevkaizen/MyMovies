package com.example.lcout.mymovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lcout.mymovies.data.FavouriteContract;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity implements VideoAdapter.ListItemClickListener{

    private final String TAG = DetailsActivity.class.getSimpleName();
    private Movie mMovie;
    private final String imgBaseURL = "http://image.tmdb.org/t/p/";
    private final String imgSize = "w185/";
    private ArrayList<Video> mVideos;
    private RecyclerView rvVideos;
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getMovieFromExtra();
        pupulateScreenInfo();
        getTrailers();
        getReviews();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavouriteMovie();
            }
        });
    }

    final String baseURL = "http://api.themoviedb.org/3";
    String trailers = "/movie/%s/videos";
    String reviews = "/movie/%s/reviews";
    final String parameterKey = "api_key";

    private void getReviews() {
        final String movieApiKey = getResources().getString(R.string.movie_key);
        Uri builtUri =
                Uri.parse(baseURL + String.format(reviews, mMovie.id.toString()))
                        .buildUpon()
                        .appendQueryParameter(parameterKey, movieApiKey)
                        .build();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, builtUri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //mTextView.setText("Response: " + response.toString());
                        Log.d(TAG + " Volley reviews", new Gson().toJson(response));
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d(TAG + " Error Volley reviews", error.toString());
                    }
                });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void getTrailers() {
        final String movieApiKey = getResources().getString(R.string.movie_key);
        Uri builtUri =
                Uri.parse(baseURL + String.format(trailers, mMovie.id.toString()))
                        .buildUpon()
                        .appendQueryParameter(parameterKey, movieApiKey)
                        .build();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, builtUri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG + " Volley videos", new Gson().toJson(response));
                        mVideos = getVideosFromJson(response);
                        prepareVideosRecyclerView();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG + " Error Volley videos", error.toString());
                        return;
                    }
                });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void prepareVideosRecyclerView() {
        rvVideos = findViewById(R.id.rv_videos);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvVideos.setLayoutManager(layoutManager);
        rvVideos.setHasFixedSize(true);
        videoAdapter = new VideoAdapter(mVideos, this, this);
        rvVideos.setAdapter(videoAdapter);

    }

    private ArrayList<Video> getVideosFromJson(JSONObject response) {
        if (response == null || response.equals(""))
            return null;

        ArrayList<Video> tempVideos = new ArrayList<>();

        try {
            JSONArray videosJSON = response.getJSONArray("results");

            for (int i = 0; i < videosJSON.length(); i++) {
                JSONObject video = videosJSON.getJSONObject(i);

                String name = video.getString("name");
                String key = video.getString("key");
                Video temp = new Video();
                temp.name = name;
                temp.key = key;

                tempVideos.add(temp);

                Log.d(TAG , "name: " + name);
                Log.d(TAG, "key: " + key);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tempVideos;

    }

    private void addFavouriteMovie() {
        if (mMovie == null || mMovie.id < 1)
            return;

        ContentValues contentValues = new ContentValues();

        contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_MOVIE_ID, mMovie.id);
        contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_TITLE, mMovie.title);

        Uri uri = getContentResolver().insert(FavouriteContract.FavouriteEntry.CONTENT_URI, contentValues);

        if (uri != null) {
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
        String fullImageURL = imgBaseURL + imgSize + mMovie.poster_path;
        Picasso.with(this).load(fullImageURL).into(thumbnail);
        overview.setText(mMovie.overview);
        overview.setMovementMethod(new ScrollingMovementMethod());
        rating.setText(mMovie.vote_average.toString());
        release_date.setText(mMovie.release_date);
    }

    private void getMovieFromExtra() {
        Intent intent = getIntent();
        if (!intent.hasExtra(Intent.EXTRA_TEXT))
            finish();

        String extra = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (TextUtils.isEmpty(extra))
            finish();

        Log.d(TAG + " LUAN", extra);
        Gson gson = new Gson();
        mMovie = gson.fromJson(extra, Movie.class);
    }

    @Override
    public void onListItemClick(int clickedIndex) {
        //TODO start new intent to open video
    }
}
