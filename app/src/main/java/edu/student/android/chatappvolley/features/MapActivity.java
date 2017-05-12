package edu.student.android.chatappvolley.features;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.student.android.chatappvolley.R;
import edu.student.android.chatappvolley.UserDetails;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double slong;
    private double dlat;
    private double dlong;
    ArrayList<LatLng> markerPoints;
    String Tag="Niyati";
    int n;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
         mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        markerPoints = new ArrayList<LatLng>();
        Bundle b1 = getIntent().getExtras();
        double slat = b1.getDouble("slat");
        dlat=b1.getDouble("dlat");
        slong=b1.getDouble("slong");
        dlong=b1.getDouble("dlong");
        n = b1.getInt("size");
        LatLng rsource = b1.getParcelable("rSource");
        LatLng rdest = b1.getParcelable("rDest");
        double checklat = 19.179086;
        double checklong = 72.842431;
        LatLng newc= new LatLng(checklat, checklong);
        LatLng spoint = new LatLng(slat,slong);
        LatLng dpoint= new LatLng(dlat,dlong);
        ArrayList<HashMap<String,LatLng>> sortedRiderList = (ArrayList<HashMap<String,LatLng>>) getIntent().getSerializableExtra("sortedRiderList");
        Log.v(Tag,"N is " +n);
        Log.v(Tag,"array is " +sortedRiderList);
        addmarkerpoint(spoint,"My Source");
        addmarkerpoint(dpoint,"My Destination");
        if(n>2){
           for(int i=0; i<sortedRiderList.size();i++)
               for(String key : sortedRiderList.get(i).keySet()) {
                   addmarkerpoint(sortedRiderList.get(i).get(key),key);
               }
        }
        /*addmarkerpoint(rsource);
        addmarkerpoint(rdest);*/
        /*
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + slat + "," + slong + "&destination=" + dlat + "," + dlong+ "&mode=driving&sensor=false";
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
        List<LatLng> list = decodePoly(encodedString);
            System.out.println(list);
            Toast.makeText(getApplicationContext(),"Check lat and long is : "+ checklat + "\n" + checklong, Toast.LENGTH_LONG).show();
           boolean f =  edu.student.android.chatappvolley.features.PolyUtil.isLocationOnEdge(newc,list,true,150);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        LatLng user = new LatLng (slat, slong);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,12));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getApplicationContext(),"Distance is: " + UserDetails.totalDistance,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addmarkerpoint(LatLng point,String label) {

        markerPoints.add(point);
        if(markerPoints.size() >= n){
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);
            String url = getDirectionsUrl(origin, dest);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);


        if(markerPoints.size()==1){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            options.title(label);
        }else if(markerPoints.size()==2){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            options.title(label);
        }else{
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            options.title(label);
        }
        mMap.addMarker(options);


    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for(int i=2;i<markerPoints.size();i++){
            LatLng point  = (LatLng) markerPoints.get(i);
            if(i==2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Excep", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.parseColor("#308D8B"));
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);

        }
    }


}