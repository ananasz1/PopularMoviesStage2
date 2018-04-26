package com.example.schmidegv.popmov2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.schmidegv.popmov2.R;
import com.example.schmidegv.popmov2.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private Review[] mReviews;

    public ReviewAdapter() {
        mReviews = null;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        final LayoutInflater inflater = LayoutInflater.from(context);

        final boolean shouldAttachToParentImmediately = false;
        final int layoutIdForRecyclerViewItem = R.layout.item_review;

        final View view = inflater.inflate(layoutIdForRecyclerViewItem, parent,
                shouldAttachToParentImmediately);

        final ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        final Review review = mReviews[position];
        holder.bind(review);
    }

    public Review[] getReviews() {
        return mReviews;
    }

    public void setReviews(final Review[] reviews) {
        this.mReviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null == mReviews) { return 0; }
        return mReviews.length;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView mReviewAuthor;
        private TextView mReviewContent;

        public ReviewViewHolder(View view) {
            super(view);
            mReviewAuthor = (TextView) view.findViewById(R.id.review_author);
            mReviewContent = (TextView) view.findViewById(R.id.review_text);
        }

        public void bind(Review review) {
            String reviewAuthor = review.getAuthor();
            mReviewAuthor.setText(reviewAuthor);
            String reviewContent = review.getContent();
            mReviewContent.setText(reviewContent);
        }
    }
}
