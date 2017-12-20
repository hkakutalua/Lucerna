package com.bitpapr.lucerna.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bitpapr.lucerna.R;
import com.bitpapr.lucerna.activities.MovieDetailsActivity;
import com.bitpapr.lucerna.adapters.MovieAdapter;
import com.bitpapr.lucerna.models.Movie;
import com.bitpapr.lucerna.utilities.NetworkUtils;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

public class PopularMoviesFragment extends android.support.v4.app.Fragment
    implements LoaderManager.LoaderCallbacks<ArrayList<Object>>,
    MovieAdapter.MovieListItemClickListener {

    private static final String TAG = PopularMoviesFragment.class.getName();

    public static final int GET_MOVIES_IN_THEATER_LOADER = 1;
    public static final String EXTRA_MOVIE = "extra_movie";

    RecyclerView mMoviesRecyclerView;
    ProgressBar mProgressBar;
    MovieAdapter mMovieAdapter;
    NetworkUtils mNetworkUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        mMoviesRecyclerView = inflatedView.findViewById(R.id.rv_movies);
        mProgressBar = inflatedView.findViewById(R.id.pb_loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);

        mMovieAdapter = new MovieAdapter(this, getContext());
        mMoviesRecyclerView.setAdapter(mMovieAdapter);
        mMoviesRecyclerView.setLayoutManager(layoutManager);

        mNetworkUtils = new NetworkUtils(getContext());

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(GET_MOVIES_IN_THEATER_LOADER, null, this);

        return inflatedView;
    }

    @Override
    public Loader<ArrayList<Object>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Object>>(getContext()) {
            ArrayList<Object> mCachedData = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (mCachedData == null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                } else {
                    deliverResult(mCachedData);
                }
            }

            @Override
            public ArrayList<Object> loadInBackground() {
                try {
                    return fetchAndCategorizeTopMovies();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    return null;
                }
            }

            @Override
            public void deliverResult(ArrayList<Object> data) {
                super.deliverResult(data);
                mCachedData = data;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Object>> loader, ArrayList<Object> data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieAdapter.setAdapterData(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Object>> loader) {
        // NOT IMPLEMENTED
    }

    @Override
    public void onMovieListItemClick(int position) {

        ArrayList<Object> adapterData = mMovieAdapter.getAdapterData();

        Movie movie = (Movie) adapterData.get(position);
        if (movie != null) {
            Intent detailsIntent = new Intent(getContext(), MovieDetailsActivity.class);
            detailsIntent.putExtra(EXTRA_MOVIE, Parcels.wrap(movie));
            startActivity(detailsIntent);
        }
    }

    private ArrayList<Object> fetchAndCategorizeTopMovies() throws IOException {

        ArrayList<Movie> actionMovies =
                mNetworkUtils.getTopMoviesByGenre(Movie.Genre.Action, getContext());
        ArrayList<Movie> dramaMovies =
                mNetworkUtils.getTopMoviesByGenre(Movie.Genre.Drama, getContext());
        ArrayList<Movie> horrorMovies =
                mNetworkUtils.getTopMoviesByGenre(Movie.Genre.Horror, getContext());
        ArrayList<Movie> sciFiMovies =
                mNetworkUtils.getTopMoviesByGenre(Movie.Genre.ScienceFiction, getContext());

        ArrayList<Object> categorizedList = new ArrayList<Object>();

        categorizedList.add(getString(R.string.action_movies_category));
        categorizedList.addAll(actionMovies.subList(0, 4));

        categorizedList.add(getString(R.string.drama_movies_category));
        categorizedList.addAll(dramaMovies.subList(0, 4));

        categorizedList.add(getString(R.string.horror_movies_category));
        categorizedList.addAll(horrorMovies.subList(0, 4));

        categorizedList.add(getString(R.string.science_fiction_movies_category));
        categorizedList.addAll(sciFiMovies.subList(0, 4));

        return categorizedList;
    }

}
