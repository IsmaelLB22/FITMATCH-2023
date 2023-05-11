package com.example.fitmatch.models;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    private String name, email;
    private String age;
    private double weight, height;
    private boolean loseWeight;
    private String id, image, gender;

    public User(){

    }
    public User(int i, String alice, String s, String perdre_du_poids) {
    }

    public User(String name, String age, String image) {
        this.name = name;
        this.age = age;
        this.image = image;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
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

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getGender() {
        return gender;
    }

    public void setLoseWeight(boolean loseWeight) {
        this.loseWeight = loseWeight;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGender(String gender) {
        this.gender = gender;

    }

    public void setImage(String image) {
        this.image = image;
    }
}
