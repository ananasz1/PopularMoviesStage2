package com.example.schmidegv.popmov2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schmidegv.popmov2.adapter.ReviewAdapter;
import com.example.schmidegv.popmov2.adapter.TrailerAdapter;
import com.example.schmidegv.popmov2.data.MoviesContract;
import com.example.schmidegv.popmov2.model.Movie;
import com.example.schmidegv.popmov2.model.Review;
import com.example.schmidegv.popmov2.model.ReviewListResponse;
import com.example.schmidegv.popmov2.model.Trailer;
import com.example.schmidegv.popmov2.model.TrailerListResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.schmidegv.popmov2.BuildConfig.THE_MOVIE_DB_API_KEY;

//import android.widget.Button;


/**
 * Created by schmidegv on 2018. 04. 16..
 */

public class DetailsActivity extends AppCompatActivity implements TrailerAdapter.OnClickListener {

    private final static String LOG_TAG = DetailsActivity.class.getSimpleName();

    public static final String INTENT_DATA = Movie.class.getName();

    private RecyclerView mReviewsRecyclerView;
    private TextView mReviewLabelTextView;
    private ReviewAdapter mReviewAdapter;

    private RecyclerView mTrailersRecyclerView;
    private TextView mTrailerLabelTextView;
    private TrailerAdapter mTrailerAdapter;
    private boolean isFavorite;
    private List<Trailer> mVideoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final TextView movieTitleTextView = findViewById(R.id.movie_title);
        final TextView movieOriginalTitleTextView = findViewById(R.id.movie_original_title);
        final TextView movieReleaseDateTextView = findViewById(R.id.movie_release_date);
        final TextView movieVoteAverageTextView = findViewById(R.id.movie_vote_average);
        final TextView movieGenreTextView = findViewById(R.id.text_movie_genre);
        final TextView movieGenreLabel = findViewById(R.id.text_label_genre);
        final TextView movieSynopsisTextView = findViewById(R.id.movie_synopsis);
        final ImageView moviePosterImageView = findViewById(R.id.movie_poster_img);
        final FloatingActionButton fabMovies = findViewById( R.id.fav_movie );

        mReviewLabelTextView = findViewById(R.id.review_label);
        mReviewsRecyclerView = findViewById(R.id.rv_movies_reviews);
        mTrailerLabelTextView = findViewById(R.id.trailers_label);
        mTrailersRecyclerView = findViewById(R.id.rv_trailers);


        final Intent intent = getIntent();

        if (intent != null && intent.hasExtra(INTENT_DATA)) {
            final Movie movie = intent.getParcelableExtra(INTENT_DATA);

            movieTitleTextView.setText(movie.getTitle());
            movieOriginalTitleTextView.setText(getString(R.string.original_title_format,
                    movie.getOriginalTitle()));
            movieReleaseDateTextView.setText(movie.getReleaseDate());
            movieVoteAverageTextView.setText(getString(R.string.average_vote_format,
                    movie.getVoteAverage()));
            movieSynopsisTextView.setText(movie.getSynopsis());

            // Set genres to the current movie
            if (movie.getGenreIds() != null) {
                String genresString = movie.getGenres(movie.getGenreIds());
                if (genresString != null) {
                    movieGenreTextView.setText(genresString);
                    Log.d(LOG_TAG, "Genres: " + genresString);
                } else {
                    Log.d(LOG_TAG, "No Genres");
                    movieGenreTextView.setText(R.string.no_genres);
                }
            } else {
                movieGenreLabel.setVisibility(View.INVISIBLE);
                movieGenreTextView.setText("");
            }

            final String posterPath = movie.getPosterPath();
            Picasso.with(this).load(posterPath).into(moviePosterImageView);

            final String movieId = movie.getId();

            final Cursor cursor = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{MoviesContract.MovieEntry.COLUMN_ID},
                    MoviesContract.MovieEntry.COLUMN_ID + "=?",
                    new String[]{movieId},
                    null);


            if (null!= cursor && cursor.getCount() != 0) {
                fabMovies.setImageResource(R.drawable.ic_favorite);
                isFavorite = true;
            } else {
                fabMovies.setImageResource(R.drawable.ic_favorite_border);
                isFavorite = false;
            }

            mReviewAdapter = new ReviewAdapter();
            mReviewsRecyclerView.setAdapter(mReviewAdapter);
            loadReview(movieId);

            mTrailerAdapter = new TrailerAdapter(this);
            mTrailersRecyclerView.setAdapter(mTrailerAdapter);
            loadTrailers(movie.getId());
        }

    }




    public void onClickFavoritesButton(View view) {
        FloatingActionButton fabButton = (FloatingActionButton) view;
        Intent intent = getIntent();
        Movie movie = (Movie) intent.getParcelableExtra(INTENT_DATA);
        
        if (!isFavorite) {
            ContentValues values = new ContentValues();
            values.put(MoviesContract.MovieEntry.COLUMN_ID, movie.getId());
            values.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            values.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());
            values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, values);

                Toast toast = Toast.makeText(this, R.string.add_to_favorites, Toast.LENGTH_SHORT);
                toast.show();
                isFavorite = true;

            fabButton.setImageResource(R.drawable.ic_favorite);
        } else {
            getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,
                    MoviesContract.MovieEntry.COLUMN_ID + "=?", new String[]{movie.getId()});

                Toast toast = Toast.makeText(this,R.string.removed_from_favorites, Toast.LENGTH_SHORT);
                toast.show();
                isFavorite = false;
            fabButton.setImageResource(R.drawable.ic_favorite_border);
        }
    }

     private void loadReview(final String movieId) {

        final String apiKey = THE_MOVIE_DB_API_KEY;

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TheMovieDbService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final Class<TheMovieDbService> theMovieDbServiceDefinition = TheMovieDbService.class;
        final TheMovieDbService theMovieDbService = retrofit.create(theMovieDbServiceDefinition);
        final Call<ReviewListResponse> call = theMovieDbService.getReviewsForMovie(movieId, apiKey);
        call.enqueue(new Callback<ReviewListResponse>() {
                         @Override
                         public void onResponse(final Call<ReviewListResponse> call,
                                                final Response<ReviewListResponse> response) {
                             if (response.isSuccessful()) {
                                 final List<Review> reviews = response.body().getReviews();
                                 mReviewAdapter.setReviews(reviews.toArray(new Review[0]));

                                 if (reviews.isEmpty()) {
                                     mReviewLabelTextView.setVisibility(View.GONE);
                                     mReviewsRecyclerView.setVisibility(View.GONE);
                                 }
                             }
                         }

                         @Override
                         public void onFailure(final Call<ReviewListResponse> call,
                                               final Throwable t) {
                             mReviewLabelTextView.setVisibility(View.GONE);
                             mReviewsRecyclerView.setVisibility(View.GONE);
                         }
                     }
        );
    }

    private void loadTrailers(final String movieId) {

        final String apiKey = THE_MOVIE_DB_API_KEY;

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TheMovieDbService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final Class<TheMovieDbService> theMovieDbServiceDefinition = TheMovieDbService.class;
        final TheMovieDbService theMovieDbService = retrofit.create(theMovieDbServiceDefinition);

        final Call<TrailerListResponse> call = theMovieDbService.getTrailersForMovie(movieId, apiKey);
        call.enqueue(new Callback<TrailerListResponse>() {
                         @Override
                         public void onResponse(final Call<TrailerListResponse> call,
                                                final Response<TrailerListResponse> response) {
                             if (response.isSuccessful()) {
                                 final List<Trailer> trailers = response.body().getTrailers();
                                 mTrailerAdapter.setTrailers(trailers.toArray(new Trailer[0]));

                                 if (trailers.isEmpty()) {
                                     mTrailerLabelTextView.setVisibility(View.GONE);
                                     mTrailersRecyclerView.setVisibility(View.GONE);
                                 }
                             }
                         }

                         @Override
                         public void onFailure(final Call<TrailerListResponse> call,
                                               final Throwable t) {
                             mTrailerLabelTextView.setVisibility(View.GONE);
                             mTrailersRecyclerView.setVisibility(View.GONE);
                         }
                     }
        );
    }

    @Override
    public void onTrailerItemClick(Trailer trailer) {
        final Uri trailerUri = Uri.parse(trailer.getLink());
        final Intent trailerIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
        if (null != trailerIntent.resolveActivity(getPackageManager())) {
            startActivity(trailerIntent);
        } else {
            Log.w(LOG_TAG, "No video player app found on the device.");
            Toast.makeText(this, "Cannot find video player app.", Toast.LENGTH_SHORT).show();
        }
    }
}

