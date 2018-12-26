package org.abondar.experimental.locationtracker.util;

public enum PermissionCodes {
    UNDEFINED(-1),
    LOCATION(24);

    private Integer code;

    PermissionCodes(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static PermissionCodes byCode(final int code){


        for (PermissionCodes codes: values()){
            if (codes.code==code){
              return codes;
            }
        }

        return UNDEFINED;
    }
}
