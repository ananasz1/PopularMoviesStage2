package com.example.schmidegv.popmov2.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.schmidegv.popmov2.R;
import com.example.schmidegv.popmov2.data.MoviesContract;
import com.example.schmidegv.popmov2.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final static String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Movie[] mMovies;

    private MovieOnClickListener mListener;

    public interface MovieOnClickListener {
        void onItemClick(Movie movie);
    }

    public MovieAdapter(MovieOnClickListener listener) {
        mMovies = null;
        mListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        final int layoutIdForListItem = R.layout.item_movie;
        final LayoutInflater inflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;

        final View view =
                inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        final MovieViewHolder viewHolder = new MovieViewHolder(context, view);

        return viewHolder;
    }

    public Movie[] getMovies() {
        return mMovies;
    }

    public void setMoviesData(final Movie[] movies) {
        this.mMovies = movies;
        notifyDataSetChanged();
    }


    public void setMoviesData(final Cursor cursor) {
        Movie[] movies = null;

        if (null != cursor) {
            movies = new Movie[cursor.getCount()];
            int currentMovieIndex = 0;
            while (cursor.moveToNext()) {
                final Movie movie = getMovieFromCursor(cursor);
                movies[currentMovieIndex++] = movie;
            }
        }

        setMoviesData(movies);
    }

    private Movie getMovieFromCursor(final Cursor cursor) {
        if (null != cursor && cursor.getCount() != 0) {
            final Movie movie = new Movie();

            movie.setId(cursor.getString(cursor.getColumnIndex(
                    MoviesContract.MovieEntry.COLUMN_ID)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(
                    MoviesContract.MovieEntry.COLUMN_TITLE)));
            movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(
                    MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
            movie.setSynopsis(cursor.getString(cursor.getColumnIndex(
                    MoviesContract.MovieEntry.COLUMN_SYNOPSIS)));
            movie.setVoteAverage(cursor.getString(cursor.getColumnIndex(
                    MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex(
                    MoviesContract.MovieEntry.COLUMN_POSTER_PATH)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(
                    MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)));

            return movie;
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        final Movie movie = mMovies[position];
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        } else {
            return mMovies.length;
        }
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final Context mContext;
        private ImageView mMoviePoster;

        public MovieViewHolder(Context context, View itemView) {
            super(itemView);

            mContext = context;
            mMoviePoster = (ImageView) itemView.findViewById(R.id.tv_movie_poster);
            mMoviePoster.setOnClickListener(this);
        }

        public void bind(final Movie movie) {
            final String posterPath = movie.getPosterPath();
            Picasso.with(mContext)
                    .load(posterPath)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(mMoviePoster);
        }

        @Override
        public void onClick(View view) {
            if (null != mMovies) {
                final int adapterPosition = getAdapterPosition();
                final Movie movie = mMovies[adapterPosition];
                mListener.onItemClick(movie);
            } else {
                Log.wtf(LOG_TAG, "OnClick handler call with empty movie list.");
            }
        }
    }
}
