package com.example.fitmatch.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.fitmatch.models.User;

import java.util.List;

public class CardStackAdapter extends CardStackView.Adapter<CardStackAdapter.ViewHolder> {

    private List<User> cardList;

    public CardStackAdapter(List<User> cardList) {
        this.cardList = cardList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User card = cardList.get(position);
        holder.cardImage.setImageResource(card.getImageId());
        holder.cardTitle.setText(card.getTitle());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    static class ViewHolder extends CardStackView.ViewHolder {
        ImageView cardImage;
        TextView cardTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.card_image);
            cardTitle = itemView.findViewById(R.id.card_title);
        }
    }
}
