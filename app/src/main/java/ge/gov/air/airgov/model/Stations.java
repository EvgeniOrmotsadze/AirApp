package ge.gov.air.airgov.model;

import com.google.gson.annotations.SerializedName;

public class Stations {

    @SerializedName("id")
    private Long id;

    @SerializedName("lat")
    private double lat;

    @SerializedName("long")
    private double longs;

    @SerializedName("representativeness_distance")
    private String representDistance;

    private float distanceInMeter;

    public Stations(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongs() {
        return longs;
    }

    public void setLongs(double longs) {
        this.longs = longs;
    }

    public String getRepresentDistance() {
        return representDistance;
    }

    public void setRepresentDistance(String representDistance) {
        this.representDistance = representDistance;
    }

    public float getDistanceInMeter() {
        return distanceInMeter;
    }

    public void setDistanceInMeter(float distanceInMeter) {
        this.distanceInMeter = distanceInMeter;
    }

    @Override
    public String toString() {
        return "Stations{" +
                "id=" + id +
                ", lat=" + lat +
                ", longs=" + longs +
                ", representDistance='" + representDistance + '\'' +
                ", distanceInMeter=" + distanceInMeter +
                '}';
    }
}
