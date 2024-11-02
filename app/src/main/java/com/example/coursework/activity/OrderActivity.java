package com.example.coursework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.adapter.OrderAdapter;
import com.example.coursework.model.Order;
import com.example.coursework.model.CartItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private EditText searchBar;
    private FloatingActionButton fabAddCourse;
    private static final int COURSE_DETAIL_REQUEST_CODE = 1001;
    private FirebaseFirestore db;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        searchBar = findViewById(R.id.searchBar);
        db = FirebaseFirestore.getInstance();
        fabAddCourse = findViewById(R.id.fabAddCourse);
        ImageView iconHome = findViewById(R.id.iconHome);
        ImageView iconUser = findViewById(R.id.iconUser);
        ImageView iconOrder = findViewById(R.id.iconOrder);


        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        recyclerViewOrders.setAdapter(orderAdapter);

        // Load dữ liệu từ Firestore
        loadOrdersFromFirestore();

        // Thêm chức năng tìm kiếm
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        fabAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, AddCourseActivity.class);
            startActivityForResult(intent, COURSE_DETAIL_REQUEST_CODE);
        });
        iconHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, MainActivity.class);
            startActivity(intent);
        });
        iconUser.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, UserActivity.class);
            startActivity(intent);
        });

        //Điều hướng đến trang Order
        iconOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, OrderActivity.class);
            startActivity(intent);
        });
    }

    private void loadOrdersFromFirestore() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("orders")
                .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING) // Sắp xếp theo orderDate giảm dần
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Order order = document.toObject(Order.class);

                                // Đặt ID của tài liệu làm orderId
                                if (order != null) {
                                    order.setOrderId(document.getId());
                                    orderList.add(order);
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            orderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OrderActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });
    }


    private void filterOrders(String query) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : orderList) {
            if (order.getUserData().getName().toLowerCase().contains(query.toLowerCase()) ||
                    order.getUserData().getPhoneNumber().contains(query) ||
                    order.getUserData().getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredOrders.add(order);
            }
        }
        orderAdapter.updateList(filteredOrders);
    }
}
