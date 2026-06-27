package com.viyu.v2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends Activity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialisation de la base de données Firebase
        mDatabase = FirebaseDatabase.getInstance("https://viyu-message-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        EditText messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);

        // Envoi de message
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                String messageId = mDatabase.child("messages").push().getKey();
                Message message = new Message(currentUserId, messageText);
                
                if (messageId != null) {
                    mDatabase.child("messages").child(messageId).setValue(message)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                messageInput.setText("");
                                Toast.makeText(this, "Message envoyé", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });

        // Réception de messages en temps réel
        mDatabase.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Traitement et affichage des messages entrants
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message msg = dataSnapshot.getValue(Message.class);
                    if (msg != null) {
                        // Logique d'affichage (à relier à votre adaptateur/liste Viyu message)
                        System.out.println("Message reçu de " + msg.getSenderId() + " : " + msg.getText());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Impossible de charger les messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Classe modèle pour la structure du message
    public static class Message {
        private String senderId;
        private String text;

        public Message() {}

        public Message(String senderId, String text) {
            this.senderId = senderId;
            this.text = text;
        }

        public String getSenderId() {
            return senderId;
        }

        public String getText() {
            return text;
        }
    }
}
