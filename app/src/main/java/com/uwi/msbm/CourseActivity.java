package com.uwi.msbm;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.uwi.msbm.models.Course;
import com.uwi.msbm.utility.QueryUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CourseActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.rv_courses) RecyclerView rvCourses;
    List<Course> courses;

    DBHelper dbCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        dbCourse = new DBHelper(this);
        courses = new ArrayList<>();


        try {
            courses = dbCourse.getAllCourses();
        }catch (SQLiteException e){
            Log.d("MOODLE" , "No database");
        }

        if (courses.size() == 0){
            SessionManager session = new SessionManager(this);
            HashMap<String, String> user = session.getUserDetails();

            // async, update courses in a callback instead
            courses = requestCourses(
                    getIntent().getStringExtra(SessionManager.KEY_USER_ID),
                    getIntent().getStringExtra(SessionManager.KEY_TOKEN));
        }

    }

    private List<Course> requestCourses(final String userId, final String token) {
//        http://ourvle.mona.uwi.edu/webservice/rest/server.php?wstoken=e23c35eeda5b1799ffcea51cec0c19b2&wsfunction=core_enrol_get_users_courses&moodlewsrestformat=json&userid=9742
        if(token != null)
            Log.d("Token" , token);
        else
            Log.d("Token" , "Null Token");

        String url = QueryUtilities.buildUrl(Constants.MOODLE_URL , Constants.WEB_SERVICE , "wstoken" , token , "wsfunction" , "core_enrol_get_users_courses" , "moodlewsrestformat","json" , "userid" , userId);
        Log.d("URL" , url);
        final List<Course> courses = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("COURSE RESPONSE" , response.toString());

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject json = (JSONObject) response.get(i);

                                Course course = new Course();
                                course.setCode(json.getInt("id"));
                                course.setShortName(json.getString("shortname"));
                                course.setFullName(json.getString("fullname"));
                                course.setParticipantCount(json.getInt("enrolledusercount"));

                                courses.add(course);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        setUpCourseList(courses);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("XML" , error.getMessage());
                        Toast.makeText(CourseActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();

                headers.put("wstoken", token);
                headers.put("userid", String.valueOf(userId));
                headers.put("wsfunction", "core_enrol_get_users_courses");
                headers.put("moodlewsrestformat", "json");

                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);

        return courses;
    }

    private void setUpCourseList(List<Course> courses) {
        rvCourses.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvCourses.setLayoutManager(layoutManager);
        CourseAdapter adapter = new CourseAdapter(this , courses);
        rvCourses.setAdapter(adapter);




    }

    public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

        private LayoutInflater mInflater;
        private List<Course> mCourses;

        public CourseAdapter(Context context, List<Course> newCourses) {
            mInflater = LayoutInflater.from(context);
            mCourses = newCourses;
        }

        @Override
        public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_course, parent, false);
            CourseViewHolder viewHolder = new CourseViewHolder(view);


            return viewHolder;
        }

        @Override
        public int getItemCount() {
            return mCourses.size();
        }

        @Override
        public void onBindViewHolder(CourseViewHolder holder, int position) {
            Course currentCourse = mCourses.get(position);

            holder.shortName.setText(currentCourse.getShortName());
            holder.fullName.setText(currentCourse.getFullName());

        }

        public class CourseViewHolder extends RecyclerView.ViewHolder {

            TextView shortName;
            TextView fullName;

            public CourseViewHolder(View itemView) {
                super(itemView);
                shortName = (TextView) itemView.findViewById(R.id.short_name);
                fullName = (TextView) itemView.findViewById(R.id.full_name);
            }
        }
    }
}
