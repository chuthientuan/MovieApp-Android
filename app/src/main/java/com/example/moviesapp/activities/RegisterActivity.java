package com.example.moviesapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moviesapp.R;
import com.example.moviesapp.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword, edtUserName;
    Button btnRegister;
    TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtUserName = findViewById(R.id.edtUserName);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String userName = edtUserName.getText().toString();
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            registerUser(email, password, userName);
        });
    }

    private void registerUser(String email, String password, String userName) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            User userProfile = new User(userName, email, "");
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            usersRef.setValue(userProfile)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(this, "Register successfully!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Failed to register user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}