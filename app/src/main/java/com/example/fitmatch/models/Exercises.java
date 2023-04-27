package com.example.fitmatch.models;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

public class Exercises implements Serializable {
    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public JSONArray getPrimaryMuscles() {
        return primaryMuscles;
    }

    public void setPrimaryMuscles(JSONArray primaryMuscles) {
        this.primaryMuscles = primaryMuscles;
    }

    public JSONArray getSecondaryMuscles() {
        return secondaryMuscles;
    }

    public void setSecondaryMuscles(JSONArray secondaryMuscles) {
        this.secondaryMuscles = secondaryMuscles;
    }

    public JSONArray getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(JSONArray workoutType) {
        this.workoutType = workoutType;
    }

    private String force, name, type, youtubeLink;
    private JSONArray primaryMuscles, secondaryMuscles, workoutType;

    public Exercises(String force, String name, JSONArray primaryMuscles, JSONArray secondaryMuscles, String type, JSONArray workoutType, String youtubeLink) {
        this.force = force;
        this.name = name;
        this.type = type;
        this.youtubeLink = youtubeLink;
        this.primaryMuscles = primaryMuscles;
        this.secondaryMuscles = secondaryMuscles;
        this.workoutType = workoutType;
    }



 //"Force":"push","Name":"Barbell Bench Press","Primary Muscles":["deltoid","pectoralis major"],"SecondaryMuscles":["triceps"],"Type":"compound","Workout Type":["strength"],"Youtube link":"https:\/\/www.youtube.com\/watch?v=rT7DgCr-3pg&t=29s&ab_channel=ScottHermanFitness"}]

}
