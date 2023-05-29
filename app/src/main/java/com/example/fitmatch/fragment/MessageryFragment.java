package com.example.fitmatch.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitmatch.R;
import com.example.fitmatch.activities.ChatActivity;
import com.example.fitmatch.activities.GroupChatActivity;
import com.example.fitmatch.adapter.GroupConversationsAdapter;
import com.example.fitmatch.adapter.RecentConversationsAdapter;
import com.example.fitmatch.listener.ConversionListener;
import com.example.fitmatch.listener.GroupConversionListener;
import com.example.fitmatch.models.ChatMessage;
import com.example.fitmatch.models.User;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageryFragment extends Fragment implements ConversionListener, GroupConversionListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView conversationsRecyclerView;

    private AppCompatButton buddies, groups;

    private ViewFlipper viewFlipper;
    private Drawable clicked, notClicked;

    private RecyclerView conversationsGroupsRecyclerView;
    private ProgressBar progressBar;
    private FirebaseFirestore database;

    private TextView textErrorMessage;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private List<String> groupConversations;
    private RecentConversationsAdapter conversationsAdapter;
    private GroupConversationsAdapter groupConversationsAdapter;


    public MessageryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageryFragment newInstance(String param1, String param2) {
        MessageryFragment fragment = new MessageryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        preferenceManager = new PreferenceManager(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messagery, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        conversationsRecyclerView = view.findViewById(R.id.conversationsRecyclerView);
        conversationsGroupsRecyclerView = view.findViewById(R.id.conversationsGroupsRecyclerView);
        textErrorMessage = view.findViewById(R.id.textErrorMessage);
        progressBar = view.findViewById(R.id.progressBar);
        buddies = view.findViewById(R.id.buttonUserConv);
        groups = view.findViewById(R.id.buttonGroupConv);
        viewFlipper = view.findViewById(R.id.viewFlipperConversations);
        clicked = getResources().getDrawable(R.drawable.custombutton);
        notClicked = getResources().getDrawable(R.drawable.custombutton3);


        buddies.setOnClickListener(e -> buddiesView());
        groups.setOnClickListener(e -> groupView());
        database = FirebaseFirestore.getInstance();

        groupConversations = new ArrayList<>();
        listenGroupConversations();

        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(getContext(), conversations, this::onConversionClicked);
        groupConversationsAdapter = new GroupConversationsAdapter(groupConversations, this::onGroupConversionClicked);
        conversationsRecyclerView.setAdapter(conversationsAdapter);
        conversationsGroupsRecyclerView.setAdapter(groupConversationsAdapter);
        listenConversations();


    }

    private void listenGroupConversations() {

        groupConversations.add(Constants.KEY_COLLECTION_CONVERSATIONS_GROUPS_BULKERS);
        groupConversations.add(Constants.KEY_COLLECTION_CONVERSATIONS_GROUPS_SUMMER_ABS);
        groupConversations.add(Constants.KEY_COLLECTION_CONVERSATIONS_GROUPS_POWER_LIFTERS);

        conversationsGroupsRecyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);

    }

    // Permet de retrouver les conversations entre utilisateurs
    private void listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            loading(true);
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                //Si c'est une nouvelle conversations => on l'a crée
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(senderId);
                    chatMessage.setReceiverId(receiverId);
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.setImage(documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME));
                        chatMessage.setConversionId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    } else {
                        chatMessage.setImage(documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(Constants.KEY_SENDER_NAME));
                        chatMessage.setConversionId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    }
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    conversations.add(chatMessage);
                    //Si c'est une conversations existantes => màj
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)) {
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            loading(false);

            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationsAdapter.notifyDataSetChanged();
            conversationsRecyclerView.smoothScrollToPosition(0);
            conversationsRecyclerView.setVisibility(View.VISIBLE);

            if (conversations.size() == 0) {
                showErrorMessage();
            } else {
                hideErrorMessage();
            }
        }
    };

    private void hideErrorMessage() {
        textErrorMessage.setVisibility(View.GONE);

    }

    private void showErrorMessage() {
        textErrorMessage.setText(String.format("%s", "No conversions"));
        textErrorMessage.setVisibility(View.VISIBLE);
    }

    //Permet d'acceder à la page preécedente du flipperview
    public void buddiesView() {

        viewFlipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);
        buddies.setBackground(clicked);
        buddies.setClickable(false);
        groups.setClickable(true);
        groups.setBackground(notClicked);

        viewFlipper.showPrevious();
    }
    //Permet d'acceder à la page suivante du flipperview

    public void groupView() {
        viewFlipper.setInAnimation(getContext(), R.anim.slide_in_right);
        viewFlipper.setOutAnimation(getContext(), R.anim.slide_out_left);
        buddies.setBackground(notClicked);
        buddies.setClickable(true);
        groups.setClickable(false);
        groups.setBackground(clicked);
        viewFlipper.showNext();
        textErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onGroupConversionClicked(String conversionName) {
        Intent intent = new Intent(getContext(), GroupChatActivity.class);
        intent.putExtra(Constants.GROUP_CONVERSATION_NAME, conversionName);
        startActivity(intent);

    }
}