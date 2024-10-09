package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnAddClass, btnViewClasses, btnSyncWithFirestore;
    private YogaClassDAO dao;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các Button từ giao diện
        btnAddClass = findViewById(R.id.btnAddClass);
        btnViewClasses = findViewById(R.id.btnViewClasses);
        btnSyncWithFirestore = findViewById(R.id.btnSyncWithFirebase);

        // Khởi tạo DAO và Firestore
        dao = new YogaClassDAO(this);
        firestore = FirebaseFirestore.getInstance();

        // Thiết lập sự kiện nhấp chuột cho Button "Thêm lớp học"
        btnAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
                startActivity(intent);
            }
        });

        // Thiết lập sự kiện nhấp chuột cho Button "Xem danh sách lớp học"
        btnViewClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListCourseActivity.class);
                startActivity(intent);
            }
        });

        // Thiết lập sự kiện nhấp chuột cho Button "Đồng bộ với Firestore"
        btnSyncWithFirestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncClassesWithFirestore();
            }
        });
    }

    private void syncClassesWithFirestore() {
        // Lấy danh sách tất cả các lớp học từ SQLite
        List<YogaCourse> yogaClasses = dao.getAllYogaClasses();

        // Kiểm tra nếu không có lớp học nào để đồng bộ
        if (yogaClasses.isEmpty()) {
            Toast.makeText(this, "No classes to sync.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đồng bộ từng lớp học lên Firestore
        for (YogaCourse yogaClass : yogaClasses) {
            Map<String, Object> classData = new HashMap<>();
            classData.put("dayOfWeek", yogaClass.getDayOfWeek());
            classData.put("time", yogaClass.getTime());
            classData.put("capacity", yogaClass.getCapacity());
            classData.put("duration", yogaClass.getDuration());
            classData.put("price", yogaClass.getPrice());
            classData.put("type", yogaClass.getType());
            classData.put("description", yogaClass.getDescription());

            // Thêm lớp học vào Firestore collection "yoga_classes"
            firestore.collection("yoga_classes")
                    .add(classData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(MainActivity.this, "Class synced successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Error syncing class: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close(); // Đóng kết nối với SQLite khi activity bị hủy
    }
}
