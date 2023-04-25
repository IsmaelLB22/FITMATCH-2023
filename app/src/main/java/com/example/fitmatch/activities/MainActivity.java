package com.example.fitmatch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.fitmatch.AccountFragment;
import com.example.fitmatch.HomeFragment;
import com.example.fitmatch.MessageryFragment;
import com.example.fitmatch.R;
import com.example.fitmatch.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ReplaceFragment(new HomeFragment());
        mAuth = FirebaseAuth.getInstance();

        binding.navigationBar.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (!connected) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        ReplaceFragment(new HomeFragment());
                    }
                    break;
                case R.id.navigation_messagery:
                    ReplaceFragment(new MessageryFragment());
                    break;
                case R.id.navigation_account:
                    ReplaceFragment(new AccountFragment());
                    break;
            }

            return true;
        });


    }

    private void updateUI(FirebaseUser currentUser) {

        connected = currentUser != null;
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}