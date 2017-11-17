package com.bitpapr.lucerna;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitpapr.lucerna.models.Movie;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import static com.bitpapr.lucerna.MainActivity.EXTRA_MOVIE;
import static com.bitpapr.lucerna.utilities.NetworkUtils.POSTER_BASE_URI;

/**
 * Created by henrick on 13-11-2017.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private boolean movieFavorited = false;
    Toast favoritedToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView movieTitleTextView = (TextView)findViewById(R.id.tv_movie_title);
        TextView movieOverviewTextView = (TextView)findViewById(R.id.tv_movie_overview);
        ImageView moviePosterImageView = (ImageView)findViewById(R.id.iv_movie_poster);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_MOVIE)) {
            Movie movie = Parcels.unwrap(intent.getParcelableExtra(EXTRA_MOVIE));

            movieTitleTextView.setText(movie.getTitle());
            movieOverviewTextView.setText(movie.getOverview());

            Uri imageUri = Uri.withAppendedPath(POSTER_BASE_URI, movie.getPosterUrlPath());

            Picasso.with(this).load(imageUri)
                .fit()
                .centerCrop()
                .into(moviePosterImageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_star:
                starMovie(item);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void starMovie(MenuItem menuItem) {

        String toastMessage;

        if (!movieFavorited) {
            menuItem.setIcon(R.drawable.ic_action_favorited);
            movieFavorited = true;
            toastMessage = getString(R.string.movie_favorited_text);
        } else {
            menuItem.setIcon(R.drawable.ic_action_not_favorited);
            movieFavorited = false;
            toastMessage = getString(R.string.movie_unfavorited_text);
        }

        if (favoritedToast != null) {
            favoritedToast.cancel();
        }

        favoritedToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
        favoritedToast.show();
    }
}
