package com.github.ricardosbarbosa.popularmovies.sync;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.github.ricardosbarbosa.popularmovies.BuildConfig;
import com.github.ricardosbarbosa.popularmovies.R;
import com.github.ricardosbarbosa.popularmovies.helpers.MovieProcessor;
import com.github.ricardosbarbosa.popularmovies.interfaces.AsyncTaskDelegate;
import com.github.ricardosbarbosa.popularmovies.models.Movie;

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
public class FetchMoviewDbTask extends AsyncTask<String, Void, List<Movie>> {

    private final Context context;
    private AsyncTaskDelegate delegate = null;

    private final String LOG_TAG = FetchMoviewDbTask.class.getSimpleName();


    public FetchMoviewDbTask(Context context, AsyncTaskDelegate responder){
        this.delegate = responder;
        this.context = context;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        if(delegate != null)
            delegate.processFinish(movies);
    }

    @Override
    protected List<Movie> doInBackground(String... params) {

        if (params.length == 0){
            return null;
        }
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;
        try {

            final String APPID_PARAM = "api_key";

            final String POPULAR_MOVIE_URL = "http://api.themoviedb.org/3/movie/popular";
            final String MOST_RATED_MOVIE_URL = "http://api.themoviedb.org/3/movie/top_rated";

            String urlString;

            if (params[0].equals(context.getString(R.string.pref_filter_popular))) {
                urlString = POPULAR_MOVIE_URL;
            } else {
                urlString = MOST_RATED_MOVIE_URL;
            }

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
            moviesJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Forecast json string: "+moviesJsonStr);

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
            return MovieProcessor.getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }

    }
}