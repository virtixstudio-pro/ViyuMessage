package com.virtixstudio.viyutest;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private EditText edtMessage;
    private Button btnSend;
    private DatabaseReference dbRef;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userPhone = getIntent().getStringExtra("user_phone");
        if (userPhone == null) userPhone = "Anonyme";

        edtMessage = findViewById(R.id.edt_message_text);
        btnSend = findViewById(R.id.btn_send_message);

        // Connexion directe à ton nœud de test Firebase
        dbRef = FirebaseDatabase.getInstance().getReference().child("TestChat");

        btnSend.setOnClickListener(v -> {
            String text = edtMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                String msgId = dbRef.push().getKey();
                
                Map<String, Object> msgMap = new HashMap<>();
                msgMap.put("sender", userPhone);
                msgMap.put("message", text);
                msgMap.put("timestamp", System.currentTimeMillis());

                if (msgId != null) {
                    dbRef.child(msgId).setValue(msgMap)
                        .addOnSuccessListener(aVoid -> edtMessage.setText(""))
                        .addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Erreur réseau Firebase", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
