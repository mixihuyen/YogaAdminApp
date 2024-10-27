package com.example.coursework;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.AlertDialog;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddCourseActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView etDayOfWeek, etTimeOfCourse;
    private EditText etCapacity, etDuration, etPrice, etTypeOfClass, etDescription;
    private Button btnSubmit, btnSelectImage;
    private ImageView ivSelectedImage;
    private Uri imageUri;
    private YogaCourseDAO dao;

    private String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private boolean[] selectedDays = new boolean[daysOfWeek.length];
    private ArrayList<String> selectedDayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        ImageButton btnBack = findViewById(R.id.btnBack);
        etDayOfWeek = findViewById(R.id.etDayOfWeek);
        etTimeOfCourse = findViewById(R.id.etTimeOfCourse);
        etCapacity = findViewById(R.id.etCapacity);
        etDuration = findViewById(R.id.etDuration);
        etPrice = findViewById(R.id.etPrice);
        etTypeOfClass = findViewById(R.id.etTypeOfCourse);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);

        etCapacity.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        dao = new YogaCourseDAO(this);

        etDayOfWeek.setOnClickListener(v -> showDayOfWeekDialog());
        etTimeOfCourse.setOnClickListener(v -> showTimePickerDialog());
        btnSelectImage.setOnClickListener(v -> openFileChooser());
        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {
            if (validateFields()) {
                if (imageUri != null) {
                    uploadImageToFirebaseStorage();
                } else {
                    saveCourseData(null);
                }
            }
        });
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
            etTimeOfCourse.setText(selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void showDayOfWeekDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Days of the Week");

        builder.setMultiChoiceItems(daysOfWeek, selectedDays, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedDayList.add(daysOfWeek[which]);
            } else {
                selectedDayList.remove(daysOfWeek[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> etDayOfWeek.setText(TextUtils.join(", ", selectedDayList)));

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("Clear All", (dialog, which) -> {
            Arrays.fill(selectedDays, false);
            selectedDayList.clear();
            etDayOfWeek.setText("");
        });

        builder.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                ivSelectedImage.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("course_images/" + System.currentTimeMillis() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveCourseData(imageUrl); // Lưu thông tin khóa học với URL ảnh
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCourseData(@Nullable String imageUrl) {
        try {
            String dayOfWeek = etDayOfWeek.getText().toString();
            String time = etTimeOfCourse.getText().toString();
            int capacity = Integer.parseInt(etCapacity.getText().toString());
            int duration = Integer.parseInt(etDuration.getText().toString());
            double price = Double.parseDouble(etPrice.getText().toString());
            String type = etTypeOfClass.getText().toString();
            String description = etDescription.getText().toString();

            // Lưu khóa học vào cơ sở dữ liệu với URL ảnh từ Firebase Storage
            dao.insertYogaCourse(dayOfWeek, time, capacity, duration, price, type, description, imageUrl);

            Toast.makeText(this, "Course added successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddCourseActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Please enter valid numbers for capacity, duration, and price.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "An unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(etDayOfWeek.getText().toString())) {
            etDayOfWeek.setError("This field is required");
            return false;
        }
        if (TextUtils.isEmpty(etTimeOfCourse.getText().toString())) {
            etTimeOfCourse.setError("This field is required");
            return false;
        }
        if (TextUtils.isEmpty(etCapacity.getText().toString())) {
            etCapacity.setError("This field is required");
            return false;
        }
        if (TextUtils.isEmpty(etDuration.getText().toString())) {
            etDuration.setError("This field is required");
            return false;
        }
        if (TextUtils.isEmpty(etPrice.getText().toString())) {
            etPrice.setError("This field is required");
            return false;
        }
        if (TextUtils.isEmpty(etTypeOfClass.getText().toString())) {
            etTypeOfClass.setError("This field is required");
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close();
    }
}
