package com.virtixstudio.viyutest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {
    private EditText edtPhone;
    private Button btnLogin;
    private TextView btnGotoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        edtPhone = findViewById(R.id.edt_phone);
        btnLogin = findViewById(R.id.btn_login);
        btnGotoRegister = findViewById(R.id.btn_goto_register);

        btnLogin.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            if (!TextUtils.isEmpty(phone)) {
                // On passe directement à l'écran de chat pour tester la liaison
                Intent intent = new Intent(AuthActivity.this, ChatActivity.class);
                intent.putExtra("user_phone", phone);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Entrez un numéro", Toast.LENGTH_SHORT).show();
            }
        });

        btnGotoRegister.setOnClickListener(v -> {
            startActivity(new Intent(AuthActivity.this, RegisterActivity.class));
        });
    }
}
