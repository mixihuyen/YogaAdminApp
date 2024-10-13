package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCourses;
    private EditText searchBar;
    private FloatingActionButton fabAddCourse;
    private YogaCourseDAO dao;
    private List<YogaCourse> yogaCourses;
    private CourseAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewCourses = findViewById(R.id.recyclerViewCourses);
        searchBar = findViewById(R.id.searchBar);
        fabAddCourse = findViewById(R.id.fabAddCourse);

        dao = new YogaCourseDAO(this);
        yogaCourses = dao.getAllYogaCourses();
        // Thiết lập GridLayoutManager với 2 cột
        recyclerViewCourses.setLayoutManager(new GridLayoutManager(this, 2));

        courseAdapter = new CourseAdapter(yogaCourses);
        recyclerViewCourses.setAdapter(courseAdapter);

        loadCourses("");

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadCourses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fabAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
            startActivity(intent);
        });
    }

    private void loadCourses(String filter) {

        if (yogaCourses == null || yogaCourses.isEmpty()) {
            Toast.makeText(this, "Không có khóa học nào", Toast.LENGTH_SHORT).show();
            return;
        }
        List<YogaCourse> filteredCourses = filterCourses(filter);
        courseAdapter.updateCourses(filteredCourses);
    }
    private List<YogaCourse> filterCourses(String filter) {
        // Lọc các khóa học theo từ khóa tìm kiếm
        if (filter.isEmpty()) {
            return yogaCourses;
        }

        List<YogaCourse> filteredList = new ArrayList<>();
        for (YogaCourse course : yogaCourses) {
            if (course.getType().toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(course);
            }
        }
        return filteredList;
    }



    @Override
    protected void onResume() {
        super.onResume();
        yogaCourses = dao.getAllYogaCourses();
        loadCourses("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close();
    }
}
