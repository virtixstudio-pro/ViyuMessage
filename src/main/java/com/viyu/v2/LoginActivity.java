package com.viyu.v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ViyuDebug", "Forçage du démarrage direct vers ChatActivity");
        try {
            startActivity(new Intent(LoginActivity.this, ChatActivity.class));
            finish();
        } catch (Exception e) {
            Log.e("ViyuCritical", "Impossible de lancer ChatActivity", e);
        }
    }
}
