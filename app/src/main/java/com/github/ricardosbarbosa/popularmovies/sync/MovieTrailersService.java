package com.github.ricardosbarbosa.popularmovies.sync;

import android.content.Context;

import com.github.ricardosbarbosa.popularmovies.helpers.MovieProcessor;
import com.github.ricardosbarbosa.popularmovies.interfaces.AsyncTaskDelegate;
import com.github.ricardosbarbosa.popularmovies.models.MovieTrailer;

import org.json.JSONException;

import java.util.List;

/**
 * Created by ricardobarbosa on 28/01/17.
 */
public class MovieTrailersService extends MovieAbstractService<String, Void, List<MovieTrailer>> {

    private final String LOG_TAG = MovieTrailersService.class.getSimpleName();

    public MovieTrailersService(Context context, AsyncTaskDelegate responder){
        super(context, responder);
    }

    @Override
    protected List<MovieTrailer> getMovieDataFromJson(String moviesDataJsonStr) throws JSONException {
        return MovieProcessor.getMovieTrailersDataFromJson(moviesDataJsonStr);
    }

    @Override
    protected String getMovieDataPath(String... params) {
        return String.format("/%s/videos", params[0]);
    }
}
