package com.example.fitmatch.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.fitmatch.R;
import com.example.fitmatch.activities.MainActivity;
import com.example.fitmatch.adapter.CardAdapter;
import com.example.fitmatch.databinding.FragmentAccountBinding;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;

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

    private FragmentAccountBinding binding;
    private PreferenceManager preferenceManager;

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
        binding = FragmentAccountBinding.inflate(getLayoutInflater());
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

        //recycler
        TextView mail = view.findViewById(R.id.mail);
        TextView name = view.findViewById(R.id.name);
        TextView age = view.findViewById(R.id.age);
        TextView gender = view.findViewById(R.id.gender);
        TextView height = view.findViewById(R.id.height);
        TextView weight = view.findViewById(R.id.weight);
        Button disconnect = view.findViewById(R.id.disconnect_button);
        mail.append(preferenceManager.getString(Constants.KEY_EMAIL));
        name.append(preferenceManager.getString(Constants.KEY_USERNAME));
        age.append(preferenceManager.getString(Constants.KEY_AGE));
        gender.append(preferenceManager.getString(Constants.KEY_GENDER));
        height.append(preferenceManager.getString(Constants.KEY_HEIGHT) + " cm");
        weight.append(preferenceManager.getString(Constants.KEY_WEIGHT) + " Kg");

        disconnect.setOnClickListener(e-> {
            preferenceManager.clear();
            startActivity(new Intent(getContext(), MainActivity.class));
        });

    }

}