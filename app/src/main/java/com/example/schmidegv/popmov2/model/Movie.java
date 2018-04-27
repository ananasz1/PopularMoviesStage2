package com.example.schmidegv.popmov2.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Movie implements Parcelable {

    private static final List<Integer> ARRAY_GENRE_IDS = Arrays.asList(
            12, 14, 16, 18, 27, 28, 35, 36, 37,
            53, 80, 99, 878, 9648, 10402, 10749,
            10751, 10752, 10770
    );

    private static final String[] ARRAY_GENRES = new String[]{
            "Adventure", "Fantasy", "Animation", "Drama", "Horror", "Action", "Comedy", "History",
            "Western", "Thriller", "Crime", "Documentary", "Science Fiction", "Mystery", "Music",
            "Romance", "Family", "War", "TV Movie"
    };

    @SerializedName("id")
    private String mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("original_title")
    private String mOriginalTitle;

    @SerializedName("poster_path")
    private String mPosterPath;

    @SerializedName("overview")
    private String mSynopsis;

    @SerializedName("vote_average")
    private String mVoteAverage;

    @SerializedName("release_date")
    private String mReleaseDate;

    @SerializedName("genre_ids")
    private int[] mMovieGenreIds;

    public Movie() {
    }

    public Movie(Parcel parcel) {
        mId = parcel.readString();
        mTitle = parcel.readString();
        mOriginalTitle = parcel.readString();
        mPosterPath = parcel.readString();
        mSynopsis = parcel.readString();
        mVoteAverage = parcel.readString();
        mReleaseDate = parcel.readString();
        mMovieGenreIds = parcel.createIntArray();
    }


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.mOriginalTitle = originalTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public void setSynopsis(String synopsis) {
        this.mSynopsis = synopsis;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.mVoteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    public int[] getGenreIds() {
        return mMovieGenreIds;
    }

    public void setGenreIds(int[] genreIds) {
        mMovieGenreIds = genreIds;
    }

    public String getGenres(int[] genreIds) {
        List<String> genresList = new ArrayList<String>();
        String genres; // list of genres separated by commas (Action, Comedy, Horror)
        int index;

        for (int id : genreIds) {
            index = ARRAY_GENRE_IDS.indexOf(id);
            if (index >= 0) {
                genresList.add(ARRAY_GENRES[index]);
            }
        }
        genres = TextUtils.join(", ", genresList);
        return genres;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mSynopsis);
        dest.writeString(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeIntArray(mMovieGenreIds);
    }


    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
