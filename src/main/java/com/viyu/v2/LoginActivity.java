package com.viyu.v2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity {
    
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation de Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            } else {
                authenticateUser(email, password);
            }
        });
    }

    private void authenticateUser(String email, String password) {
        Toast.makeText(this, "Connexion en cours...", Toast.LENGTH_SHORT).show();
        
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(this, "Authentification réussie : " + (user != null ? user.getEmail() : "Utilisateur reconnu"), Toast.LENGTH_LONG).show();
                    // Ici, nous lancerons l'activité principale de messagerie plus tard.
                } else {
                    Toast.makeText(this, "Erreur d'authentification : " + (task.getException() != null ? task.getException().getMessage() : "Identifiants invalides"), Toast.LENGTH_LONG).show();
                }
            });
    }
}
