package com.bitpapr.lucerna.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.bitpapr.lucerna.R;
import com.bitpapr.lucerna.models.Movie;

import org.joda.time.LocalDate;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by henrick on 13-11-2017.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URI = "https://api.themoviedb.org/3";
    public static final Uri POSTER_BASE_THUMBNAIL_URI = Uri.parse("http://image.tmdb.org/t/p/w185");
    public static final Uri POSTER_BASE_URI = Uri.parse("http://image.tmdb.org/t/p/w500");

    // Query Parameters
    private static final String API_KEY_QUERY = "api_key";
    private static final String SORT_BY_QUERY = "sort_by";
    private static final String LANGUAGE_QUERY = "language";
    private static final String GENRE_QUERY = "with_genres";

    // GTE = Greater or equal than
    // LTE = Less or equal than
    private static final String PRIMARY_RELEASE_DATE_GTE = "primary_release_date.gte";
    private static final String PRIMARY_RELEASE_DATE_LTE = "primary_release_date.lte";

    // Query Values
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String LANGUAGE_VALUE = "pt-PT";
    public static final int GENRE_ACTION = 28;

    // API Endpoints
    private static final Uri DISCOVER_URI = Uri.parse(BASE_URI + "/discover/movie")
            .buildUpon()
            .appendQueryParameter(SORT_BY_QUERY, SORT_BY_POPULARITY)
            .appendQueryParameter(LANGUAGE_QUERY, LANGUAGE_VALUE)
            .build();

    Context mContext;
    ODMGenreConverter mOdmGenreConverter;

    public NetworkUtils(Context context) {
        mContext = context;
        mOdmGenreConverter = new ODMGenreConverter();
    }

    public ArrayList<Movie> getTopMoviesByGenre(Movie.Genre genre, Context context)
        throws IOException {

        String apiKey = context.getString(R.string.the_movie_db_api_key);

        // The request is made for movies with release dates between
        // the previous month and the current month
        LocalDate previousMonthFirstDate = LocalDate.now()
            .minusMonths(1)
            .withDayOfMonth(1);
        LocalDate currentMothLastDate = LocalDate.now()
            .dayOfMonth()
            .withMaximumValue();

        int odmMovieGenre = mOdmGenreConverter.convertToOdmGenre(genre);

        Uri requestUri = DISCOVER_URI.buildUpon()
            .appendQueryParameter(API_KEY_QUERY, apiKey)
            .appendQueryParameter(GENRE_QUERY, String.valueOf(odmMovieGenre))
            .appendQueryParameter(PRIMARY_RELEASE_DATE_GTE, previousMonthFirstDate.toString())
            .appendQueryParameter(PRIMARY_RELEASE_DATE_LTE, currentMothLastDate.toString())
            .build();

        URL url = new URL(requestUri.toString());
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                String data = scanner.next();
                return JsonParseUtils.parseMoviesFromJson(data);
            } else {
                return null;
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            Log.e(TAG, jsonException.getMessage());
        } finally {
            connection.disconnect();
        }

        return null;
    }
}
