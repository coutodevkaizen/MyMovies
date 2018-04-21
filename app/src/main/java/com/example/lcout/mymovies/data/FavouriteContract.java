package com.example.lcout.mymovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lcout on 19/03/2018.
 */

public class FavouriteContract {

    public static final String AUTHORITY = "com.example.lcout.mymovies";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVOURITE_MOVIES = "favourite_movies";

    public static final class FavouriteEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE_MOVIES).build();

        public static final String TABLE_NAME = "favourite_movies";
        public static final String COLUMN_TITLE = "title";
        //Movie_ID on imdb database
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";

    }


}
