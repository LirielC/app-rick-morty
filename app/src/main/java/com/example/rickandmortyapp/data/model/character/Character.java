package com.example.rickandmortyapp.data.model.character;

import java.io.Serializable;
import java.util.List;

public class Character implements Serializable
{
    private int id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;
    private CharacterOrigin origin;
    private CharacterLocation location;
    private String image;
    private List<String> episode;
    private String url;
    private String created;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecies() {
        return species;
    }

    public String getType() {
        return type;
    }

    public String getGender() {
        return gender;
    }

    public CharacterOrigin getOrigin() {
        return origin;
    }

    public CharacterLocation getLocation() {
        return location;
    }

    public String getImage() {
        return image;
    }

    public List<String> getEpisode() {
        return episode;
    }

    public String getUrl() {
        return url;
    }

    public String getCreated() {
        return created;
    }
}


