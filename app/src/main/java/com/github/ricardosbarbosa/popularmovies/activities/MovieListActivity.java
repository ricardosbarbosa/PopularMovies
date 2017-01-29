package com.github.ricardosbarbosa.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Select;
import com.facebook.stetho.Stetho;
import com.github.ricardosbarbosa.popularmovies.R;
import com.github.ricardosbarbosa.popularmovies.adapters.MovieAdapter;
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

    private ArrayAdapter<Movie> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build()
        );

        Configuration.Builder config = new Configuration.Builder(this);
        config.addModelClasses(Movie.class);
        ActiveAndroid.initialize(config.create());

        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        GridView gridView = (GridView) findViewById(R.id.movie_list);
        assert gridView != null;
        setupRecyclerView((GridView) gridView);

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
                List<Movie> favoritesMovies = new Select()
                        .from(Movie.class)
                        .where("favorite = ?", true)
                        .execute();
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

    private void setupRecyclerView(@NonNull final GridView gridView) {
        arrayAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        gridView.setAdapter(arrayAdapter);

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

            GridView gridView = (GridView) findViewById(R.id.movie_list);
            gridView.setAdapter(new MovieAdapter(this, new ArrayList<Movie>(movies) ));
        }else{
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }
    }

}
