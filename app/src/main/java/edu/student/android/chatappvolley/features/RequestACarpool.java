package edu.student.android.chatappvolley.features;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import edu.student.android.chatappvolley.FireObjects;
import edu.student.android.chatappvolley.R;
import edu.student.android.chatappvolley.UserDetails;

/**
 * Created by Chinmay Deshpande on 5/7/2017.
 */

public class RequestACarpool extends AppCompatActivity implements OnMapReadyCallback {
    static String  TAG="RequestingACarpool";
    int count= 0;
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
    String slocation,dlocation;
    List<Address> addresses = null;
    static ArrayList<LatLng> riderList= new ArrayList<>();
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
    String test="";
    //ArrayList<Carpool> rcarpool;
  //  ArrayList<Carpool> rcarpool = new ArrayList<>();

    ListView listView;

    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    LinearLayout r;

    private GoogleMap mMap;
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_acarpool);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.list);
      FireObjects.rcarpool = new ArrayList<Carpool>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        mMap = googleMap;
        selectDest = (Button) findViewById(R.id.selectdest1);
        r = (LinearLayout) findViewById(R.id.relativeLayout1);
        submit = (Button) findViewById(R.id.submit1);
        mapFragment.getMapAsync(this);
        gc = new Geocoder(this, Locale.US);
        gps=new GPSTracker(RequestACarpool.this);
        sourcelatitude = gps.getLatitude();
        sourcelongitude = gps.getLongitude();
        destinationlatitude = gps.getLatitude();
        destinationlongitude = gps.getLongitude();
        Double s = gps.getLatitude();
        if(s.equals(0.0)){

            Toast.makeText(this, "Please check your network. Unable to trace your location", Toast.LENGTH_SHORT).show();
            finish();
        }
        dp = (Button) findViewById(R.id.dp1);
        setLoc(sourcelatitude,sourcelongitude);
        setdestLoc(destinationlatitude,destinationlongitude);
        d=(EditText) findViewById(R.id.date11);
        sourcebuttonselect = (Button) findViewById(R.id.sourcebuttonloc1);
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
                test = "source";
                //sourcebuttonselect.setVisibility(View.VISIBLE);
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
                        gps=new GPSTracker(RequestACarpool.this);
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
           //     selectDest.setVisibility(View.VISIBLE);
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
                        gps=new GPSTracker(RequestACarpool.this);
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
                //final String email = UserDetails.useremail;
                //Log.v(TAG,"Email id is "+email);
                queryDatabase(sourcelatitude,sourcelongitude,destinationlatitude,destinationlongitude);


            }
        });




    }

    private boolean queryDatabase(final double sourcelatitude, final double sourcelongitude, final double destinationlatitude, final double destinationlongitude) {

            StringRequest request = new StringRequest(Request.Method.GET, FireObjects.ownCarpool, new Response.Listener<String>() {
                final String email = UserDetails.useremail;

                @Override
                public void onResponse(String response) {
                    Firebase ref = FireObjects.ownCarpoolRef;
                    Firebase requestRef = FireObjects.requestCarpoolRef;
                    System.out.println(response);
                    if (!response.equals("null")) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Iterator<String> iter = obj.keys();
                            while (iter.hasNext()) {
                                final String otherEmail = iter.next();
                                if (!otherEmail.equals(email)) {
                                    JSONObject ownObj = (JSONObject)obj.get(otherEmail);
                                    if (ownObj != null) {
                                        Iterator<String> otherIter = ownObj.keys();
                                        while (otherIter.hasNext()) {
                                            String id = otherIter.next();
                                            JSONObject otherUserDetails = (JSONObject) ownObj.get(id);
                                            Log.v(TAG, "The details for " + otherEmail + " are " + otherUserDetails);
                                            String owner = otherUserDetails.getString("owner");
                                            String date = otherUserDetails.getString("date");
                                            double slat = otherUserDetails.getDouble("slatitude");
                                            double slon = otherUserDetails.getDouble("slongitude");
                                            double dlat = otherUserDetails.getDouble("dlatitude");
                                            double dlon = otherUserDetails.getDouble("dlongitude");
                                                   /*LatLng source = new LatLng(slat,slon);
                                            LatLng destination = new LatLng(dlat,dlon);*/
                                            String ownerSourceLocation = getLocation(slat, slon);
                                            String ownerDestLocation = getLocation(dlat, dlon);
                                            boolean check = checkTolerance(slat, slon, dlat, dlon);
                                            if (check) {
                                                count++;
                                                Log.v(TAG, "This owner " + otherEmail + " is near the requester");

                                                riderList.add(new LatLng(sourcelatitude,sourcelongitude));
                                                riderList.add(new LatLng(destinationlatitude,destinationlongitude));
                                                FireObjects.rcarpool.add(new Carpool(id,ownerSourceLocation, ownerDestLocation, date, slat, slon, dlat, dlon,riderList,owner,false));
                                                Log.v(TAG, "FireObjects " + FireObjects.rcarpool);
                                            } else {
                                                Log.v(TAG, "Check is false");
                                            }
                                        }
                                    }
                                    Intent i = new Intent(RequestACarpool.this, AcceptRides.class);
                                    Bundle args = new Bundle();
                                    args.putParcelable("request_source", new LatLng(sourcelatitude,sourcelongitude));
                                    args.putParcelable("request_dest", new LatLng(destinationlatitude,destinationlongitude));
                                    i.putExtra("bundle", args);
                                    startActivity(i);
                                   /*String emailJson = "https://carpoolapp-2ec11.firebaseio.com/own/" + otherEmail + ".json";
                                    StringRequest request1 = new StringRequest(Request.Method.GET, emailJson, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject ownObj = new JSONObject(response);
                                                //Log.v(TAG,"Details "+ownObj);
                                                if (ownObj != null) {
                                                    Iterator<String> otherIter = ownObj.keys();
                                                    while (otherIter.hasNext()) {
                                                        String id = otherIter.next();
                                                        JSONObject otherUserDetails = (JSONObject) ownObj.get(id);
                                                        Log.v(TAG, "The details for " + otherEmail + " are " + otherUserDetails);
                                                        String owner = otherUserDetails.getString("owner");
                                                        String date = otherUserDetails.getString("date");
                                                        double slat = otherUserDetails.getDouble("slatitude");
                                                        double slon = otherUserDetails.getDouble("slongitude");
                                                        double dlat = otherUserDetails.getDouble("dlatitude");
                                                        double dlon = otherUserDetails.getDouble("dlongitude");
                                                   *//*LatLng source = new LatLng(slat,slon);
                                                   LatLng destination = new LatLng(dlat,dlon);*//*
                                                        String ownerSourceLocation = getLocation(slat, slon);
                                                        String ownerDestLocation = getLocation(dlat, dlon);
                                                        boolean check = checkTolerance(slat, slon, dlat, dlon);
                                                        if (check) {
                                                            count++;
                                                            Log.v(TAG, "This owner " + otherEmail + " is near the requester");
                                                            Firebase requestRef = FireObjects.requestCarpoolRef;

                                                            riderList.add(new LatLng(sourcelatitude,sourcelongitude));
                                                            riderList.add(new LatLng(destinationlatitude,destinationlongitude));
                                                            FireObjects.rcarpool.add(new Carpool(id,ownerSourceLocation, ownerDestLocation, date, slat, slon, dlat, dlon,riderList,owner,false));
                                                            Log.v(TAG, "FireObjects " + FireObjects.rcarpool);
                                                        } else {
                                                            Log.v(TAG, "Check is false");
                                                        }
                                                    }
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if(count<2 && count!=0) {
                                                Intent i = new Intent(RequestACarpool.this, AcceptRides.class);
                                                Bundle args = new Bundle();
                                                args.putParcelable("request_source", new LatLng(sourcelatitude,sourcelongitude));
                                                args.putParcelable("request_dest", new LatLng(destinationlatitude,destinationlongitude));
                                                i.putExtra("bundle", args);
                                                startActivity(i);
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });
                                    RequestQueue rQueue = Volley.newRequestQueue(RequestACarpool.this);
                                    rQueue.add(request1);*/
                                }
                                else{
                                   // Toast.makeText(RequestACarpool.this, "No Rides Available", Toast.LENGTH_SHORT).show();
                                   // finish();
                                }

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
            RequestQueue rQueue = Volley.newRequestQueue(RequestACarpool.this);
            rQueue.add(request);
        return false;
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
    private boolean checkTolerance(double oslat, double oslng, double odlat, double odlng) {
       List<LatLng> list=  getList(oslat,oslng,odlat,odlng);
        LatLng rsource = new LatLng(sourcelatitude,sourcelongitude);
        LatLng rdest = new LatLng(destinationlatitude,destinationlongitude);
        boolean rsFlag =  edu.student.android.chatappvolley.features.PolyUtil.isLocationOnEdge(rsource,list,true,3000);
        boolean rdFlag =  edu.student.android.chatappvolley.features.PolyUtil.isLocationOnEdge(rdest,list,true,3000);
        if(rsFlag && rdFlag){
            return true;
        }else{
            return false;
        }
    }

    private List<LatLng> getList(double oslat, double oslng, double odlat, double odlng) {
        List<LatLng> list = new ArrayList<LatLng>();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + oslat + "," + oslng + "&destination=" + odlat + "," + odlng+ "&mode=driving&sensor=false";
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
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            list = decodePoly(encodedString);
            System.out.println(list);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
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
        destText = (AutoCompleteTextView) findViewById(R.id.destination1);
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
        sourceText= (AutoCompleteTextView)findViewById(R.id.source1);
        sourceText.setThreshold(THRESHOLD);
        sourceText.setAdapter(new GeoAutoCompleteAdapter(this));

        String s = addresses.get(0).getAddressLine(0);
        sourceText.setText(s);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gps=new GPSTracker(RequestACarpool.this);
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




