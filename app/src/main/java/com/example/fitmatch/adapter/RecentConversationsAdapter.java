package com.example.fitmatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitmatch.databinding.ItemContainerRecentConversionBinding;
import com.example.fitmatch.listener.ConversionListener;
import com.example.fitmatch.models.ChatMessage;
import com.example.fitmatch.models.User;
import com.example.fitmatch.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;


public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;
    private static Context context;

    public RecentConversationsAdapter(Context context, List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        RecentConversationsAdapter.context = context;
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @Override
    public ConversionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecentConversionBinding binding;

        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }

        void setData(ChatMessage chatMessage) {

            User user = new User();
            user.setId(chatMessage.getConversionId());
            user.setImage(chatMessage.getImage());
            FirebaseFirestore database = FirebaseFirestore.getInstance();

            database.collection(Constants.KEY_COLLECTION_USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                if (user.getId().equals(queryDocumentSnapshot.getId())) {
                                    user.setName(queryDocumentSnapshot.getString(Constants.KEY_USERNAME));
                                    user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                                    user.setBirthDate(queryDocumentSnapshot.getString(Constants.KEY_AGE));
                                    user.setHeight(queryDocumentSnapshot.getString(Constants.KEY_HEIGHT));
                                    user.setWeight(queryDocumentSnapshot.getString(Constants.KEY_WEIGHT));
                                    user.setGender(queryDocumentSnapshot.getString(Constants.KEY_GENDER));
                                    user.setLoseWeight(queryDocumentSnapshot.getBoolean(Constants.KEY_LOSEWEIGHT));
                                    user.setDescription(queryDocumentSnapshot.getString(Constants.KEY_DESCRIPTION));
                                    GeoPoint geoPoint = queryDocumentSnapshot.getGeoPoint(Constants.KEY_ADDRESS);
                                    user.setLatitude(geoPoint.getLatitude());
                                    user.setLongitude(geoPoint.getLongitude());
                                }
                            }
                        }
                    });


            Glide.with(context)
                    .load(chatMessage.getImage())
                    .into(binding.imageUser);

            binding.textUsername.setText(chatMessage.getConversionName());
            binding.textRecentMessage.setText(chatMessage.getConversionMessage());
            binding.getRoot().setOnClickListener(v -> conversionListener.onConversionClicked(user));
        }


    }


}
