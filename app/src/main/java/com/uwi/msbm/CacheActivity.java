package com.uwi.msbm;

import android.content.Context;
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

import com.uwi.msbm.models.Course;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Travis on 26/11/2015.
 */
public class CacheActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_courses)
    RecyclerView rvCourses;
    List<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        courses = new ArrayList<>();
        readCourses();

    }

    private void readCourses(){
        File f = new File(getBaseContext().getFilesDir(), "courses.json");
        FileInputStream fis = null;
        String input = "";
        try{
            int content;
            fis = new FileInputStream(f);
            while((content = fis.read()) != -1){
                input += (char)content;
            }
            Log.d("FILE READ" , input);
        }catch (IOException e){
            Log.d("FILE READ", e.getMessage());

        }finally {
            try {
                if (fis != null)
                    fis.close();
            }catch (IOException e) {
                Log.d("STREAM CLOSE", e.getMessage());
            }
        }
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
