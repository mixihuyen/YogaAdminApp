package com.example.coursework;

public class YogaCourse {
    private int id;
    private String dayOfWeek;
    private String time;
    private int capacity;
    private int duration;
    private double price;
    private String type;
    private String description;

    public YogaCourse(int id, String dayOfWeek, String time, int capacity, int duration, double price, String type, String description) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.type = type;
        this.description = description;
    }

    // Getter cho id
    public int getId() {
        return id;
    }

    // Getter và setter cho các thuộc tính khác
    public String getDayOfWeek() {
        return dayOfWeek;
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

    // Override phương thức toString() để hiển thị thông tin yoga class
    @Override
    public String toString() {
        return type + " on " + dayOfWeek + " at " + time;
    }
}
