package com.github.ricardosbarbosa.popularmovies.models;

/**
 * Created by ricardobarbosa on 27/01/17.
 */
public class MovieReview {
    public final String autor;
    public final String content;

    public MovieReview(String autor, String review) {
        this.autor = autor;
        this.content = review;
    }
}
