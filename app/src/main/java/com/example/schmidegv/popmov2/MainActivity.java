package com.example.schmidegv.popmov2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.schmidegv.popmov2.adapter.MovieAdapter;
import com.example.schmidegv.popmov2.data.MoviesContract;
import com.example.schmidegv.popmov2.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickListener,
        FetchMoviesTask.Listener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String SAVED_CURRENT_SELECTED_MOVIE_LIST_KEY = "saved-current-selected-movie-list-key";
    private static final String SAVED_MOVIES_KEY = "saved-movies-key";

    private static final int ID_LOADER_FAVORITE_MOVIES = 1;

    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mAdapter;
    private ProgressBar mLoadProgressBar;
    private TextView mMessageTextView;
    private String subtitle;

    private int mSelectedMovieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subtitle = getResources().getString(R.string.most_popular);
        getSupportActionBar().setSubtitle(subtitle);

        mLoadProgressBar = findViewById(R.id.loading_indicator);
        mMessageTextView = findViewById(R.id.message_display);
        mMoviesRecyclerView = findViewById(R.id.rv_movies);

        final MovieAdapter.MovieOnClickListener listener = this;

        mAdapter = new MovieAdapter(listener);
        mMoviesRecyclerView.setAdapter(mAdapter);

        int spanCount = 3;
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 4;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        mMoviesRecyclerView.setLayoutManager(layoutManager);

        showLoadProgressBar();

        if (null == savedInstanceState) {
            loadMovieList(FetchMoviesTask.MOVIE_LIST_POPULAR);
        } else {
            final ArrayList<Movie> savedMovieList = savedInstanceState.getParcelableArrayList(SAVED_MOVIES_KEY);
            final Movie[] savedMovieArray = savedMovieList.toArray(new Movie[0]);
            mAdapter.setMoviesData(savedMovieArray);
            mSelectedMovieList = savedInstanceState.getInt(SAVED_CURRENT_SELECTED_MOVIE_LIST_KEY);
            showMoviesList();
        }

        getLoaderManager().initLoader(ID_LOADER_FAVORITE_MOVIES, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final ArrayList<Movie> movies = new ArrayList(Arrays.asList(mAdapter.getMovies()));
        outState.putParcelableArrayList(SAVED_MOVIES_KEY, movies);
        outState.putInt(SAVED_CURRENT_SELECTED_MOVIE_LIST_KEY, mSelectedMovieList);
        outState.putString("subtitle", subtitle);
    }

    protected void onRestoreInstanceState(Bundle state){
        super.onRestoreInstanceState(state);
        if (null == state) {
            loadMovieList(FetchMoviesTask.MOVIE_LIST_POPULAR);
        } else {
            final ArrayList<Movie> savedMovieList = state.getParcelableArrayList(SAVED_MOVIES_KEY);
            final Movie[] savedMovieArray = savedMovieList.toArray(new Movie[0]);
            mAdapter.setMoviesData(savedMovieArray);
            mSelectedMovieList = state.getInt(SAVED_CURRENT_SELECTED_MOVIE_LIST_KEY);
            subtitle = state.getString("subtitle");
            getSupportActionBar().setSubtitle(subtitle);
            showMoviesList();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_item_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        switch (itemId) {
            case R.id.switch_most_popular:
                mSelectedMovieList = itemId;
                loadMovieList(FetchMoviesTask.MOVIE_LIST_POPULAR);
                subtitle = getResources().getString(R.string.most_popular);
                getSupportActionBar().setSubtitle(subtitle);
                break;
            case R.id.switch_highest_rated:
                mSelectedMovieList = itemId;
                loadMovieList(FetchMoviesTask.MOVIE_LIST_TOP_RATED);
                subtitle = getResources().getString(R.string.top_rated);
                getSupportActionBar().setSubtitle(subtitle);
                break;
            case R.id.switch_favorites:
                mSelectedMovieList = itemId;
                loadFavoriteMovieList();
                subtitle = getResources().getString(R.string.favorites);
                getSupportActionBar().setSubtitle(subtitle);
                break;
            default:
                Log.w(LOG_TAG, "Menu selection is not handled. ItemId: " + itemId);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Movie movie) {
        Intent intentToStartDetailsActivity = new Intent(this, DetailsActivity.class);
        intentToStartDetailsActivity.putExtra(DetailsActivity.INTENT_DATA, movie);
        startActivity(intentToStartDetailsActivity);
    }

    @Override
    public void onFetchFinished(Movie[] movies) {
        final boolean moviesDisplayed = (0 != mAdapter.getItemCount());
        final boolean newMovieListAvailable = (null != movies);

        if (newMovieListAvailable) {
            showMoviesList();
            mAdapter.setMoviesData( movies );
        } else {
            if (moviesDisplayed) {
                Log.w( LOG_TAG, "No new data available to refresh the movies list." );
            } else {
                showMessage( R.string.error_no_internet );
            }
        }
    }


    public void loadMovieList(int listType) {
        showLoadProgressBar();
        final FetchMoviesTask.Listener listener = this;
        new FetchMoviesTask(listener, listType).execute();
    }

    public void loadFavoriteMovieList() {
        showLoadProgressBar();
        getLoaderManager().restartLoader(ID_LOADER_FAVORITE_MOVIES, null, this);
    }

    private void showLoadProgressBar() {
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mMessageTextView.setVisibility(View.INVISIBLE);
        mLoadProgressBar.setVisibility(View.VISIBLE);
    }

    private void showMoviesList() {
        mMessageTextView.setVisibility(View.INVISIBLE);
        mLoadProgressBar.setVisibility(View.INVISIBLE);
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showMessage(int messageStringResourceId) {
        mLoadProgressBar.setVisibility(View.INVISIBLE);
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);

        String message = getString(messageStringResourceId);
        mMessageTextView.setText(message);
        mMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = MainActivity.this;
        switch (id) {
            case ID_LOADER_FAVORITE_MOVIES:
                try {
                    return new CursorLoader(context,
                            MoviesContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (R.id.switch_favorites == mSelectedMovieList) {
            mAdapter.setMoviesData(data);
            if (mAdapter.getItemCount() != 0) {
                showMoviesList();
            } else {
                showMessage(R.string.error_no_movie);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (R.id.switch_favorites == mSelectedMovieList) {
            mAdapter.setMoviesData((Cursor) null);
            showMessage(R.string.error_no_internet);
        }
    }

    }

