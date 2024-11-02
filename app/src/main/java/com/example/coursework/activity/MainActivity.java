package com.example.coursework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.YogaCourseDAO;
import com.example.coursework.adapter.CourseAdapter;
import com.example.coursework.model.YogaCourse;
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
    private static final int COURSE_DETAIL_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewCourses = findViewById(R.id.recyclerViewCourses);
        searchBar = findViewById(R.id.searchBar);
        fabAddCourse = findViewById(R.id.fabAddCourse);
        ImageView iconHome = findViewById(R.id.iconHome);
        ImageView iconUser = findViewById(R.id.iconUser);
        ImageView iconOrder = findViewById(R.id.iconOrder);

        dao = new YogaCourseDAO(this);

        // Đồng bộ dữ liệu Firestore nếu cần thiết và khởi tạo RecyclerView
        dao.syncDataFromFirestoreIfNeeded(() -> {
            yogaCourses = dao.getAllYogaCourses();

            // Thiết lập layout và adapter cho RecyclerView
            setupRecyclerView();
            loadCourses(""); // Hiển thị các khóa học ngay khi đồng bộ hoàn tất
        });

        // Tìm kiếm khóa học khi văn bản thay đổi
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

        // Chuyển sang màn hình thêm khóa học khi nhấn vào nút FAB
        fabAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
            startActivityForResult(intent, COURSE_DETAIL_REQUEST_CODE);
        });
        iconHome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });
        iconUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
        });

         //Điều hướng đến trang Order
        iconOrder.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        // Kiểm tra danh sách khóa học, khởi tạo nếu cần
        if (yogaCourses == null) {
            yogaCourses = new ArrayList<>();
        }

        // Thiết lập RecyclerView với GridLayoutManager
        recyclerViewCourses.setLayoutManager(new GridLayoutManager(this, 2));
        courseAdapter = new CourseAdapter(yogaCourses);
        recyclerViewCourses.setAdapter(courseAdapter);
    }

    // Tải và hiển thị danh sách khóa học dựa trên bộ lọc tìm kiếm
    private void loadCourses(String filter) {
        if (yogaCourses == null || yogaCourses.isEmpty()) {
            Toast.makeText(this, "No courses available", Toast.LENGTH_SHORT).show();
            return;
        }

        List<YogaCourse> filteredCourses = filterCourses(filter);
        if (courseAdapter != null) {
            courseAdapter.updateCourses(filteredCourses);
            courseAdapter.notifyDataSetChanged();
        }
    }

    // Lọc khóa học dựa trên chuỗi tìm kiếm
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
        if (dao != null) {
            yogaCourses = dao.getAllYogaCourses();
            loadCourses("");
        }
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
            loadCourses(""); // Làm mới danh sách khóa học từ SQLite
        }
    }
}
