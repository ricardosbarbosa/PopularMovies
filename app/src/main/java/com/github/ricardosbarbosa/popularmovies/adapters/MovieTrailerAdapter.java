package com.github.ricardosbarbosa.popularmovies.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ricardosbarbosa.popularmovies.R;
import com.github.ricardosbarbosa.popularmovies.models.MovieTrailer;

import java.util.List;

/**
 * Created by ricardobarbosa on 28/01/17.
 */
public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerViewHolder> {

    private final Context mContext;
    private List<MovieTrailer> movieTrailers;

    public MovieTrailerAdapter(Context context, List<MovieTrailer> movieTrailers) {
        this.movieTrailers = movieTrailers;
        this.mContext = context;
    }

    public void swap(List<MovieTrailer> data){
        movieTrailers.clear();
        movieTrailers.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public MovieTrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailer_list_row, null);
        MovieTrailerViewHolder viewHolder = new MovieTrailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieTrailerViewHolder holder, int position) {
        final MovieTrailer movieReview = this.movieTrailers.get(position);

        holder.nameTrailer.setText(movieReview.name);
        holder.imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(mContext,"Exibir "+movieReview.name+" trailer", Toast.LENGTH_SHORT).show();
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + movieReview.key));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + movieReview.key));
                try {
                    mContext.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    mContext.startActivity(webIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.movieTrailers != null ? this.movieTrailers.size() : 0;
    }


    class MovieTrailerViewHolder extends RecyclerView.ViewHolder{
        public final TextView nameTrailer;
        public final ImageButton imageButtonPlay;

        public MovieTrailerViewHolder(View itemView) {
            super(itemView);
            nameTrailer = (TextView) itemView.findViewById(R.id.movie_trailer_name);
            imageButtonPlay = (ImageButton) itemView.findViewById(R.id.movie_trailer_image_button);

        }
    }
}
