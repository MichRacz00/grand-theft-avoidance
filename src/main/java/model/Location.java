package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Location {

    private int id;
    private int latitude;
    private int longitude;
    private int thefts;

    public Location() { }

    public Location(int id, int latitude, int longitude, int thefts) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.thefts = thefts;
    }

    public int getId() {
        return id;
    }

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public int getThefts() {
        return thefts;
    }
}
