package com.example.petfinder;

public class Pet {
    private int petImageResource;
    private String petName, petAge;

    public Pet(String petName, String petAge, int petImage) {
        this.petName = petName;
        this.petAge = petAge;
        this.petImageResource = petImage;
    }

    public int getPetImageResource() {
        return petImageResource;
    }

    public String getPetName() {
        return petName;
    }

    public String getPetAge() {
        return petAge;
    }
}
