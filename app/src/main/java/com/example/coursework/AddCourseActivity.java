package com.example.coursework;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddCourseActivity extends AppCompatActivity {

    private EditText etDayOfWeek, etTimeOfCourse, etCapacity, etDuration, etPrice, etTypeOfClass, etDescription;
    private Button btnSubmit, btnUpload;
    private YogaClassDAO dao;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        etDayOfWeek = findViewById(R.id.etDayOfWeek);
        etTimeOfCourse = findViewById(R.id.etTimeOfCourse);
        etCapacity = findViewById(R.id.etCapacity);
        etDuration = findViewById(R.id.etDuration);
        etPrice = findViewById(R.id.etPrice);
        etTypeOfClass = findViewById(R.id.etTypeOfClass);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnUpload = findViewById(R.id.btnUpload);

        dao = new YogaClassDAO(this);

        // Khởi tạo Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("yoga_classes");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Lưu thông tin yoga class vào SQLite
                    saveYogaClassToLocalDatabase();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tải dữ liệu lên Firebase
                uploadYogaClassesToFirebase();
            }
        });
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

    private void saveYogaClassToLocalDatabase() {
        String dayOfWeek = etDayOfWeek.getText().toString();
        String time = etTimeOfCourse.getText().toString();
        int capacity = Integer.parseInt(etCapacity.getText().toString());
        int duration = Integer.parseInt(etDuration.getText().toString());
        double price = Double.parseDouble(etPrice.getText().toString());
        String type = etTypeOfClass.getText().toString();
        String description = etDescription.getText().toString();

        dao.insertYogaClass(dayOfWeek, time, capacity, duration, price, type, description);
        dao.close();

        Toast.makeText(AddCourseActivity.this, "Yoga class saved locally!", Toast.LENGTH_LONG).show();
    }

    private void uploadYogaClassesToFirebase() {
        List<YogaCourse> yogaClasses = dao.getAllYogaClasses();

        for (YogaCourse yogaClass : yogaClasses) {
            String key = databaseReference.push().getKey();
            databaseReference.child(key).setValue(yogaClass);
        }

        Toast.makeText(AddCourseActivity.this, "All classes uploaded to Firebase!", Toast.LENGTH_LONG).show();
    }
}
