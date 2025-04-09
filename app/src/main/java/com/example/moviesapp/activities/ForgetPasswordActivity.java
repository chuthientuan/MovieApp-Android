package com.example.moviesapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moviesapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    private ImageView backImg;
    private EditText edtEmail;
    private Button btnSendPasswordResetEmail;
    private TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backImg = findViewById(R.id.backImg);
        edtEmail = findViewById(R.id.edtEmail);
        txtLogin = findViewById(R.id.txtLogin);
        btnSendPasswordResetEmail = findViewById(R.id.btnSendPasswordResetEmail);
        btnSendPasswordResetEmail.setOnClickListener(v -> sendPasswordResetEmail());

        backImg.setOnClickListener(v -> finish());
        txtLogin.setOnClickListener(v -> finish());
    }

    private void sendPasswordResetEmail() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                       finish();
                   } else {
                       Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                   }
                });
    }
}