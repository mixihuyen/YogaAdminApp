package com.example.coursework;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ListCourseActivity extends AppCompatActivity {

    private ListView listView;
    private YogaClassDAO dao;
    private List<YogaCourse> yogaClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_courses);

        listView = findViewById(R.id.listViewYogaCourse);
        dao = new YogaClassDAO(this);
        yogaClasses = dao.getAllYogaClasses();

        ArrayAdapter<YogaCourse> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, yogaClasses);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YogaCourse selectedClass = yogaClasses.get(position);
                dao.deleteYogaClass(selectedClass.getId());
                Toast.makeText(ListCourseActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                yogaClasses.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close();
    }
}

