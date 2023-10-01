package com.lazypanda.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lazypanda.R;
import com.lazypanda.adapters.AdapterChats;
import com.lazypanda.databinding.ActivityChatBinding;
import com.lazypanda.models.ModelChat;

import com.lazypanda.models.ModelUsers;
import com.lazypanda.notifications.Data;
//import com.lazypanda.notifications.Response;
import com.lazypanda.notifications.Sender;
import com.lazypanda.notifications.Token;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;


public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    AppBarLayout appBarLayout;

    ImageView chat_users_img, senderImage;
    TextView chat_users_name, chat_users_online_status, chat_users_typing_status;
    ImageButton send_msg;
    EditText enter_msg;
    RecyclerView recyclerView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseRefer;
    FirebaseUser fUser;
    FirebaseAuth fAuth;

    ValueEventListener seenValeListener;
    DatabaseReference seenReference;

    List<ModelChat> chatList;
    AdapterChats adapterChat;

    String hisUid;
    String myUid;
    String hisImage;
    
    private RequestQueue requestQueue;
    private boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar a = findViewById(R.id.appbar);
        setSupportActionBar(a);

        fAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRefer = firebaseDatabase.getReference("Users");

        chat_users_img = findViewById(R.id.chat_user_image);
        chat_users_name = findViewById(R.id.chat_user_name);
        chat_users_online_status = findViewById(R.id.user_online);
        send_msg = findViewById(R.id.send_msg_btn);
        enter_msg = findViewById(R.id.chat_user_msg);
        recyclerView = findViewById(R.id.chat_recycler_view);
        
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        LinearLayoutManager linearLM = new LinearLayoutManager(ChatActivity.this);
        linearLM.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(linearLM);
        
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        Query query = databaseRefer.orderByChild("uId").equalTo(hisUid);
        query.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            hisImage = "" + ds.child("image").getValue();

                            String typingStatus = "" + ds.child("typingTo").getValue();

                            if (typingStatus.equals(myUid)) {
                                chat_users_online_status.setText("typing");
                            } else {
                                String onlineStatus = "" + ds.child("onlineStatus").getValue();
                                if (onlineStatus.equals("online")) {
                                    chat_users_online_status.setText(onlineStatus);
                                } else {
                                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                    cal.setTimeInMillis(Long.parseLong(onlineStatus));
                                    SimpleDateFormat dayFormat =
                                            new SimpleDateFormat("E", Locale.ENGLISH);
                                    String dayOfWeek = dayFormat.format(cal.getTime());
                                    SimpleDateFormat timeFormat =
                                            new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                                    String time = timeFormat.format(cal.getTime());
                                    String dateTime = time+"/"+dayOfWeek;

                                    chat_users_online_status.setText(dateTime);
                                }
                            }

                            chat_users_name.setText(name);

                            try {
                                Glide.with(ChatActivity.this)
                                        .load(hisImage)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .circleCrop()
                                        .into(chat_users_img);
                            } catch (Exception e) {
                                Glide.with(ChatActivity.this)
                                        .load(R.drawable.p)
                                        .circleCrop()
                                        .into(chat_users_img);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError arg0) {}
                });

        send_msg.setOnClickListener(
                new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        notify = true;
                        String enter_msg_string = enter_msg.getText().toString().trim();
                        if (TextUtils.isEmpty(enter_msg_string)) {
                            Toast.makeText(
                                            ChatActivity.this,
                                            "Cannot send empty message",
                                            Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            sendMessage(enter_msg_string);
                        }
                        enter_msg.setText(null);
                    }
                });

        enter_msg.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.toString().trim().length()==0) {
                        	checkTypingStatus("noOne");
                        }
                        else{
                            checkTypingStatus(hisUid);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {}
                });

        readMessage();
        seenMessages();
    }

    private void seenMessages() {
        seenReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenValeListener =
                seenReference.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnap) {
                                for (DataSnapshot ds : dataSnap.getChildren()) {
                                    ModelChat chat = ds.getValue(ModelChat.class);
                                    if (chat.getReceiver().equals(myUid)
                                            && chat.getSender().equals(hisUid)) {
                                        HashMap<String, Object> hashMapSeen = new HashMap<>();
                                        hashMapSeen.put("isSeen", true);
                                        ds.getRef().updateChildren(hashMapSeen);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError arg0) {}
                        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbRefer = FirebaseDatabase.getInstance().getReference("Chats");
        dbRefer.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapShot) {
                        chatList.clear();
                        for (DataSnapshot ds : dataSnapShot.getChildren()) {
                            ModelChat chat = ds.getValue(ModelChat.class);
                            if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)
                                    || chat.getReceiver().equals(hisUid)
                                            && chat.getSender().equals(myUid)) {
                                chatList.add(chat);
                            }
                            adapterChat = new AdapterChats(ChatActivity.this, chatList, hisImage);
                            adapterChat.notifyDataSetChanged();
                            recyclerView.setAdapter(adapterChat);
                            recyclerView.scrollToPosition(adapterChat.getItemCount() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError arg0) {}
                });
    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("isSeen", false);
        databaseReference.child("Chats").push().setValue(hashMap);
        
        String msg = message;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        database.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUsers user = dataSnapshot.getValue(ModelUsers.class);
                if(notify) {
                	sendNotification(hisUid, user.getName(), message);
                }
                notify = false;    
            }

            @Override
            public void onCancelled(DatabaseError arg0) {
            }
        });
    }
    
    private void sendNotification(final String hisUid, final String name, final String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot datasnapShot) {
             for(DataSnapshot ds: datasnapShot.getChildren()){
                 Token token = ds.getValue(Token.class);
                 Data data = new Data(myUid, name+" : "+message, "New Message", hisUid, R.drawable.person);
                 
                 Sender sender = new Sender(data, token.getToken());
                 try { 
                     JSONObject senderJsonObject = new JSONObject(new Gson().toJson(sender));
                     JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send",senderJsonObject, new Response.Listener<JSONObject>(){
                         @Override
                         public void onResponse(JSONObject response){
                             Log.d("JSON_RESPONSE", "onResponse"+response.toString());
                         }
                     },new Response.ErrorListener(){
                         @Override
                         public void onErrorResponse(VolleyError error){
                             Log.d("JSON_RESPONSE", "onResponse"+error.toString());
                         }       
                     }){
                         @Override
                         public Map<String, String> getHeaders() throws AuthFailureError {
                                    
                             Map<String, String> headers = new HashMap<>();
                             headers.put("Content-Type", "application/json");
                             headers.put("Authorization", "key=AAAAEK1mqXs:APA91bHDLzTYU8n9uNOfl3ihhKDMFpuZWM_3KmjQ7c1V8HcGtGrsTgWxdbDuzf7O4IxO4RWzskicGWUiVts-MxEndDWfxM-yga9Ay6V5KISbZZ0MgBQ5534fbg3LudLLjeK9t2O_SnCc");
                                           
                             return headers;
                         }
                     };
                            
                     requestQueue.add(jsonObjectRequest);
                            
                 }catch(JSONException e){
                     e.printStackTrace();
                 }
                        
             }       
        }

        @Override
        public void onCancelled(DatabaseError arg0) {
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = fAuth.getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        } else {
            startActivity(new Intent(ChatActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    private void checkOnlineStatus(String status) {
        DatabaseReference onlineDR =
                FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        onlineDR.updateChildren(hashMap);
    }

    private void checkTypingStatus(String typing) {
        DatabaseReference onlineDR =
                FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        onlineDR.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: Implement this method
        String timeStamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        checkTypingStatus("noOne");
        seenReference.removeEventListener(seenValeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Implement this method
        checkOnlineStatus("online");
    }
}
