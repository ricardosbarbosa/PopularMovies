package com.github.ricardosbarbosa.popularmovies.helpers;

import com.github.ricardosbarbosa.popularmovies.models.Movie;
import com.github.ricardosbarbosa.popularmovies.models.MovieReview;
import com.github.ricardosbarbosa.popularmovies.models.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ricardobarbosa on 27/01/17.
 */
public class MovieProcessor {

    public static List<Movie> getMovieDataFromJson(String moviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String LIST = "results";
        final String ID = "id";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String USER_RATING = "vote_average";
        final String ORIGINAL_TITLE = "original_title";
        final String RELEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(LIST);

        List<Movie> resultStrs = new ArrayList<Movie>();
        for(int i = 0; i < moviesArray.length(); i++) {

            // Get the JSON object representing the movie
            JSONObject movieJson = moviesArray.getJSONObject(i);

            Integer id = movieJson.getInt(ID);
            String overview = movieJson.optString(OVERVIEW);
            String title = movieJson.optString(ORIGINAL_TITLE);
            Double rating = movieJson.optDouble(USER_RATING);
            String posterPath = movieJson.optString(POSTER_PATH);
            String releaseDate = movieJson.optString(RELEASE_DATE);

            Movie movie = new Movie(id, overview, title, rating, posterPath, releaseDate, false);
            resultStrs.add(movie);
        }

        return resultStrs;
    }

    public static List<MovieReview> getMovieReviewsDataFromJson(String jsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String LIST = "results";

        final String AUTHOR = "author";
        final String CONTENT = "content";

        JSONObject moviesJson = new JSONObject(jsonStr);
        JSONArray moviesReviewsArray = moviesJson.getJSONArray(LIST);

        List<MovieReview> resultStrs = new ArrayList<MovieReview>();
        for(int i = 0; i < moviesReviewsArray.length(); i++) {

            // Get the JSON object representing the movie
            JSONObject movieJson = moviesReviewsArray.getJSONObject(i);

            String autor = movieJson.optString(AUTHOR);
            String content = movieJson.optString(CONTENT);

            MovieReview movie = new MovieReview(autor, content);
            resultStrs.add(movie);
        }

        return resultStrs;
    }

    public static List<MovieTrailer> getMovieTrailersDataFromJson(String jsonStr)  throws JSONException{
        // These are the names of the JSON objects that need to be extracted.
        final String LIST = "results";

        final String ID = "id";
        final String KEY = "key";
        final String NAME = "name";
        final String SITE = "site";
        final String TYPE = "type";

        JSONObject moviesJson = new JSONObject(jsonStr);
        JSONArray moviesTrailersArray = moviesJson.getJSONArray(LIST);

        List<MovieTrailer> movieTrailers = new ArrayList<MovieTrailer>();
        for(int i = 0; i < moviesTrailersArray.length(); i++) {

            // Get the JSON object representing the movie
            JSONObject movieJson = moviesTrailersArray.getJSONObject(i);

            String id = movieJson.optString(ID);
            String key = movieJson.optString(KEY);
            String name = movieJson.optString(NAME);
            String site = movieJson.optString(SITE);
            String type = movieJson.optString(TYPE);

            MovieTrailer movieTrailer = new MovieTrailer(id, key, name, site, type);
            movieTrailers.add(movieTrailer);
        }

        return movieTrailers;
    }
}
