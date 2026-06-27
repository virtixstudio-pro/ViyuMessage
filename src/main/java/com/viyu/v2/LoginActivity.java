package com.viyu.v2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            
            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Accès refusé : Données manquantes.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Analyse des identifiants...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
