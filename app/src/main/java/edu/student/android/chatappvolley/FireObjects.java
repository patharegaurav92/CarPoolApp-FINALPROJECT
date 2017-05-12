package edu.student.android.chatappvolley;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import edu.student.android.chatappvolley.features.Carpool;

/**
 * Created by Gaurav on 31-03-2017.
 */

public class FireObjects {

    public static Firebase ref = new Firebase("https://carpoolapp-2ec11.firebaseio.com/users");
    public static String url = "https://carpoolapp-2ec11.firebaseio.com/users.json";
    public static String ownCarpool = "https://carpoolapp-2ec11.firebaseio.com/own.json";
    public static Firebase ownCarpoolRef = new Firebase("https://carpoolapp-2ec11.firebaseio.com/own");
    public static String requestCarpool = "https://carpoolapp-2ec11.firebaseio.com/request.json";
    public static Firebase requestCarpoolRef = new Firebase("https://carpoolapp-2ec11.firebaseio.com/request");
    public static ArrayList<Carpool> rcarpool;
}
