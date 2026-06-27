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

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(30, 30, 30, 30);
        mainLayout.setBackgroundColor(Color.parseColor("#121212"));

        messagesListView = new ListView(this);
        messagesListView.setDivider(null);
        messagesListView.setStackFromBottom(true);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        mainLayout.addView(messagesListView, listParams);

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);
        inputLayout.setBackgroundColor(Color.parseColor("#1E1E1E"));
        inputLayout.setPadding(15, 15, 15, 15);

        EditText messageInput = new EditText(this);
        messageInput.setHint("Saisir un message...");
        messageInput.setTextColor(Color.WHITE);
        messageInput.setHintTextColor(Color.parseColor("#888888"));
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        inputLayout.addView(messageInput, inputParams);

        Button sendButton = new Button(this);
        sendButton.setText("Envoyer");
        sendButton.setBackgroundColor(Color.parseColor("#00E676"));
        sendButton.setTextColor(Color.parseColor("#121212"));
        inputLayout.addView(sendButton);

        LinearLayout.LayoutParams inputLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputLayoutParam.topMargin = 15;
        mainLayout.addView(inputLayout, inputLayoutParam);

        setContentView(mainLayout);

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

        messageList = new ArrayList<>();
        adapter = new MessagesAdapter();
        messagesListView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                String messageId = mDatabase.child("TestChat").push().getKey();
                String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                ChatMessage message = new ChatMessage(currentUserId, messageText, timestamp);

                if (messageId != null) {
                    mDatabase.child("TestChat").child(messageId).setValue(message)
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

        mDatabase.child("TestChat").addValueEventListener(new ValueEventListener() {
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
            LinearLayout messageWrapper;
            LinearLayout messageContainer;
            TextView textMessage;
            TextView textTime;

            if (convertView == null) {
                messageWrapper = new LinearLayout(ChatActivity.this);
                messageWrapper.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
                messageWrapper.setPadding(15, 15, 15, 15);

                messageContainer = new LinearLayout(ChatActivity.this);
                messageContainer.setOrientation(LinearLayout.VERTICAL);
                messageContainer.setPadding(25, 20, 25, 20);
                
                LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                messageContainer.setLayoutParams(containerParams);

                textMessage = new TextView(ChatActivity.this);
                textMessage.setTextSize(16);
                messageContainer.addView(textMessage);

                textTime = new TextView(ChatActivity.this);
                textTime.setTextSize(10);
                textTime.setGravity(Gravity.END);
                
                LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                timeParams.topMargin = 8;
                timeParams.gravity = Gravity.END;
                messageContainer.addView(textTime, timeParams);

                messageWrapper.addView(messageContainer);
                convertView = messageWrapper;
                
                ViewHolder holder = new ViewHolder(messageContainer, textMessage, textTime);
                LinearLayout vTag = messageWrapper;
                vTag.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            ChatMessage msg = messageList.get(position);

            holder.textMessage.setText(msg.getText());
            holder.textTime.setText(msg.getTime());

            LinearLayout.LayoutParams wrapperParams = (LinearLayout.LayoutParams) holder.messageContainer.getLayoutParams();

            if (msg.getSenderId().equals(currentUserId)) {
                holder.messageContainer.setBackgroundColor(Color.parseColor("#00E676"));
                holder.textMessage.setTextColor(Color.parseColor("#121212"));
                holder.textTime.setTextColor(Color.parseColor("#004D40"));
                wrapperParams.gravity = Gravity.END;
            } else {
                holder.messageContainer.setBackgroundColor(Color.parseColor("#333333"));
                holder.textMessage.setTextColor(Color.WHITE);
                holder.textTime.setTextColor(Color.parseColor("#BBBBBB"));
                wrapperParams.gravity = Gravity.START;
            }
            
            holder.messageContainer.setLayoutParams(wrapperParams);
            return convertView;
        }

        private class ViewHolder {
            LinearLayout messageContainer;
            TextView textMessage;
            TextView textTime;

            public ViewHolder(LinearLayout container, TextView msg, TextView time) {
                this.messageContainer = container;
                this.textMessage = msg;
                this.textTime = time;
            }
        }
    }
}
