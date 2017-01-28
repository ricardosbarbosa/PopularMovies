package com.github.ricardosbarbosa.popularmovies.sync;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.github.ricardosbarbosa.popularmovies.BuildConfig;
import com.github.ricardosbarbosa.popularmovies.helpers.MovieProcessor;
import com.github.ricardosbarbosa.popularmovies.interfaces.AsyncTaskDelegate;
import com.github.ricardosbarbosa.popularmovies.models.MovieReview;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by ricardobarbosa on 27/01/17.
 */
public class MovieReviewsService extends AsyncTask<String, Void, List<MovieReview>> {


    private final Context context;
    private AsyncTaskDelegate delegate = null;

    private final String LOG_TAG = MovieReviewsService.class.getSimpleName();


    public MovieReviewsService(Context context, AsyncTaskDelegate responder){
        this.delegate = responder;
        this.context = context;
    }

    @Override
    protected void onPostExecute(List<MovieReview> movies) {
        super.onPostExecute(movies);
        if(delegate != null)
            delegate.processFinish(movies);
    }

    @Override
    protected List<MovieReview> doInBackground(String... params) {

        if (params.length == 0){
            return null;
        }
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesReviwsJsonStr = null;
        try {

            final String APPID_PARAM = "api_key";

            final String MOVIE_REVIEWS_URL = "http://api.themoviedb.org/3/movie/%s/reviews";

            String urlString = String.format(MOVIE_REVIEWS_URL, params[0]);

            Uri builtUri = Uri.parse(urlString).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY ).build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI: "+ builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesReviwsJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Reviews json string: "+moviesReviwsJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }  finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        try {
            return MovieProcessor.getMovieReviewsDataFromJson(moviesReviwsJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }

    }

}

