package edu.student.android.chatappvolley.features;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Gaurav on 07-04-2017.
 */

public class Carpool {

    private String id;
    private String slocation;
    private String dlocation;
    private String date;
    private double dlat;
    private double dlong;
    private double slat;
    private double slong;

    public boolean isRiderExist() {
        return riderExist;
    }

    public void setRiderExist(boolean riderExist) {
        this.riderExist = riderExist;
    }

    private boolean riderExist;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    private String owner;

    public ArrayList<LatLng> getRiderList() {
        return riderList;
    }

    public void setRiderList(ArrayList<LatLng> riderList) {
        this.riderList = riderList;
    }

    private ArrayList<LatLng> riderList;


    public Carpool(String id,String slocation, String dlocation, String date,double slat,double slong,double dlat, double dlong, ArrayList<LatLng> riderList, String owner, boolean riderExist) {
        this.id=id;
       this.riderExist = riderExist;
        this.slocation = slocation;
        this.dlocation = dlocation;
        this.date = date;
        this.slat = slat;
        this.dlat = dlat;
        this.slong = slong;
        this.dlong = dlong;
        this.riderList = riderList;
        this.owner = owner;
        System.out.println("Niyati this.slat is" + this.slat);
    }

    public String getSlocation() {
        return slocation;
    }

    public void setSlocation(String slocation) {
        this.slocation = slocation;
    }

    public String getDlocation() {
        return dlocation;
    }

    public void setDlocation(String dlocation) {
        this.dlocation = dlocation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getslat() { return slat; }
    public double getslong() { return slong; }

    public double getdlat() { return dlat; }
    public double getdlong() { return dlong; }
    public String getId() { return id;}

    public void setId(String id) { this.id = id; }

}
