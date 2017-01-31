package com.github.ricardosbarbosa.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.github.ricardosbarbosa.popularmovies.R;
import com.github.ricardosbarbosa.popularmovies.activities.MovieDetailActivity;
import com.github.ricardosbarbosa.popularmovies.fragments.MovieDetailFragment;
import com.github.ricardosbarbosa.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ricardobarbosa on 27/01/17.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> implements ListAdapter {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private final List<Movie> movies;
    private final Context context;
    private boolean mTwoPane;

    public MovieAdapter(Context context, List<Movie> movies, boolean mTwoPane) {
        if(movies == null)
            movies = new ArrayList<>();

        this.movies = movies;
        this.context = context;
        this.mTwoPane = mTwoPane;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.movie_list_content, parent, false);
        }

        final MovieViewHolder movieViewHolder = new MovieViewHolder(convertView);
        Picasso.with(context).load(movie.getFullPosterPath()).into(movieViewHolder.moviePosterView);

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return movies.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return movies != null ? movies.size() : 0;
    }

    @Override
    public Movie getItem(int position) {
        return movies != null ? movies.get(position) : null;
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final Movie movie = movies.get(position);

        holder.movie = movie;
        Picasso.with(context).load(movie.getFullPosterPath()).into(holder.moviePosterView);

        holder.moviePosterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Movie.PARCELABLE_KEY, holder.movie);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra(Movie.PARCELABLE_KEY, holder.movie);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() { return movies != null ? movies.size() : 0; }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder{
        public Movie movie;

        public final ImageView moviePosterView;

        public MovieViewHolder(View view) {
            super(view);
            moviePosterView = (ImageView) view.findViewById(R.id.movie_poster);
        }

    }

}
