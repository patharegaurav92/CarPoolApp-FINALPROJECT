package edu.student.android.chatappvolley;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import static edu.student.android.chatappvolley.FireObjects.url;

public class Login extends AppCompatActivity {
    TextView register;
    EditText useremail, password;
    Button loginButton;
    String email, pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = (TextView)findViewById(R.id.register);
        useremail = (EditText)findViewById(R.id.useremaillogin);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        Firebase.setAndroidContext(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = useremail.getText().toString();
                pass = password.getText().toString();

                final ProgressDialog pd = new ProgressDialog(Login.this);
                pd.setMessage("Loading...");
                pd.show();

                StringRequest request = new StringRequest(Request.Method.GET, FireObjects.url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        if(s.equals("null")){
                            Toast.makeText(Login.this, "User not found", Toast.LENGTH_LONG).show();
                        }
                        else{
                            try {
                                JSONObject obj = new JSONObject(s);

                                if(!obj.has(EncodeString(email))){
                                    Toast.makeText(Login.this, "User not found", Toast.LENGTH_LONG).show();
                                }
                                else if(obj.getJSONObject(EncodeString(email)).getString("password").equals(pass)){
                                    String user = obj.getJSONObject(EncodeString(email)).getString("name");
                                    System.out.println(user);
                                    UserDetails.username = user;
                                    UserDetails.useremail= EncodeString(email);
                                    UserDetails.password = pass;
                                    startActivity(new Intent(Login.this, Home.class));
                                }
                                else {
                                    Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        pd.dismiss();
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError);
                        pd.dismiss();
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                rQueue.add(request);
            }

        });
    }
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }
    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }
}
