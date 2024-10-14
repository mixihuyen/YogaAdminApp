package com.example.coursework;

public class ClassInstance {
    private int id;
    private String name;
    private String date;
    private String teacher;
    private String comments;

    // Constructor không tham số
    public ClassInstance() {
    }

    // Constructor có tham số
    public ClassInstance(int id, String name, String date, String teacher, String comments) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.teacher = teacher;
        this.comments = comments;
    }

    // Getter và Setter cho từng thuộc tính

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
