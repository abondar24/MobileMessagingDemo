package org.abondar.experimental.locationtracker;

public enum PermissionCodes {
    IMEI(24),
    LOCATION(27);

    private Integer code;

    PermissionCodes(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
