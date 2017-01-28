package com.github.ricardosbarbosa.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

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
        }
    }

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
        }

        return rootView;
    }



}
