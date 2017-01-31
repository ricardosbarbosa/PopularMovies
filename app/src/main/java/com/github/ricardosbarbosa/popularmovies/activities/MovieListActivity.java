package com.github.ricardosbarbosa.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.github.ricardosbarbosa.popularmovies.R;
import com.github.ricardosbarbosa.popularmovies.adapters.MovieAdapter;
import com.github.ricardosbarbosa.popularmovies.database.MovieContract;
import com.github.ricardosbarbosa.popularmovies.database.MovieContract.MovieEntry;
import com.github.ricardosbarbosa.popularmovies.helpers.NetworkUtils;
import com.github.ricardosbarbosa.popularmovies.interfaces.AsyncTaskDelegate;
import com.github.ricardosbarbosa.popularmovies.models.Movie;
import com.github.ricardosbarbosa.popularmovies.sync.MovieDetailsService;

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
public class MovieListActivity extends AppCompatActivity implements AsyncTaskDelegate {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build()
        );

//        Configuration.Builder config = new Configuration.Builder(this);
//        config.addModelClasses(Movie.class);
//        ActiveAndroid.initialize(config.create());

        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());



        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (mTwoPane) {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movie_list_recycle);
            assert recyclerView != null;
            setupRecyclerView(recyclerView);
        }
        else {
            GridView gridView = (GridView) findViewById(R.id.movie_list);
            assert gridView != null;
            setupRecyclerView(gridView);
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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String filter = sharedPreferences.getString(getString(R.string.pref_filter_key), getString(R.string.pref_filter_default));

            if (filter.equals(getString(R.string.pref_filter_favorites))) {
//                List<Movie> favoritesMovies = new Select()
//                        .from(Movie.class)
//                        .where("favorite = ?", true)
//                        .execute();
                List<Movie> favoritesMovies = consultarFavoritos();
                this.processFinish(favoritesMovies);
            }else {
                //Aqui fazemos a chamada ao servico responsavel por carregar os filmes de acordo com as preferencias do usuario
                MovieDetailsService moviewDbTask = new MovieDetailsService(context, this);
                moviewDbTask.execute(filter);
            }
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

    private List<Movie> consultarFavoritos() {
        List<Movie> movies = new ArrayList<Movie>();
        // Sort order:  Ascending, by date.
        String sortOrder = MovieContract.MovieEntry.COLUMN_TITLE + " ASC";
        Uri movieFavoritesUri = MovieContract.MovieEntry.buildMovieFavorites();

        // Get the joined Weather data for a specific date
        Cursor weatherCursor = this.getContentResolver().query(
                movieFavoritesUri,
                null,
                null,
                null,
                sortOrder
        );

//        weatherCursor.moveToFirst();

        for (int i = 0; i < weatherCursor.getCount(); i++) {
            weatherCursor.moveToNext();

            Integer moviddb_id = weatherCursor.getInt(weatherCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_DB_ID));
            String title = weatherCursor.getString(weatherCursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
            String overview = weatherCursor.getString(weatherCursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
            Double rating = weatherCursor.getDouble(weatherCursor.getColumnIndex(MovieEntry.COLUMN_RATING));
            String posterPath = weatherCursor.getString(weatherCursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
            String release = weatherCursor.getString(weatherCursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
            boolean favorite = weatherCursor.getInt(weatherCursor.getColumnIndex(MovieEntry.COLUMN_FAVORITE)) > 0;
            Movie m = new Movie(moviddb_id, title, overview, rating, posterPath, release, favorite);

            movies.add(m);
        }

        return movies;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView) {
        movieAdapter = new MovieAdapter(this, new ArrayList<Movie>(),mTwoPane);
        recyclerView.setAdapter(movieAdapter);
    }

    private void setupRecyclerView(@NonNull final GridView gridView) {
        movieAdapter = new MovieAdapter(this, new ArrayList<Movie>(), mTwoPane);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) gridView.getAdapter().getItem(position);

                Intent detailIntent = new Intent(gridView.getContext(), MovieDetailActivity.class);
                detailIntent.putExtra(Movie.PARCELABLE_KEY, movie);
                startActivity(detailIntent);
            }
        });
    }

    @Override
    public void processFinish(Object output) {
        if(output != null){
            List<Movie> movies = (List<Movie>) output;

            if (mTwoPane) {
                RecyclerView gridView = (RecyclerView) findViewById(R.id.movie_list_recycle);
                gridView.setAdapter(new MovieAdapter(this, new ArrayList<Movie>(movies), mTwoPane ));
            }else {
                GridView gridView = (GridView) findViewById(R.id.movie_list);
                gridView.setAdapter(new MovieAdapter(this, new ArrayList<Movie>(movies), mTwoPane ));
            }

        }else{
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }
    }

}
