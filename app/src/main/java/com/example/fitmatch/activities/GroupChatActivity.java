package com.example.fitmatch.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitmatch.adapter.GroupChatAdapter;
import com.example.fitmatch.databinding.ActivityChatBinding;
import com.example.fitmatch.models.ChatMessage;
import com.example.fitmatch.utilities.Constants;
import com.example.fitmatch.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class GroupChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private String groupConvName;
    private List<String> chatMessagesNotFormatted;
    private List<ChatMessage> chatMessages;

    private GroupChatAdapter groupChatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        listenMessages();
        setListeners();
    }


    //Init values & layout data
    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatMessagesNotFormatted = new ArrayList<>();
        groupChatAdapter = new GroupChatAdapter(
                getApplicationContext(),
                chatMessages,
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        binding.chatRecyclerView.setAdapter(groupChatAdapter);
        database = FirebaseFirestore.getInstance();
        groupConvName = (String) getIntent().getSerializableExtra(Constants.GROUP_CONVERSATION_NAME);
        binding.textName.setText(groupConvName);
        binding.imageInfo.setVisibility(View.INVISIBLE);
    }

    //Init des listeners
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());

    }

    //envoyer un message
    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_USERNAME));
        message.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        //Ajout du message à la base de données
        database.collection(groupConvName).add(message);
        //Apres l'envoie du message on vide l'input
        binding.inputMessage.setText(null);

    }

    //récuperer les messages qui concernent 2 utilisateurs et un article
    private void listenMessages() {
        database.collection(groupConvName)
                .addSnapshotListener(eventListener);


    }
    //conversion image

    //conversion date
    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }


    //Récuperer tout les messages
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {

            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    chatMessage.setConversionName(documentChange.getDocument().getString(Constants.KEY_SENDER_NAME));
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_MESSAGE));
                    chatMessage.setImage(documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE));
                    chatMessage.setDateTime(getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                groupChatAdapter.notifyDataSetChanged();
            } else {
                groupChatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);

    };

    //afficher un toast
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    //Methode retour en arrière
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
