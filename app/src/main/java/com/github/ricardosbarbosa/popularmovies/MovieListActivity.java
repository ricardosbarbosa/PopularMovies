package com.github.ricardosbarbosa.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private ArrayAdapter<Movie> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        GridView gridView = (GridView) findViewById(R.id.movie_list);
        assert gridView != null;
        setupRecyclerView((GridView) gridView);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        Context context = MovieListActivity.this;

        //Se há	conexão disponível
        if (NetworkUtils.isNetworkConnected(context)) {
            //Aqui fazemos a chamada ao servico responsavel por carregar os filmes de acordo com as preferencias do usuario
            FetchMoviewDbTask moviewDbTask = new FetchMoviewDbTask();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String filter = sharedPreferences.getString(getString(R.string.pref_filter_key), getString(R.string.pref_filter_default));
            moviewDbTask.execute(filter);
        } else {
            //Se não há	conexão disponível, exibe a mensagem
            View view = this.findViewById(R.id.movie_list);
            Snackbar snackbar = Snackbar.make(view, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
                //Ao clicar na snackbar, uma nova tentativa de atualizar a lista é efetuada :-)
                @Override
                public void onClick(View view) {
                    updateMovies();
                }
            });
            snackbar.show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        if (item.getItemId() == R.id.action_refresh) {
//            updateMovies();
//            return true;
//        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void setupRecyclerView(@NonNull final GridView gridView) {
        arrayAdapter = new MoviewAdapter(this, new ArrayList<Movie>());
        gridView.setAdapter(arrayAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) gridView.getAdapter().getItem(position);

                Intent detailIntent = new Intent(gridView.getContext(), MovieDetailActivity.class);
                detailIntent.putExtra(MovieDetailFragment.ARG_MOVIE_ID, movie);
                startActivity(detailIntent);
            }
        });
    }


    public static class MoviewAdapter extends ArrayAdapter<Movie> {
        private static final String LOG_TAG = MoviewAdapter.class.getSimpleName();

        public MoviewAdapter(Activity context, List<Movie> movies) {
            super(context, 0, movies);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Movie movie = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.movie_list_content, parent, false);
            }

            ImageView moviePoster = (ImageView) convertView.findViewById(R.id.movie_poster);
            Picasso.with(getContext()).load(movie.getFullPosterPath()).into(moviePoster);

            return convertView;
        }
    }

    /*public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Movie> mValues;

        public SimpleItemRecyclerViewAdapter(List<Movie> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.movie = mValues.get(position);
            holder.mImagePosterView.setImageBitmap(mValues.get(position).image);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MovieDetailFragment.ARG_MOVIE_ID, holder.movie.id);
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.ARG_MOVIE_ID, holder.movie.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImagePosterView;
            public Movie movie;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImagePosterView = (ImageView) view.findViewById(R.id.movie_poster);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + movie.toString() + "'";
            }
        }
    }*/

    public class FetchMoviewDbTask extends AsyncTask<String, Void, Movie[]> {


        private final String LOG_TAG = FetchMoviewDbTask.class.getSimpleName();

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);

            if (movies.length > 0) {
                arrayAdapter.clear();
                for (Movie mo : movies) {
                    arrayAdapter.add(mo);
                }
            }
        }

        private Movie[] getMovieDataFromJson(String moviesJsonStr)
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

            Movie[] resultStrs = new Movie[moviesArray.length()];
            for(int i = 0; i < moviesArray.length(); i++) {

                // Get the JSON object representing the movie
                JSONObject movieJson = moviesArray.getJSONObject(i);

                Integer id = movieJson.getInt(ID);
                String overview = movieJson.getString(OVERVIEW);
                String title = movieJson.getString(ORIGINAL_TITLE);
                Double rating = movieJson.getDouble(USER_RATING);
                String posterPath = movieJson.getString(POSTER_PATH);
                String releaseDate = movieJson.getString(RELEASE_DATE);

                Movie movie = new Movie(id, overview, title, rating, posterPath, releaseDate);
                resultStrs[i] = movie;
            }

            return resultStrs;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0){
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;
            try {

                final String APPID_PARAM = "api_key";

                final String POPULAR_MOVIE_URL = "http://api.themoviedb.org/3/movie/popular";
                final String MOST_RATED_MOVIE_URL = "http://api.themoviedb.org/3/movie/top_rated";

                String urlString;

                if (params[0].equals(getString(R.string.pref_filter_popular))) {
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
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }

        }
    }
}
