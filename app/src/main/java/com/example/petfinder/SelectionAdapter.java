package com.example.petfinder;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder> {
    List<Pet> pets;
    Context mainContext;
    List<Pet> selectedPets = new LinkedList<>();
    Button startSearchButton;

    public SelectionAdapter(List<Pet> pets, Context mainContext, Button startSearchButton) {
        this.pets = pets;
        this.mainContext = mainContext;
        this.startSearchButton = startSearchButton;
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
        Glide.with(mainContext).load(currentPet.getPetImageResource()).into(holder.petImage);
    }

    void searchButton (){
        boolean isSelectedListEmpty = selectedPets.isEmpty();
        startSearchButton.setEnabled(!isSelectedListEmpty);
        int backgroundColor = !isSelectedListEmpty ? Color.RED: Color.rgb(158,62,55);
        startSearchButton.setBackgroundColor(backgroundColor);
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView petImage;
        TextView petName, petAge;
        boolean selected = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petName = itemView.findViewById(R.id.pet_name);
            petAge = itemView.findViewById(R.id.pet_age);
            petImage = itemView.findViewById(R.id.pet_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Pet actualPet = pets.get(getAdapterPosition());
            if (selected) {
                view.setBackgroundColor(Color.DKGRAY);
                selectedPets.remove(actualPet);
                selected = false;
            } else {
                view.setBackgroundColor(Color.CYAN);
                selectedPets.add(actualPet);
                selected = true;
            }
            searchButton();
        }
    }
}
