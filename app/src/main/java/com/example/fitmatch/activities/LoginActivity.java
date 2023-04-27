package com.example.fitmatch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitmatch.databinding.ActivityLoginBinding;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private PreferenceManager preferenceManager;

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        setListeners();
    }

    private void setListeners() {
        binding.buttonLogin.setOnClickListener(e-> loginUser());
        binding.textNewAccount.setOnClickListener(e-> openRegisterActivity());
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    private void loginUser() {
        if (checkInputData()){
            loading(true);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                    .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            //Sauvegarde des données user localement
                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                            preferenceManager.putString(Constants.KEY_USERNAME, documentSnapshot.getString(Constants.KEY_USERNAME));
                            preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                            preferenceManager.putString(Constants.KEY_GENDER, documentSnapshot.getString(Constants.KEY_GENDER));
                            preferenceManager.putString(Constants.KEY_AGE, documentSnapshot.getString(Constants.KEY_AGE));
                            preferenceManager.putString(Constants.KEY_WEIGHT,documentSnapshot.getString(Constants.KEY_WEIGHT));
                            preferenceManager.putString(Constants.KEY_HEIGHT, documentSnapshot.getString(Constants.KEY_HEIGHT));
                            preferenceManager.putBoolean(Constants.KEY_LOSEWEIGHT, documentSnapshot.getBoolean(Constants.KEY_LOSEWEIGHT));



                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            loading(false);
                            Toast.makeText(getApplicationContext(), "Login failed !", Toast.LENGTH_SHORT).show();
                        }
                    });
        }



    }

    private boolean checkInputData() {
       if (binding.inputEmail.getText().toString().trim().isEmpty()){
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
        } else {
            return true;
        }

    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.buttonLogin.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonLogin.setVisibility(View.VISIBLE);
        }
    }



}