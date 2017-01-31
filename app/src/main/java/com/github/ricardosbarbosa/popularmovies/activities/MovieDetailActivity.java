package com.github.ricardosbarbosa.popularmovies.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.ricardosbarbosa.popularmovies.R;
import com.github.ricardosbarbosa.popularmovies.database.MovieContract;
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
            Cursor movieCursor = this.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    "moviedb_id = ?",
                    new String[]{movie.moviedb_id.toString()},
                    null
            );

            if (movieCursor.moveToFirst()) {
                Integer moviddb_id = movieCursor.getInt(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID));
                String title = movieCursor.getString(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID));
                String overview = movieCursor.getString(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID));
                Double rating = movieCursor.getDouble(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID));
                String posterPath = movieCursor.getString(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID));
                String release = movieCursor.getString(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID));
                boolean favorite = movieCursor.getInt(movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)) > 0 ;
                Movie movieFromDb = new Movie(moviddb_id, title, overview, rating, posterPath, release, favorite);

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
//                    movie.favorite();

                    movie.favorite = !movie.favorite;
                    favoriteMovie(movie.moviedb_id, movie.title, movie.overview, movie.releaseDate, movie.rating, movie.favorite, movie.posterPath);
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


    public long favoriteMovie(Integer moviedb_id, String title, String overview, String release_date,
                     double rating, boolean favorite, String poster_path) {
        long movieId;

        // First, check if the location with this city name exists in the db
        Cursor movieCursor = this.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID + " = ?",
                new String[]{moviedb_id.toString()},
                null);

        if (movieCursor.moveToFirst()) {
            //update

            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues locationValues = new ContentValues();

            locationValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favorite);

            // Finally, insert location data into the database.
            int rowsUpdated = this.getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI,
                    locationValues,
                    MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID + " = ?",
                    new String[]{moviedb_id.toString()}
            );

            if (rowsUpdated != 1) {
                int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
                movieId = movieCursor.getLong(movieIdIndex);
            }else  {
                movieId = -1;
            }

        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues locationValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            locationValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID, moviedb_id);
            locationValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            locationValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            locationValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
            locationValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
            locationValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favorite);
            locationValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);

            // Finally, insert location data into the database.
            Uri insertedUri = this.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    locationValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            movieId = ContentUris.parseId(insertedUri);
        }

        movieCursor.close();
        // Wait, that worked?  Yes!
        return movieId;
    }
}
