package com.example.fitmatch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitmatch.databinding.ActivityRegisterBinding;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        // ...
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setListeners();

    }

    public void previousView(){
        binding.viewFlipper.showPrevious();
        binding.nextButton.setVisibility(View.VISIBLE);
        binding.backButton.setVisibility(View.GONE);
    }

    public void nextView(){
        binding.viewFlipper.showNext();
        binding.nextButton.setVisibility(View.GONE);
        binding.backButton.setVisibility(View.VISIBLE);
    }


    private void setListeners() {
        binding.registerButton.setOnClickListener(e-> registerNewUser());
        binding.nextButton.setOnClickListener(e-> nextView());
        binding.backButton.setOnClickListener(e-> previousView());

        binding.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // on below line we are adding click listener
        // for our pick date button
        binding.age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                binding.age.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });
    }

    private void registerNewUser() {
        if (checkInputData()){
            loading(true);

            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put(Constants.KEY_USERNAME, binding.inputUsername.getText().toString());
            user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
            user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
            user.put(Constants.KEY_AGE, binding.age.getText().toString());
            if (binding.female.isChecked()){
                user.put(Constants.KEY_GENDER, "female");
            } else {
                user.put(Constants.KEY_GENDER, "male");
            }
            user.put(Constants.KEY_HEIGHT, binding.height.getText().toString());
            user.put(Constants.KEY_WEIGHT, binding.weight.getText().toString());
            if (binding.loseWeight.isChecked()){
                user.put(Constants.KEY_LOSEWEIGHT, true);
            } else {
                user.put(Constants.KEY_LOSEWEIGHT, false);
            }
            // Add a new document with a generated ID
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(RegisterActivity.this, "Register Successful!", Toast.LENGTH_SHORT).show();
                            loading(false);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                            preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
                            preferenceManager.putString(Constants.KEY_USERNAME, binding.inputUsername.getText().toString());
                            //preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                            preferenceManager.putString(Constants.KEY_GENDER, (String) user.get(Constants.KEY_GENDER));
                            preferenceManager.putString(Constants.KEY_AGE, binding.age.getText().toString());
                            preferenceManager.putString(Constants.KEY_WEIGHT,binding.weight.getText().toString());
                            preferenceManager.putString(Constants.KEY_HEIGHT, binding.height.getText().toString());
                            preferenceManager.putBoolean(Constants.KEY_LOSEWEIGHT, Boolean.valueOf((Boolean) user.get(Constants.KEY_LOSEWEIGHT)));


                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Register failed.", Toast.LENGTH_SHORT).show();
                            loading(false);

                        }
                    });


        }
    }

    private boolean checkInputData() {
        if (binding.inputUsername.getText().toString().trim().isEmpty()){
            previousView();
            binding.inputUsername.setError("Enter username!");
            binding.inputUsername.requestFocus();
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()){
            previousView();
            binding.inputEmail.setError("Email is required!");
            binding.inputEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            previousView();
            binding.inputEmail.setError("Enter valid email!");
            binding.inputEmail.requestFocus();
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()){
            previousView();
            binding.inputPassword.setError("Enter password!");
            binding.inputPassword.requestFocus();
            return false;
        } else if (binding.inputCPassword.getText().toString().trim().isEmpty()){
            previousView();
            binding.inputCPassword.setError("Confirm your password!");
            binding.inputCPassword.requestFocus();
            return false;
        } else if (!binding.inputCPassword.getText().toString().equals(binding.inputCPassword.getText().toString())){
            previousView();
            binding.inputCPassword.setError("Password & confirm password must be same!");
            binding.inputCPassword.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.registerButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.registerButton.setVisibility(View.VISIBLE);
        }
    }


}