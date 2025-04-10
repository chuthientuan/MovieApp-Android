package com.example.moviesapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.moviesapp.R;
import com.example.moviesapp.entities.User;
import com.example.moviesapp.util.FirebaseUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

public class EditProfileActivity extends AppCompatActivity {
    private final int REQUEST_CODE_STORAGE = 1;
    private final int REQUEST_CODE_SELECT_IMAGE = 2;
    private ImageView backImg;
    private ImageView avatarImg;
    private TextView emailText;
    private TextInputEditText nameEditText;
    private TextView txtChangePassword;
    private LinearLayout passwordFieldsLayout;
    private TextInputEditText currentPasswordEditText;
    private TextInputEditText newPasswordEditText;
    private TextInputEditText confirmPasswordEditText;
    private MaterialButton saveBtn;
    private User currentUser;
    private String selectedImagePath = "";
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backImg = findViewById(R.id.backImg);
        avatarImg = findViewById(R.id.avatarImg);
        emailText = findViewById(R.id.emailText);
        nameEditText = findViewById(R.id.nameEditText);
        txtChangePassword = findViewById(R.id.txtChangePassword);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        saveBtn = findViewById(R.id.saveBtn);
        passwordFieldsLayout = findViewById(R.id.passwordFieldsLayout);

        backImg.setOnClickListener(v -> finish());
        txtChangePassword.setOnClickListener(v -> {
            if (passwordFieldsLayout.getVisibility() == View.GONE) {
                passwordFieldsLayout.setVisibility(View.VISIBLE);
            } else {
                passwordFieldsLayout.setVisibility(View.GONE);
            }
        });
        avatarImg.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_STORAGE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                }
            } else {
                pickImage();
            }
        });
        loadUserProfile();
        saveBtn.setOnClickListener(v -> {
            updateUserName();
            updatePassword();
            if (!selectedImagePath.isEmpty()) {
                uploadAvatar(selectedImagePath);
            }
        });
    }

    private void loadUserProfile() {
        FirebaseUtil.getDataUser().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                currentUser = task.getResult().getValue(User.class);
                if (currentUser != null) {
                    emailText.setText(currentUser.getEmail());
                    nameEditText.setText(currentUser.getUserName());
                    if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                        Glide.with(this).load(currentUser.getAvatar()).into(avatarImg);
                    } else {
                        avatarImg.setImageResource(R.drawable.avatar_default);
                    }
                }
            } else {
                Toast.makeText(this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserName() {
        String newUserName = nameEditText.getText().toString().trim();
        if (!newUserName.isEmpty()) {
            FirebaseUtil.getDataUser().child("userName").setValue(newUserName)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show());
        }
    }

    private void updatePassword() {
        String currentPassword = currentPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        if (!currentPassword.isEmpty() && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPasswordEditText.getText().toString())) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(passwordTask -> {
                        if (passwordTask.isSuccessful()) {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            currentPasswordEditText.setText("");
                            newPasswordEditText.setText("");
                            confirmPasswordEditText.setText("");
                        } else {
                            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    Toast.makeText(this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadAvatar(String imageUri) {
        Uri fileUri  = Uri.parse(imageUri);
        if (fileUri  == null) return;
        String fileName = "avatars/" + user.getUid();
        FirebaseStorage.getInstance().getReference(fileName).putFile(fileUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            FirebaseStorage.getInstance().getReference(fileName)
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String imageUrl = uri.toString();
                                        FirebaseUtil.getDataUser().child("avatar").setValue(imageUrl)
                                                .addOnSuccessListener(unused ->
                                                        Toast.makeText(this, "Avatar updated successfully", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(this, "Failed to update avatar in database", Toast.LENGTH_SHORT).show());
                                    });
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    selectedImagePath = selectedImage.toString();
                    Glide.with(this).load(selectedImage).into(avatarImg);
                } else {
                    selectedImagePath = "";
                }
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}