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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uwi.msbm.utility.QueryUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_registration_no)
    EditText etRegistrationNo;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.view_cached_courses)
    Button cacheButton;
    @Bind(R.id.layout_coordinator)
    CoordinatorLayout layoutContainer;
    @Bind(R.id.progressBar)
    ProgressBar bar;
    String id = "";
    String token = "";
    String registrationNo = "";
    HashMap<String, String> parametersHash;

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


        cacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getApplicationContext() , CacheActivity.class));
            }
        });
    }

    public void submitUserId() {
//        http://ourvle.mona.uwi.edu/webservice/rest/server.php?wstoken=e23c35eeda5b1799ffcea51cec0c19b2&wsfunction=core_webservice_get_site_info&moodlewsrestformat=json
        String url = QueryUtilities.buildUrl(Constants.MOODLE_URL , Constants.WEB_SERVICE , "wstoken", token, "wsfunction", "core_webservice_get_site_info", "moodlewsrestformat", "json");


        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            id = response.getString("userid");

                            if (!id.isEmpty()) {
                                SessionManager session = new SessionManager(LoginActivity.this);
                                session.createLoginSession(registrationNo, id, token);

                                Snackbar.make(layoutContainer, "token: " + token + " id " + id, Snackbar.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), CourseActivity.class);
                                intent.putExtra(SessionManager.KEY_USER_ID , id);
                                intent.putExtra(SessionManager.KEY_TOKEN , token);
                                Log.d("SENT TOKEN" , token);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
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
        bar.setVisibility(View.VISIBLE);

        //btnLogin.setEnabled(false);

//        "http://ourvle.mona.uwi.edu/login/token.php?username=620065739&password=19941206&service=moodle_mobile_app"



        String url = QueryUtilities.buildUrl(Constants.MOODLE_URL, Constants.LOGIN, "username", registrationNo, "password", password, "service", "moodle_mobile_app");

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

                            if (response.has("token")) {
                                token = response.getString("token");
                                Log.d("MOODLE", token);
                            } else {
                                Log.d("MOODLE", response.toString());
                                Toast.makeText(getApplicationContext(), "Unexpected Error", Toast.LENGTH_SHORT).show();
                            }

                            submitUserId();
                            bar.setVisibility(View.INVISIBLE);
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
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);

    }

}
