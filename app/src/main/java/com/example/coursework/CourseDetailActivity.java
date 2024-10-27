package com.example.coursework;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int ADD_CLASS_REQUEST_CODE = 1001;
    private RecyclerView recyclerViewClass;
    private ScrollView scrollView;

    private TextView tvCourseType, tvCourseDescription, tvCourseDay, tvCourseTime, tvCourseCapacity, tvCourseDuration, tvCoursePrice;
    private ImageView ivCourseImage;
    private Button btnEditCourse, btnPush, btnDeleteCourse;
    private YogaCourseDAO dao;
    private int courseId;
    private String currentImagePath;
    private ImageView ivEditImage;
    private Uri newImageUri;
    private YogaCourse yogaCourse;
    private EditText searchBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        recyclerViewClass = findViewById(R.id.recyclerViewClass);
        searchBar = findViewById(R.id.searchBar);
        scrollView = findViewById(R.id.scrollView);

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
        btnPush = findViewById(R.id.btnPush);
        ImageButton btnBack = findViewById(R.id.btnBack);
        FloatingActionButton btnAddClassInstance = findViewById(R.id.btnAddClassInstance);


        dao = new YogaCourseDAO(this);
        recyclerViewClass.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the course ID passed from the MainActivity
        Intent intent = getIntent();
        if (intent != null) {
            courseId = intent.getIntExtra("COURSE_ID", -1);
            if (courseId != -1) {
                yogaCourse = dao.getYogaCourseById(courseId);
                displayCourseDetails(yogaCourse);
            }
        }
        searchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.post(() -> scrollView.smoothScrollTo(0, searchBar.getTop()));
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì ở đây
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Lọc danh sách khi văn bản thay đổi
                filterClassInstances(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì ở đây
            }
        });


        btnAddClassInstance.setOnClickListener(v -> {
            if (courseId != -1) {
                Intent intentToAddClassInstance = new Intent(CourseDetailActivity.this, AddClassInstanceActivity.class);
                intentToAddClassInstance.putExtra("COURSE_ID", courseId);
                startActivityForResult(intentToAddClassInstance, ADD_CLASS_REQUEST_CODE);
            } else {
                Toast.makeText(CourseDetailActivity.this, "Error: Course ID not found!", Toast.LENGTH_SHORT).show();
            }
        });

        btnPush.setOnClickListener(v -> {
            List<ClassInstance> classInstanceList = dao.getClassInstancesByCourseId(courseId);
            pushCourseAndClassInstancesToFirestore(yogaCourse, classInstanceList);
        });


        // Set click listener for editing the course
        btnEditCourse.setOnClickListener(v -> showEditPopup());

        btnBack.setOnClickListener(v -> finish());

        btnDeleteCourse.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Course")
                    .setMessage("Are you sure you want to delete this course?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("courses")
                                .document(String.valueOf(courseId))
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    dao.deleteYogaCourse(courseId);
                                    setResult(RESULT_OK, new Intent());
                                    Toast.makeText(CourseDetailActivity.this, "Course deleted successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(CourseDetailActivity.this, "Failed to delete from Firestore!", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

    }

    private void displayCourseDetails(YogaCourse yogaCourse) {
        if (yogaCourse != null) {
            tvCourseType.setText(yogaCourse.getType());
            tvCourseDescription.setText(yogaCourse.getDescription());
            tvCourseDay.setText(yogaCourse.getDayOfWeek());
            tvCourseTime.setText(yogaCourse.getTime());
            tvCourseCapacity.setText(String.valueOf(yogaCourse.getCapacity()) + " people");
            tvCourseDuration.setText(String.valueOf(yogaCourse.getDuration()) + " minutes");
            tvCoursePrice.setText("£" + yogaCourse.getPrice());

            TextView syncStatus = findViewById(R.id.tvSyncStatus);
            syncStatus.setText(yogaCourse.isSynced() ? "Synchronized" : "Not synchronized");
            syncStatus.setTextColor(yogaCourse.isSynced() ? Color.GREEN : Color.RED);

            currentImagePath = yogaCourse.getImageUrl();
            loadImage(currentImagePath, ivCourseImage);

            loadClassInstances();
        }
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageLoader imageLoader = Coil.imageLoader(this);
            ImageRequest request = new ImageRequest.Builder(this)
                    .data(imageUrl)
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

        TextView etDayOfWeek = dialogView.findViewById(R.id.etDayOfWeek);
        TextView etTimeOfCourse = dialogView.findViewById(R.id.etTimeOfCourse);
        EditText etCapacity = dialogView.findViewById(R.id.etCapacity);
        EditText etDuration = dialogView.findViewById(R.id.etDuration);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);
        EditText etTypeOfClass = dialogView.findViewById(R.id.etTypeOfClass);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        ivEditImage = dialogView.findViewById(R.id.ivEditImage);
        Button btnChangeImage = dialogView.findViewById(R.id.btnChangeImage);

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

        etTimeOfCourse.setOnClickListener(v -> showTimePickerDialog(etTimeOfCourse));
        etDayOfWeek.setOnClickListener(v -> showDayOfWeekDialog(etDayOfWeek));

        btnChangeImage.setOnClickListener(v -> openFileChooser());

        builder.setPositiveButton("Save", (dialog, which) -> {
            yogaCourse.setDayOfWeek(etDayOfWeek.getText().toString());
            yogaCourse.setTime(etTimeOfCourse.getText().toString());
            yogaCourse.setCapacity(Integer.parseInt(etCapacity.getText().toString()));
            yogaCourse.setDuration(Integer.parseInt(etDuration.getText().toString()));
            yogaCourse.setPrice(Double.parseDouble(etPrice.getText().toString()));
            yogaCourse.setType(etTypeOfClass.getText().toString());
            yogaCourse.setDescription(etDescription.getText().toString());

            if (newImageUri != null) {
                uploadImageToFirebaseStorage(newImageUri);
            } else {

                yogaCourse.setSynced(false);
                dao.updateYogaCourse(yogaCourse);
                displayCourseDetails(yogaCourse);
                Toast.makeText(CourseDetailActivity.this, "Course updated successfully!", Toast.LENGTH_SHORT).show();
            } });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDayOfWeekDialog(TextView etDayOfWeek) {
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        boolean[] selectedDays = new boolean[daysOfWeek.length];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Days of the Week");
        builder.setMultiChoiceItems(daysOfWeek, selectedDays, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedDays[which] = true;
            } else {
                selectedDays[which] = false;
            }
        });
        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder selectedDaysStr = new StringBuilder();
            for (int i = 0; i < selectedDays.length; i++) {
                if (selectedDays[i]) {
                    if (selectedDaysStr.length() > 0) {
                        selectedDaysStr.append(", ");
                    }
                    selectedDaysStr.append(daysOfWeek[i]);
                }
            }
            etDayOfWeek.setText(selectedDaysStr.toString());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("Clear All", (dialog, which) -> etDayOfWeek.setText(""));
        builder.show();
    }

    private void showTimePickerDialog(TextView etTimeOfCourse) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
            etTimeOfCourse.setText(selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
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

        if (requestCode == ADD_CLASS_REQUEST_CODE && resultCode == RESULT_OK) {
            loadClassInstances();
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("course_images/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    yogaCourse.setImageUrl(uri.toString());
                    dao.updateYogaCourse(yogaCourse);
                    displayCourseDetails(yogaCourse);
                    Toast.makeText(this, "Course updated successfully!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(this, "Failed to get image URL!", Toast.LENGTH_SHORT).show())
        ).addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show());
    }

    private void pushCourseAndClassInstancesToFirestore(YogaCourse course, List<ClassInstance> classInstanceList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        DocumentReference courseRef = db.collection("courses").document(String.valueOf(course.getId()));
        batch.set(courseRef, course);

        // Delete instances marked for deletion
        for (ClassInstance instance : instancesToDelete) {
            DocumentReference instanceRef = courseRef.collection("class_instances").document(String.valueOf(instance.getId()));
            batch.delete(instanceRef);
        }

        // Add non-deleted instances to Firestore
        for (ClassInstance classInstance : classInstanceList) {
            if (!instancesToDelete.contains(classInstance)) {
                DocumentReference instanceRef = courseRef.collection("class_instances").document(String.valueOf(classInstance.getId()));
                batch.set(instanceRef, classInstance);
            }
        }

        batch.commit().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Changes pushed to Firestore successfully!", Toast.LENGTH_SHORT).show();
            instancesToDelete.clear(); // Clear deletion list after successful push
            course.setSynced(true);
            dao.updateYogaCourse(course);
            displayCourseDetails(course);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to push changes to Firestore!", Toast.LENGTH_SHORT).show();
        });
    }


    private void loadClassInstances() {
        List<ClassInstance> classInstanceList = dao.getClassInstancesByCourseId(courseId);
        ClassInstanceAdapter classInstanceAdapter = new ClassInstanceAdapter(classInstanceList,
                classInstance -> showEditClassInstancePopup(classInstance), // Sự kiện khi nhấn để chỉnh sửa
                classInstance -> showDeleteConfirmationDialog(classInstance)); // Sự kiện khi nhấn để xóa

        recyclerViewClass.setAdapter(classInstanceAdapter);
    }
    private List<ClassInstance> instancesToDelete = new ArrayList<>();

    // On delete button click, add instance to deletion list and remove from local database
    private void showDeleteConfirmationDialog(ClassInstance classInstance) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Class Instance")
                .setMessage("Are you sure you want to delete this class instance?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    instancesToDelete.add(classInstance);
                    dao.deleteClassInstance(classInstance.getId()); // Delete from local DB immediately
                    dao.markCourseAsNotSynced(courseId); // Mark course to sync again
                    loadClassInstances(); // Refresh UI
                    Toast.makeText(this, "Class instance deleted successfully!", Toast.LENGTH_SHORT).show();
                    yogaCourse = dao.getYogaCourseById(courseId);
                    displayCourseDetails(yogaCourse);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }






    private void showEditClassInstancePopup(ClassInstance classInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_class_instance, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etName);
        Spinner spinnerDates = dialogView.findViewById(R.id.spinnerDates); // Sử dụng Spinner thay cho TextView
        EditText etTeacher = dialogView.findViewById(R.id.etTeacher);
        EditText etComments = dialogView.findViewById(R.id.etComments);

        // Đặt giá trị ban đầu
        etName.setText(classInstance.getName());
        etTeacher.setText(classInstance.getTeacher());
        etComments.setText(classInstance.getComments());

        // Lấy danh sách các thứ mà khóa học diễn ra từ yogaCourse (giả sử là getDaysOfWeek())
        List<String> courseDaysOfWeek =  yogaCourse.getDaysOfWeek();

        // Lấy danh sách các ngày hợp lệ từ hôm nay đến 1 năm sau dựa trên các thứ đã chọn
        List<String> validDates = generateValidDates(courseDaysOfWeek);

        // Thiết lập adapter cho Spinner để hiển thị các ngày
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, validDates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDates.setAdapter(adapter);

        // Đặt ngày hiện tại của class instance làm mặc định trong Spinner
        int selectedDatePosition = validDates.indexOf(classInstance.getDate());
        if (selectedDatePosition != -1) {
            spinnerDates.setSelection(selectedDatePosition);
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Cập nhật lại giá trị sau khi người dùng chỉnh sửa
            classInstance.setName(etName.getText().toString());
            classInstance.setDate(spinnerDates.getSelectedItem().toString()); // Lấy giá trị ngày từ Spinner
            classInstance.setTeacher(etTeacher.getText().toString());
            classInstance.setComments(etComments.getText().toString());

            // Lưu thay đổi vào cơ sở dữ liệu
            dao.updateClassInstance(classInstance);
            dao.markCourseAsNotSynced(courseId);
            yogaCourse = dao.getYogaCourseById(courseId);
            displayCourseDetails(yogaCourse);
            loadClassInstances();



            Toast.makeText(CourseDetailActivity.this, "Class instance updated successfully!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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




    private void filterClassInstances(String query) {
        List<ClassInstance> classInstanceList = dao.getClassInstancesByCourseId(courseId);
        List<ClassInstance> filteredList = new ArrayList<>();

        for (ClassInstance classInstance : classInstanceList) {
            // Kiểm tra xem query có nằm trong tên lớp hoặc tên giáo viên không
            if (classInstance.getName().toLowerCase().contains(query.toLowerCase()) ||
                    classInstance.getTeacher().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(classInstance);
            }
        }

        // Cập nhật adapter của RecyclerView với danh sách đã lọc
        ClassInstanceAdapter classInstanceAdapter = new ClassInstanceAdapter(filteredList,
                classInstance -> showEditClassInstancePopup(classInstance), // Sự kiện chỉnh sửa
                classInstance -> showDeleteConfirmationDialog(classInstance)); // Sự kiện xóa

        recyclerViewClass.setAdapter(classInstanceAdapter);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        yogaCourse = dao.getYogaCourseById(courseId);
        displayCourseDetails(yogaCourse);
        loadClassInstances();
    }
}