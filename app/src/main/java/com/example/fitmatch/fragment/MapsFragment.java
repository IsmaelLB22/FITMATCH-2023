package com.example.fitmatch.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.fitmatch.R;
import com.example.fitmatch.models.User;
import com.example.fitmatch.utilities.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {

    private User user;
    private GoogleMap mMap;
    private LatLng userLatLong;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(Constants.KEY_USER);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                userLatLong = new LatLng(user.getLatitude(), user.getLongitude());
                mMap.clear();
                //marquage du point
                mMap.addMarker(new MarkerOptions().position(userLatLong).title(user.getName() + "'s localisation"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLong));


            }
        });
        return view;
    }

}