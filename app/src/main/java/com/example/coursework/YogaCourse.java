package com.example.coursework;

import java.util.ArrayList;
import java.util.List;

public class YogaCourse {
    private int id;
    private String dayOfWeek;
    private String time;
    private int capacity;
    private int duration;
    private double price;
    private String type;
    private String description;
    private String imageUrl; // Thêm trường ảnh
    private boolean isSynced;

    public YogaCourse(int id, String dayOfWeek, String time, int capacity, int duration, double price, String type, String description, String imageUrl, boolean isSynced) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.type = type;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isSynced = isSynced;
    }

    // Getter và Setter cho tất cả các thuộc tính, bao gồm imageUrl
    public int getId() {
        return id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }
    public List<String> getDaysOfWeek() {
        List<String> selectedDays = new ArrayList<>();
        if (dayOfWeek != null && !dayOfWeek.isEmpty()) {
            String[] daysArray = dayOfWeek.split(", "); // Chia chuỗi thành các phần tử
            for (String day : daysArray) {
                selectedDays.add(day.trim()); // Loại bỏ khoảng trắng
            }
        }
        return selectedDays;
    }


    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return type + " on " + dayOfWeek + " at " + time;
    }

    // Constructor, getters, setters
    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
