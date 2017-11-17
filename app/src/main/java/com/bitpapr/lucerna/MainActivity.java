package com.bitpapr.lucerna;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bitpapr.lucerna.models.Movie;
import com.bitpapr.lucerna.utilities.NetworkUtils;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Object>>,
                   MovieAdapter.MovieListItemClickListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int GET_MOVIES_IN_THEATER_LOADER = 1;
    public static final String EXTRA_MOVIE = "extra_movie";

    RecyclerView mMoviesRecyclerView;
    ProgressBar mProgressBar;
    MovieAdapter mMovieAdapter;
    NetworkUtils mNetworkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNetworkUtils = new NetworkUtils(this);

        mMoviesRecyclerView = (RecyclerView)findViewById(R.id.rv_movies);
        mProgressBar = (ProgressBar)findViewById(R.id.pb_loading);

        mMovieAdapter = new MovieAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false);

        mMoviesRecyclerView.setAdapter(mMovieAdapter);
        mMoviesRecyclerView.setLayoutManager(layoutManager);

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(GET_MOVIES_IN_THEATER_LOADER, null, this);
    }

    @Override
    public Loader<ArrayList<Object>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Object>>(this) {
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
            Intent detailsIntent = new Intent(this, MovieDetailsActivity.class);
            detailsIntent.putExtra(EXTRA_MOVIE, Parcels.wrap(movie));
            startActivity(detailsIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_refresh:
                getSupportLoaderManager().restartLoader(GET_MOVIES_IN_THEATER_LOADER,
                    null, this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Object> fetchAndCategorizeTopMovies() throws IOException {

        ArrayList<Movie> actionMovies = mNetworkUtils.getTopMoviesByGenre(Movie.Genre.Action,
            MainActivity.this);
        ArrayList<Movie> dramaMovies = mNetworkUtils.getTopMoviesByGenre(Movie.Genre.Drama,
                MainActivity.this);
        ArrayList<Movie> horrorMovies = mNetworkUtils.getTopMoviesByGenre(Movie.Genre.Horror,
                MainActivity.this);
        ArrayList<Movie> sciFiMovies = mNetworkUtils.getTopMoviesByGenre(Movie.Genre.ScienceFiction,
                MainActivity.this);

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
