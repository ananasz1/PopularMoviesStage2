package com.example.schmidegv.popmov2.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public final class TrailerListResponse {

    @SerializedName("youtube")
    private List<Trailer> mTrailers = new ArrayList<>();

    public List<Trailer> getTrailers() {
        return mTrailers;
    }

}
