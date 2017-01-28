package com.github.ricardosbarbosa.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ricardobarbosa on 27/01/17.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private final List<Movie> movies;
    private final Context context;

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.movie_list_content, movies);

        if(movies == null)
            movies = new ArrayList<>();

        this.movies = movies;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_list_content, parent, false);
        }

        final MovieViewHolder movieViewHolder = new MovieViewHolder(convertView);
        Picasso.with(context).load(movie.getFullPosterPath()).into(movieViewHolder.moviePosterView);

        return convertView;
    }

    @Override
    public Movie getItem(int position) {
        return movies != null ? movies.get(position) : super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getCount() {
        return movies != null ? movies.size() : 0;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder{
        public final ImageView moviePosterView;

        public MovieViewHolder(View view) {
            super(view);
            moviePosterView = (ImageView) view.findViewById(R.id.movie_poster);
        }

    }

}
