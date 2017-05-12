package edu.student.android.chatappvolley;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import edu.student.android.chatappvolley.features.AcceptRides;
import edu.student.android.chatappvolley.features.PendingRiders;
import edu.student.android.chatappvolley.features.PendingRidersAdapter;
import edu.student.android.chatappvolley.features.ViewYourCarpool;

import static edu.student.android.chatappvolley.R.id.email;
import static edu.student.android.chatappvolley.R.id.pending_requests;
import static edu.student.android.chatappvolley.R.id.source;

/**
 * Created by Gaurav on 11-05-2017.
 */

public class PendingActivity extends AppCompatActivity {
    ListView listView;
    public static final String TAG="PendingActivity";
    ArrayList<PendingRiders> pendingRiders = new ArrayList<PendingRiders>();
    PendingRidersAdapter pendingRidersAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.pending_list);
        listView = (ListView) findViewById(R.id.p_list);
        try {
        JSONArray pendingRides = UserDetails.pending_rider_details;
        //Log.v(TAG,"Pending Rides"+pendingRides);
            JSONObject pRides= pendingRides.getJSONObject(0);
           // Log.v(TAG,"pRides"+pRides);

            Iterator<String> keys = pRides.keys();
            while (keys.hasNext()){
                String ride_id_string = keys.next();
                JSONObject ride_id = pRides.getJSONObject(ride_id_string);
                //Log.v(TAG,"riderDetails "+ride_id);
                    Iterator<String> iter2 = ride_id.keys();
                    while(iter2.hasNext()){
                        String ride_email_string = iter2.next();
                        //Log.v(TAG,"Rider email "+ride_email_string);
                        JSONObject riderEmail = ride_id.getJSONObject(ride_email_string);
                        //Log.v(TAG,"Rider Email Obj"+riderEmail);
                        String source = riderEmail.getString("source");
                        String destination = riderEmail.getString("destination");
                        Log.v(TAG,"Details are ID: "+ride_id_string+" Email: "+ride_email_string+" Source:"+source+" Destination: "+destination);
                        pendingRiders.add(new PendingRiders(ride_id_string,DecodeString(ride_email_string),source,destination));
                    }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pendingRidersAdapter= new PendingRidersAdapter(getApplicationContext(), pendingRiders);
        listView.setAdapter(pendingRidersAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                new AlertDialog.Builder(PendingActivity.this)
                        .setTitle("Are you sure you want to accept?")
                        .setMessage("This Rider will be added to your carpool!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG,"Yes");
                                acceptRider(view,position);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG,"No");
                                rejectRider(view,position);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void rejectRider(View view, int position) {
        TextView id = (TextView) view.findViewById(R.id.p_ride_id);
        final int ride_id = Integer.parseInt(id.getText().toString());
        TextView email=(TextView) view.findViewById(R.id.p_user_id);
        final String rider_email = email.getText().toString();
        TextView s=(TextView) view.findViewById(R.id.p_source);
        final String source =s.getText().toString();
        String so[] = source.split("\\,");
        double rslat = Double.parseDouble(so[0]);
        double rslong= Double.parseDouble(so[1]);
        TextView d=(TextView) view.findViewById(R.id.p_dest);
        final String dest = d.getText().toString();
        String de[] = dest.split("\\,");
        double rdlat = Double.parseDouble(de[0]);
        double rdlong= Double.parseDouble(de[1]);
        //Toast.makeText(this, "Ride ID: "+ride_id, Toast.LENGTH_SHORT).show();
        String getEmail = "https://carpoolapp-2ec11.firebaseio.com/request/"+ride_id+".json";
        final StringRequest request = new StringRequest(Request.Method.GET, getEmail, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v(TAG,"response is not null");
                if(!response.equals("null")) {
                    try {

                        JSONObject ownObj = new JSONObject(response);
                        String owner = ownObj.getString("owner");
                        String date = ownObj.getString("date");
                        /*Firebase obj = new Firebase("https://carpoolapp-2ec11.firebaseio.com/own/"+owner+"/"+ride_id);
                        obj.child("riders").child(EncodeString(rider_email)).child("source").setValue(source);
                        obj.child("riders").child(EncodeString(rider_email)).child("destination").setValue(dest);
                        Firebase obj1 =new Firebase("https://carpoolapp-2ec11.firebaseio.com/accepted");
                        obj1.child(EncodeString(rider_email)).child(String.valueOf(ride_id)).child("id").setValue(ride_id);
                        obj1.child(EncodeString(rider_email)).child(String.valueOf(ride_id)).child("owner").setValue(owner);
                        obj1.child(EncodeString(rider_email)).child(String.valueOf(ride_id)).child("destination").setValue(dest);
                        obj1.child(EncodeString(rider_email)).child(String.valueOf(ride_id)).child("source").setValue(source);
                        obj1.child(EncodeString(rider_email)).child(String.valueOf(ride_id)).child("date").setValue(date);
                       */
                        Firebase firebase=new Firebase("https://carpoolapp-2ec11.firebaseio.com/own/"+owner+"/"+ride_id+"/"+"pending_requests");
                        firebase.child(EncodeString(rider_email)).removeValue();
                        final Handler h = new Handler() {
                            @Override
                            public void handleMessage(Message message) {
                                Intent i = new Intent(PendingActivity.this,Home.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                        };
                        h.sendMessageDelayed(new Message(), 2000);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.v(TAG,"response is null");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(PendingActivity.this);
        rQueue.add(request);
    }

    private void acceptRider(View view ,int position) {
        TextView id = (TextView) view.findViewById(R.id.p_ride_id);
        final int ride_id = Integer.parseInt(id.getText().toString());
        TextView email=(TextView) view.findViewById(R.id.p_user_id);
        final String rider_email = email.getText().toString();
        TextView s=(TextView) view.findViewById(R.id.p_source);
        final String source =s.getText().toString();
        String so[] = source.split("\\,");
        double rslat = Double.parseDouble(so[0]);
        double rslong= Double.parseDouble(so[1]);
        TextView d=(TextView) view.findViewById(R.id.p_dest);
        final String dest = d.getText().toString();
        String de[] = dest.split("\\,");
        double rdlat = Double.parseDouble(de[0]);
        double rdlong= Double.parseDouble(de[1]);
        //Toast.makeText(this, "Ride ID: "+ride_id, Toast.LENGTH_SHORT).show();
        String getEmail = "https://carpoolapp-2ec11.firebaseio.com/request/"+ride_id+".json";
        final StringRequest request = new StringRequest(Request.Method.GET, getEmail, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v(TAG,"response is not null");
                if(!response.equals("null")) {
                    try {

                        JSONObject ownObj = new JSONObject(response);
                        String owner = ownObj.getString("owner");
                        String date = ownObj.getString("date");
                        Firebase obj = new Firebase("https://carpoolapp-2ec11.firebaseio.com/own/"+owner+"/"+ride_id);
                        obj.child("riders").child(EncodeString(rider_email)).child("source").setValue(source);
                        obj.child("riders").child(EncodeString(rider_email)).child("destination").setValue(dest);
                        Firebase obj1 =new Firebase("https://carpoolapp-2ec11.firebaseio.com/accepted");
                        obj1.child(EncodeString(rider_email)).child(String.valueOf(ride_id)).child("id").setValue(ride_id);
                        obj1.child(EncodeString(rider_email)).child(String.valueOf(ride_id)).child("owner").setValue(owner);
                        obj1.child(EncodeString(rider_email)).child(String.valueOf(ride_id)).child("destination").setValue(dest);
                        obj1.child(EncodeString(rider_email)).child(String.valueOf(ride_id)).child("source").setValue(source);
                        obj1.child(EncodeString(rider_email)).child(String.valueOf(ride_id)).child("date").setValue(date);
                        Firebase firebase=new Firebase("https://carpoolapp-2ec11.firebaseio.com/own/"+owner+"/"+ride_id+"/"+"pending_requests");
                        firebase.child(EncodeString(rider_email)).removeValue();
                        final Handler h = new Handler() {
                            @Override
                            public void handleMessage(Message message) {
                                 Intent i = new Intent(PendingActivity.this,Home.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                        };
                        h.sendMessageDelayed(new Message(), 2000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.v(TAG,"response is null");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(PendingActivity.this);
        rQueue.add(request);
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }
    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }
}
