package com.example.fitmatch.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitmatch.databinding.ItemContainerExerciseBinding;
import com.example.fitmatch.models.Exercises;

import java.util.ArrayList;
import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder>{

    private List<Exercises> exercisesList;

    public ExercisesAdapter(ArrayList<Exercises> exercisesList) {
        this.exercisesList = exercisesList;
    }

    @Override
    public ExercisesAdapter.ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemContainerExerciseBinding itemContainerExerciseBinding = ItemContainerExerciseBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );


        return new ExercisesAdapter.ExerciseViewHolder(itemContainerExerciseBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercisesAdapter.ExerciseViewHolder holder, int position) {
        holder.setExerciseData(exercisesList.get(position));
    }

    @Override
    public int getItemCount() {
        return exercisesList.size();
    }

    public void setNewExercises(ArrayList<Exercises> exercisesList) {
        this.exercisesList = exercisesList;
        notifyDataSetChanged();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ItemContainerExerciseBinding binding;
        public ExerciseViewHolder(ItemContainerExerciseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setExerciseData(Exercises exercises) {
            binding.title.setText(exercises.getName());
        }
    }
}
