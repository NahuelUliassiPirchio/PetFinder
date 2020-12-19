package com.example.petfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder> {
    List<Pet> pets;
    Context mainContext;

    public SelectionAdapter(List<Pet> pets, Context context) {
        this.pets = pets;
        mainContext = context;
    }

    @NonNull
    @Override
    public SelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectionAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lost_pet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionAdapter.ViewHolder holder, int position) {
        Pet currentPet = pets.get(position);
        holder.petName.setText(currentPet.getPetName());
        holder.petAge.setText(currentPet.getPetAge());
        Toast.makeText(mainContext,currentPet.getPetAge(),Toast.LENGTH_SHORT).show();
        Glide.with(mainContext).load(currentPet.getPetImageResource()).into(holder.petImage);
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView petImage;
        TextView petName, petAge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.pet_name);
            petAge = itemView.findViewById(R.id.pet_age);
            petImage = itemView.findViewById(R.id.pet_image);
        }

    }
}
