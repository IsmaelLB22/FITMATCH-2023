package com.example.fitmatch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
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

    private void setListeners() {
        binding.registerButton.setOnClickListener(e-> registerNewUser());
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
            binding.inputUsername.setError("Enter username!");
            binding.inputUsername.requestFocus();
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()){
            binding.inputEmail.setError("Email is required!");
            binding.inputEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            binding.inputEmail.setError("Enter valid email!");
            binding.inputEmail.requestFocus();
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()){
            binding.inputPassword.setError("Enter password!");
            binding.inputPassword.requestFocus();
            return false;
        } else if (binding.inputCPassword.getText().toString().trim().isEmpty()){
            binding.inputCPassword.setError("Confirm your password!");
            binding.inputCPassword.requestFocus();
            return false;
        } else if (!binding.inputCPassword.getText().toString().equals(binding.inputCPassword.getText().toString())){
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