package edu.student.android.chatappvolley.features;

import android.location.Address;

/**
 * Created by Gaurav on 07-04-2017.
 */

public class GeoSearchResults {

    private Address address;

    public GeoSearchResults(Address address)
    {
        this.address = address;
    }

    public String getAddress(){

        String display_address = "";

        display_address += address.getAddressLine(0) + "\n";

        for(int i = 1; i < address.getMaxAddressLineIndex(); i++)
        {
            display_address += address.getAddressLine(i) + ", ";
        }

        display_address = display_address.substring(0, display_address.length() - 2);

        return display_address;
    }

    public String toString(){
        String display_address = "";

        if(address.getFeatureName() != null)
        {
            display_address += address + ", ";
        }

        for(int i = 0; i < address.getMaxAddressLineIndex(); i++)
        {
            display_address += address.getAddressLine(i);
        }

        return display_address;
    }

}
