package com.github.ricardosbarbosa.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.github.ricardosbarbosa.popularmovies.R;
import com.github.ricardosbarbosa.popularmovies.fragments.MovieDetailFragment;
import com.github.ricardosbarbosa.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MovieListActivity}.
 */
public class MovieDetailActivity extends AppCompatActivity {
    public static final String ARG_MOVIE_ID = "id";

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        movie = (Movie) getIntent().getParcelableExtra(Movie.PARCELABLE_KEY);

        if (movie!= null ) {
            Movie movieFromDb = new Select()
                    .from(Movie.class)
                    .where("moviedb_id = ?", movie.moviedb_id)
                    .executeSingle();

            if (movieFromDb != null) {
                this.movie = movieFromDb;
            }
        }
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(
                movie.favorite ? android.R.drawable.star_big_on :android.R.drawable.star_big_off
        );

        ImageView imageViewToolbar =  (ImageView) findViewById(R.id.image_movie_poster_toolbar);
        Picasso.with(this).load(movie.getFullPosterPath()).into(imageViewToolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movie!= null) {
                    movie.favorite();
                    fab.setImageResource(
                            movie.favorite ? android.R.drawable.star_big_on :android.R.drawable.star_big_off
                    );
                }

//                Snackbar.make(view, "Movi", Snackbar.LENGTH_LONG).show();
            }
        });
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(Movie.PARCELABLE_KEY,
                    getIntent().getParcelableExtra(Movie.PARCELABLE_KEY));
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
