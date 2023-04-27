package com.example.fitmatch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.fitmatch.fragment.AccountFragment;
import com.example.fitmatch.fragment.ExercisesFragment;
import com.example.fitmatch.fragment.HomeFragment;
import com.example.fitmatch.fragment.MessageryFragment;
import com.example.fitmatch.R;
import com.example.fitmatch.databinding.ActivityMainBinding;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private boolean connected = false;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ReplaceFragment(new HomeFragment());
        mAuth = FirebaseAuth.getInstance();


            binding.navigationBar.setOnItemSelectedListener(item -> {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        ReplaceFragment(new HomeFragment());
                        break;
                    case R.id.navigation_messagery:
                        ReplaceFragment(new MessageryFragment());
                        break;
                    case R.id.navigation_exercises:
                        ReplaceFragment(new ExercisesFragment());
                        break;
                    case R.id.navigation_account:
                        ReplaceFragment(new AccountFragment());
                        break;
                }

                return true;
            });



    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (preferenceManager.getString(Constants.KEY_USER_ID) == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            connected =false;
        } else {
            connected = true;
        }
    }

    private void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}