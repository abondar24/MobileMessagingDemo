package org.abondar.experimental.mobilemessaging.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id",
        "lat",
        "lon",
        "alt"
})
public class LocationData {

    @JsonProperty("id")
    private String deviceId;

    @JsonProperty("lat")
    private String latitude;

    @JsonProperty("lon")
    private String longitude;

    @JsonProperty("alt")
    private String altitude;

    public LocationData(String latitude, String longitude, String altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public LocationData(){}

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "deviceId='" + deviceId + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", altitude='" + altitude + '\'' +
                '}';
    }
}
