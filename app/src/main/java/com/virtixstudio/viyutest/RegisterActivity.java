package com.virtixstudio.viyutest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnReg = findViewById(R.id.btn_register);
        btnReg.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.class, ChatActivity.class);
            intent.putExtra("user_phone", "Nouveau_Compte");
            startActivity(intent);
            finish();
        });
    }
}
