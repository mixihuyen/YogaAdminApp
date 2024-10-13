package com.example.coursework;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CourseDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView tvCourseType, tvCourseDescription, tvCourseDay, tvCourseTime, tvCourseCapacity, tvCourseDuration, tvCoursePrice;
    private ImageView ivCourseImage;
    private Button btnEditCourse, btnBack, btnDeleteCourse;
    private YogaCourseDAO dao;
    private int courseId;
    private String currentImagePath;
    private ImageView ivEditImage;
    private Uri newImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // Initialize UI components
        tvCourseType = findViewById(R.id.tvCourseType);
        tvCourseDescription = findViewById(R.id.tvCourseDescription);
        tvCourseDay = findViewById(R.id.tvCourseDay);
        tvCourseTime = findViewById(R.id.tvCourseTime);
        tvCourseCapacity = findViewById(R.id.tvCourseCapacity);
        tvCourseDuration = findViewById(R.id.tvCourseDuration);
        tvCoursePrice = findViewById(R.id.tvCoursePrice);
        ivCourseImage = findViewById(R.id.ivCourseImage);
        btnEditCourse = findViewById(R.id.btnEditCourse);
        btnDeleteCourse = findViewById(R.id.btnDeleteCourse);
        ImageButton btnBack = findViewById(R.id.btnBack);

        dao = new YogaCourseDAO(this);

        // Retrieve the course ID passed from the MainActivity
        Intent intent = getIntent();
        if (intent != null) {
            courseId = intent.getIntExtra("COURSE_ID", -1);
            if (courseId != -1) {
                displayCourseDetails(courseId);
            }
        }

        // Set click listener for editing the course
        btnEditCourse.setOnClickListener(v -> showEditPopup());

        // Back button action
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng activity hiện tại và quay lại màn hình trước
                finish();
            }
        });

        // Delete button action
        btnDeleteCourse.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Course")
                    .setMessage("Are you sure you want to delete this course?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dao.deleteYogaCourse(courseId);
                        Toast.makeText(CourseDetailActivity.this, "Course deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void displayCourseDetails(int courseId) {
        YogaCourse yogaCourse = dao.getYogaCourseById(courseId);

        if (yogaCourse != null) {
            tvCourseType.setText(yogaCourse.getType());
            tvCourseDescription.setText(yogaCourse.getDescription());
            tvCourseDay.setText(yogaCourse.getDayOfWeek());
            tvCourseTime.setText(yogaCourse.getTime());
            tvCourseCapacity.setText(String.valueOf(yogaCourse.getCapacity())+ " people");
            tvCourseDuration.setText(String.valueOf(yogaCourse.getDuration()) + " minutes");
            tvCoursePrice.setText("£" + yogaCourse.getPrice());

            currentImagePath = yogaCourse.getImageUrl();
            loadImage(currentImagePath, ivCourseImage);
        }
    }

    private void loadImage(String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            ImageLoader imageLoader = Coil.imageLoader(this);
            ImageRequest request = new ImageRequest.Builder(this)
                    .data(file)
                    .target(imageView)
                    .placeholder(R.drawable.image)
                    .build();
            imageLoader.enqueue(request);
        } else {
            imageView.setImageResource(R.drawable.image);
        }
    }

    private void showEditPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_course, null);
        builder.setView(dialogView);

        EditText etDayOfWeek = dialogView.findViewById(R.id.etDayOfWeek);
        EditText etTimeOfCourse = dialogView.findViewById(R.id.etTimeOfCourse);
        EditText etCapacity = dialogView.findViewById(R.id.etCapacity);
        EditText etDuration = dialogView.findViewById(R.id.etDuration);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);
        EditText etTypeOfClass = dialogView.findViewById(R.id.etTypeOfClass);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        ivEditImage = dialogView.findViewById(R.id.ivEditImage);
        Button btnChangeImage = dialogView.findViewById(R.id.btnChangeImage);

        YogaCourse yogaCourse = dao.getYogaCourseById(courseId);
        if (yogaCourse != null) {
            etDayOfWeek.setText(yogaCourse.getDayOfWeek());
            etTimeOfCourse.setText(yogaCourse.getTime());
            etCapacity.setText(String.valueOf(yogaCourse.getCapacity()));
            etDuration.setText(String.valueOf(yogaCourse.getDuration()));
            etPrice.setText(String.valueOf(yogaCourse.getPrice()));
            etTypeOfClass.setText(yogaCourse.getType());
            etDescription.setText(yogaCourse.getDescription());

            loadImage(yogaCourse.getImageUrl(), ivEditImage);
        }

        btnChangeImage.setOnClickListener(v -> openFileChooser());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String dayOfWeek = etDayOfWeek.getText().toString();
            String time = etTimeOfCourse.getText().toString();
            int capacity = Integer.parseInt(etCapacity.getText().toString());
            int duration = Integer.parseInt(etDuration.getText().toString());
            double price = Double.parseDouble(etPrice.getText().toString());
            String type = etTypeOfClass.getText().toString();
            String description = etDescription.getText().toString();

            // If a new image is selected, save it
            if (newImageUri != null) {
                currentImagePath = saveImage(newImageUri);
            }

            YogaCourse updatedCourse = new YogaCourse(courseId, dayOfWeek, time, capacity, duration, price, type, description, currentImagePath);
            dao.updateYogaCourse(updatedCourse);

            Toast.makeText(CourseDetailActivity.this, "Course updated successfully!", Toast.LENGTH_SHORT).show();
            displayCourseDetails(courseId);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            newImageUri = data.getData();
            getContentResolver().takePersistableUriPermission(newImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                InputStream inputStream = getContentResolver().openInputStream(newImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                ivEditImage.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            String fileName = "course_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close();
    }
}
