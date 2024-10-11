package com.example.coursework;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ListCourseActivity extends AppCompatActivity {

    private GridLayout gridLayoutCourses;
    private YogaCourseDAO dao;
    private List<YogaCourse> yogaCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gridLayoutCourses = findViewById(R.id.gridLayoutCourses);
        dao = new YogaCourseDAO(this);
        yogaCourses = dao.getAllYogaCourses();

        loadCourses();
    }

    private void loadCourses() {
        gridLayoutCourses.removeAllViews(); // Xóa các view cũ nếu có

        // Duyệt qua danh sách các khóa học và thêm từng phần tử vào GridLayout
        for (int i = 0; i < yogaCourses.size(); i++) {
            final YogaCourse yogaCourse = yogaCourses.get(i);

            // Tạo LinearLayout cho từng khóa học
            LinearLayout courseLayout = new LinearLayout(this);
            courseLayout.setOrientation(LinearLayout.VERTICAL);
            courseLayout.setPadding(16, 16, 16, 16);
            courseLayout.setGravity(Gravity.CENTER);
            courseLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            // Tạo ImageView để hiển thị hình ảnh khóa học (có thể dùng placeholder)
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200)); // Kích thước hình ảnh
            imageView.setImageResource(R.drawable.ic_placeholder); // Thay bằng ảnh thực tế nếu có

            // Tạo TextView cho tiêu đề khóa học
            TextView titleView = new TextView(this);
            titleView.setText(yogaCourse.getType());
            titleView.setGravity(Gravity.CENTER);
            titleView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            // Thêm ImageView và TextView vào courseLayout
            courseLayout.addView(imageView);
            courseLayout.addView(titleView);

            // Thêm sự kiện nhấp chuột để mở chi tiết khóa học hoặc xóa
            courseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dao.deleteYogaCourse(yogaCourse.getId());
                    Toast.makeText(ListCourseActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                    yogaCourses.remove(yogaCourse);
                    loadCourses(); // Cập nhật lại GridLayout sau khi xóa
                }
            });

            // Thêm courseLayout vào GridLayout
            gridLayoutCourses.addView(courseLayout);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close();
    }
}
