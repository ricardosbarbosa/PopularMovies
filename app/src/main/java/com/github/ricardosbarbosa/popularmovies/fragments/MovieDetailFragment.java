package com.github.ricardosbarbosa.popularmovies.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ricardosbarbosa.popularmovies.R;
import com.github.ricardosbarbosa.popularmovies.activities.MovieDetailActivity;
import com.github.ricardosbarbosa.popularmovies.activities.MovieListActivity;
import com.github.ricardosbarbosa.popularmovies.adapters.MovieReviewAdapter;
import com.github.ricardosbarbosa.popularmovies.adapters.MovieTrailerAdapter;
import com.github.ricardosbarbosa.popularmovies.helpers.NetworkUtils;
import com.github.ricardosbarbosa.popularmovies.interfaces.AsyncTaskDelegate;
import com.github.ricardosbarbosa.popularmovies.models.Movie;
import com.github.ricardosbarbosa.popularmovies.models.MovieReview;
import com.github.ricardosbarbosa.popularmovies.models.MovieTrailer;
import com.github.ricardosbarbosa.popularmovies.sync.MovieReviewsService;
import com.github.ricardosbarbosa.popularmovies.sync.MovieTrailersService;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements AsyncTaskDelegate {

    private Movie movie;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(Movie.PARCELABLE_KEY)) {
            movie = (Movie) getArguments().getParcelable(Movie.PARCELABLE_KEY);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(movie.title);
            }

            if (NetworkUtils.isNetworkConnected(getContext())) {
                String idMovie = this.movie.moviedb_id.toString();

                MovieReviewsService movieReviewsService = new MovieReviewsService(getContext(), this);
                movieReviewsService.execute(idMovie);

                MovieTrailersService movieTrailersService = new MovieTrailersService(getContext(), this);
                movieTrailersService.execute(idMovie);
            }
        }
    }

    RecyclerView recyclerViewReviews;
    RecyclerView recyclerViewTrailers;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        if (movie != null) {
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.title);
            ((TextView) rootView.findViewById(R.id.movie_rate)).setText(movie.rating.toString());
            ((TextView) rootView.findViewById(R.id.movie_overview)).setText(movie.overview);
            ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(movie.releaseDate);
            ImageView imageView =((ImageView) rootView.findViewById(R.id.movie_poster));
            Picasso.with(getContext()).load(movie.getFullPosterPath()).into(imageView);

            //reviews
            recyclerViewReviews = (RecyclerView) rootView.findViewById(R.id.listViewReviews);
            recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
            MovieReviewAdapter moviewReviewAdapters = new MovieReviewAdapter(getContext(), movie.getReviews());
            recyclerViewReviews.setAdapter(moviewReviewAdapters);

            //trailes
            recyclerViewTrailers = (RecyclerView) rootView.findViewById(R.id.list_view_trailers);
            recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(getContext()));
            MovieTrailerAdapter movieTrailerAdapter = new MovieTrailerAdapter(getContext(), movie.getTrailers());
            recyclerViewTrailers.setAdapter(movieTrailerAdapter);

        }

        return rootView;
    }


    @Override
    public void processFinish(Object output) {
        if(output != null){

            List list = (List) output;

            if (!list.isEmpty()) {
                if (MovieReview.class.isInstance(list.iterator().next()) ) {
                    List<MovieReview> movieReviews = (List<MovieReview>) output;

                    this.movie.setReviews(movieReviews);
                    MovieReviewAdapter moviewReviewAdapters = (MovieReviewAdapter) recyclerViewReviews.getAdapter();
                    moviewReviewAdapters.swap(movieReviews);
                }

                if (MovieTrailer.class.isInstance(list.iterator().next())) {
                    List<MovieTrailer> movieTrailers = (List<MovieTrailer>) output;

                    this.movie.setTrailers(movieTrailers);
                    MovieTrailerAdapter movieTrailerAdapter = (MovieTrailerAdapter) recyclerViewTrailers.getAdapter();
                    movieTrailerAdapter.swap(movieTrailers);
                }
            }

        }else{
            Toast.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }

    }
}
