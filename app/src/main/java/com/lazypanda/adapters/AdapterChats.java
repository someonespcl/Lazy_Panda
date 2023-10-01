package com.lazypanda.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lazypanda.R;
import com.lazypanda.models.ModelChat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChats extends RecyclerView.Adapter<AdapterChats.ChatHolder> {

    private static final int MESSAGE_TYPE_LEFT = 0;
    private static final int MESSAGE_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;
    
    FirebaseUser fUser;

    public AdapterChats(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }
    
    @Override
    public com.lazypanda.adapters.AdapterChats.ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == MESSAGE_TYPE_RIGHT) {
        	View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, viewGroup, false);
            return new ChatHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, viewGroup, false);
            return new ChatHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChats.ChatHolder chatHolder, int i) {
        String message = chatList.get(i).getMessage();
        String timestamp = chatList.get(i).getTimeStamp();
        
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("hh:mm aa", cal).toString();
        
        chatHolder.user_message_view.setText(message);
        //Animation fade = AnimationUtils.loadAnimation(context ,R.anim.fade);
        //chatHolder.itemView.startAnimation(fade);
        chatHolder.user_time_view.setText(dateTime);

        try {
        	Glide.with(context).load(imageUrl).circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(chatHolder.user_image_view);
        } catch(Exception err) {
        	//Glide.with(context).load(R.drawable.p).circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(chatHolder.user_image_view);
        }
        
        if(i == chatList.size() - 1) {
        	if(chatList.get(i).getIsSeen()) {
        		chatHolder.user_is_seen_view.setText("Seen");
        	}else{
                chatHolder.user_is_seen_view.setText("Delivered");
            }
        }else{
            chatHolder.user_is_seen_view.setVisibility(View.GONE);
        }

        /* Apply the sliding-up animation based on the sender
        if (getItemViewType(i) == MESSAGE_TYPE_LEFT) {
            // Incoming message
            Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            chatHolder.itemView.startAnimation(slideUp);
        } else {
            // Outgoing message
            // You can apply a different animation for outgoing messages if needed
            Animation fade = AnimationUtils.loadAnimation(context ,R.anim.fade);
            chatHolder.itemView.startAnimation(fade);
        }*/
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid())) {
        	return MESSAGE_TYPE_RIGHT;
        }else {
            return MESSAGE_TYPE_LEFT;
        }
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        ImageView user_image_view;
        TextView user_message_view, user_time_view, user_is_seen_view;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            user_image_view = itemView.findViewById(R.id.chat_user_image);
            user_message_view = itemView.findViewById(R.id.chat_user_message);
            user_time_view = itemView.findViewById(R.id.chat_message_time);
            user_is_seen_view = itemView.findViewById(R.id.chat_message_sent_or_not);
        }
    }
}
