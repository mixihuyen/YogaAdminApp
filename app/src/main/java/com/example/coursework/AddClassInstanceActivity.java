package com.example.coursework;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddClassInstanceActivity extends AppCompatActivity {

    private EditText etName, etTeacher, etComments;
    private Button btnSave;
    private int courseId;
    private Spinner spinnerDates;
    private YogaCourse yogaCourse;
    private YogaCourseDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class_instance);

        ImageButton btnBack = findViewById(R.id.btnBack);

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etTeacher = findViewById(R.id.etTeacher);
        etComments = findViewById(R.id.etComments);
        btnSave = findViewById(R.id.btnSave);

        // Initialize DAO
        dao = new YogaCourseDAO(this);

        Intent intent = getIntent();
        courseId = intent.getIntExtra("COURSE_ID", -1);
        yogaCourse = dao.getYogaCourseById(courseId); // Fix: Initialize dao before using it
        spinnerDates = findViewById(R.id.spinnerDates);

        if (yogaCourse == null) {
            Toast.makeText(this, "Error: Course not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        // Lấy danh sách các thứ mà khóa học diễn ra từ yogaCourse (giả sử là getDaysOfWeek())
        List<String> courseDaysOfWeek =  yogaCourse.getDaysOfWeek();

        // Lấy danh sách các ngày hợp lệ từ hôm nay đến 1 năm sau dựa trên các thứ đã chọn
        List<String> validDates = generateValidDates(courseDaysOfWeek);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, validDates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDates.setAdapter(adapter);

        if (courseId == -1) {
            // Handle error when no courseId is provided
            Toast.makeText(this, "Error: No Course ID provided!", Toast.LENGTH_SHORT).show();
            finish(); // Close Activity if no valid courseId is received
        }

        btnBack.setOnClickListener(v -> finish());

        // Set click listener to save the class instance
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String date = spinnerDates.getSelectedItem().toString(); // Get selected date from spinner
            String teacher = etTeacher.getText().toString();
            String comments = etComments.getText().toString();

            if (validateFields(name, date, teacher)) {
                saveClassInstance(courseId, name, date, teacher, comments);
            } else {
                Toast.makeText(this, "Please fill in the required fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateFields(String name, String date, String teacher) {
        return !name.isEmpty() && !date.isEmpty() && !teacher.isEmpty();
    }

    private void saveClassInstance(int courseId, String name, String date, String teacher, String comments) {
        YogaCourseDAO dao = new YogaCourseDAO(this);
        dao.addClassInstance(courseId, name, date, teacher, comments);
        dao.markCourseAsNotSynced(courseId);
        Toast.makeText(this, "Class instance added successfully!", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity after saving
    }

    // Hàm này tạo danh sách các ngày hợp lệ từ hôm nay đến 1 năm sau dựa trên nhiều thứ trong tuần
    private List<String> generateValidDates(List<String> courseDaysOfWeek) {
        List<String> validDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(); // Ngày hiện tại
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Lặp qua các ngày từ hôm nay tới 1 năm sau
        for (int i = 0; i < 365; i++) {
            String dayOfWeek = getDayOfWeek(calendar);

            // Kiểm tra xem ngày hiện tại có phải là một trong những thứ của khóa học không
            if (courseDaysOfWeek.contains(dayOfWeek)) {
                validDates.add(dateFormat.format(calendar.getTime()));
            }
            // Tăng ngày lên 1
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return validDates;
    }

    // Hàm để lấy thứ trong tuần từ Calendar
    private String getDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            default:
                return "";
        }
    }
}
