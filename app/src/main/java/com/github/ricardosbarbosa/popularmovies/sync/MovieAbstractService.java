package com.github.ricardosbarbosa.popularmovies.sync;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.github.ricardosbarbosa.popularmovies.BuildConfig;
import com.github.ricardosbarbosa.popularmovies.interfaces.AsyncTaskDelegate;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ricardobarbosa on 28/01/17.
 */
public abstract class MovieAbstractService<S, V, L> extends AsyncTask<S, V, L> {

    protected final Context context;
    protected AsyncTaskDelegate delegate = null;

    protected final String LOG_TAG = this.getClass().getSimpleName();

    public MovieAbstractService(Context context, AsyncTaskDelegate responder){
        this.delegate = responder;
        this.context = context;
    }

    @Override
    protected void onPostExecute(L list) {
        super.onPostExecute(list);
        if(delegate != null)
            delegate.processFinish(list);
    }

    @Override
    protected L doInBackground(S... params) {

        if (params.length == 0){
            return null;
        }
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesDataJsonStr = null;
        try {

            final String APPID_PARAM = "api_key";

            String urlString = getMovieUrlService(params);

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
            moviesDataJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Videos json string: "+moviesDataJsonStr);

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
            return getMovieDataFromJson(moviesDataJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }

    }

    protected abstract L getMovieDataFromJson(String moviesDataJsonStr) throws JSONException;
    
    private String getMovieUrlService(S... params) {
        return "http://api.themoviedb.org/3/movie" + getMovieDataPath(params);
    }

    protected abstract String getMovieDataPath(S... params);
}
