package com.github.ricardosbarbosa.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Movies")
public class Movie implements Parcelable{

    public static final String PARCELABLE_KEY = "movie";

    final String baseURL = "http://image.tmdb.org/t/p/";
    final String defaultSize = "w185";

    @Column(name = "moviedb_id") public Integer moviedb_id;
    @Column(name = "overview") public String overview;
    @Column(name = "title") public String title;
    @Column(name = "rating") public Double rating;
    @Column(name = "posterPath") public String posterPath;
    @Column(name = "releaseDate") public String releaseDate;
    @Column(name = "favorite")  public boolean favorite = false;

    private List<MovieReview> reviews;
    private List<MovieTrailer> trailers;

    public Movie(){
        super();
    }
    public Movie(Integer moviedb_id, String overview, String title, Double rating, String posterPath, String releaseDate, boolean favorite) {
        super();
        this.moviedb_id = moviedb_id;
        this.overview = overview;
        this.title = title;
        this.rating = rating;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.reviews = new ArrayList<MovieReview>();
        this.trailers = new ArrayList<MovieTrailer>();
        this.favorite = favorite;
    }

    private Movie(Parcel in){
        super();
        this.moviedb_id = in.readInt();
        this.overview = in.readString();
        this.title = in.readString();
        this.rating = in.readDouble();
        this.posterPath = in.readString();
        this.releaseDate = in.readString();

        this.reviews = new ArrayList<MovieReview>();
        this.trailers = new ArrayList<MovieTrailer>();
    }

    public List<MovieReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<MovieReview> reviews) {
        this.reviews = reviews;
    }

    public List<MovieTrailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<MovieTrailer> trailers) {
        this.trailers = trailers;
    }

    public String getFullPosterPath() {
        return baseURL + defaultSize + posterPath;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(moviedb_id);
        dest.writeString(overview);
        dest.writeString(title);
        dest.writeDouble(rating);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };

}
