package com.example.coursework;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<YogaCourse> courseList;
    private Context context;

    public CourseAdapter(List<YogaCourse> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        YogaCourse course = courseList.get(position);
        holder.titleView.setText(course.getType());

        if (course.getImageUrl() != null && !course.getImageUrl().isEmpty()) {
            File file = new File(course.getImageUrl());
            ImageLoader imageLoader = Coil.imageLoader(context);
            ImageRequest request = new ImageRequest.Builder(context)
                    .data(file)
                    .target(holder.imageView)
                    .placeholder(R.drawable.image)
                    .build();
            imageLoader.enqueue(request);
        } else {
            holder.imageView.setImageResource(R.drawable.image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseDetailActivity.class);
            intent.putExtra("COURSE_ID", course.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void updateCourses(List<YogaCourse> updatedCourses) {
        this.courseList = updatedCourses;
        notifyDataSetChanged();
    }


    static class CourseViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.courseImage);
            titleView = itemView.findViewById(R.id.courseTitle);
        }
    }
}
