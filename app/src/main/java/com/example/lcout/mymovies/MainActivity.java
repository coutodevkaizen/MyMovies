package com.example.lcout.mymovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> listMovies;
    private ProgressBar mLoading;
    private int lastOrderOption = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMovies = findViewById(R.id.rv_movies);
        mLoading = findViewById(R.id.pb_loading_indicator);

        if(!isOnline()){
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            return;
        }

        if(listMovies == null || listMovies.isEmpty()){
            showLoading();
            new GetMovies(this, GetMovies.POPULAR).execute();
        }else{
            prepareRecyclerView();
            showRecyclerView();
        }
    }

    //TODO: best to move it for a util class
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
        if(!isOnline()){
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            return true;
        }
        if (itemThatWasClickedId == R.id.order_popular) {
            if(lastOrderOption == GetMovies.POPULAR)
                return true;
            lastOrderOption = GetMovies.POPULAR;
            showLoading();
            new GetMovies(this, GetMovies.POPULAR).execute();
            return true;
        }else if (itemThatWasClickedId == R.id.order_rating) {
            if(lastOrderOption == GetMovies.RATING)
                return true;
            lastOrderOption = GetMovies.RATING;
            showLoading();
            new GetMovies(this, GetMovies.RATING).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvMovies.setLayoutManager(layoutManager);
        rvMovies.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(listMovies, this, this);
        rvMovies.setAdapter(movieAdapter);
    }

    private void showRecyclerView() {
        mLoading.setVisibility(View.INVISIBLE);
        rvMovies.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
        rvMovies.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClick(int clickedIndex) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(listMovies.get(clickedIndex));
        Log.d(TAG + " Luan ", jsonString);

        Intent details = new Intent(this, DetailsActivity.class);
        details.putExtra(Intent.EXTRA_TEXT, jsonString);
        startActivity(details);

    }

    public class GetMovies extends AsyncTask<Void, Void, String> {

        final String TAG = GetMovies.class.getSimpleName();
        final String baseURL = "http://api.themoviedb.org/3";
        final String popularURL = "/movie/popular";
        final String ratingURL = "/movie/top_rated";
        final String parameterKey = "api_key";
        private Context mContext;
        private int mOrder;
        public static final int POPULAR = 0x00000101;
        public static final int RATING = 0x00000102;

        public GetMovies(Context context, int order){
            mOrder = order;
            mContext = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            URL mURL = buildURL();
            String mResponse = null;
            try {
                mResponse = getResponseFromHttpUrl(mURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG + " - LUAN", "URL: " + mResponse);
            return mResponse;
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null && !json.equals("")) {
                try {
                    JSONObject base = new JSONObject(json);
                    JSONArray moviesJSON = base.getJSONArray("results");
                    ArrayList<Movie> tempMovies = new ArrayList<Movie>();

                    for (int i = 0; i< moviesJSON.length(); i++) {
                        JSONObject movie = moviesJSON.getJSONObject(i);

                        Movie temp = getMovieFromJSON(movie);
                        tempMovies.add(temp);

                        Log.d(TAG + " - LUAN", "movie: " + temp.toString());

                    }

                    listMovies = new ArrayList<Movie>();
                    listMovies.addAll(tempMovies);

                    prepareRecyclerView();
                    showRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @NonNull
        private Movie getMovieFromJSON(JSONObject movie) throws JSONException {
            Long id = movie.getLong("id");
            String title = movie.getString("title");
            String poster_path = movie.getString("poster_path");
            String overview = movie.getString("overview");
            Long vote_average = movie.getLong("vote_average");
            String release_date = movie.getString("release_date");

            Movie temp = new Movie();
            temp.id = id;
            temp.title = title;
            temp.poster_path = poster_path;
            temp.overview = overview;
            temp.vote_average = vote_average;
            temp.release_date = release_date;
            return temp;
        }

        private URL buildURL() {
            String movieApiKey = mContext.getResources().getString(R.string.movie_key);
            Uri builtUri =
                    Uri.parse(baseURL + (mOrder == POPULAR ? popularURL : ratingURL))
                            .buildUpon()
                            .appendQueryParameter(parameterKey, movieApiKey)
                            .build();

            URL url = null;
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d(TAG + " - LUAN", "URL: " + url);
            return url;
        }

        public String getResponseFromHttpUrl(URL url) throws IOException {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
            } finally {
                urlConnection.disconnect();
            }
        }
    }
}
