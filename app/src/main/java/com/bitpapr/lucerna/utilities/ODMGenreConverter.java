package com.bitpapr.lucerna.utilities;

import com.bitpapr.lucerna.models.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by henrick on 17-11-2017.
 */

public class ODMGenreConverter {

    /*  These constants contain the movie genres ids defined in the Open Db Movie
        API, we'll use them to convert to our model. Note that not every genre
        in ODM API are here (i ignored some of them)
     */
    private static final int ACTION_GENRE_ID = 28;
    private static final int ADVENTURE_GENRE_ID = 12;
    private static final int ANIMATION_GENRE_ID = 16;
    private static final int COMEDY_GENRE_ID = 35;
    private static final int CRIME_GENRE_ID = 80;
    private static final int DOCUMENTARY_GENRE_ID = 99;
    private static final int DRAMA_GENRE_ID = 18;
    private static final int FAMILY_GENRE_ID = 10751;
    private static final int HORROR_GENRE_ID = 27;
    private static final int MUSIC_GENRE_ID = 10402;
    private static final int ROMANCE_GENRE_ID = 10749;
    private static final int SCIENCE_FICTION_GENRE_ID = 878;
    private static final int THRILLER_GENRE_ID = 53;
    private static final int WAR_GENRE_ID = 10752;

    private static final Map<Integer, Movie.Genre> OdmApiGenresDictionary =
            createOdmApiGenresDictionary();

    private static Map<Integer, Movie.Genre> createOdmApiGenresDictionary() {
        Map<Integer, Movie.Genre> map = new HashMap<Integer, Movie.Genre>();

        map.put(ACTION_GENRE_ID, Movie.Genre.Action);
        map.put(ADVENTURE_GENRE_ID, Movie.Genre.Adventure);
        map.put(ANIMATION_GENRE_ID, Movie.Genre.Animation);
        map.put(COMEDY_GENRE_ID, Movie.Genre.Comedy);
        map.put(CRIME_GENRE_ID, Movie.Genre.Crime);
        map.put(DOCUMENTARY_GENRE_ID, Movie.Genre.Documentary);
        map.put(DRAMA_GENRE_ID, Movie.Genre.Drama);
        map.put(FAMILY_GENRE_ID, Movie.Genre.Family);
        map.put(HORROR_GENRE_ID, Movie.Genre.Horror);
        map.put(MUSIC_GENRE_ID, Movie.Genre.Music);
        map.put(ROMANCE_GENRE_ID, Movie.Genre.Romance);
        map.put(SCIENCE_FICTION_GENRE_ID, Movie.Genre.ScienceFiction);
        map.put(THRILLER_GENRE_ID, Movie.Genre.Thriller);
        map.put(WAR_GENRE_ID, Movie.Genre.War);

        return map;
    }

    /*
        This method convert an array of integer based genres got from Open Database Movies API
        to an ArrayList containing genres defined by our model enum Movie.Genre
        Note: some genres in ODM API are ignored here, so expect this method sometimes
              return an arraylist with less length than the original (passed by argument)
    */
    public ArrayList<Movie.Genre> convertToModelGenre(int[] openDbMoviesGenres) {

        ArrayList<Movie.Genre> genres = new ArrayList<>();

        for (int i = 0; i < openDbMoviesGenres.length; i++) {
            final int key = openDbMoviesGenres[i];
            if (OdmApiGenresDictionary.containsKey(key)) {
                genres.add(OdmApiGenresDictionary.get(key));
            }
        }

        if (genres.isEmpty()) {
            return null;
        } else {
            return genres;
        }
    }

    public int convertToOdmGenre(Movie.Genre genre) {

        Set<Map.Entry<Integer, Movie.Genre>> keyValuePairs = OdmApiGenresDictionary.entrySet();
        for (Map.Entry<Integer, Movie.Genre> entry : keyValuePairs) {
            if (entry.getValue() == genre) {
                return entry.getKey();
            }
        }

        throw new UnsupportedOperationException("Genre " + genre.toString() + "not found");
    }
}
