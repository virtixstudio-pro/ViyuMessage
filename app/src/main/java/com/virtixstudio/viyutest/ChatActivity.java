package com.virtixstudio.viyutest;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private EditText edtMessage;
    private Button btnSend;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        
        if (user != null) {
            currentUserId = user.getUid();
        } else {
            currentUserId = "Utilisateur_Non_Authentifie";
        }

        edtMessage = findViewById(R.id.edt_message_text);
        btnSend = findViewById(R.id.btn_send_message);

        dbRef = FirebaseDatabase.getInstance().getReference().child("TestChat");

        btnSend.setOnClickListener(v -> {
            String text = edtMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                String msgId = dbRef.push().getKey();

                Map<String, Object> msgMap = new HashMap<>();
                msgMap.put("sender", currentUserId);
                msgMap.put("message", text);
                msgMap.put("timestamp", System.currentTimeMillis());

                if (msgId != null) {
                    dbRef.child(msgId).setValue(msgMap)
                        .addOnSuccessListener(aVoid -> {
                            edtMessage.setText("");
                            Toast.makeText(ChatActivity.this, "Message envoyé", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FIREBASE_WRITE_ERROR", "Erreur lors de l'envoi", e);
                            Toast.makeText(ChatActivity.this, "Refus Firebase : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                }
            } else {
                Toast.makeText(ChatActivity.this, "Le champ est vide", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
