package com.example.fitmatch.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class User implements Serializable {
    private String name, email;
    private String birthDate;
    private boolean loseWeight;
    private String id, image, gender, weight, height;
    private String description;
    private double latitude, longitude;


    public User(String name, String email, String birthdate, String weight, String height, boolean loseWeight, String image, String gender, String description, String latitude, String longitude) {
        this.name = name;
        this.email = email;
        this.birthDate = birthdate;
        this.weight = weight;
        this.height = height;
        this.loseWeight = loseWeight;
        this.gender = gender;
        this.image = image;
    }

    public User(String name, String birthDate, String image) {
        this.name = name;
        this.birthDate = birthDate;
        this.image = image;
    }

    public User() {

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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String age) {
        this.birthDate = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
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

    public String getGoal() {
        if (this.loseWeight)
            return "Lose Weight";
        else
            return "Gain Weight";
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

    public String getAge() {
        int age = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = dateFormat.parse(this.birthDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Obtention de la date actuelle
            Calendar currentDate = Calendar.getInstance();

            // Calcul de la différence entre les années
            age = currentDate.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);

            // Vérification si l'anniversaire n'est pas encore passé cette année
            if (currentDate.get(Calendar.MONTH) < calendar.get(Calendar.MONTH) ||
                    (currentDate.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                            currentDate.get(Calendar.DAY_OF_MONTH) < calendar.get(Calendar.DAY_OF_MONTH))) {
                age--; // Décrémentation de l'âge si l'anniversaire n'est pas encore passé
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.valueOf(age);
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
