package edu.student.android.chatappvolley.features;

/**
 * Created by Gaurav on 11-05-2017.
 */

public class PendingRiders {
    public PendingRiders(String ride_id, String user_id, String source, String destination) {
        this.ride_id = ride_id;
        this.user = user_id;
        this.source = source;
        this.destination = destination;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    String ride_id;
    String user;
    String source;
    String destination;

}
