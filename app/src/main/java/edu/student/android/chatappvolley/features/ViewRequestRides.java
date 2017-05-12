package edu.student.android.chatappvolley.features;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import edu.student.android.chatappvolley.Point;
import edu.student.android.chatappvolley.R;
import edu.student.android.chatappvolley.UserDetails;
import edu.student.android.chatappvolley.features.Carpool;
import edu.student.android.chatappvolley.kClosestLocations;

import static edu.student.android.chatappvolley.R.id.slat;

/**
 * Created by Gaurav on 10-05-2017.
 */

public class ViewRequestRides extends AppCompatActivity{
    Geocoder gc;
    int count= 0;
    FirebaseDatabase firebaseDatabase;
    List<Address> addresses= null;
    String date,slocation,dlocation;
    ArrayList<Carpool> carpool = new ArrayList<>();
    CarpoolDetailsAdapter carpoolDetailsAdapter;
    ListView listView;
    GoogleMap map;
    Polyline polyline;
    Boolean noRider = false;
    static String TAG = "ViewRequestedRides";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpool_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.list);
        final String email = UserDetails.useremail;
        gc = new Geocoder(this, Locale.US);
        UserDetails.riderDetails = new ArrayList<Point>();
        String viewCarpool = "https://carpoolapp-2ec11.firebaseio.com/accepted/"+email+".json";
        slocation = getLocation(19.1773055,72.8424098);
        Log.v(TAG,"SLocation "+slocation);
        StringRequest request = new StringRequest(Request.Method.GET, viewCarpool, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
             doThis(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(ViewRequestRides.this);
        rQueue.add(request);

    }
   private void doThis(String response) {

        Log.v(TAG,"in do This");
        if (!response.equals("null")) {
            Log.v(TAG,"response is not null");
            try {
                JSONObject obj = new JSONObject(response);
                Iterator<String> iter = obj.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject obj1 = obj.getJSONObject(key);
                        Log.v(TAG,"Id is "+key);
                        String owner = obj1.getString("owner");
                        Log.v(TAG,"Owner is "+owner);
                        String source = obj1.getString("source");
                        Log.v(TAG,"Source "+source);
                        String destination= obj1.getString("destination");
                        Log.v(TAG,"Dest "+destination);
                        String date = obj1.getString("date");
                        Log.v(TAG,"Date "+date);
                        String[] destLatLng = destination.split("\\,");
                        String[] sourceLatLng = source.split("\\,");

                        double slat = Double.parseDouble(sourceLatLng[0]);
                        Log.v(TAG,"slat "+slat);
                        double slon = Double.parseDouble(sourceLatLng[1]);
                        Log.v(TAG,"slon "+slon);
                        double dlat = Double.parseDouble(destLatLng[0]);
                        Log.v(TAG,"dlat "+dlat);
                        double dlon = Double.parseDouble(destLatLng[1]);
                        Log.v(TAG,"dlon "+dlon);
                        slocation = getLocation(slat,slon);
                        Log.v(TAG,"slocation "+slocation);
                        dlocation = getLocation(dlat,dlon);
                        Log.v(TAG,"dlocation "+dlocation);
                        ArrayList<LatLng> rideList = new ArrayList<LatLng>();
                        //Log.v(TAG,"The list is "+key+","+slocation+","+slat+","+date+","+owner);
                        carpool.add(new Carpool(key,slocation,dlocation,date,slat,slon,dlat,dlon,rideList,owner,false   ));
                        //Log.v(TAG,"The owner is "+owner);
                       // StringRequest request1 = new StringRequest(Request.Method.GET,);
                    } catch (JSONException e) {
                        // Something went wrong!
                    }

                }
                carpoolDetailsAdapter = new CarpoolDetailsAdapter(getApplicationContext(),carpool);
                listView.setAdapter(carpoolDetailsAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.v(TAG,"No Requested Rides");
            Toast.makeText(ViewRequestRides.this, "No Requested Rides", Toast.LENGTH_SHORT).show();
            finish();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView r_id = (TextView) view.findViewById(R.id.ride_id);
                int ride_id = Integer.parseInt(r_id.getText().toString());
                TextView slat = (TextView) view.findViewById(R.id.slat);
                double slat1 = Double.parseDouble(slat.getText().toString());
                TextView slong = (TextView) view.findViewById(R.id.slong);
                double slong1 = Double.parseDouble(slong.getText().toString());
                TextView dlat = (TextView) view.findViewById(R.id.dlat);
                double dlat1 = Double.parseDouble(dlat.getText().toString());
                TextView dlong = (TextView) view.findViewById(R.id.dlong);
                double dlong1 = Double.parseDouble(dlong.getText().toString());

                Intent intent= new Intent(ViewRequestRides.this,MapActivity.class);

                // new kClosestLocations();
                Bundle b= new Bundle();
                b.putDouble("slat",slat1);
                b.putDouble("slong",slong1);
                b.putDouble("dlat",dlat1);
                b.putDouble("dlong",dlong1);
                b.putInt("size",2);
                intent.putExtras(b);
                startActivity(intent);

            }
        });
    }

    public String getLocation(double slat, double slon) {
        Log.v(TAG,"in getLocation");

        String s= null;
        try {

            addresses = gc.getFromLocation(slat,slon,15);
            Log.v(TAG,"Addresses"+addresses);
            s= addresses.get(0).getAddressLine(0);
            Log.v(TAG,"Final string "+s);

        } catch (IOException e) {
            e.printStackTrace();

        }
        Log.v(TAG,"s before return "+s);
        return s;
    }

    public static String EncodeString(String string) {
            return string.replace(".", ",");
        }
        public static String DecodeString(String string) {
            return string.replace(",", ".");
        }
}
