package com.github.ricardosbarbosa.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ricardosbarbosa.popularmovies.R;
import com.github.ricardosbarbosa.popularmovies.models.MovieReview;

import java.util.List;

/**
 * Created by ricardobarbosa on 27/01/17.
 */
public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

    private final Context mContext;
    private List<MovieReview> movieReviews;

    public MovieReviewAdapter(Context context, List<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
        this.mContext = context;
    }

    public void swap(List<MovieReview> data){
        movieReviews.clear();
        movieReviews.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_review_list_row, null);
        MovieReviewViewHolder viewHolder = new MovieReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {

        MovieReview movieReview = this.movieReviews.get(position);

        holder.autor.setText(movieReview.autor);
        holder.review.setText(movieReview.content);

    }

    @Override
    public int getItemCount() {
        return this.movieReviews != null ? this.movieReviews.size() : 0;
    }


    class MovieReviewViewHolder extends RecyclerView.ViewHolder{
        public final TextView autor;
        public final TextView review;


        public MovieReviewViewHolder(View itemView) {
            super(itemView);

            autor = (TextView) itemView.findViewById(R.id.movie_review_autor);
            review = (TextView) itemView.findViewById(R.id.movie_review_review);
        }
    }

}
