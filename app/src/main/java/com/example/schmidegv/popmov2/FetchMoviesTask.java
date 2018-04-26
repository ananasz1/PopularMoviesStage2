package com.example.schmidegv.popmov2;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.schmidegv.popmov2.model.Movie;
import com.example.schmidegv.popmov2.model.MovieListResponse;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.schmidegv.popmov2.BuildConfig.THE_MOVIE_DB_API_KEY;

public class FetchMoviesTask extends AsyncTask <Void, Void, Movie[]> {

    private static final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private static final String POSTER_PATH_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    public static final int MOVIE_LIST_POPULAR = 1;
    public static final int MOVIE_LIST_TOP_RATED = 2;

    private Listener mListener;
    private TheMovieDbService mTheMovieDbService;
    private int mListType;

    interface Listener {
        void onFetchFinished(Movie[] movies);
    }

    public FetchMoviesTask(Listener listener, int listType) {

        if (null == listener) {
            final String errorMessage = "The listener cannot be null.";
            Log.e(LOG_TAG, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        mListener = listener;

        if (MOVIE_LIST_POPULAR != listType && MOVIE_LIST_TOP_RATED != listType) {
            final String errorMessage = "Unknown list type for FetchMoviesTask.";
            Log.e(LOG_TAG, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        mListType = listType;

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TheMovieDbService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Class<TheMovieDbService> theMovieDbServiceDefinition = TheMovieDbService.class;

        mTheMovieDbService = retrofit.create(theMovieDbServiceDefinition);
    }

    @Override
    protected Movie[] doInBackground(Void... params) {

        final Call<MovieListResponse> moviesCall = getMovieListCall(mListType);
        if (null == moviesCall) {
            return null;
        }

        try {
            final Response<MovieListResponse> response = moviesCall.execute();
            if (response.isSuccessful()) {
                final List<Movie> movies = response.body().getMovies();

                for (Movie movie : movies) {
                    final String relativePosterPath = movie.getPosterPath();

                    final Uri posterUri = Uri.parse(POSTER_PATH_BASE_URL).buildUpon()
                            .appendEncodedPath(relativePosterPath)
                            .build();

                    final String absolutePosterPath = posterUri.toString();
                    movie.setPosterPath(absolutePosterPath);
                }

                return movies.toArray(new Movie[0]);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "The Movie DB service connection error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        mListener.onFetchFinished(movies);
    }

    private Call<MovieListResponse> getMovieListCall(int listType) {
        final String apiKey = THE_MOVIE_DB_API_KEY;

        Call<MovieListResponse> movieCall = null;
        switch (listType) {
            case MOVIE_LIST_POPULAR:
                movieCall = mTheMovieDbService.getPopularMovies(apiKey);
                break;
            case MOVIE_LIST_TOP_RATED:
                movieCall =  mTheMovieDbService.getTopRatedMovies(apiKey);
                break;
            default:
                Log.wtf(LOG_TAG, "Unknown list type for FetchMoviesTask.");
        }

        return movieCall;
    }
}
