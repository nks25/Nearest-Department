package com.example.nearest_dept;

import com.google.android.gms.maps.model.LatLng;

public class Place{
    public String name,image,papers_url;
    public LatLng latlng;

    public Place(String name, LatLng latlng, String image, String papers_url) {
        this.name = name;
        this.latlng = latlng;
        this.image = image;
        this.papers_url=papers_url;
    }

    public String getPapers_url() {
        return papers_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public LatLng getLatlng() {
        return latlng;
    }
}