package com.example.schmidegv.popmov2;

import com.example.schmidegv.popmov2.model.MovieListResponse;
import com.example.schmidegv.popmov2.model.ReviewListResponse;
import com.example.schmidegv.popmov2.model.TrailerListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface TheMovieDbService {

    public static final String BASE_URL = "http://api.themoviedb.org/3/";

    @GET("movie/popular/")
    Call<MovieListResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated/")
    Call<MovieListResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewListResponse> getReviewsForMovie(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("movie/{id}/trailers")
    Call<TrailerListResponse>  getTrailersForMovie(@Path("id") String id, @Query("api_key") String apiKey);

}
