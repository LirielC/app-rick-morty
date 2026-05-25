package com.example.rickandmortyapp.ui.characters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.character.Character;
import com.example.rickandmortyapp.util.CharacterImageCache;
import com.example.rickandmortyapp.util.CharacterValueMapper;
import com.google.android.material.chip.Chip;

public class CharacterViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final TextView nameTextView;
    private final Chip statusChip;
    private final TextView speciesTextView;
    private final TextView genderTextView;
    private final TextView locationTextView;

    public CharacterViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.characterImageView);
        nameTextView = itemView.findViewById(R.id.characterNameTextView);
        statusChip = itemView.findViewById(R.id.characterStatusChip);
        speciesTextView = itemView.findViewById(R.id.characterSpeciesTextView);
        genderTextView = itemView.findViewById(R.id.characterGenderTextView);
        locationTextView = itemView.findViewById(R.id.characterLocationTextView);
    }

    public void bind(Character character, CharacterAdapter.OnCharacterClickListener listener) {
        String cachedImageUri = CharacterImageCache.get(imageView.getContext(), character.getId());
        Glide.with(imageView.getContext())
                .load(cachedImageUri != null && !cachedImageUri.isEmpty() ? cachedImageUri : character.getImage())
                .placeholder(R.drawable.placeholder_character)
                .error(R.drawable.placeholder_character)
                .into(imageView);
        nameTextView.setText(character.getName());
        String rawStatus = safeText(character.getStatus());
        statusChip.setText(CharacterValueMapper.toDisplayStatus(itemView.getContext(), rawStatus));
        statusChip.setChipBackgroundColorResource(resolveStatusColor(rawStatus));
        speciesTextView.setText(itemView.getContext().getString(
                R.string.character_species_value,
                CharacterValueMapper.toDisplaySpecies(itemView.getContext(), character.getSpecies())
        ));
        genderTextView.setText(itemView.getContext().getString(
                R.string.character_gender_value,
                CharacterValueMapper.toDisplayGender(itemView.getContext(), safeText(character.getGender()))
        ));
        locationTextView.setText(itemView.getContext().getString(
                R.string.character_location_value,
                character.getLocation() != null ? safeText(character.getLocation().getName()) : itemView.getContext().getString(R.string.unknown)
        ));
        itemView.setOnClickListener(v -> listener.onCharacterClick(character));
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty()
                ? itemView.getContext().getString(R.string.unknown)
                : value;
    }

    private int resolveStatusColor(String status) {
        if ("alive".equalsIgnoreCase(status)) {
            return R.color.app_chip_alive;
        }
        if ("dead".equalsIgnoreCase(status)) {
            return R.color.app_chip_dead;
        }
        return R.color.app_chip_unknown;
    }
}
