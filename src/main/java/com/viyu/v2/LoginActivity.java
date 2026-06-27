package com.viyu.v2;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Redirection directe vers ChatActivity pour tester le module de message
        startActivity(new Intent(this, ChatActivity.java.equals("") ? LoginActivity.class : ChatActivity.class));
        finish();
    }
}
