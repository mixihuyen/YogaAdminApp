package com.example.coursework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.adapter.UserAdapter;
import com.example.coursework.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    private RecyclerView recyclerViewUsers;
    private EditText searchBar;
    private FloatingActionButton fabAddCourse;
    private static final int COURSE_DETAIL_REQUEST_CODE = 1001;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        searchBar = findViewById(R.id.searchBar);
        fabAddCourse = findViewById(R.id.fabAddCourse);
        ImageView iconHome = findViewById(R.id.iconHome);
        ImageView iconUser = findViewById(R.id.iconUser);
        ImageView iconOrder = findViewById(R.id.iconOrder);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);

        // Fetch data from Firestore
        fetchUserDataFromFirestore();

        // Set up search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        fabAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, AddCourseActivity.class);
            startActivityForResult(intent, COURSE_DETAIL_REQUEST_CODE);
        });
        iconHome.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            startActivity(intent);
        });
        iconUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, UserActivity.class);
            startActivity(intent);
        });

        //Điều hướng đến trang Order
        iconOrder.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, OrderActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserDataFromFirestore() {
        CollectionReference usersCollection = firestore.collection("users");

        usersCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot value, @NonNull FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(UserActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", error.getMessage());
                    return;
                }

                userList.clear();
                for (QueryDocumentSnapshot document : value) {
                    String email = document.getString("email");
                    String firstName = document.getString("firstName");
                    String lastName = document.getString("lastName");
                    String phoneNumber = document.getString("phoneNumber");

                    User user = new User(email, firstName, lastName, phoneNumber);
                    userList.add(user);
                }

                userAdapter.notifyDataSetChanged();
            }
        });
    }

    private void filter(String query) {
        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getPhoneNumber().contains(query) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        userAdapter.updateList(filteredList);
    }
}
