package com.github.ricardosbarbosa.popularmovies.sync;

import android.content.Context;

import com.github.ricardosbarbosa.popularmovies.R;
import com.github.ricardosbarbosa.popularmovies.helpers.MovieProcessor;
import com.github.ricardosbarbosa.popularmovies.interfaces.AsyncTaskDelegate;
import com.github.ricardosbarbosa.popularmovies.models.Movie;

import org.json.JSONException;

import java.util.List;

/**
 * Created by ricardobarbosa on 27/01/17.
 */
public class MovieDetailsService extends MovieAbstractService<String, Void, List<Movie>> {

    public MovieDetailsService(Context context, AsyncTaskDelegate responder) {
        super(context, responder);
    }

    @Override
    protected List<Movie> getMovieDataFromJson(String moviesDataJsonStr) throws JSONException {
        return MovieProcessor.getMovieDataFromJson(moviesDataJsonStr);
    }

    @Override
    protected String getMovieDataPath(String... params) {

        final String POPULAR_MOVIE_URL = "http://api.themoviedb.org/3/movie/popular";
        final String MOST_RATED_MOVIE_URL = "http://api.themoviedb.org/3/movie/top_rated";

        String urlString;

        if (params[0].equals(context.getString(R.string.pref_filter_popular))) {
            urlString = POPULAR_MOVIE_URL;
        } else {
            urlString = MOST_RATED_MOVIE_URL;
        }

        return urlString;
    }
}