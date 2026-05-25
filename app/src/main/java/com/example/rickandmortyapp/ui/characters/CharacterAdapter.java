package com.example.rickandmortyapp.ui.characters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.character.Character;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CharacterAdapter extends ListAdapter<Character, CharacterViewHolder> {
    public interface OnCharacterClickListener {
        void onCharacterClick(Character character);
    }

    private static final DiffUtil.ItemCallback<Character> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Character>() {
                @Override
                public boolean areItemsTheSame(@NonNull Character oldItem, @NonNull Character newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Character oldItem, @NonNull Character newItem) {
                    return Objects.equals(oldItem.getName(), newItem.getName())
                            && Objects.equals(oldItem.getStatus(), newItem.getStatus())
                            && Objects.equals(oldItem.getSpecies(), newItem.getSpecies())
                            && Objects.equals(oldItem.getGender(), newItem.getGender())
                            && Objects.equals(oldItem.getImage(), newItem.getImage())
                            && Objects.equals(
                            oldItem.getLocation() != null ? oldItem.getLocation().getName() : null,
                            newItem.getLocation() != null ? newItem.getLocation().getName() : null
                    );
                }
            };

    private final OnCharacterClickListener listener;

    public CharacterAdapter(OnCharacterClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @Override
    public void submitList(List<Character> characters) {
        super.submitList(characters == null ? null : new ArrayList<>(characters));
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CharacterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }
}
