package com.bitpapr.lucerna.utilities;

import android.content.Context;

import com.bitpapr.lucerna.R;
import com.bitpapr.lucerna.models.Movie;

/**
 * Created by henrick on 17-11-2017.
 */

public class GenreToStringConverter {

    private final Context mContext;

    public GenreToStringConverter(Context context) {
        mContext = context;
    }

    public String convertToString(Movie.Genre genre) {

        switch (genre) {
            case Action:
                return mContext.getString(R.string.action_genre_text);
            case Adventure:
                return mContext.getString(R.string.adventure_genre_text);
            case Animation:
                return mContext.getString(R.string.animation_genre_text);
            case Comedy:
                return mContext.getString(R.string.comedy_genre_text);
            case Crime:
                return mContext.getString(R.string.crime_genre_text);
            case Documentary:
                return mContext.getString(R.string.documentary_genre_text);
            case Drama:
                return mContext.getString(R.string.drama_genre_text);
            case Family:
                return mContext.getString(R.string.family_genre_text);
            case Horror:
                return mContext.getString(R.string.horror_genre_text);
            case Music:
                return mContext.getString(R.string.music_genre_text);
            case Romance:
                return mContext.getString(R.string.romance_genre_text);
            case ScienceFiction:
                return mContext.getString(R.string.science_fiction_genre_text);
            case Thriller:
                return mContext.getString(R.string.thriller_genre_text);
            case War:
                return mContext.getString(R.string.war_genre_text);
            default:
                throw new UnsupportedOperationException("Invalid case");
        }
    }
}
