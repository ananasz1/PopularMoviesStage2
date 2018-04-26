package com.example.schmidegv.popmov2.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReviewListResponse {

    @SerializedName("results")
    private List<Review> mReviews = new ArrayList<>();

    public List<Review> getReviews() {
        return mReviews;
    }
}
