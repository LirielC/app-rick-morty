package com.example.rickandmortyapp.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.character.Character;
import com.example.rickandmortyapp.ui.state.CharacterProfileUiState;
import com.example.rickandmortyapp.util.CharacterImageCache;
import com.example.rickandmortyapp.util.CharacterValueMapper;
import com.example.rickandmortyapp.util.PermissionUtils;
import com.example.rickandmortyapp.util.UiState;
import com.example.rickandmortyapp.viewmodel.CharacterProfileViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CharacterProfileActivity extends AppCompatActivity {
    public static final String EXTRA_CHARACTER = "extra_character";
    private static final String STATE_CURRENT_PHOTO_URI = "state_current_photo_uri";

    private CharacterProfileViewModel viewModel;
    private ImageView profileImageView;
    private ProgressBar progressBar;
    private LinearLayout loadingContainer;
    private Uri currentPhotoUri;
    private TextView profileNameTextView;
    private TextView profileFullNameTextView;
    private TextView profileIdTextView;
    private TextView profileStatusTextView;
    private TextView profileSpeciesTextView;
    private TextView profileTypeTextView;
    private TextView profileGenderTextView;
    private TextView profileOriginTextView;
    private TextView profileOriginUrlTextView;
    private TextView profileLocationTextView;
    private TextView profileLocationUrlTextView;
    private TextView profileEpisodesTextView;
    private TextView profileImageUrlTextView;
    private TextView profileApiUrlTextView;
    private TextView profileCreatedTextView;
    private Chip profileStatusChip;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) {
                    openCamera();
                } else {
                    Snackbar.make(profileImageView, R.string.camera_permission_denied, Snackbar.LENGTH_LONG).show();
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && currentPhotoUri != null) {
                    viewModel.onImageCaptured(currentPhotoUri);
                    Snackbar.make(profileImageView, R.string.camera_success, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(profileImageView, R.string.camera_error, Snackbar.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_profile);

        viewModel = new ViewModelProvider(this).get(CharacterProfileViewModel.class);
        bindViews();
        MaterialToolbar toolbar = findViewById(R.id.profileToolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        Character character = (Character) getIntent().getSerializableExtra(EXTRA_CHARACTER);
        if (character != null) {
            viewModel.setCharacter(character);
            String persistedImageUri = CharacterImageCache.get(this, character.getId());
            if (persistedImageUri != null && !persistedImageUri.isEmpty()) {
                viewModel.restoreCapturedImage(persistedImageUri);
            }
        }

        if (savedInstanceState != null) {
            String restoredCurrentPhotoUri = savedInstanceState.getString(STATE_CURRENT_PHOTO_URI);
            if (restoredCurrentPhotoUri != null && !restoredCurrentPhotoUri.isEmpty()) {
                currentPhotoUri = Uri.parse(restoredCurrentPhotoUri);
                viewModel.restoreCapturedImage(restoredCurrentPhotoUri);
            }
        }

        Button cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(v -> {
            if (PermissionUtils.hasPermission(this, Manifest.permission.CAMERA)) {
                openCamera();
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        viewModel.getProfileState().observe(this, this::renderState);
    }

    private void bindViews() {
        profileImageView = findViewById(R.id.profileImageView);
        progressBar = findViewById(R.id.profileProgressBar);
        loadingContainer = findViewById(R.id.profileLoadingContainer);
        profileNameTextView = findViewById(R.id.profileNameTextView);
        profileFullNameTextView = findViewById(R.id.profileFullNameTextView);
        profileIdTextView = findViewById(R.id.profileIdTextView);
        profileStatusTextView = findViewById(R.id.profileStatusTextView);
        profileSpeciesTextView = findViewById(R.id.profileSpeciesTextView);
        profileTypeTextView = findViewById(R.id.profileTypeTextView);
        profileGenderTextView = findViewById(R.id.profileGenderTextView);
        profileOriginTextView = findViewById(R.id.profileOriginTextView);
        profileOriginUrlTextView = findViewById(R.id.profileOriginUrlTextView);
        profileLocationTextView = findViewById(R.id.profileLocationTextView);
        profileLocationUrlTextView = findViewById(R.id.profileLocationUrlTextView);
        profileEpisodesTextView = findViewById(R.id.profileEpisodesTextView);
        profileImageUrlTextView = findViewById(R.id.profileImageUrlTextView);
        profileApiUrlTextView = findViewById(R.id.profileApiUrlTextView);
        profileCreatedTextView = findViewById(R.id.profileCreatedTextView);
        profileStatusChip = findViewById(R.id.profileStatusChip);
    }

    private void renderCharacter(Character character) {
        if (character == null) {
            return;
        }

        String cachedImageUri = CharacterImageCache.get(this, character.getId());
        Glide.with(this)
                .load(cachedImageUri != null && !cachedImageUri.isEmpty() ? cachedImageUri : character.getImage())
                .placeholder(R.drawable.placeholder_character)
                .error(R.drawable.placeholder_character)
                .into(profileImageView);

        profileNameTextView.setText(safeText(character.getName()));
        profileFullNameTextView.setText(safeText(character.getName()));
        profileIdTextView.setText(String.valueOf(character.getId()));
        String rawStatus = safeText(character.getStatus());
        String displayStatus = CharacterValueMapper.toDisplayStatus(this, rawStatus);
        profileStatusTextView.setText(displayStatus);
        profileStatusChip.setText(displayStatus);
        profileStatusChip.setChipBackgroundColorResource(resolveStatusColor(rawStatus));
        profileSpeciesTextView.setText(CharacterValueMapper.toDisplaySpecies(this, character.getSpecies()));
        profileTypeTextView.setText(CharacterValueMapper.toDisplayType(character.getType()));
        profileGenderTextView.setText(CharacterValueMapper.toDisplayGender(this, safeText(character.getGender())));
        profileOriginTextView.setText(character.getOrigin() != null ? safeText(character.getOrigin().getName()) : getString(R.string.unknown));
        profileOriginUrlTextView.setText(character.getOrigin() != null ? safeText(character.getOrigin().getUrl()) : getString(R.string.unknown));
        profileLocationTextView.setText(character.getLocation() != null ? safeText(character.getLocation().getName()) : getString(R.string.unknown));
        profileLocationUrlTextView.setText(character.getLocation() != null ? safeText(character.getLocation().getUrl()) : getString(R.string.unknown));
        profileEpisodesTextView.setText(String.valueOf(character.getEpisode() != null ? character.getEpisode().size() : 0));
        profileImageUrlTextView.setText(safeText(character.getImage()));
        profileApiUrlTextView.setText(safeText(character.getUrl()));
        profileCreatedTextView.setText(safeText(character.getCreated()));
    }

    private void renderState(CharacterProfileUiState state) {
        Character character = state.getCharacter();
        if (character != null) {
            renderCharacter(character);
        }

        String imageUri = state.getCapturedImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            if (character != null) {
                CharacterImageCache.put(this, character.getId(), imageUri);
            }
            Glide.with(this).load(imageUri).into(profileImageView);
        }

        UiState<String> postState = state.getPostState();
        boolean isLoading = postState.getStatus() == UiState.Status.LOADING;
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        Integer messageRes = state.getPostMessageEvent() != null
                ? state.getPostMessageEvent().getContentIfNotHandled()
                : null;
        if (messageRes != null) {
            Snackbar.make(profileImageView, messageRes, Snackbar.LENGTH_LONG).show();
        }
    }

    private void openCamera() {
        try {
            File file = createImageFile();
            currentPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
            cameraLauncher.launch(intent);
        } catch (IOException exception) {
            Snackbar.make(profileImageView, R.string.camera_error, Snackbar.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentPhotoUri != null) {
            outState.putString(STATE_CURRENT_PHOTO_URI, currentPhotoUri.toString());
        }
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? getString(R.string.unknown) : value;
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
