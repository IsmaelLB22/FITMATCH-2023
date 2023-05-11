package com.example.fitmatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitmatch.databinding.CardBinding;
import com.example.fitmatch.models.User;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.myViewHolder> {

    private List<User> cardList;
    private static Context context;
    public CardAdapter (Context context, List<User> cardList){
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater li=(LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // CardBinding binding = CardBinding.inflate(li);
       // return new myViewHolder(binding);

        CardBinding cardBinding = CardBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new CardAdapter.myViewHolder(cardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.myViewHolder holder, int position) {

        holder.setCardData(cardList.get(position));

    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void setNewCards(List<User> cardList) {
        this.cardList = cardList;
        notifyDataSetChanged();
    }


    public static class myViewHolder extends RecyclerView.ViewHolder{
        CardBinding binding;
        public myViewHolder(@NonNull CardBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setCardData(User user) {

            binding.content.setText(user.getName() + " " + user.getAge());

            Glide.with(context)
                    .load(user.getImage())
                    .into(binding.image);


        }
    }
}
