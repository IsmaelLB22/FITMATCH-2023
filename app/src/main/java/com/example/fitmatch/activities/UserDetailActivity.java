package com.example.fitmatch.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fitmatch.R;
import com.example.fitmatch.databinding.ActivityUserDetailBinding;
import com.example.fitmatch.fragment.MapsFragment;
import com.example.fitmatch.models.User;
import com.example.fitmatch.utilities.Constants;

public class UserDetailActivity extends AppCompatActivity {

    private ActivityUserDetailBinding binding;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadDetails();

        //Creation du fragment Map
        Fragment fragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapView, fragment).commit();
        //Envoie des données au fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_USER, user);
        fragment.setArguments(bundle);

        // Get a handle to the fragment and register the callback.
    }

    //Ici on charge les données de l'user dans la page
    private void loadDetails() {
        user = (User) getIntent().getSerializableExtra(Constants.KEY_USER);

        Glide.with(getApplicationContext())
                .load(user.getImage())
                .into(binding.imageUser);

        binding.textNameAge.setText(user.getName() + ",  " + user.getAge());
        binding.textDescription.setText(user.getDescription());

    }
}