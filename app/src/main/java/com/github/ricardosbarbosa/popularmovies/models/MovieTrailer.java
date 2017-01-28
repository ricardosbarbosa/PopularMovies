package com.github.ricardosbarbosa.popularmovies.models;

/**
 * Created by ricardobarbosa on 28/01/17.
 */
public class MovieTrailer {

    public final String id;
    public final String key;
    public final String name;
    public final String site;
    public final String type;

    public MovieTrailer(String id, String key, String name, String site, String type) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }
}
