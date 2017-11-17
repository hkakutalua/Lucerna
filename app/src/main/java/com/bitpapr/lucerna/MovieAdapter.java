package com.bitpapr.lucerna;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitpapr.lucerna.models.Movie;
import com.bitpapr.lucerna.utilities.GenreToStringConverter;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;

import java.util.ArrayList;

import static com.bitpapr.lucerna.utilities.NetworkUtils.POSTER_BASE_THUMBNAIL_URI;

/**
 * Created by henrick on 13-11-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MOVIE = 0;
    private static final int CATEGORY_SEPARATOR = 1;

    private final Context mContext;
    private ArrayList<Object> mMoviesAndCategoriesList;
    private MovieListItemClickListener mClickListener;
    private GenreToStringConverter mGenreToStringConverter;

    public MovieAdapter(MovieListItemClickListener clickListener, Context context) {
        mClickListener = clickListener;
        mContext = context;
        mGenreToStringConverter = new GenreToStringConverter(context);
    }

    @Override
    public int getItemViewType(int position) {

        Object item = mMoviesAndCategoriesList.get(position);

        if (item instanceof Movie) {
            return MOVIE;
        } else if (item instanceof String) {
            return CATEGORY_SEPARATOR;
        }

        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case MOVIE:
                View movieView = inflater.inflate(R.layout.movie_list_item, viewGroup, false);
                viewHolder = new MovieViewHolder(movieView);
                break;
            case CATEGORY_SEPARATOR:
                View separatorView = inflater.inflate(R.layout.movie_category_separator, viewGroup, false);
                viewHolder = new CategorySeparatorViewHolder(separatorView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case MOVIE:
                configureMovieViewHolder((MovieViewHolder) holder, position);
                break;
            case CATEGORY_SEPARATOR:
                configureSeparatorViewHolder((CategorySeparatorViewHolder)holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mMoviesAndCategoriesList == null) {
            return 0;
        } else {
            return mMoviesAndCategoriesList.size();
        }
    }

    public void setAdapterData(ArrayList<Object> moviesList) {
        mMoviesAndCategoriesList = moviesList;
        notifyDataSetChanged();
    }

    public ArrayList<Object> getAdapterData() {
        return mMoviesAndCategoriesList;
    }

    private void configureMovieViewHolder(MovieViewHolder viewHolder, int position) {

        final Movie movie = (Movie) mMoviesAndCategoriesList.get(position);
        if (movie != null) {
            String title = movie.getTitle();
            String overview = movie.getOverview();
            LocalDate releaseDate = movie.getReleaseDate();

            String movieGenres = "";
            for (Movie.Genre genre : movie.getGenres()) {
                String genreString = mGenreToStringConverter.convertToString(genre);
                movieGenres = movieGenres.concat(genreString + " | ");
            }

            if (overview.isEmpty()) {
                String noOverviewText = mContext.getString(R.string.no_overview_text);
                viewHolder.mMovieOverviewTextView.setText(noOverviewText);
            } else {
                viewHolder.mMovieOverviewTextView.setText(overview);
            }

            viewHolder.mMovieTitleTextView.setText(title);
            viewHolder.mMovieYearTextView.setText(Integer.toString(releaseDate.getYear()));
            viewHolder.mMovieGenresTextView.setText(movieGenres);

            // TODO: this is so wrong, the image fetching responsibility shouldn't belong to this class
            String posterPath = movie.getPosterUrlPath();
            Picasso.with(mContext).load(Uri.withAppendedPath(POSTER_BASE_THUMBNAIL_URI, posterPath))
                    .into(viewHolder.mMoviePosterImageView);
        }
    }

    private void configureSeparatorViewHolder(CategorySeparatorViewHolder viewHolder, int position) {
        viewHolder.mCategoryTextView.setText((String)mMoviesAndCategoriesList.get(position));
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        private TextView mMovieTitleTextView;
        private TextView mMovieOverviewTextView;
        private ImageView mMoviePosterImageView;
        private TextView mMovieYearTextView;
        private TextView mMovieGenresTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mMovieTitleTextView = itemView.findViewById(R.id.tv_movie_title);
            mMovieOverviewTextView = itemView.findViewById(R.id.tv_movie_overview);
            mMoviePosterImageView = itemView.findViewById(R.id.iv_movie_poster);
            mMovieYearTextView = itemView.findViewById(R.id.tv_movie_year);
            mMovieGenresTextView = itemView.findViewById(R.id.tv_movie_genres);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickListener.onMovieListItemClick(position);
        }
    }

    class CategorySeparatorViewHolder extends RecyclerView.ViewHolder {

        private TextView mCategoryTextView;

        public CategorySeparatorViewHolder(View itemView) {
            super(itemView);
            mCategoryTextView = itemView.findViewById(R.id.tv_category);
        }
    }

    public interface MovieListItemClickListener {
        void onMovieListItemClick(int position);
    }
}
