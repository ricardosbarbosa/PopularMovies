package com.github.ricardosbarbosa.popularmovies.sync;

import android.content.Context;

import com.github.ricardosbarbosa.popularmovies.helpers.MovieProcessor;
import com.github.ricardosbarbosa.popularmovies.interfaces.AsyncTaskDelegate;
import com.github.ricardosbarbosa.popularmovies.models.MovieReview;

import org.json.JSONException;

import java.util.List;

/**
 * Created by ricardobarbosa on 27/01/17.
 */
public class MovieReviewsService extends MovieAbstractService<String, Void, List<MovieReview>> {

    public MovieReviewsService(Context context, AsyncTaskDelegate responder) {
        super(context, responder);
    }

    @Override
    protected List<MovieReview> getMovieDataFromJson(String moviesDataJsonStr) throws JSONException {
        return MovieProcessor.getMovieReviewsDataFromJson(moviesDataJsonStr);
    }

    @Override
    protected String getMovieDataPath(String... params) {
        return String.format("/%s/reviews", params[0]);
    }

}
