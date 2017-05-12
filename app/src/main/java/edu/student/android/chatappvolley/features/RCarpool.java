package edu.student.android.chatappvolley.features;

/**
 * Created by Gaurav on 07-04-2017.
 */

public class RCarpool {
    private double dlatitude;
    private double dlongitude;
    private double slatitude;
    private double slongitude;
    private String slocation;
    private String dlocation;
    private String date;

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

    public RCarpool(String slocation, String dlocation , String date) {
        this.slocation = slocation;
        this.dlocation = dlocation;
        this.date = date;
    }

    public double getDlatitude() {
        return dlatitude;
    }

    public void setDlatitude(double dlatitude) {
        this.dlatitude = dlatitude;
    }

    public double getDlongitude() {
        return dlongitude;
    }

    public void setDlongitude(double dlongitude) {
        this.dlongitude = dlongitude;
    }

    public double getSlatitude() {
        return slatitude;
    }

    public void setSlatitude(double slatitude) {
        this.slatitude = slatitude;
    }

    public double getSlongitude() {
        return slongitude;
    }

    public void setSlongitude(double slongitude) {
        this.slongitude = slongitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RCarpool() {
    }}
