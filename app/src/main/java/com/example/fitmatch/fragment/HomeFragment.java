package com.example.fitmatch.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.fitmatch.R;
import com.example.fitmatch.activities.MainActivity;
import com.example.fitmatch.adapter.CardAdapter;
import com.example.fitmatch.databinding.FragmentHomeBinding;
import com.example.fitmatch.models.User;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View rootView;
    private AppCompatImageView imageBuddy;
    private AppCompatButton matchButton;
    private TextView nameAge;
    private CardAdapter cardAdapter;
    private FragmentHomeBinding binding;
    private PreferenceManager preferenceManager;
    private List<User> users; // La liste des utilisateurs que vous voulez afficher



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
        //binding = FragmentHomeBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //getRandomQuotesFromAPI();
        users = new ArrayList<>();

        users.add(new User("Hamid","19", "https://i.pinimg.com/736x/b0/a0/ea/b0a0ea30556521996bb798bf7866f38f.jpg" ));
        users.add(new User("Joshua","20", "https://ik.imagekit.io/yynn3ntzglc/cms/212_contenu1_104ac041a9_FVEhuH-TJHT.jpg" ));
        users.add(new User("Alice", "22", "https://randomuser.me/api/portraits/women/1.jpg"));
        users.add(new User("Bob", "30", "https://randomuser.me/api/portraits/men/2.jpg"));
        users.add(new User("Charlie", "27", "https://randomuser.me/api/portraits/men/3.jpg"));
        users.add(new User("Danielle", "19", "https://randomuser.me/api/portraits/women/4.jpg"));
        users.add(new User("Ethan", "25", "https://randomuser.me/api/portraits/men/5.jpg"));

        getUserFromDB();

    }

    private void getUserFromDB() {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (queryDocumentSnapshot.get(Constants.KEY_LOSEWEIGHT) == preferenceManager.getBoolean(Constants.KEY_LOSEWEIGHT)){
                            User user = new User();
                            user.setName(queryDocumentSnapshot.getString(Constants.KEY_USERNAME));
                            user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                            user.setGender(queryDocumentSnapshot.getString(Constants.KEY_GENDER));
                            user.setAge(queryDocumentSnapshot.getString(Constants.KEY_AGE));
                            user.setHeight(Double.parseDouble(queryDocumentSnapshot.getString(Constants.KEY_HEIGHT)));
                            user.setWeight(Double.parseDouble(queryDocumentSnapshot.getString(Constants.KEY_WEIGHT)));
                            user.setLoseWeight(Boolean.parseBoolean(String.valueOf(queryDocumentSnapshot.getBoolean(Constants.KEY_LOSEWEIGHT))));
                            user.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                            user.setId(queryDocumentSnapshot.getId());
                            users.add(user);
                            }
                        }
                    } else {
                      //  showErrorMessage();
                    }
                });
    }


    private void getRandomQuotesFromAPI() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://api.api-ninjas.com/v1/quotes?category=inspirational";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray  response)  {
                        // Display the first 500 characters of the response string.

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, viewGroup, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recycler
        //RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        //recyclerView.setLayoutManager(layoutManager);

        // define an adapter
        //cardAdapter = new CardAdapter(getContext(), users);



        //cardAdapter.setNewCards(users);
        //recyclerView.setAdapter(cardAdapter);

        imageBuddy = view.findViewById(R.id.imageBuddy);
        nameAge = view.findViewById(R.id.description);
        matchButton = view.findViewById(R.id.buttonMatch);

        User user = new User("Hamid","19", "https://i.pinimg.com/736x/b0/a0/ea/b0a0ea30556521996bb798bf7866f38f.jpg" );

        matchButton.setOnClickListener(e-> {
            Random random = new Random();
            int index = random.nextInt(users.size());
            User randomUser = users.get(index);
            loadUserInPage(randomUser);
        });


        loadUserInPage(user);
    }

    private void loadUserInPage(User user) {


        nameAge.setText(user.getName() + "\n" + user.getAge() + " years");

        Glide.with(getContext())
                .load(user.getImage())
                .into(imageBuddy);
    }

}