package com.viyu.v2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ListView chatListView;
    private ArrayList<String> listMessages;
    private ArrayAdapter<String> adapter;
    private EditText inputMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.e("VabeirDebug", "Erreur activation persistance Firebase", e);
        }

        setContentView(R.layout.activity_chat);

        mDatabase = FirebaseDatabase.getInstance("https://viyu-message-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("TestChat");
        mDatabase.keepSynced(true);

        chatListView = findViewById(R.id.chatListView);
        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);

        listMessages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listMessages);
        if (chatListView != null) {
            chatListView.setAdapter(adapter);
        }

        loadRealtimeMessages();

        if (btnSend != null) {
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = inputMessage.getText().toString().trim();
                    if (!message.isEmpty()) {
                        mDatabase.push().setValue(message)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ChatActivity.this, "Message envoyé", Toast.LENGTH_SHORT).show();
                                inputMessage.setText("");
                            })
                            .addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(ChatActivity.this, "Le message est vide", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void loadRealtimeMessages() {
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                String message = snapshot.getValue(String.class);
                if (message != null) {
                    listMessages.add(message);
                    adapter.notifyDataSetChanged();
                    if (chatListView != null) {
                        chatListView.setSelection(adapter.getCount() - 1);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {}

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("VabeirDebug", "Erreur lecture Firebase: " + error.getMessage());
            }
        });
    }
}
