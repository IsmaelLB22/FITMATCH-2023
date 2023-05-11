package com.example.fitmatch.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.fitmatch.R;
import com.example.fitmatch.activities.MainActivity;
import com.example.fitmatch.activities.RegisterActivity;
import com.example.fitmatch.adapter.CardAdapter;
import com.example.fitmatch.databinding.FragmentAccountBinding;
import com.example.fitmatch.models.User;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private PreferenceManager preferenceManager;

    private EditText mail, name, age, height, weight;
    private RadioButton genderMale, genderFemale;
    private Button updateData, disconnect;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, viewGroup, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setElements(view);
        loadUserDetails();
        setListeners();


    }

    private void setListeners() {
        updateData.setOnClickListener(e -> {
            updateUserData();
        });

        disconnect.setOnClickListener(e -> {
            preferenceManager.clear();
            startActivity(new Intent(getContext(), MainActivity.class));
        });

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                age.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });
    }

    private void loadUserDetails() {
        mail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        name.setText(preferenceManager.getString(Constants.KEY_USERNAME));
        age.setText(preferenceManager.getString(Constants.KEY_AGE));
        if (preferenceManager.getString(Constants.KEY_GENDER).equals("male"))
            genderMale.setChecked(true);
        else genderFemale.setChecked(true);
        height.setText(preferenceManager.getString(Constants.KEY_HEIGHT));
        weight.setText(preferenceManager.getString(Constants.KEY_WEIGHT));
    }

    private void setElements(View view) {
        mail = view.findViewById(R.id.inputEmail);
        name = view.findViewById(R.id.inputUsername);
        age = view.findViewById(R.id.inputUserBirthDate);
        genderMale = view.findViewById(R.id.male);
        genderFemale = view.findViewById(R.id.female);
        height = view.findViewById(R.id.inputUserHeight);
        weight = view.findViewById(R.id.inputUserWeight);
        updateData = view.findViewById(R.id.update_button);
        disconnect = view.findViewById(R.id.disconnect_button);
    }

    private void updateUserData() {
        HashMap<String, Object> user = new HashMap<>();

        user.put(Constants.KEY_USERNAME, name.getText().toString());
        user.put(Constants.KEY_EMAIL, mail.getText().toString());
        if (genderFemale.isChecked()) user.put(Constants.KEY_GENDER, "female");
        else user.put(Constants.KEY_GENDER, "male");
        user.put(Constants.KEY_AGE, age.getText().toString());
        user.put(Constants.KEY_HEIGHT, height.getText().toString());
        user.put(Constants.KEY_WEIGHT, weight.getText().toString());


        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("User data updated successfully!");
                        preferenceManager.putString(Constants.KEY_EMAIL, user.get(Constants.KEY_EMAIL).toString());
                        preferenceManager.putString(Constants.KEY_USERNAME, user.get(Constants.KEY_USERNAME).toString());
                        preferenceManager.putString(Constants.KEY_GENDER, user.get(Constants.KEY_GENDER).toString());
                        preferenceManager.putString(Constants.KEY_AGE, user.get(Constants.KEY_AGE).toString());
                        preferenceManager.putString(Constants.KEY_WEIGHT, user.get(Constants.KEY_WEIGHT).toString());
                        preferenceManager.putString(Constants.KEY_HEIGHT, user.get(Constants.KEY_HEIGHT).toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error updating user data: " + e.getMessage());
                    }
                });
    }
}