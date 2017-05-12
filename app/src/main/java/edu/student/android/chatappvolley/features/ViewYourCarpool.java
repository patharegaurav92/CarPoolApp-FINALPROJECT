package edu.student.android.chatappvolley.features;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.firebase.client.Firebase;
import com.firebase.client.snapshot.DoubleNode;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import edu.student.android.chatappvolley.Chat;
import edu.student.android.chatappvolley.FireObjects;
import edu.student.android.chatappvolley.Point;
import edu.student.android.chatappvolley.R;
import edu.student.android.chatappvolley.Register;
import edu.student.android.chatappvolley.UserDetails;
import edu.student.android.chatappvolley.kClosestLocations;

import static edu.student.android.chatappvolley.FireObjects.url;
import static edu.student.android.chatappvolley.R.id.destination;
import static edu.student.android.chatappvolley.R.id.dlat;
import static edu.student.android.chatappvolley.R.id.dlong;
import static edu.student.android.chatappvolley.R.id.slat;
import static edu.student.android.chatappvolley.R.id.slong;
import static edu.student.android.chatappvolley.UserDetails.riderDetails;
import static edu.student.android.chatappvolley.UserDetails.sortedRidersList;

public class ViewYourCarpool extends AppCompatActivity {
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
    static String TAG = "ViewYourCarpool";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpool_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.list);
        final String email = UserDetails.useremail;
        gc = new Geocoder(this, Locale.US);


        /*firebaseDatabase.getInstance().getReference().child("own").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  RCarpool carpoolDetails = snapshot.getValue(RCarpool.class);
                    date = carpoolDetails.getDate();
                    d1ouble slat = carpoolDetails.getSlatitude();
                    double slon = carpoolDetails.getSlongitude();
                   slocation = getLocation(slat,slon);
                    double dlat = carpoolDetails.getDlatitude();
                    double dlon = carpoolDetails.getDlongitude();
                    dlocation = getLocation(dlat,dlon);
                    //System.out.println(date);
                    addToArrayList();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        String viewCarpool = "https://carpoolapp-2ec11.firebaseio.com/own/"+email+".json";
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
        RequestQueue rQueue = Volley.newRequestQueue(ViewYourCarpool.this);
        rQueue.add(request);



    }

    private void doThis(String response) {
        if(!response.equals("null")){
            try {
                JSONObject obj = new JSONObject(response);
                Iterator<String> iter = obj.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = (JSONObject) obj.get(key);
                        boolean flag = value.has("riders");
                        date = value.getString("date");
                        String owner = value.getString("owner");
                        double slat = value.getDouble("slatitude");
                        double slon = value.getDouble("slongitude");
                        slocation = getLocation(slat,slon);
                        double dlat = value.getDouble("dlatitude");
                        double dlon = value.getDouble("dlongitude");
                        dlocation = getLocation(dlat,dlon);
                        System.out.println(slocation+" / "+dlocation+" / "+date);
                        System.out.println("Niyati "  + slat);
                        ArrayList<LatLng> rideList = new ArrayList<LatLng>();
                        carpool.add(new Carpool(key,slocation,dlocation,date,slat,slon,dlat,dlon,rideList,owner,flag));
                    } catch (JSONException e) {
                        // Something went wrong!
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else
        {
            Toast.makeText(ViewYourCarpool.this, "No Rides", Toast.LENGTH_SHORT).show();
        }

       carpoolDetailsAdapter = new CarpoolDetailsAdapter(getApplicationContext(),carpool);
        listView.setAdapter(carpoolDetailsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserDetails.riderDetails = new ArrayList<Point>();
                TextView id = (TextView) view.findViewById(R.id.ride_id);
                int ride_id = Integer.parseInt(id.getText().toString());
                TextView slat = (TextView) view.findViewById(R.id.slat);
                double slat1 = Double.parseDouble(slat.getText().toString());
                TextView slong = (TextView) view.findViewById(R.id.slong);
                double slong1 = Double.parseDouble(slong.getText().toString());
                TextView dlat = (TextView) view.findViewById(R.id.dlat);
                double dlat1 = Double.parseDouble(dlat.getText().toString());
                TextView dlong = (TextView) view.findViewById(R.id.dlong);
                double dlong1 = Double.parseDouble(dlong.getText().toString());
                //Toast.makeText(ViewYourCarpool.this, "Ride id is "+ride_id, Toast.LENGTH_SHORT).show();
                 queryInDataBase(id,slat1,slong1,dlat1,dlong1);

               //Toast.makeText(getApplicationContext(),slat1+"\n" + slong1 + "\n" + dlat1 + "\n" + dlong1,Toast.LENGTH_LONG).show();
               // polyline = map.addPolyline(new PolylineOptions().add(new LatLng(slat1,slong1),new LatLng(dlat1,dlong1))
               /// .width(5).color(Color.RED)
                //);


            }
        });
    }

    private void queryInDataBase(TextView id, final double oslat, final double oslong,final double odlat, final double odlong) {
        Log.v(TAG,"In QueryInDatabase");
       final String u_id= id.getText().toString();
        final LatLng rsource,rdest;
        UserDetails.riderDetails.add(new Point(oslat,oslong,"Owners Source"));
        UserDetails.riderDetails.add(new Point(odlat,odlong,"Owners Destination"));
        Log.v(TAG, "Source "+oslat+","+oslong+" Dest "+odlat+","+odlong);
        String url = "https://carpoolapp-2ec11.firebaseio.com/own/"+UserDetails.useremail+"/"+u_id+"/riders.json";
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.v(TAG,"Response is "+response);
                if(!response.equals("null")){
                    Log.v(TAG,"Response is not null");
                    try {

                        JSONObject obj = null;

                            obj = new JSONObject(response);

                            final Iterator<String> iter = obj.keys();
                            while (iter.hasNext()) {
                                Log.v(TAG, "iter has next");
                                final String riderEmail = iter.next();
                                Log.v(TAG,""+riderEmail);
                                JSONObject riderDetails = obj.getJSONObject(riderEmail);

                                /*JSONArray riderDetails = obj.getJSONArray(riderEmail);
                                for (int j = 0; j < riderDetails.length(); j++) {
                                Log.v(TAG,""+riderDetails.get(j));

                                }*/
                           String dest = riderDetails.getString("destination");
                            String source = riderDetails.getString("source");
                            String[] destLatLng = dest.split("\\,");
                            String[] sourceLatLng = source.split("\\,");

                            Log.v(TAG,"the details are "+destLatLng[0] +" "+destLatLng[1]+" "+sourceLatLng[0]+" "+sourceLatLng[1]);
                            LatLng rsource,rdest;

                            rsource = new LatLng(Double.parseDouble(sourceLatLng[0]), Double.parseDouble(sourceLatLng[1]));
                            rdest = new LatLng(Double.parseDouble(destLatLng[0]), Double.parseDouble(destLatLng[1]));
                            count++;
                            UserDetails.riderDetails.add(new Point(rsource.latitude,rsource.longitude,DecodeString1(riderEmail)+"'s Source"));
                            UserDetails.riderDetails.add(new Point(rdest.latitude,rdest.longitude,DecodeString1(riderEmail)+"'s Destination"));


                            }


                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                        Intent intent= new Intent(ViewYourCarpool.this,MapActivity.class);
                        Log.v(TAG, "The Size is " + UserDetails.riderDetails.size());
                        new kClosestLocations();
                        // System.out.println(UserDetails.sortedRidersList);
                        Bundle b= new Bundle();
                        b.putDouble("slat",oslat);
                        b.putDouble("slong",oslong);
                        b.putDouble("dlat",odlat);
                        b.putDouble("dlong",odlong);
                        b.putInt("size",UserDetails.sortedRidersList.size()+2);
                        intent.putExtra("sortedRiderList",UserDetails.sortedRidersList);
                        intent.putExtras(b);
                        startActivity(intent);

                }else{
                    Intent intent= new Intent(ViewYourCarpool.this,MapActivity.class);

                   // new kClosestLocations();
                    Bundle b= new Bundle();
                    b.putDouble("slat",oslat);
                    b.putDouble("slong",oslong);
                    b.putDouble("dlat",odlat);
                    b.putDouble("dlong",odlong);
                    b.putInt("size",2);
                    intent.putExtras(b);
                    startActivity(intent);
                    Log.v(TAG,"No Riders");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(ViewYourCarpool.this);
        rQueue.add(request);


     /*   Log.v(TAG,"count is "+count);*/
    }


    public String getLocation(double lat, double lon){


        String s= null;
        try {

            addresses = gc.getFromLocation(lat,lon,15);
          s= addresses.get(0).getAddressLine(0);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;
    }
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }
    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }
    public static String DecodeString1(String string) {
        String[] stringTest = string.split("@");
        return stringTest[0];
    }


}
