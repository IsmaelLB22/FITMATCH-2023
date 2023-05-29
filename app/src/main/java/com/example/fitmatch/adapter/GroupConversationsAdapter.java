package com.example.fitmatch.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fitmatch.databinding.ItemContainerGroupConversionBinding;
import com.example.fitmatch.listener.GroupConversionListener;

import java.util.List;


public class GroupConversationsAdapter extends RecyclerView.Adapter<GroupConversationsAdapter.ConversionViewHolder> {

    private final List<String> groupConversations;
    private final GroupConversionListener groupConversionListener;

    public GroupConversationsAdapter(List<String> groupConversations, GroupConversionListener groupConversionListener) {

        this.groupConversations = groupConversations;
        this.groupConversionListener = groupConversionListener;
    }

    @Override
    public ConversionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerGroupConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(ConversionViewHolder holder, int position) {
        holder.setData(groupConversations.get(position));
    }

    @Override
    public int getItemCount() {
        return groupConversations.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {
        ItemContainerGroupConversionBinding binding;

        ConversionViewHolder(ItemContainerGroupConversionBinding itemContainerGroupConversionBinding) {
            super(itemContainerGroupConversionBinding.getRoot());
            binding = itemContainerGroupConversionBinding;
        }

        void setData(String groupConversations) {
            binding.title.setText(groupConversations);
            binding.getRoot().setOnClickListener(v -> groupConversionListener.onGroupConversionClicked(groupConversations));
        }


    }


}
