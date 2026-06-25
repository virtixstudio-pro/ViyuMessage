package com.virtixstudio.viyutest;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private EditText edtMessage;
    private Button btnSend;
    private ListView listMessages;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private ArrayList<ChatMessage> messageList;
    private MessageAdapter adapter;

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
        listMessages = findViewById(R.id.list_messages);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter();
        listMessages.setAdapter(adapter);

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
                            Log.e("FIREBASE_WRITE_ERROR", "Erreur", e);
                            Toast.makeText(ChatActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                }
            } else {
                Toast.makeText(ChatActivity.this, "Le champ est vide", Toast.LENGTH_SHORT).show();
            }
        });

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage msg = snapshot.getValue(ChatMessage.class);
                if (msg != null) {
                    messageList.add(msg);
                    adapter.notifyDataSetChanged();
                    listMessages.setSelection(adapter.getCount() - 1);
                }
            }
            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static class ChatMessage {
        public String sender;
        public String message;
        public long timestamp;
        public ChatMessage() {}
    }

    private class MessageAdapter extends ArrayAdapter<ChatMessage> {
        public MessageAdapter() {
            super(ChatActivity.this, 0, messageList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_message, parent, false);
            }

            ChatMessage msg = getItem(position);
            LinearLayout bubbleLayout = convertView.findViewById(R.id.bubble_layout);
            TextView txtContent = convertView.findViewById(R.id.txt_message_content);
            TextView txtTime = convertView.findViewById(R.id.txt_message_time);

            if (msg != null) {
                txtContent.setText(msg.message);
                
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                txtTime.setText(sdf.format(new Date(msg.timestamp)));

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bubbleLayout.getLayoutParams();
                if (params == null) {
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                if (msg.sender != null && msg.sender.equals(currentUserId)) {
                    params.gravity = Gravity.END;
                    bubbleLayout.setBackgroundResource(R.drawable.bubble_out);
                    txtContent.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                    txtTime.setTextColor(android.graphics.Color.parseColor("#E0E0E0"));
                } else {
                    params.gravity = Gravity.START;
                    bubbleLayout.setBackgroundResource(R.drawable.bubble_in);
                    txtContent.setTextColor(android.graphics.Color.parseColor("#000000"));
                    txtTime.setTextColor(android.graphics.Color.parseColor("#666666"));
                }
                bubbleLayout.setLayoutParams(params);
            }
            return convertView;
        }
    }
}
