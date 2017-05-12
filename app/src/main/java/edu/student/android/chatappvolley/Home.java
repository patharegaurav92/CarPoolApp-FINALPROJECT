package edu.student.android.chatappvolley;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import edu.student.android.chatappvolley.features.AboutUsActivity;
import edu.student.android.chatappvolley.features.OwnACarpool;
import edu.student.android.chatappvolley.features.PendingRiders;
import edu.student.android.chatappvolley.features.RequestACarpool;
import edu.student.android.chatappvolley.features.ViewRequestRides;
import edu.student.android.chatappvolley.features.ViewYourCarpool;

import static edu.student.android.chatappvolley.R.id.ride_id;
import static edu.student.android.chatappvolley.UserDetails.riderDetails;

/**
 * Created by Gaurav on 31-03-2017.
 */

public class Home extends AppCompatActivity {
TextView chat,updateProfile,own,view,request,myrequestrides,pending;
    JSONArray rider_details= new JSONArray();
    private static final String TAG = "HomeActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}
        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Enable Network/Location");
            dialog.setPositiveButton("Open Location Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
        UserDetails.pending_rider_details=new JSONArray();
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*chat = (TextView) findViewById(R.id.chat);
        updateProfile = (TextView) findViewById(R.id.updateprofile);*/
        own = (TextView)  findViewById(R.id.ownacarpool);
        view = (TextView) findViewById(R.id.viewcarpool);
        request=(TextView) findViewById(R.id.requestacarpool);
        myrequestrides = (TextView) findViewById(R.id.myrequestedrides);
        pending = (TextView) findViewById(R.id.pending_requests);
        pending.setVisibility(View.GONE);
        /*chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Users.class));
            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this,ViewProfile.class);
                i.putExtra("status","0");
                startActivity(i);
            }
        });*/
        own.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, OwnACarpool.class));
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, ViewYourCarpool.class));
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, RequestACarpool.class));
            }
        });
        myrequestrides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,ViewRequestRides.class));
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetails.pending_rider_details = rider_details;
            startActivity(new Intent(Home.this,PendingActivity.class));
            }
        });
        //new kClosestLocations();
        String url = "https://carpoolapp-2ec11.firebaseio.com/own/"+UserDetails.useremail+".json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals(null)){
                boolean b = false;
                    int  count = 0;
                       // Log.v(TAG,"response is not null");
                    try {
                        JSONObject _id = new JSONObject();
                        JSONObject obj = new JSONObject(response);
                        Iterator<String> iter= obj.keys();
                        while(iter.hasNext()){
                            String ride_id_string = iter.next();
                          //  Log.v(TAG,"Ride id: "+ride_id_string);
                            JSONObject ride_id = obj.getJSONObject(ride_id_string);

                            //Log.v(TAG,""+ride_id);
                            if(ride_id.has("pending_requests")){

                                b= true;
                               // Log.v(TAG,""+ride_id.getJSONObject("pending_requests"));
                                _id.put(ride_id_string,ride_id.getJSONObject("pending_requests"));


                            }else{
                                Log.v(TAG,"No pending requests");
                            }


                        }




                        if(b){
                            rider_details.put(_id);
                            Log.v(TAG,""+rider_details);
                            //  JSONArray pendingRides = UserDetails.pending_rider_details;
                            //Log.v(TAG,"Pending Rides"+pendingRides);
                            JSONObject pRides= rider_details.getJSONObject(0);
                            // Log.v(TAG,"pRides"+pRides);

                            Iterator<String> keys = pRides.keys();
                            while (keys.hasNext()){
                                String ride_id_string = keys.next();
                                JSONObject ride_id = pRides.getJSONObject(ride_id_string);
                                //Log.v(TAG,"riderDetails "+ride_id);
                                Iterator<String> iter2 = ride_id.keys();
                                while(iter2.hasNext()){
                                    count++;
                                    String ride_email_string = iter2.next();
                                    Log.v(TAG,"count "+count);
                                }

                            }

                            pending.setVisibility(View.VISIBLE);
                            pending.setText("Pending Requests("+count+")");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(Home.this);
        rQueue.add(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.menu_list, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.view_profile) {


            Intent i = new Intent(Home.this,ViewProfile.class);
            i.putExtra("status","0");
            startActivity(i);


        }


        if (id == R.id.chat_with_existing_user) {
            Intent intent_chat_with_existing_user = new Intent(Home.this, Users.class);
            startActivity(intent_chat_with_existing_user);
        }
        if (id == R.id.about_us) {
            Intent intent_chat_with_existing_user = new Intent(Home.this, AboutUsActivity.class);
            startActivity(intent_chat_with_existing_user);
        }

        return super.onOptionsItemSelected(item);
    }
}
