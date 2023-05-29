package com.example.fitmatch.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitmatch.R;
import com.example.fitmatch.adapter.ExercisesAdapter;
import com.example.fitmatch.databinding.FragmentExercisesBinding;
import com.example.fitmatch.models.Exercises;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExercisesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExercisesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<Exercises> exercisesList;
    private ArrayList<String> musclesList;
    private ExercisesAdapter exercisesAdapter;
    private Bundle bundle;
    private FragmentExercisesBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExercisesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExercicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExercisesFragment newInstance(String param1, String param2) {
        ExercisesFragment fragment = new ExercisesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentExercisesBinding.inflate(getLayoutInflater());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        exercisesList = new ArrayList<>();
        bundle = new Bundle();
        bundle = this.getArguments();
        musclesList = new ArrayList<>();
        //reception de la liste des muscles
        musclesList = bundle.getStringArrayList("Muscle List");


        exercisesAdapter = new ExercisesAdapter(getContext(), exercisesList);
        binding.recyclerView.setAdapter(exercisesAdapter);


    }

    //obtention des exercices d'un musclé donné
    private void getExercisesFromAPI(String muscle) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://exerciseapi3.p.rapidapi.com/search/?primaryMuscle=" + URLEncoder.encode(muscle);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        Exercises exercise = new Exercises(responseObj.getString("Force"), responseObj.getString("Name"), responseObj.getJSONArray("Primary Muscles"), responseObj.getJSONArray("SecondaryMuscles"), responseObj.getString("Type"), responseObj.getJSONArray("Workout Type"), responseObj.getString("Youtube link"));
                        exercisesList.add(exercise);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                exercisesAdapter.setNewExercises(exercisesList);

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
        queue.add(jsonArrayRequest);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercises, viewGroup, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recycler
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // define an adapter
        exercisesAdapter = new ExercisesAdapter(getContext(), exercisesList);
        exercisesAdapter.setNewExercises(exercisesList);


        recyclerView.setAdapter(exercisesAdapter);
        ViewFlipper viewFlipper = view.findViewById(R.id.view_flipper_muscu);
        GridLayout gridLayout = view.findViewById(R.id.gridLayout);
        TextView textView = view.findViewById(R.id.textTitleMuscle);
        //Affichage des boutons pour chaque muscle
        int cpt = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 11; j++) {
                TextView button = new TextView(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.columnSpec = GridLayout.spec(j, 1f);
                params.rowSpec = GridLayout.spec(i, 1f);
                params.setMargins(8, 8, 8, 8); // Ajouter des marges
                button.setLayoutParams(params);
                Drawable myDrawable = getResources().getDrawable(R.drawable.custombutton2);
                button.setBackground(myDrawable);
                button.setText(String.valueOf(cpt + 1));
                button.setId(cpt);
                button.setTextColor(Color.WHITE);
                button.setGravity(Gravity.CENTER);
                cpt++;
                gridLayout.addView(button);
                button.setOnClickListener(e -> {
                    exercisesList.clear();
                    viewFlipper.showNext();
                    textView.setText(musclesList.get(button.getId()));
                    getExercisesFromAPI(musclesList.get(button.getId()));
                });
            }
        }


    }

}