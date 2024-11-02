// OrderAdapter.java
package com.example.coursework.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.model.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());

        // Format and set the order date
        if (order.getOrderDate() != null) {
            String formattedDate = dateFormat.format(order.getOrderDate());
            holder.orderDateTextView.setText("Order Date: " + formattedDate);
        } else {
            holder.orderDateTextView.setText("Order Date: N/A");
        }

        holder.totalPriceTextView.setText("Total: £" + order.getTotalPrice());

        // Display user information
        if (order.getUserData() != null) {
            holder.userNameTextView.setText("Name: " + order.getUserData().getName());
            holder.userEmailTextView.setText("Email: " + order.getUserData().getEmail());
            holder.userPhoneTextView.setText("Phone: " + order.getUserData().getPhoneNumber());
        }

        // Setup RecyclerView for CartItems
        CartItemAdapter cartItemAdapter = new CartItemAdapter(order.getCartItems());
        holder.cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.cartItemsRecyclerView.setAdapter(cartItemAdapter);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateList(List<Order> filteredOrders) {
        this.orderList = filteredOrders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderIdTextView, orderDateTextView, totalPriceTextView;
        TextView userNameTextView, userEmailTextView, userPhoneTextView;
        RecyclerView cartItemsRecyclerView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userEmailTextView = itemView.findViewById(R.id.userEmailTextView);
            userPhoneTextView = itemView.findViewById(R.id.userPhoneTextView);
            cartItemsRecyclerView = itemView.findViewById(R.id.cartItemsRecyclerView);
        }
    }
}
