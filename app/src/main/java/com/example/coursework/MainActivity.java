package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCourses;
    private EditText searchBar;
    private FloatingActionButton fabAddCourse;
    private YogaCourseDAO dao;
    private List<YogaCourse> yogaCourses;
    private CourseAdapter courseAdapter;
    private static final int COURSE_DETAIL_REQUEST_CODE = 1001; // Định nghĩa COURSE_DETAIL_REQUEST_CODE

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
            startActivityForResult(intent, COURSE_DETAIL_REQUEST_CODE); // Dùng startActivityForResult để nhận kết quả từ AddCourseActivity
        });
    }

    private void loadCourses(String filter) {
        if (yogaCourses == null || yogaCourses.isEmpty()) {
            Toast.makeText(this, "No courses available", Toast.LENGTH_SHORT).show();
            return;
        }

        List<YogaCourse> filteredCourses = filterCourses(filter);
        courseAdapter.updateCourses(filteredCourses);
    }

    private List<YogaCourse> filterCourses(String filter) {
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
        courseAdapter.updateCourses(yogaCourses); // Cập nhật adapter
        courseAdapter.notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật giao diện
        loadCourses("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COURSE_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            yogaCourses = dao.getAllYogaCourses();
            courseAdapter.updateCourses(yogaCourses);
            courseAdapter.notifyDataSetChanged();
            // Làm mới danh sách khóa học từ SQLite
            loadCourses("");
        }
    }
}
