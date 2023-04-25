package com.example.fitmatch;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitmatch.databinding.FragmentHomeBinding;
import com.example.fitmatch.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment, AppCompatActivity implements CardStackListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentHomeBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        getExercisesFromAPI();
        getRandomQuotesFromAPI();

        List<User> cardList = new ArrayList<>();
        cardList.add(new User(R.drawable.card_image_1, "Card 1"));
        cardList.add(new User(R.drawable.card_image_2, "Card 2"));
        cardList.add(new User(R.drawable.card_image_3, "Card 3"));


    }

    private void getRandomQuotesFromAPI() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://api.api-ninjas.com/v1/quotes?category=inspirational";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray  response)  {
                        // Display the first 500 characters of the response string.
                        binding.textView.setText("Response is: ");


                        System.out.println("QUOTES");
                        System.out.println(response);

                        for (int i = 0; i < response.length(); i++) {
                            // creating a new json object and
                            // getting each object from our json array.
                            try {
                                // we are getting each json object.
                                JSONObject responseObj = response.getJSONObject(i);

                                String quote = responseObj.getString("quote");
                                String author = responseObj.getString("author");
                                String category = responseObj.getString("category");
                                System.out.println(quote);
                                System.out.println(author);
                                System.out.println(category);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.textView.setText("That didn't work!");
                System.out.println("That didn't work!");
                System.out.println(error.toString());
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                HashMap header  = new HashMap();
                header.put("X-Api-Key", "w64eQQv2AkYVVktJIpWHAA==TZKT6D5xIWfemSub");
                return header;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }

    private void getExercisesFromAPI() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://exerciseapi3.p.rapidapi.com/search/?name=Barbell%20Bench%20Press";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray  response)  {
                        // Display the first 500 characters of the response string.
                        binding.textView.setText("Response is: ");


                        System.out.println("HELlo");
                        System.out.println(response);

                        for (int i = 0; i < response.length(); i++) {
                            // creating a new json object and
                            // getting each object from our json array.
                            try {
                                // we are getting each json object.
                                JSONObject responseObj = response.getJSONObject(i);

                                // now we get our response from API in json object format.
                                // in below line we are extracting a string with
                                // its key value from our json object.
                                // similarly we are extracting all the strings from our json object.
                                String force = responseObj.getString("Force");
                                String name = responseObj.getString("Name");
                                JSONArray primaryMuscles = responseObj.getJSONArray("Primary Muscles");
                                System.out.println(force);
                                System.out.println(name);
                                System.out.println(primaryMuscles);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.textView.setText("That didn't work!");
                System.out.println("That didn't work!");
                System.out.println(error.toString());
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                HashMap header  = new HashMap();
                header.put("X-RapidAPI-Key", "6a56f693demsh100ded91c4d48dap1d2819jsn279ce0ccaec0");
                header.put("X-RapidAPI-Host", "exerciseapi3.p.rapidapi.com");
                return header;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}