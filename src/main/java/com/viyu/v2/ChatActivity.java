package com.viyu.v2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText inputMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d("DebugViyu", "ChatActivity onCreate");
        mDatabase = FirebaseDatabase.getInstance("https://viyu-message-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);

        if (btnSend == null) {
            Log.e("DebugViyu", "ERREUR: btnSend est null ! Le XML ne correspond pas.");
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DebugViyu", "Bouton envoyer cliqué");
                String message = inputMessage.getText().toString().trim();
                Log.d("DebugViyu", "Message saisi : " + message);
                if (!message.isEmpty()) {
                    sendMessage(message);
                } else {
                    Log.d("DebugViyu", "Message vide détecté");
                    Toast.makeText(ChatActivity.this, "Le message est vide", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(String text) {
        Log.d("DebugViyu", "Tentative d'envoi Firebase : " + text);
        mDatabase.child("TestChat").push().setValue(text)
            .addOnSuccessListener(aVoid -> {
                Log.d("DebugViyu", "Envoi Firebase réussi");
                Toast.makeText(ChatActivity.this, "Message envoyé !", Toast.LENGTH_SHORT).show();
                inputMessage.setText("");
            })
            .addOnFailureListener(e -> {
                String errorMessage = e.getMessage();
                Log.e("DebugViyu", "Erreur envoi Firebase: " + errorMessage, e);
                Toast.makeText(ChatActivity.this, "Erreur : " + errorMessage, Toast.LENGTH_LONG).show();
            });
    }
}
