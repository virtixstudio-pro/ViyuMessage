package com.viyu.v2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends Activity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private ListView messagesListView;
    private MessagesAdapter adapter;
    private ArrayList<ChatMessage> messageList;

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

        mDatabase = FirebaseDatabase.getInstance("https://viyu-message-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        messagesListView = findViewById(R.id.messagesListView);
        messageList = new ArrayList<>();
        adapter = new MessagesAdapter();
        messagesListView.setAdapter(adapter);

        EditText messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                String messageId = mDatabase.child("messages").push().getKey();
                String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                ChatMessage message = new ChatMessage(currentUserId, messageText, timestamp);

                if (messageId != null) {
                    mDatabase.child("messages").child(messageId).setValue(message)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                messageInput.setText("");
                            } else {
                                Toast.makeText(this, "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });

        mDatabase.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage msg = dataSnapshot.getValue(ChatMessage.class);
                    if (msg != null) {
                        messageList.add(msg);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Impossible de charger les messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ChatMessage {
        private String senderId;
        private String text;
        private String time;

        public ChatMessage() {}

        public ChatMessage(String senderId, String text, String time) {
            this.senderId = senderId;
            this.text = text;
            this.time = time;
        }

        public String getSenderId() {
            return senderId;
        }

        public String getText() {
            return text;
        }

        public String getTime() {
            return time;
        }
    }

    private class MessagesAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public Object getItem(int position) {
            return messageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.item_message, parent, false);
            }

            ChatMessage msg = messageList.get(position);

            LinearLayout messageContainer = convertView.findViewById(R.id.messageContainer);
            TextView textMessage = convertView.findViewById(R.id.textMessage);
            TextView textTime = convertView.findViewById(R.id.textTime);

            textMessage.setText(msg.getText());
            textTime.setText(msg.getTime());

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageContainer.getLayoutParams();

            if (msg.getSenderId().equals(currentUserId)) {
                messageContainer.setBackgroundColor(Color.parseColor("#00E676"));
                textMessage.setTextColor(Color.parseColor("#121212"));
                textTime.setTextColor(Color.parseColor("#004D40"));
                params.gravity = Gravity.END;
            } else {
                messageContainer.setBackgroundColor(Color.parseColor("#333333"));
                textMessage.setTextColor(Color.parseColor("#FFFFFF"));
                textTime.setTextColor(Color.parseColor("#BBBBBB"));
                params.gravity = Gravity.START;
            }
            
            messageContainer.setLayoutParams(params);

            return convertView;
        }
    }
}
