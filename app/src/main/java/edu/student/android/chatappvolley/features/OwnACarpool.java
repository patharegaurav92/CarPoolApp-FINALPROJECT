package edu.student.android.chatappvolley.features;

import android.app.DatePickerDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import edu.student.android.chatappvolley.FireObjects;
import edu.student.android.chatappvolley.R;
import edu.student.android.chatappvolley.UserDetails;

import static edu.student.android.chatappvolley.R.id.source;
import static edu.student.android.chatappvolley.R.id.submit;

import java.lang.Object.*;

public class OwnACarpool extends AppCompatActivity implements OnMapReadyCallback {
    double sourcelatitude;
    double sourcelongitude;
    double destinationlatitude;
    double destinationlongitude;
    Button sourcebuttonselect;
    private Integer THRESHOLD = 2;
    GoogleMap googleMap;
    double latitude;
    double longitude;
    double distance;
    List<Address> addresses = null;
    Geocoder gc;
    int day;
    int year;
    int month;
    AutoCompleteTextView sourceText;
    EditText d;
    AutoCompleteTextView  destText;
    Button dp;
    Button selectDest;
    Button submit;
    String test="" ;

    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    LinearLayout r;

    private GoogleMap mMap;
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acarpool);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        mMap = googleMap;
        selectDest = (Button) findViewById(R.id.selectdest);
        r = (LinearLayout) findViewById(R.id.relativeLayout);
        submit = (Button) findViewById(R.id.submit);
        mapFragment.getMapAsync(this);
        gc = new Geocoder(this, Locale.US);
        gps=new GPSTracker(OwnACarpool.this);
        sourcelatitude = gps.getLatitude();
        sourcelongitude = gps.getLongitude();
        destinationlatitude = gps.getLatitude();
        destinationlongitude = gps.getLongitude();
        Double s = gps.getLatitude();
        if(s.equals(0.0)){

            Toast.makeText(this, "Please check your network. Unable to trace your location", Toast.LENGTH_SHORT).show();
            finish();
        }
        dp = (Button) findViewById(R.id.dp);
        setLoc(sourcelatitude,sourcelongitude);
        setdestLoc(destinationlatitude,destinationlongitude);
        d=(EditText) findViewById(R.id.date1);
        sourcebuttonselect = (Button) findViewById(R.id.sourcebuttonloc);
            /*Calendar c =Calendar.getInstance();

            year =  c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);*/
        Calendar newCalendar = Calendar.getInstance();
        year =  newCalendar.get(Calendar.YEAR);
        month = newCalendar.get(Calendar.MONTH);
        day = newCalendar.get(Calendar.DAY_OF_MONTH);

        newCalendar.set(year,month,day);
        d.setText(dateFormatter.format(newCalendar.getTime()));
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDatePickerDialog.show();
            }
        });
        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                d.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        sourceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDest.setVisibility(View.INVISIBLE);
                r.setVisibility(View.VISIBLE);
               // sourcebuttonselect.setVisibility(View.VISIBLE);
                test = "source";
            }
        });
        sourceText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    GeoSearchResults result = (GeoSearchResults) adapterView.getItemAtPosition(i);
                sourceText.setText(result.getAddress());
                String sourceautotext = sourceText.getText().toString();

                try {
                    List<Address> locations = gc.getFromLocationName(sourceautotext,1);
                    for(Address a : locations ) {
                        latitude = a.getLatitude();
                        longitude = a.getLongitude();
                        setLoc(latitude,longitude);
                        gps=new GPSTracker(OwnACarpool.this);
                        LatLng user = new LatLng (latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(user).title("User Marker"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,15));


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        destText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourcebuttonselect.setVisibility(View.INVISIBLE);
                r.setVisibility(View.VISIBLE);
                test = "dest";
              //  selectDest.setVisibility(View.VISIBLE);
            }
        });

        destText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

                GeoSearchResults result = (GeoSearchResults) adapterView.getItemAtPosition(i);
                destText.setText(result.getAddress());
                String sourceautotext = destText.getText().toString();

                try {
                    List<Address> locations = gc.getFromLocationName(sourceautotext,1);
                    for(Address a : locations ) {
                        latitude = a.getLatitude();
                        longitude = a.getLongitude();
                        setdestLoc(latitude,longitude);
                        gps=new GPSTracker(OwnACarpool.this);
                        LatLng user = new LatLng (latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(user).title("User Marker"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,15));

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String date = d.getText().toString();
                final String email = UserDetails.useremail;
                Random random = new Random();
                final int x = random.nextInt(900) + 100;

                //Toast.makeText(getApplicationContext(),sourcelatitude+" "+sourcelongitude+" "+date, Toast.LENGTH_SHORT).show();
                StringRequest request = new StringRequest(Request.Method.GET, FireObjects.ownCarpool, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Firebase ref = FireObjects.ownCarpoolRef;
                        Firebase requestRef = FireObjects.requestCarpoolRef;
                        System.out.println(response);
                        if(response.equals("null")){
                            Firebase b = ref.child(email).child(String.valueOf(x)); // assigns an id for each rcarpool ride made by user
                            b.child("slatitude").setValue(sourcelatitude);
                            b.child("slongitude").setValue(sourcelongitude);
                            b.child("dlatitude").setValue(destinationlatitude);
                            b.child("dlongitude").setValue(destinationlongitude);
                            b.child("date").setValue(date);
                            b.child("owner").setValue(email);
                            requestRef.child(String.valueOf(x)).child("owner").setValue(email);
                            requestRef.child(String.valueOf(x)).child("date").setValue(date);
                            finish();
                        }else{
                            Firebase b = ref.child(email).child(String.valueOf(x)); // assigns an id for each rcarpool ride made by user
                            b.child("slatitude").setValue(sourcelatitude);
                            b.child("slongitude").setValue(sourcelongitude);
                            b.child("dlatitude").setValue(destinationlatitude);
                            b.child("dlongitude").setValue(destinationlongitude);
                            b.child("date").setValue(date);
                            b.child("owner").setValue(email);
                            requestRef.child(String.valueOf(x)).child("owner").setValue(email);
                            requestRef.child(String.valueOf(x)).child("date").setValue(date);
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                RequestQueue rQueue = Volley.newRequestQueue(OwnACarpool.this);
                rQueue.add(request);
                //Toast.makeText(getApplicationContext(),"Source lat is : " + sourcelatitude + "Source long is" + sourcelongitude + "Destination lat is : " + destinationlatitude + "Destination long is" + destinationlongitude,Toast.LENGTH_SHORT).show();
        distance = getDistanceInfo(sourcelatitude, sourcelongitude,destinationlatitude,destinationlongitude);
                System.out.println("Source lat is : " + sourcelatitude + "Source long is" + sourcelongitude + "Destination lat is : " + destinationlatitude + "Destination long is" + destinationlongitude);

                String source= String.valueOf(sourcelatitude);
               Log.v("hi",source);




                ///System.out.println("distance is :" + distance);
                //Toast.makeText(getApplicationContext(),"Distance is : " + distance , Toast.LENGTH_SHORT).show();
            }
        });




    }

    private double getDistanceInfo(double slat, double slng,double dlat, double dlng ) {
        StringBuilder stringBuilder = new StringBuilder();
        Double dist = 0.0;
        try {

            String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + slat + "," + slng + "&destination=" + dlat + "," + dlng+ "&mode=driving&sensor=false";

            HttpPost httppost = new HttpPost(url);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject = new JSONObject(stringBuilder.toString());

            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject distance = steps.getJSONObject("distance");

            Log.i("Distance", distance.toString());
            dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dist;
    }

    public void setdestLoc (double latitude, double longitude){
        destinationlatitude = latitude;
        destinationlongitude = longitude;
        try {
            // addresses.add(gc.getFromLocation(latitude,longitude,1));
            addresses = gc.getFromLocation(latitude,longitude,1);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String s = addresses.get(0).getAddressLine(0);
        destText = (AutoCompleteTextView) findViewById(R.id.destination);
        destText.setThreshold(THRESHOLD);
        destText.setAdapter(new GeoAutoCompleteAdapter(this));

        destText.setText(s);
    }
    public void setLoc (double latitude, double longitude){
        sourcelatitude = latitude;
        sourcelongitude =longitude;
        try {
            // addresses.add(gc.getFromLocation(latitude,longitude,1));
            addresses = gc.getFromLocation(latitude,longitude,1);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        sourceText= (AutoCompleteTextView)findViewById(R.id.source);
        sourceText.setThreshold(THRESHOLD);
        sourceText.setAdapter(new GeoAutoCompleteAdapter(this));

        String s = addresses.get(0).getAddressLine(0);
        sourceText.setText(s);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        gps=new GPSTracker(OwnACarpool.this);
        latitude= gps.getLatitude();
        longitude = gps.getLongitude();
        LatLng mark = new LatLng(sourcelatitude, sourcelongitude);
        mMap.addMarker(new MarkerOptions().position(mark).title("Marker in home location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mark,15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(test.equals("source")){
                    sourcebuttonselect.setVisibility(View.VISIBLE);
                }
                else
                {
                    selectDest.setVisibility(View.VISIBLE);
                }
                latitude = latLng.latitude;
                longitude  = latLng.longitude;
                   /* destinationlatitude = latLng.latitude;
                    destinationlongitude = latLng.longitude;*/
                mMap.clear();
                LatLng sydney = new LatLng(latitude, longitude);
                //LatLng dest = new LatLng(destinationlatitude,destinationlongitude);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in test"));
                //mMap.addMarker(new MarkerOptions().position(dest).title("Marker in destination"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dest,15));
            }
        });

        sourcebuttonselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(latitude+" "+longitude);
                setLoc(latitude,longitude);
                r.setVisibility(View.INVISIBLE);
                sourcebuttonselect.setVisibility(View.INVISIBLE);


            }
        });

        selectDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(latitude+" "+longitude);
                setdestLoc(latitude,longitude);
                r.setVisibility(View.INVISIBLE);
                selectDest.setVisibility(View.INVISIBLE);

            }
        });


    }

}
