package com.example.fitmatch.models;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    private String name, email;
    private int age;
    private String photoId;
    private double weight, height;
    private boolean loseWeight;


    public User(int i, String alice, String s, String perdre_du_poids) {
    }

    public User(String name, int age, String photoId) {
        this.name = name;
        this.age = age;
        this.photoId = photoId;
    }

    public String getPhotoId() {
        return photoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean isLoseWeight() {
        return loseWeight;
    }

    public void setLoseWeight(boolean loseWeight) {
        this.loseWeight = loseWeight;
    }
}
