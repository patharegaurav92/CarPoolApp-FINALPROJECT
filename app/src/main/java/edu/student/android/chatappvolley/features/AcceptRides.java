package edu.student.android.chatappvolley.features;

import android.app.ProgressDialog;
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
import com.firebase.client.Firebase;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;

import edu.student.android.chatappvolley.FireObjects;
import edu.student.android.chatappvolley.Home;
import edu.student.android.chatappvolley.PendingActivity;
import edu.student.android.chatappvolley.R;
import edu.student.android.chatappvolley.UserDetails;
import edu.student.android.chatappvolley.kClosestLocations;

import static edu.student.android.chatappvolley.R.id.slat;

/**
 * Created by Chinmay Deshpande on 5/8/2017.
 */

public class AcceptRides extends AppCompatActivity {
    ListView listView;
    String TAG="Gaurav";
    ProgressDialog progress;
    CarpoolDetailsAdapter carpoolDetailsAdapter;
    boolean accepted= false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        final LatLng request_source = bundle.getParcelable("request_source");
        final LatLng request_dest = bundle.getParcelable("request_dest");
        Log.v(TAG,"The obj is "+FireObjects.rcarpool);
        listView = (ListView) findViewById(R.id.rlist);
        carpoolDetailsAdapter = new CarpoolDetailsAdapter(getApplicationContext(), FireObjects.rcarpool);
        listView.setAdapter(carpoolDetailsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                progress = ProgressDialog.show(AcceptRides.this, "Please wait...",
                        "", true);
                TextView id = (TextView) view.findViewById(R.id.ride_id);
                final int ride_id = Integer.parseInt(id.getText().toString());
                TextView date1 = (TextView) view.findViewById(R.id.date);
                final String date = date1.getText().toString();
                TextView slat = (TextView) view.findViewById(R.id.slat);
                double slat1 = Double.parseDouble(slat.getText().toString());
                TextView slong = (TextView) view.findViewById(R.id.slong);
                double slong1 = Double.parseDouble(slong.getText().toString());
                TextView dlat = (TextView) view.findViewById(R.id.dlat);
                double dlat1 = Double.parseDouble(dlat.getText().toString());
                TextView dlong = (TextView) view.findViewById(R.id.dlong);
                double dlong1 = Double.parseDouble(dlong.getText().toString());
             //   Toast.makeText(AcceptRides.this, "Ride id is "+ride_id, Toast.LENGTH_SHORT).show();
                String getEmail = "https://carpoolapp-2ec11.firebaseio.com/request/"+ride_id+".json";
                String checkIfRidePresent = "https://carpoolapp-2ec11.firebaseio.com/accepted/"+UserDetails.useremail+".json";
                StringRequest request1 = new StringRequest(Request.Method.GET, checkIfRidePresent, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equals("null")){
                            Log.v(TAG,"Response is not null");
                        try {
                            Log.v(TAG,"helloo");
                            JSONObject ownObj = new JSONObject(response);
                           // Log.v(TAG,"Obj id : "+ownObj.getString("id"));
                            Log.v(TAG,"ride_id : "+ride_id);
                            Iterator<String> iter = ownObj.keys();
                            boolean checkIfRideIDPresent =false;
                            while(iter.hasNext()) {
                                JSONObject obj = ownObj.getJSONObject(iter.next());
                                if (Integer.parseInt(obj.getString("id")) == ride_id) {
                                    Log.v(TAG, "ID matches");
                                    checkIfRideIDPresent = true;
                                }
                            }
                            if(checkIfRideIDPresent){
                                accepted =true;
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
                RequestQueue rQueue = Volley.newRequestQueue(AcceptRides.this);
                rQueue.add(request1);
               final StringRequest request = new StringRequest(Request.Method.GET, getEmail, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            Log.v(TAG,"ride can be accepted");
                            JSONObject ownObj = new JSONObject(response);
                            String owner = ownObj.getString("owner");
                            String rsource = request_source.latitude+","+request_source.longitude;
                            String rdest = request_dest.latitude+","+request_dest.longitude;
                           /* Firebase obj = new Firebase("https://carpoolapp-2ec11.firebaseio.com/own/"+owner+"/"+ride_id);
                                    obj.child("riders").child(UserDetails.useremail).child("source").setValue(rsource);
                                    obj.child("riders").child(UserDetails.useremail).child("destination").setValue(rdest);
                            */
                           Firebase obj1 =new Firebase("https://carpoolapp-2ec11.firebaseio.com/own/"+owner+"/"+ride_id+"/pending_requests");
                            obj1.child(UserDetails.useremail).child("source").setValue(rsource);
                            obj1.child(UserDetails.useremail).child("destination").setValue(rdest);
                             /*       obj1.child(UserDetails.useremail).child(String.valueOf(ride_id)).child("id").setValue(ride_id);
                                    obj1.child(UserDetails.useremail).child(String.valueOf(ride_id)).child("owner").setValue(owner);
                                   obj1.child(UserDetails.useremail).child(String.valueOf(ride_id)).child("destination").setValue(rdest);
                                   obj1.child(UserDetails.useremail).child(String.valueOf(ride_id)).child("source").setValue(rsource);
                            obj1.child(UserDetails.useremail).child(String.valueOf(ride_id)).child("date").setValue(date);
                            */

                            Toast.makeText(AcceptRides.this, "Request sent to "+owner, Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(AcceptRides.this,Home.class);
                           i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                       }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                final Handler h = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        if(!accepted) {
                            Log.v(TAG,"ride can be accepted");
                            RequestQueue rQueue1 = Volley.newRequestQueue(AcceptRides.this);
                            rQueue1.add(request);
                        }else{
                            progress.dismiss();
                            Toast.makeText(AcceptRides.this, "You are already in this carpool!", Toast.LENGTH_SHORT).show();
                            accepted=false;
                            Intent i = new Intent(AcceptRides.this,Home.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }
                    }
                };
                h.sendMessageDelayed(new Message(), 2000);

                /*Intent intent= new Intent(ViewYourCarpool.this,MapActivity.class);

                Bundle b= new Bundle();
                b.putDouble("slat",slat1);
                b.putDouble("slong",slong1);
                b.putDouble("dlat",dlat1);
                b.putDouble("dlong",dlong1);

                intent.putExtras(b);

                startActivity(intent);*/
                //Toast.makeText(getApplicationContext(),slat1+"\n" + slong1 + "\n" + dlat1 + "\n" + dlong1,Toast.LENGTH_LONG).show();
                // polyline = map.addPolyline(new PolylineOptions().add(new LatLng(slat1,slong1),new LatLng(dlat1,dlong1))
                /// .width(5).color(Color.RED)
                //);
            }
        });
    }
}
