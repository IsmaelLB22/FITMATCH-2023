package com.example.fitmatch.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.fitmatch.R;
import com.example.fitmatch.databinding.ActivityRegisterBinding;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity implements LocationListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActivityRegisterBinding binding;
    private PreferenceManager preferenceManager;
    private Uri imagePath = null;
    private LocationManager locationManager;
    private String address;
    private Address addressManual;
    private int calculatedAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();

    }

    //Permet d'acceder à la page preécedente du flipperview
    public void previousView() {
        binding.viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        binding.viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
        if (binding.pagination.getText().equals("3/3")) {
            binding.registerLayout.setVisibility(View.GONE);
            binding.nextButton.setVisibility(View.VISIBLE);
            binding.title.setText("Personal Data");
            binding.pagination.setText("2/3");
        } else if (binding.pagination.getText().equals("2/3")) {
            binding.backButton.setVisibility(View.GONE);
            binding.pagination.setText("1/3");
            binding.title.setText("Login Data");
        }
        binding.viewFlipper.showPrevious();
    }
    //Permet d'acceder à la page suivante du flipperview

    public void nextView() {
        binding.viewFlipper.setInAnimation(this, R.anim.slide_in_right);
        binding.viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
        if (binding.pagination.getText().equals("1/3")) {
            binding.backButton.setVisibility(View.VISIBLE);
            binding.title.setText("Personal Data");
            binding.pagination.setText("2/3");
        } else if (binding.pagination.getText().equals("2/3")) {
            binding.nextButton.setVisibility(View.GONE);
            binding.registerLayout.setVisibility(View.VISIBLE);
            binding.title.setText("Personal Address");
            binding.pagination.setText("3/3");
        }
        binding.viewFlipper.showNext();
    }

    //Init des listeners
    private void setListeners() {
        binding.registerButton.setOnClickListener(e -> registerNewUser());
        binding.nextButton.setOnClickListener(e -> nextView());
        binding.backButton.setOnClickListener(e -> previousView());
        //Permet de charger une image
        binding.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, 1);
            }
        });


        //permet d'ouvrir un calendrier pour l'insertion de sa date de naissance
        binding.birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        binding.birthDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        Calendar today = Calendar.getInstance();
                        calculatedAge = today.get(Calendar.YEAR) - year;
                        if (today.get(Calendar.DAY_OF_MONTH) < dayOfMonth) {
                            calculatedAge--;
                        }

                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        //Ce bouton permet d'obtenir sa localisation et l'afficher dans les input
        binding.getPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPosition(true);
                if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                }
                getLocation();

            }
        });
    }

    //Localiser l'user
    @SuppressLint("MissingPermission")
    private void getLocation() {
        // LocationManager class provides the facility to get latitude and longitude coordinates of current location
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Profile Picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            getImageInImageView();
        }
    }

    //Chargement de l'image à l'ecran
    private void getImageInImageView() {
        Glide.with(getApplicationContext()).load(imagePath).into(binding.profilePicture);
    }

    //Inscription de l'utilisateur
    private void registerNewUser() {

        if (checkInputData()) { //Verif des donnees
            loading(true);

            GeoPoint geoPoint = getGeoPoint();
            // Creation de l'user à inserer en db
            Map<String, Object> user = new HashMap<>();
            user.put(Constants.KEY_USERNAME, binding.inputUsername.getText().toString());
            user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
            user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
            user.put(Constants.KEY_AGE, binding.birthDate.getText().toString());
            if (binding.female.isChecked()) {
                user.put(Constants.KEY_GENDER, "female");
            } else {
                user.put(Constants.KEY_GENDER, "male");
            }
            user.put(Constants.KEY_HEIGHT, binding.height.getText().toString());
            user.put(Constants.KEY_WEIGHT, binding.weight.getText().toString());
            if (binding.loseWeight.isChecked()) {
                user.put(Constants.KEY_LOSEWEIGHT, true);
            } else {
                user.put(Constants.KEY_LOSEWEIGHT, false);
            }
            user.put(Constants.KEY_DESCRIPTION, binding.description.getText().toString());
            user.put(Constants.KEY_ADDRESS, geoPoint);
            // Ajout de l'user à la db
            db.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(RegisterActivity.this, "Register Successful!", Toast.LENGTH_SHORT).show();
                    //ajout de son image à la db
                    addImageToFireStorage();
                    loading(false);
                    //enregistrement de ses données localemnt
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
                    preferenceManager.putString(Constants.KEY_USERNAME, binding.inputUsername.getText().toString());
                    preferenceManager.putString(Constants.KEY_GENDER, (String) user.get(Constants.KEY_GENDER));
                    preferenceManager.putString(Constants.KEY_AGE, binding.birthDate.getText().toString());
                    preferenceManager.putString(Constants.KEY_WEIGHT, binding.weight.getText().toString());
                    preferenceManager.putString(Constants.KEY_HEIGHT, binding.height.getText().toString());
                    preferenceManager.putBoolean(Constants.KEY_LOSEWEIGHT, (Boolean) user.get(Constants.KEY_LOSEWEIGHT));
                    preferenceManager.putString(Constants.KEY_DESCRIPTION, binding.description.getText().toString());
                    preferenceManager.putString(Constants.KEY_ADDRESS, addressManual.toString());

                    //Redirection vers la page main
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "Register failed.", Toast.LENGTH_SHORT).show();
                    loading(false);
                }
            });


        }
    }

    //Obtention du geopoint
    private GeoPoint getGeoPoint() {
        address = binding.street.getText() + ", " + binding.locality.getText() + ", " + binding.postalCode.getText() + ", " + binding.country.getText();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(address, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addressManual = addresses.get(0);


        // Get the latitude and longitude of the address
        double latitude = addressManual.getLatitude();
        double longitude = addressManual.getLongitude();

        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        return geoPoint;
    }

    //ajout de l'image à la db
    private void addImageToFireStorage() {
        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                updateProfilePicture(task.getResult().toString());
                            }
                        }
                    });
                }
            }
        });
    }

    //Ici on met a jour sa pp
    private void updateProfilePicture(String url) {
        Map<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_IMAGE, url);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID)).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                preferenceManager.putString(Constants.KEY_IMAGE, user.get(Constants.KEY_IMAGE).toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error updating user data: " + e.getMessage());
            }
        });

    }

    //Verification de toutes les données inserer
    private boolean checkInputData() {
        if (imagePath == null) {
            previousView();
            previousView();
            binding.profilePicture.requestFocus();
            Toast.makeText(RegisterActivity.this, "Please set a profile picture.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.inputUsername.getText().toString().trim().isEmpty()) {
            previousView();
            binding.inputUsername.setError("Enter username!");
            binding.inputUsername.requestFocus();
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            previousView();
            previousView();
            binding.inputEmail.setError("Email is required!");
            binding.inputEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            previousView();
            previousView();
            binding.inputEmail.setError("Enter valid email!");
            binding.inputEmail.requestFocus();
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            previousView();
            previousView();
            binding.inputPassword.setError("Enter password!");
            binding.inputPassword.requestFocus();
            return false;
        } else if (binding.inputCPassword.getText().toString().trim().isEmpty()) {
            previousView();
            previousView();
            binding.inputCPassword.setError("Confirm your password!");
            binding.inputCPassword.requestFocus();
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputCPassword.getText().toString())) {
            previousView();
            previousView();
            binding.inputCPassword.setError("Password & confirm password must be same!");
            binding.inputCPassword.requestFocus();
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputCPassword.getText().toString())) {
            previousView();
            previousView();
            binding.inputCPassword.setError("Password & confirm password must be same!");
            binding.inputCPassword.requestFocus();
            return false;
        } else if (!binding.female.isChecked() && !binding.male.isChecked()) {
            previousView();
            Toast.makeText(RegisterActivity.this, "Please check your gender!", Toast.LENGTH_SHORT).show();
            binding.genderRadioBox.requestFocus();
            return false;
        } else if (binding.birthDate.getText().toString().isEmpty()) {
            previousView();
            Toast.makeText(RegisterActivity.this, "Please insert your birth date!", Toast.LENGTH_SHORT).show();
            binding.birthDate.requestFocus();
            return false;
        } else if (calculatedAge < 18) {
            previousView();
            Toast.makeText(RegisterActivity.this, "You must be 18 or older to register!" + calculatedAge, Toast.LENGTH_SHORT).show();
            binding.birthDate.requestFocus();
            return false;
        } else if (binding.height.getText().toString().isEmpty()) {
            previousView();
            binding.height.setError("Please insert your height!");
            binding.height.requestFocus();
            return false;
        } else if (binding.weight.getText().toString().isEmpty()) {
            previousView();
            binding.weight.setError("Please insert your weight!");
            binding.weight.requestFocus();
            return false;
        } else if (!binding.loseWeight.isChecked() && !binding.gainWeight.isChecked()) {
            previousView();
            binding.loseWeight.setError("Please check your goal!");
            binding.loseWeight.requestFocus();
            return false;
        } else if (binding.street.getText().toString().isEmpty()) {
            binding.street.setError("Please enter your street!");
            binding.street.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    //Progressbar inscription
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.registerButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.registerButton.setVisibility(View.VISIBLE);
        }
    }

    //Progressbar position
    private void loadingPosition(Boolean isLoading) {
        if (isLoading) {
            binding.getPositionButton.setVisibility(View.INVISIBLE);
            binding.progressBarGetPosition.setVisibility(View.VISIBLE);
        } else {
            binding.progressBarGetPosition.setVisibility(View.INVISIBLE);
            binding.getPositionButton.setVisibility(View.VISIBLE);
        }
    }

    //à l'obtention de la position, on les inseres dans les input
    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Test to see lat and long
        try {
            //Gecoder Class for refers to transform street adress or any adress into lat and long
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            // Adress class helps in fetching the street adresse,locality,city,country etc
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            binding.street.setText(addresses.get(0).getAddressLine(0).substring(0, addresses.get(0).getAddressLine(0).indexOf(',')));
            binding.locality.setText(addresses.get(0).getLocality());
            binding.postalCode.setText(addresses.get(0).getPostalCode());
            binding.country.setText(addresses.get(0).getCountryName());

            loadingPosition(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

}