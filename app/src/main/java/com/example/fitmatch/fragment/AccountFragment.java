package com.example.fitmatch.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fitmatch.R;
import com.example.fitmatch.activities.MainActivity;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private Uri imagePath = null;

    private EditText mail, name, age, height, weight, description;
    private RadioButton genderMale, genderFemale, gain, lose;
    private Button updateData, disconnect;

    private RoundedImageView image;

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

        //Deconnexion
        disconnect.setOnClickListener(e -> {
            preferenceManager.clear();
            startActivity(new Intent(getContext(), MainActivity.class));
        });

        //permet d'ouvrir un calendrier pour l'insertion de sa date de naissance
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                age.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            getImageInImageView(imagePath);
        }
    }

    //chargement de la photo dans l'image view
    private void getImageInImageView(Object imageToLoad) {
        Glide.with(getContext())
                .load(imageToLoad)
                .into(image);
    }

    //On charge les donnees dans les elements xml
    private void loadUserDetails() {

        getImageInImageView(preferenceManager.getString(Constants.KEY_IMAGE));

        mail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        name.setText(preferenceManager.getString(Constants.KEY_USERNAME));
        age.setText(preferenceManager.getString(Constants.KEY_AGE));
        if (preferenceManager.getString(Constants.KEY_GENDER).equals("male"))
            genderMale.setChecked(true);
        else genderFemale.setChecked(true);
        height.setText(preferenceManager.getString(Constants.KEY_HEIGHT));
        weight.setText(preferenceManager.getString(Constants.KEY_WEIGHT));
        if (preferenceManager.getBoolean(Constants.KEY_LOSEWEIGHT))
            lose.setChecked(true);
        else gain.setChecked(true);
        description.setText(preferenceManager.getString(Constants.KEY_DESCRIPTION));
    }

    //On init les elements xml
    private void setElements(View view) {
        image = view.findViewById(R.id.profile_image);
        mail = view.findViewById(R.id.inputEmail);
        name = view.findViewById(R.id.inputName);
        age = view.findViewById(R.id.inputBirthDate);
        genderMale = view.findViewById(R.id.male);
        genderFemale = view.findViewById(R.id.female);
        height = view.findViewById(R.id.inputUserHeight);
        weight = view.findViewById(R.id.inputUserWeight);
        description = view.findViewById(R.id.inputDescription);
        gain = view.findViewById(R.id.gainWeight);
        lose = view.findViewById(R.id.loseWeight);
        updateData = view.findViewById(R.id.update_button);
        disconnect = view.findViewById(R.id.disconnect_button);
    }

    //Cette methode mets à jour les données user dans la bd et localement
    private void updateUserData() {
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_USERNAME, name.getText().toString());
        user.put(Constants.KEY_EMAIL, mail.getText().toString());
        if (genderFemale.isChecked()) user.put(Constants.KEY_GENDER, "female");
        else user.put(Constants.KEY_GENDER, "male");
        user.put(Constants.KEY_AGE, age.getText().toString());
        user.put(Constants.KEY_HEIGHT, height.getText().toString());
        user.put(Constants.KEY_WEIGHT, weight.getText().toString());
        if (gain.isChecked()) user.put(Constants.KEY_LOSEWEIGHT, false);
        else user.put(Constants.KEY_LOSEWEIGHT, true);
        user.put(Constants.KEY_DESCRIPTION, description.getText().toString());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("User data updated successfully!");
                        if (imagePath !=null){
                            addImageToFireStorage();
                        }
                        preferenceManager.putString(Constants.KEY_EMAIL, user.get(Constants.KEY_EMAIL).toString());
                        preferenceManager.putString(Constants.KEY_USERNAME, user.get(Constants.KEY_USERNAME).toString());
                        preferenceManager.putString(Constants.KEY_GENDER, user.get(Constants.KEY_GENDER).toString());
                        preferenceManager.putString(Constants.KEY_AGE, user.get(Constants.KEY_AGE).toString());
                        preferenceManager.putString(Constants.KEY_WEIGHT, user.get(Constants.KEY_WEIGHT).toString());
                        preferenceManager.putString(Constants.KEY_HEIGHT, user.get(Constants.KEY_HEIGHT).toString());
                        preferenceManager.putBoolean(Constants.KEY_LOSEWEIGHT, (Boolean) user.get(Constants.KEY_LOSEWEIGHT));
                        preferenceManager.putString(Constants.KEY_DESCRIPTION, user.get(Constants.KEY_DESCRIPTION).toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error updating user data: " + e.getMessage());
                    }
                });
    }

    //Ajout de la photo dans la bd
    private void addImageToFireStorage() {
        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                updateProfilePicture(task.getResult().toString());
                            }
                        }
                    });
                }
            }
        });
    }

    //Màj de la photo
    private void updateProfilePicture(String url) {
        Map<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_IMAGE, url);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferenceManager.putString(Constants.KEY_IMAGE, user.get(Constants.KEY_IMAGE).toString());
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