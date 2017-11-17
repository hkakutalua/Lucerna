package com.bitpapr.lucerna.utilities;

import com.bitpapr.lucerna.models.Movie;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by henrick on 13-11-2017.
 */

public class JsonParseUtils {

    private static final String ID_FIELD = "id";
    private static final String TITLE_FIELD = "title";
    private static final String RELEASE_DATE_FIELD = "release_date";
    private static final String OVERVIEW_FIELD = "overview";
    private static final String POSTER_PATH_FIELD = "poster_path";
    private static final String GENRE_IDS_FIELD = "genre_ids";


    /*
        This function converts a Json based string containing movies data
        and parses each movie

     */
    public static ArrayList<Movie> parseMoviesFromJson(String jsonString) throws JSONException {

        JSONObject rootObject = new JSONObject(jsonString);
        JSONArray resultsArray = rootObject.getJSONArray("results");
        ArrayList<Movie> parsedMovies = new ArrayList<Movie>();

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject currentMovieObject = resultsArray.getJSONObject(i);
            Movie movie = parseMovieData(currentMovieObject);
            parsedMovies.add(movie);
        }

        return parsedMovies;
    }

    private static Movie parseMovieData(JSONObject movieObject) throws JSONException {

        int id = movieObject.getInt(ID_FIELD);
        String title = movieObject.getString(TITLE_FIELD);
        String releaseDateString = movieObject.getString(RELEASE_DATE_FIELD);
        String overview = movieObject.getString(OVERVIEW_FIELD);
        String posterUrl = movieObject.getString(POSTER_PATH_FIELD);

        LocalDate releaseDate = LocalDate.parse(releaseDateString);
        ODMGenreConverter genreConverter = new ODMGenreConverter();

        JSONArray genresArray = movieObject.getJSONArray(GENRE_IDS_FIELD);
        int[] odmApiGenres = parseOdmGenresFromJsonArray(genresArray);

        ArrayList<Movie.Genre> convertedGenres =
                genreConverter.convertToModelGenre(odmApiGenres);

        return new Movie(id, title, overview, posterUrl, releaseDate, convertedGenres);
    }

    private static int[] parseOdmGenresFromJsonArray(JSONArray array)
        throws JSONException {

        int[] genresArray = new int[array.length()];
        for (int j = 0; j < array.length(); j++) {
            genresArray[j] = array.getInt(j);
        }

        return genresArray;
    }
}
