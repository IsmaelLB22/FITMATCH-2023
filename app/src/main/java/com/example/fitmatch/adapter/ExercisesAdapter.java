package com.example.fitmatch.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitmatch.databinding.ItemContainerExerciseBinding;
import com.example.fitmatch.models.Exercises;

import java.util.ArrayList;
import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder> {

    private List<Exercises> exercisesList;
    private static Context context;

    public ExercisesAdapter(Context context, ArrayList<Exercises> exercisesList) {
        ExercisesAdapter.context = context;
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
            binding.exerciseName.setText(exercises.getName());
            binding.exerciseMuscles.setText("Primary muscles worked: " + exercises.getPrimaryMuscles());
            binding.exerciseSecondaryMuscles.setText("Secondary muscles worked: " + exercises.getSecondaryMuscles());
            binding.exerciseWorkoutType.setText("Workout type: " + exercises.getWorkoutType());
            binding.watchVideoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String videoId = exercises.getYoutubeLink(); // Replace VIDEO_ID with the actual video ID
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoId));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("VIDEO_ID", videoId);
                    context.startActivity(intent);
                }
            });
        }
    }
}
