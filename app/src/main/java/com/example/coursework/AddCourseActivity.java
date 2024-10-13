package com.example.coursework;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddCourseActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etDayOfWeek, etTimeOfCourse, etCapacity, etDuration, etPrice, etTypeOfClass, etDescription;
    private Button btnSubmit, btnSelectImage;
    private ImageView ivSelectedImage;
    private Uri imageUri;
    private YogaCourseDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        ImageButton btnBack = findViewById(R.id.btnBack);

        // Khởi tạo các thành phần giao diện
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

        // Chỉ cho phép nhập số cho Capacity, Duration và Price
        etTimeOfCourse.setInputType(InputType.TYPE_CLASS_NUMBER);
        etCapacity.setInputType(InputType.TYPE_CLASS_NUMBER);
        etDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Khởi tạo DAO để lưu vào local database
        dao = new YogaCourseDAO(this);

        // Xử lý nút chọn ảnh
        btnSelectImage.setOnClickListener(v -> openFileChooser());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng activity hiện tại và quay lại màn hình trước
                finish();
            }
        });

        // Xử lý nút lưu thông tin khóa học
        btnSubmit.setOnClickListener(v -> {
            if (validateFields()) {
                if (imageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                        String imagePath = saveImageToInternalStorage(selectedImage);
                        if (imagePath != null) {
                            saveYogaClassToLocalDatabase(imagePath);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to save image locally", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    saveYogaClassToLocalDatabase(null);
                }
            }
        });
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

    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            String fileName = "yoga_course_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveYogaClassToLocalDatabase(@Nullable String imagePath) {
        try {
            String dayOfWeek = etDayOfWeek.getText().toString();
            String time = etTimeOfCourse.getText().toString();
            int capacity = Integer.parseInt(etCapacity.getText().toString());
            int duration = Integer.parseInt(etDuration.getText().toString());
            double price = Double.parseDouble(etPrice.getText().toString());
            String type = etTypeOfClass.getText().toString();
            String description = etDescription.getText().toString();

            // Lưu khóa học vào cơ sở dữ liệu
            dao.insertYogaCourse(dayOfWeek, time, capacity, duration, price, type, description, imagePath);

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
