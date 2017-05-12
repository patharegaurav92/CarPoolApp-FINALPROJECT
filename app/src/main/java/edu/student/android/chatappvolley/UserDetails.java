package edu.student.android.chatappvolley;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gaurav on 31-03-2017.
 */

public class UserDetails {
    public static String useremail = "";
    public static String username = "";
    public static String password = "";
    public static String chatWith = "";
    public static String chatWithEmail = "";
    public static ArrayList<Point> riderDetails;
    public static ArrayList<HashMap<String,LatLng>> sortedRidersList;
    public static double totalDistance;
    public static int count;
    public static JSONArray pending_rider_details;

}
