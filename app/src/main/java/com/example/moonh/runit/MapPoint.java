package com.example.moonh.runit;

/**
 * Created by moonh on 2017-06-12.
 */

public class MapPoint {
    private String Name;
    private double latitude;
    private double longitude;

    public MapPoint() {
        super();
    }

    public MapPoint(String Name, double latitude, double longitude)
    {
        this.Name = Name;
        this.latitude   = latitude;
        this.longitude = longitude;
    }

    public String getName(){
        return Name;
    }

    public void setName(){
        this.Name = Name;
    }
    public double getlatitude(){
        return latitude;
    }

    public void setlatitude(){
        this.latitude = latitude;
    }
    public double getlongitude(){
        return longitude;
    }

    public void setlongitude(){
        this.longitude = longitude;
    }
}


