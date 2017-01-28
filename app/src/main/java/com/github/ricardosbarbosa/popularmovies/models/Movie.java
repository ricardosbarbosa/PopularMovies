package com.github.ricardosbarbosa.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Movie implements Parcelable{

    public static final String PARCELABLE_KEY = "movie";

    final String baseURL = "http://image.tmdb.org/t/p/";
    final String defaultSize = "w185";

    public final Integer id;
    public final String overview;
    public final String title;
    public final Double rating;
    public final String posterPath;
    public final String releaseDate;

    private List<MovieReview> reviews;

    public Movie(Integer id, String overview, String title, Double rating, String posterPath, String releaseDate) {
        this.id = id;
        this.overview = overview;
        this.title = title;
        this.rating = rating;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in){
        this.id = in.readInt();
        this.overview = in.readString();
        this.title = in.readString();
        this.rating = in.readDouble();
        this.posterPath = in.readString();
        this.releaseDate = in.readString();

        this.reviews = new ArrayList<MovieReview>();
    }

    public List<MovieReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<MovieReview> reviews) {
        this.reviews = reviews;
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
        dest.writeInt(id);
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
