package com.uwi.msbm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.et_registration_no) EditText etRegistrationNo;
    @Bind(R.id.et_password) EditText etPassword;
    @Bind(R.id.btn_login) Button btnLogin;
    @Bind(R.id.layout_coordinator) CoordinatorLayout layoutContainer;
    String id = "";
    String token = "";
    String registrationNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        SessionManager session = new SessionManager(this);

        if (session.isLoggedIn())
            startActivity(new Intent(LoginActivity.this, CourseActivity.class));


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegistrationAndPassword(v);
            }
        });


    }

    public void submitUserId() {
//        http://ourvle.mona.uwi.edu/webservice/rest/server.php?wstoken=e23c35eeda5b1799ffcea51cec0c19b2&wsfunction=core_webservice_get_site_info&moodlewsrestformat=json
        String url = Constants.MOODLE_URL + Constants.WEB_SERVICE;


        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            id = response.getString("userid");

                            if (id.length() != 0) {
                                SessionManager session = new SessionManager(LoginActivity.this);
                                session.createLoginSession(registrationNo, id, token);

                                Snackbar.make(layoutContainer, "token: " + token + " id " + id, Snackbar.LENGTH_SHORT).show();

                                startActivity(new Intent(LoginActivity.this, CourseActivity.class));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("wstoken", token);
                headers.put("wsfunction", "core_webservice_get_site_info");
                headers.put("moodlewsrestformat", "json");

                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    public void submitRegistrationAndPassword(View view) {
        registrationNo = etRegistrationNo.getText().toString();
        final String password = etPassword.getText().toString();

        etPassword.setText("");
        btnLogin.setEnabled(false);

//        "http://ourvle.mona.uwi.edu/login/token.php?username=620065739&password=19941206&service=moodle_mobile_app"

        String url = Constants.MOODLE_URL + Constants.LOGIN + buildQueryParameters(registrationNo , password);

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
//                            response = response.getJSONObject("args");
//                            String site = response.getString("site"),
//                                    network = response.getString("network");
//                            System.out.println("Site: " + site + "\nNetwork: " + network);

                            if(response.has("token")) {
                                token = response.getString("token");
                                Log.d("MOODLE" , token);
                            }else{
                                Log.d("MOODLE" , response.toString());
                                Toast.makeText(getApplicationContext() , "Unexpected Error" , Toast.LENGTH_SHORT);
                            }

                            submitUserId();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        btnLogin.setEnabled(true);
                        Snackbar.make(layoutContainer, "Error", Snackbar.LENGTH_SHORT).show();
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("username", registrationNo);
                headers.put("password", password);
                headers.put("service", "moodle_mobile_app");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);

    }

    private String buildQueryParameters(String username , String password){
       return String.format("?username=%s&password=%s&service=moodle_mobile_app" , username , password);
    }

}
