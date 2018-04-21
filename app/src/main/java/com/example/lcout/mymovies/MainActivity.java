package com.example.lcout.mymovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lcout.mymovies.data.FavouriteContract;
import com.example.lcout.mymovies.model.Movie;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private final String TAG = MainActivity.class.getSimpleName();
    private static final int POPULAR_LOADER_ID = 0;
    private static final int RATING_LOADER_ID = 1;
    private static final int FAVORITE_LOADER_ID = 2;
    private static int lastOrderOption = 0;


    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;
    private ProgressBar mLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMovies = findViewById(R.id.rv_movies);
        rvMovies.setHasFixedSize(true);
        mLoading = findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvMovies.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(this, this);
        rvMovies.setAdapter(movieAdapter);

        if ((lastOrderOption == POPULAR_LOADER_ID || lastOrderOption == RATING_LOADER_ID) && !isOnline())
            Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        else
            getSupportLoaderManager().initLoader(lastOrderOption, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ((lastOrderOption == POPULAR_LOADER_ID || lastOrderOption == RATING_LOADER_ID) && !isOnline())
            Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        else
            getSupportLoaderManager().restartLoader(lastOrderOption, null, this);
    }

    //TODO: best to move it to an util class
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if ((itemThatWasClickedId == R.id.order_popular || itemThatWasClickedId == R.id.order_rating) && !isOnline()){
            Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return true;
        }

        if (itemThatWasClickedId == R.id.order_popular) {
            if (lastOrderOption == POPULAR_LOADER_ID)
                return true;
            lastOrderOption = POPULAR_LOADER_ID;
            showLoading();
            getSupportLoaderManager().restartLoader(POPULAR_LOADER_ID, null, this);
            return true;
        } else if (itemThatWasClickedId == R.id.order_rating) {
            if (lastOrderOption == RATING_LOADER_ID)
                return true;
            lastOrderOption = RATING_LOADER_ID;
            showLoading();
            getSupportLoaderManager().restartLoader(RATING_LOADER_ID, null, this);
            return true;
        } else if (itemThatWasClickedId == R.id.order_favourite) {
            if (lastOrderOption == FAVORITE_LOADER_ID)
                return true;
            lastOrderOption = FAVORITE_LOADER_ID;
            showLoading();
            getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRecyclerView() {
        mLoading.setVisibility(View.INVISIBLE);
        rvMovies.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
        rvMovies.setVisibility(View.INVISIBLE);
    }

    private void startDetailsActivity(Movie movie) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(movie);
        Log.d(TAG, jsonString);

        Intent details = new Intent(this, DetailsActivity.class);
        details.putExtra(Intent.EXTRA_TEXT, jsonString);
        startActivity(details);
    }

    @Override
    public void onCursorItemClick(int clickedPosition) {
        Movie movie = movieAdapter.mMovies.get(clickedPosition);
        startDetailsActivity(movie);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(final int loaderID, Bundle args) {
        lastOrderOption = loaderID;
        return new AsyncTaskLoader<List<Movie>>(this) {
            List<Movie> mMovieData = null;

            @Override
            public List<Movie> loadInBackground() {
                URL url;
                String response;
                JSONArray moviesJSON;
                switch (loaderID) {
                    case POPULAR_LOADER_ID:

                        url = buildURL(POPULAR_LOADER_ID);
                        try {
                            response = getResponseFromHttpUrl(url);
                            JSONObject jsonObject = new JSONObject(response);

                            mMovieData = getMoviesFromJson(jsonObject);

                        } catch (Exception e) {
                            mMovieData = null;
                            e.printStackTrace();
                        }
                        break;
                    case RATING_LOADER_ID:
                        url = buildURL(RATING_LOADER_ID);
                        try {
                            response = getResponseFromHttpUrl(url);
                            JSONObject jsonObject = new JSONObject(response);

                            mMovieData = getMoviesFromJson(jsonObject);
                        } catch (Exception e) {
                            mMovieData = null;
                            e.printStackTrace();
                        }
                        break;
                    case FAVORITE_LOADER_ID:
                        try {
                            Cursor c = getContentResolver().query(FavouriteContract.FavouriteEntry.CONTENT_URI,
                                    null,
                                    null,
                                    null,
                                    null);
                            mMovieData = getFavoritesFromCursor(c);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to asynchronously load data from database.");
                            e.printStackTrace();
                            mMovieData = null;
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("Invalid loaderID : " + loaderID);
                }
                return mMovieData;
            }

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            public void deliverResult(List<Movie> data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        movieAdapter.swapCursor(data);
        showRecyclerView();
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        movieAdapter.swapCursor(null);
    }


    private List<Movie> getFavoritesFromCursor(Cursor cursor) {
        List<Movie> tempFavoriteMovies = new ArrayList<Movie>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Movie m = new Movie();
            m.overview = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_OVERVIEW));
            m.release_date = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_RELEASE_DATE));
            m.title = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_TITLE));
            m.poster_path = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_POSTER_PATH));
            m.id = cursor.getLong(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_MOVIE_ID));
            m.vote_average = cursor.getLong(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_VOTE_AVERAGE));
            tempFavoriteMovies.add(m);
            cursor.moveToNext();
        }
        return tempFavoriteMovies;
    }

    private URL buildURL(int filter) {
        String movieApiKey = getResources().getString(R.string.movie_key);
        String baseURL = "http://api.themoviedb.org/3";
        String popularURL = "/movie/popular";
        String ratingURL = "/movie/top_rated";
        String parameterKey = "api_key";
        Uri builtUri =
                Uri.parse(baseURL + (filter == POPULAR_LOADER_ID ? popularURL : ratingURL))
                        .buildUpon()
                        .appendQueryParameter(parameterKey, movieApiKey)
                        .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG + " - buildURL", "URL: " + url);
        return url;
    }

    private String getResponseFromHttpUrl(URL url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    @NonNull
    private ArrayList<Movie> getMoviesFromJson(JSONObject json) throws JSONException {

        JSONArray moviesJson = json.getJSONArray("results");
        ArrayList<Movie> tempMovies = new ArrayList<Movie>();

        for (int i = 0; i < moviesJson.length(); i++) {
            JSONObject jsonMovie = moviesJson.getJSONObject(i);
            Long id = jsonMovie.getLong("id");
            String title = jsonMovie.getString("title");
            String poster_path = jsonMovie.getString("poster_path");
            String overview = jsonMovie.getString("overview");
            Long vote_average = jsonMovie.getLong("vote_average");
            String release_date = jsonMovie.getString("release_date");

            Movie temp = new Movie();
            temp.id = id;
            temp.title = title;
            temp.poster_path = poster_path;
            temp.overview = overview;
            temp.vote_average = vote_average;
            temp.release_date = release_date;
            tempMovies.add(temp);
        }
        return tempMovies;
    }
}