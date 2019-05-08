package ge.gov.air.airgov;

import android.location.Location;

public class StationModel {

    private String id;
    private String text;
    private String location;
    private Location loc;
    private String color;

    public StationModel(String id) {
        this.id = id;
    }

    public StationModel(String id, String text, String location) {
        this.id = id;
        this.text = text;
        this.location = location;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
