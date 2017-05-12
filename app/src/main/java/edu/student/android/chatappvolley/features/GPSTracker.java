package edu.student.android.chatappvolley.features;

/**
 * Created by Gaurav on 06-04-2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class GPSTracker extends Service implements LocationListener {

    private  final Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static  final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }
    public Location getLocation() {

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isNetworkEnabled && !isGPSEnabled) {


        } else {

            this.canGetLocation = true;
            if (ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission((Activity)mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, 10);


            }
            if (isNetworkEnabled) {
                Log.v("GAURAVPATHARE","Location Mgr "+locationManager);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.v("GAURAVPATHARE","Location Mgr "+locationManager);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.v("GAURAVPATHARE","Location  "+location);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.v("GAURAVPATHARE","LAT LON  "+latitude+" "+longitude);
                    }
                }
            }
            if (isGPSEnabled) {
                Log.v("GAURAVPATHARE","GPSLocation Mgr "+locationManager);
                Log.v("GAURAVPATHARE","GPSLocation  "+location);
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.v("GAURAVPATHARE","GPSLocation Mgr "+locationManager);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Log.v("GAURAVPATHARE","GPSLocation  "+location);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Log.v("GAURAVPATHARE","LAT LON  "+latitude+" "+longitude);

                        }
                    }
                }
            }


        }

        return location;
    }

    public double getLatitude(){
        if(location!=null){
            latitude = location.getLatitude();
        }
        return latitude;
    }
    public double getLongitude(){
        if(location!=null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }
    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    public void stopUsingGPS(){
        if(locationManager!=null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public void showSettings(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("Please enable GPS. Do you want to go to setting menu");
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(i);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
