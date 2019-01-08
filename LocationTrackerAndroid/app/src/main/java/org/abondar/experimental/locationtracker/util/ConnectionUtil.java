package org.abondar.experimental.locationtracker.util;

public class ConnectionUtil {
    public static final String STOMP_PORT="61614";
    private static final  String HOST = "192.168.0.24";//"shamanbox-51";
    public static final String STOMP_URI ="ws://"+HOST+":";
    public static final String STOMP_ENDPOINT ="/";
    public static final String STOMP_TOPIC ="stomp.topic";
}
