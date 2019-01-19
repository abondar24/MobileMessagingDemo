package org.abondar.experimental.messagingclient.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id",
        "pitch",
        "roll",
        "yaw"
})
public class MotionData {

    @JsonProperty("id")
    private String deviceId;

    @JsonProperty("pitch")
    private float pitch;

    @JsonProperty("roll")
    private float roll;

    @JsonProperty("yaw")
    private float yaw;

    public MotionData(float pitch, float roll, float yaw) {
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
    }

    public MotionData(){}

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    public String toString() {
        return "MotionData{" +
                "deviceId='" + deviceId + '\'' +
                ", pitch=" + pitch +
                ", roll=" + roll +
                ", yaw=" + yaw +
                '}';
    }
}
