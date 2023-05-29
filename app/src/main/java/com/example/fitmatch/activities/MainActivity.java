package com.example.fitmatch.activities;

import static com.example.fitmatch.utilities.Notifications.setNotification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitmatch.R;
import com.example.fitmatch.databinding.ActivityMainBinding;
import com.example.fitmatch.fragment.AccountFragment;
import com.example.fitmatch.fragment.ExercisesFragment;
import com.example.fitmatch.fragment.HomeFragment;
import com.example.fitmatch.fragment.MessageryFragment;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ActivityMainBinding binding;
    private boolean connected = false;
    private PreferenceManager preferenceManager;

    private ArrayList<String> musclesList;

    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ReplaceFragment(new HomeFragment());
        getMusclesFromAPI();
        musclesList = new ArrayList<>();
        checkSession();


        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        setNotification(this);


        if (!connected) {
            binding.getRoot().setOnClickListener(e -> {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            });


            binding.navigationBar.setOnItemSelectedListener(item -> {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return false;
            });

        } else {
            binding.navigationBar.setOnItemSelectedListener(item -> {


                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        ReplaceFragment(new HomeFragment());
                        break;
                    case R.id.navigation_messagery:
                        ReplaceFragment(new MessageryFragment());
                        break;
                    case R.id.navigation_exercises:

                        bundle = new Bundle();
                        bundle.putStringArrayList("Muscle List", musclesList);
                        // set Fragmentclass Arguments
                        ExercisesFragment exercisesFragment = new ExercisesFragment();
                        exercisesFragment.setArguments(bundle);
                        ReplaceFragment(exercisesFragment);
                        break;
                    case R.id.navigation_account:
                        ReplaceFragment(new AccountFragment());
                        break;
                }

                return true;
            });
        }


    }

    private void checkSession() {
        if (preferenceManager.getString(Constants.KEY_USER_ID) == null) {
            connected = false;
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


    //reception de la liste des muscles API
    private void getMusclesFromAPI() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://exerciseapi3.p.rapidapi.com/search/muscles/";


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        // we are getting each json object.
                        String muscle = String.valueOf(response.getString(i));
                        musclesList.add(muscle);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
                System.out.println(error.toString());
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap header = new HashMap();
                header.put("X-RapidAPI-Key", "6a56f693demsh100ded91c4d48dap1d2819jsn279ce0ccaec0");
                header.put("X-RapidAPI-Host", "exerciseapi3.p.rapidapi.com");
                return header;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }
}