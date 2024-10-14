package com.example.coursework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClassInstanceAdapter extends RecyclerView.Adapter<ClassInstanceAdapter.ClassInstanceViewHolder> {

    private List<ClassInstance> classInstanceList;
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;

    // Interface for handling click events
    public interface OnItemClickListener {
        void onItemClick(ClassInstance classInstance);
    }

    // Interface for handling delete click events
    public interface OnDeleteClickListener {
        void onDeleteClick(ClassInstance classInstance);
    }

    // Constructor that takes both classInstanceList and listeners for click and delete
    public ClassInstanceAdapter(List<ClassInstance> classInstanceList, OnItemClickListener listener, OnDeleteClickListener deleteListener) {
        this.classInstanceList = classInstanceList;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ClassInstanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_instance, parent, false);
        return new ClassInstanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClassInstanceViewHolder holder, int position) {
        ClassInstance classInstance = classInstanceList.get(position);
        holder.bind(classInstance, listener, deleteListener);
    }
    public void updateData(List<ClassInstance> newClassInstanceList) {
        this.classInstanceList = newClassInstanceList;
        notifyDataSetChanged();  // Gọi để cập nhật RecyclerView
    }

    @Override
    public int getItemCount() {
        return classInstanceList.size();
    }

    public static class ClassInstanceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDate, tvTeacher;
        private ImageButton btnDelete;

        public ClassInstanceViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            btnDelete = itemView.findViewById(R.id.btnDelete); // Nút X để xóa
        }

        public void bind(final ClassInstance classInstance, final OnItemClickListener listener, final OnDeleteClickListener deleteListener) {
            tvName.setText(classInstance.getName());
            tvDate.setText(classInstance.getDate());
            tvTeacher.setText(classInstance.getTeacher());

            // Handle click event for editing class instance
            itemView.setOnClickListener(v -> listener.onItemClick(classInstance));

            // Handle delete button click
            btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(classInstance));
        }
    }

}
