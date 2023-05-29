package com.example.fitmatch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fitmatch.R;
import com.example.fitmatch.activities.UserDetailActivity;
import com.example.fitmatch.models.User;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View rootView;
    private AppCompatImageView imageBuddy;

    private AppCompatButton matchButton, nextButton;
    private TextView nameAge;
    private boolean connected = false;

    private FirebaseFirestore database;

    private PreferenceManager preferenceManager;
    private User actualUser;
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
        preferenceManager = new PreferenceManager(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        checkSession();

        users = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
        getUserFromDB();
    }

    private void checkSession() {
        connected = preferenceManager.getString(Constants.KEY_USER_ID) != null;
    }

    //Obtention des users inscrits
    private void getUserFromDB() {

        database.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    //On affichera seulement les users ayant le meme objectif que nous
                    if (queryDocumentSnapshot.get(Constants.KEY_LOSEWEIGHT) == preferenceManager.getBoolean(Constants.KEY_LOSEWEIGHT) && !queryDocumentSnapshot.getId().equals(preferenceManager.getString(Constants.KEY_USER_ID))) {

                        User user = new User();
                        user.setName(queryDocumentSnapshot.getString(Constants.KEY_USERNAME));
                        user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                        user.setGender(queryDocumentSnapshot.getString(Constants.KEY_GENDER));
                        user.setBirthDate(queryDocumentSnapshot.getString(Constants.KEY_AGE));
                        user.setHeight(queryDocumentSnapshot.getString(Constants.KEY_HEIGHT));
                        user.setWeight(queryDocumentSnapshot.getString(Constants.KEY_WEIGHT));
                        user.setLoseWeight(Boolean.parseBoolean(String.valueOf(queryDocumentSnapshot.getBoolean(Constants.KEY_LOSEWEIGHT))));
                        user.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                        user.setId(queryDocumentSnapshot.getId());
                        user.setDescription(queryDocumentSnapshot.getString(Constants.KEY_DESCRIPTION));
                        GeoPoint geoPoint = queryDocumentSnapshot.getGeoPoint(Constants.KEY_ADDRESS);
                        user.setLatitude(geoPoint.getLatitude());
                        user.setLongitude(geoPoint.getLongitude());
                        users.add(user);

                    }

                }
                displayNextUser();
            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, viewGroup, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init element xml
        imageBuddy = view.findViewById(R.id.imageBuddy);
        nameAge = view.findViewById(R.id.description);
        matchButton = view.findViewById(R.id.buttonMatch);
        nextButton = view.findViewById(R.id.buttonNext);
        if (connected) {

            nameAge.setOnClickListener(e -> {
                Intent intent = new Intent(getContext(), UserDetailActivity.class);
                intent.putExtra(Constants.KEY_USER, actualUser);
                startActivity(intent);
            });


            nextButton.setOnClickListener(e -> {
                displayNextUser();
            });

            matchButton.setOnClickListener(e -> {
                sendMatchMessage(actualUser);

                displayNextUser();
            });

        }


    }

    //affichagee du prochain utilisateur
    private void displayNextUser() {
        Random random = new Random();
        int index = random.nextInt(users.size());

        // Vérification de l'index généré
        if (index < 0) index = 0;
        else if (index >= users.size()) index = users.size() - 1;

        User randomUser = users.get(index);
        loadUserInPage(randomUser);
    }

    //enovie du msg lorsqu on match
    private void sendMatchMessage(User receiverUser) {
        String defaultMessage = "Hey, I like your profile and I'd like to train with you";
        //Ajout du message à la base de données
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.getId());
        message.put(Constants.KEY_MESSAGE, defaultMessage);
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        //Ajout de la conversation à la base de données
        HashMap<String, Object> conversion = new HashMap<>();
        conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_USERNAME));
        conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
        conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.getId());
        conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.getName());
        conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.getImage());
        conversion.put(Constants.KEY_LAST_MESSAGE, defaultMessage);
        conversion.put(Constants.KEY_TIMESTAMP, new Date());
        addConversion(conversion);

    }

    private void addConversion(HashMap<String, Object> conversion) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).add(conversion);
    }

    private void loadUserInPage(User user) {

        actualUser = user;

        nameAge.setText(user.getName() + "\n" + user.getAge() + " years");

        Glide.with(getContext()).load(user.getImage()).into(imageBuddy);
    }

}