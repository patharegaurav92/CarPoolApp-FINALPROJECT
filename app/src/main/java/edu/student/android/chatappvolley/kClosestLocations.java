package edu.student.android.chatappvolley;

import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

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
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.x;
import static android.R.attr.y;
import static edu.student.android.chatappvolley.UserDetails.riderDetails;

/**
 * Created by Gaurav on 02-04-2017.
 */

class Node{
    protected Point data;
    protected Node next, prev;

    public Node(Point d)
    {
        data = d;

    }
}
class linkedList{
    Node start,end;
    int size;
    public linkedList()
    {
        start = null;
        end = null;
        size = 0;
    }
    private float distanceBetween(Point from, Point to) {
        return (float) Math.sqrt(Math.pow(from.y-to.y, 2) + Math.pow(from.x-to.x, 2));
    }


    public void insertPoint(Point p){
        Node prev =null;
        Node current = start;
        Node prevNode=null,nextNode=null;
        double pDistanceWithCurrent=0;
        double pDistanceWithCurrentsNext=0;
        double minimumDistanceWithCurrent=Float.MAX_VALUE;
        double minimumDistanceWithCurrentsNext=Float.MAX_VALUE;
        double minimumcummuDistance=Float.MAX_VALUE;
        double currentcummuDistance=0;
        double currentDistanceWithCurrentsNext=0;
        String info=null;
        boolean found = false;







        do{
            System.out.println("\nCurrent is "+current.data.x+","+current.data.y);
            pDistanceWithCurrent = distanceBetween(current.data,p);
            pDistanceWithCurrentsNext = distanceBetween(current.next.data,p);
            currentDistanceWithCurrentsNext = distanceBetween(current.data,current.next.data);
            currentcummuDistance = pDistanceWithCurrent + pDistanceWithCurrentsNext;

            System.out.println("Distance between "+current.data.x+","+current.data.y+" and "+p.x+","+p.y+" :- "+ pDistanceWithCurrent);
            System.out.println("Distance between "+current.next.data.x+","+current.next.data.y+" and "+p.x+","+p.y+" :- "+ pDistanceWithCurrentsNext);
            System.out.println("Cummumative Distance "+ currentcummuDistance);
            double distance_p_C_plus_p_CN = pDistanceWithCurrent+pDistanceWithCurrentsNext;
            double distance_p_C_plus_C_CN = pDistanceWithCurrent+currentDistanceWithCurrentsNext;
            double distance_p_CN_plus_C_CN = pDistanceWithCurrentsNext+currentDistanceWithCurrentsNext;
            if(currentcummuDistance<minimumcummuDistance) {


                double distance[] = new double[3];
                distance[0] = distance_p_C_plus_p_CN;
                distance[1] = distance_p_C_plus_C_CN;
                distance[2] = distance_p_CN_plus_C_CN;
                double minDistance = getMinValue(distance);
                if (minDistance == distance_p_C_plus_p_CN) {
                    System.out.println("distance_p_C_plus_p_CN");
                    // if(pDistanceWithCurrent<pDistanceWithCurrentsNext){
                    minimumcummuDistance = currentcummuDistance;

                    prevNode = current;
                    nextNode = current.next;


                } else if (minDistance == distance_p_C_plus_C_CN) {
                    System.out.println("distance_p_C_plus_C_CN");
                    // if(pDistanceWithCurrent<currentDistanceWithCurrentsNext){
                    minimumcummuDistance = currentcummuDistance;
                    if (current == start) {
                        prevNode = current;
                        nextNode = current.next;
                    } else {
                        prevNode = current.prev;
                        nextNode = current;
                    }

                } else if (minDistance == distance_p_CN_plus_C_CN) {
                    System.out.println("distance_p_CN_plus_C_CN");

                    prevNode = current.next;
                    nextNode = current.next.next;

                }


            }
            current = current.next;

        }while(current!=end);


        Node nNode = new Node(p);
        nNode.prev = prevNode;
        nNode.next = nextNode;
        prevNode.next = nNode;
        nextNode.prev = nNode;


        size++;
    }
    public static double getMinValue(double[] array) {
        double minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }
    public void display()
    {
        System.out.print("\nDoubly Linked List = ");

        if (start.next == null)
        {
            System.out.println("("+start.data.x+","+start.data.y+")" );
            return;
        }
        Node ptr = start;

        System.out.print("("+start.data.x+","+start.data.y+")"+ " <-> ");
        ptr = start.next;
        while (ptr.next != null)
        {
            HashMap<String,LatLng> map = new HashMap<String, LatLng>();

            LatLng pt =new LatLng(ptr.data.x,ptr.data.y);
            map.put(ptr.data.label,pt);
            UserDetails.sortedRidersList.add(map);
           // Structures.userPoints.add(new LatLng(ptr.data.x,ptr.data.y));
            System.out.print("("+ptr.data.x+","+ptr.data.y+")"+ " <-> ");
            ptr = ptr.next;
        }
        System.out.print("("+ptr.data.x+","+ptr.data.y+")"+ "\n");
    }
}
public class kClosestLocations {


    public kClosestLocations(){
        //Structures.userPoints = new ArrayList<LatLng>();
        linkedList list = new linkedList();
        Point start = UserDetails.riderDetails.get(0);
        Point end = UserDetails.riderDetails.get(1);
        UserDetails.sortedRidersList = new ArrayList<HashMap<String,LatLng>>();
        /*Point start = new Point(33.783218, -118.113852); // 2001 north beverly plaza
        Point end = new Point(33.940101, -118.089784); // Santa fe high school*/
        list.start = new Node(start);
        list.end = new Node(end);
        list.start.next = list.end;
        list.end.prev = list.start;

        for (int i = 2; i < UserDetails.riderDetails.size(); i++) {
          list.insertPoint(UserDetails.riderDetails.get(i));
        }


        list.display();







    }


}



