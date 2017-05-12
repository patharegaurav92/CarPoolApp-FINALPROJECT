package edu.student.android.chatappvolley;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.databind.util.ViewMatcher;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.label;
import static edu.student.android.chatappvolley.FireObjects.url;

public class ViewProfile extends AppCompatActivity {

LinearLayout linear;
    TextView name,email,ph;
    String uemail;
    Button edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String status  = getIntent().getStringExtra("status");
        System.out.println(getIntent().getStringExtra("status"));
        name = (TextView) findViewById(R.id.viewUserName);
        email = (TextView) findViewById(R.id.viewEmail);
        ph = (TextView) findViewById(R.id.viewPhone);
        linear = (LinearLayout) findViewById(R.id.linear);
        edit = (Button) findViewById(R.id.editProfileButton);
        Firebase.setAndroidContext(this);

        uemail = UserDetails.useremail;
        if(status.equals("1")){
            System.out.println(UserDetails.chatWithEmail);
            edit.setVisibility(View.GONE);
            StringRequest request = new StringRequest(Request.Method.GET, FireObjects.url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if(obj.has(UserDetails.chatWithEmail)){
                            if(obj.getJSONObject(UserDetails.chatWithEmail).getString("private").equals("false")){
                                email.setText(obj.getJSONObject(UserDetails.chatWithEmail).getString("email"));
                                name.setText(obj.getJSONObject(UserDetails.chatWithEmail).getString("name"));
                                ph.setText(obj.getJSONObject(UserDetails.chatWithEmail).getString("phone"));
                            }else{

                            }
                        }else {
                            Toast.makeText(ViewProfile.this, "user not found", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue rQueue = Volley.newRequestQueue(ViewProfile.this);
            rQueue.add(request);
        }else {
            StringRequest request = new StringRequest(Request.Method.GET, FireObjects.url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response.equals(null)) {
                        Toast.makeText(ViewProfile.this, "user not found", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (obj.has(uemail)) {
                                email.setText(DecodeString(uemail));
                                name.setText(obj.getJSONObject(uemail).getString("name"));
                                ph.setText(obj.getJSONObject(uemail).getString("phone"));
                            } else {
                                Toast.makeText(ViewProfile.this, "user not found", Toast.LENGTH_LONG).show();
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
            RequestQueue rQueue = Volley.newRequestQueue(ViewProfile.this);
            rQueue.add(request);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ViewProfile.this, UpdateProfile.class));
                    finish();
                }
            });
        }

    }
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }
    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

}
