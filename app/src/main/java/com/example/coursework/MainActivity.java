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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayoutCourses;
    private EditText searchBar;
    private FloatingActionButton fabAddCourse;
    private YogaCourseDAO dao;
    private List<YogaCourse> yogaCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayoutCourses = findViewById(R.id.gridLayoutCourses);
        searchBar = findViewById(R.id.searchBar);
        fabAddCourse = findViewById(R.id.fabAddCourse);

        dao = new YogaCourseDAO(this);
        yogaCourses = dao.getAllYogaCourses();

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
        gridLayoutCourses.removeAllViews();

        if (yogaCourses == null || yogaCourses.isEmpty()) {
            Toast.makeText(this, "Không có khóa học nào", Toast.LENGTH_SHORT).show();
            return;
        }

        for (YogaCourse yogaCourse : yogaCourses) {
            if (!yogaCourse.getType().toLowerCase().contains(filter.toLowerCase())) {
                continue;
            }

            LinearLayout courseLayout = new LinearLayout(this);
            courseLayout.setOrientation(LinearLayout.VERTICAL);
            courseLayout.setPadding(16, 16, 16, 16);
            courseLayout.setGravity(Gravity.CENTER);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(16, 16, 16, 16);
            courseLayout.setLayoutParams(params);

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(500, 500);
            imageParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(imageParams);

            if (yogaCourse.getImageUrl() != null && !yogaCourse.getImageUrl().isEmpty()) {
                loadImageWithCoil(imageView, yogaCourse.getImageUrl());
            } else {
                imageView.setImageResource(R.drawable.ic_placeholder);
            }

            TextView titleView = new TextView(this);
            titleView.setText(yogaCourse.getType());
            titleView.setGravity(Gravity.CENTER);
            titleView.setTextSize(18);
            titleView.setTypeface(titleView.getTypeface(), android.graphics.Typeface.BOLD); // Đặt kiểu chữ in đậm
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            titleParams.gravity = Gravity.CENTER;
            titleView.setLayoutParams(titleParams);

            courseLayout.addView(imageView);
            courseLayout.addView(titleView);

            // Set OnClickListener để chuyển đến CourseDetailActivity
            courseLayout.setOnClickListener(v -> openCourseDetail(yogaCourse));

            gridLayoutCourses.addView(courseLayout);
        }
    }

    private void loadImageWithCoil(ImageView imageView, String imagePath) {
        File file = new File(imagePath);
        ImageLoader imageLoader = Coil.imageLoader(this);
        ImageRequest request = new ImageRequest.Builder(this)
                .data(file)
                .target(imageView)
                .placeholder(R.drawable.ic_placeholder)
                .build();
        imageLoader.enqueue(request);
    }

    private void openCourseDetail(YogaCourse yogaCourse) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra("COURSE_ID", yogaCourse.getId());
        startActivity(intent);
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
