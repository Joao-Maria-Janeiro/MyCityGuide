package com.cityguide.joaomjaneiro.cityguide.PointsOfInterest;

public class Point {
    private String title;
    private String description;

    public Point(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Point(){
        //Empty Constructor
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
