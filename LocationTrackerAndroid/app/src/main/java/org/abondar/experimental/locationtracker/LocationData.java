package org.abondar.experimental.locationtracker;

public class LocationData {
    private String latitiude;
    private String longitude;
    private String altitude;

    public LocationData(String latitiude, String longitude, String altitude) {
        this.latitiude = latitiude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public LocationData(){}

    public String getLatitiude() {
        return latitiude;
    }

    public void setLatitiude(String latitiude) {
        this.latitiude = latitiude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }
}
