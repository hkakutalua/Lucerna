package com.bitpapr.lucerna.models;

import org.joda.time.LocalDate;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;

/**
 * Created by henrick on 14-11-2017.
 */

@Parcel(Parcel.Serialization.BEAN)
public class Movie {

    public enum Genre {
        Action,
        Adventure,
        Animation,
        Comedy,
        Crime,
        Documentary,
        Drama,
        Family,
        Horror,
        Music,
        Romance,
        ScienceFiction,
        Thriller,
        War
    };

    private long mId;
    private String mTitle;
    private String mOverview;
    private String mPosterUrlPath;
    private LocalDate mReleaseDate;
    private ArrayList<Genre> mGenres;

    @ParcelConstructor
    public Movie(
        long id,
        String title,
        String overview,
        String posterUrlPath,
        LocalDate releaseDate,
        ArrayList<Genre> genres) {

        mId = id;
        mTitle = title;
        mOverview = overview;
        mPosterUrlPath = posterUrlPath;
        mReleaseDate = releaseDate;
        mGenres = genres;
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getPosterUrlPath() {
        return mPosterUrlPath;
    }

    public LocalDate getReleaseDate() {
        return mReleaseDate;
    }

    public ArrayList<Genre> getGenres() {
        return mGenres;
    }
}
