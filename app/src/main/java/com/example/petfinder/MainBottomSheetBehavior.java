package com.example.petfinder;

import android.content.Context;
import android.content.res.TypedArray;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

public class MainBottomSheetBehavior {

    private GoogleMap mainMap;
    private Context mainContext;
    private LinearLayout bottomSheetLinear;
    private RecyclerView lostPetsRecycler;
    private SelectionAdapter lostPetsAdapter;
    private List<Pet> petList = new ArrayList<>();

    public MainBottomSheetBehavior(Context mainContext, LinearLayout bottomSheetLinear) {
        this.mainContext = mainContext;
        this.bottomSheetLinear = bottomSheetLinear;
        graphicRecycler();
    }

    private void graphicRecycler() {
        initializeData();

        Button startSearchButton = bottomSheetLinear.findViewById(R.id.start_search_button);
        lostPetsAdapter = new SelectionAdapter(petList, mainContext, startSearchButton);
        lostPetsRecycler = bottomSheetLinear.findViewById(R.id.lost_pets_recycler);
        lostPetsRecycler.addItemDecoration(new DividerItemDecoration(mainContext, DividerItemDecoration.HORIZONTAL));
        lostPetsRecycler.setAdapter(lostPetsAdapter);
        lostPetsRecycler.setLayoutManager(new LinearLayoutManager(mainContext, LinearLayoutManager.HORIZONTAL, false));
    }

    public void setMainMap(GoogleMap mMap) {
        mainMap = mMap;
    }

    private void initializeData() {
        String[] petNames = mainContext.getResources()
                .getStringArray(R.array.pet_names);
        String[] petAges = mainContext.getResources()
                .getStringArray(R.array.pet_ages);
        TypedArray petImageResources = mainContext.getResources()
                .obtainTypedArray(R.array.pet_images);

        for (int i = 0; i < petNames.length; i++) {
            petList.add(new Pet(
                    petNames[i],
                    petAges[i],
                    petImageResources.getResourceId(i, 0)
            ));
        }
        petImageResources.recycle();
    }
}