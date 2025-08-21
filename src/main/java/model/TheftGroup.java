package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TheftGroup {

    private int location;
    private int number;
    private String date;
    private String time;
    private int category;
    private int value;

    public TheftGroup(String date, int number) {
        this.date = date;
        this.number = number;
    }

    public TheftGroup(int location, int category, int number) {
        this.location = location;
        this.category = category;
        this.number = number;
    }

    public int getLocation() {
        return location;
    }

    public int getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }
    
    public String getTime() {
        return time;
    }

    public int getCategory() {
        return category;
    }

    public int getValue() {
        return value;
    }
}
