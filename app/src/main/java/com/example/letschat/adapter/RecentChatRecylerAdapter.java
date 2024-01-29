package com.example.letschat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.ChatActivity;
import com.example.letschat.R;
import com.example.letschat.model.ChatRoomModel;
import com.example.letschat.model.UserModel;
import com.example.letschat.utils.AndroidUtil;
import com.example.letschat.utils.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;




public class RecentChatRecylerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecylerAdapter.ChatRoomModelViewHolder>{

    Context context;
    public RecentChatRecylerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FireBaseUtil.getOtherUserFromChatRoom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean lastMessageSentByMe = model.getLastMessageSendId().equals(FireBaseUtil.currentUserId());

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);


                        FireBaseUtil.getOtherProfilepicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if(task.isSuccessful()){
                                        Uri uri = t.getResult();
                                        AndroidUtil.setProfilePic(context,uri,holder.profilePic);
                                    }
                                });
                        holder.userNameText.setText(otherUserModel.getUsername());
                        if (lastMessageSentByMe) {

                            holder.lastMessageText.setText("YOU :" + model.getLastMessage());
                        } else {
                            holder.lastMessageText.setText(model.getLastMessage());
                        }
                        holder.lastMessageTime.setText(FireBaseUtil.timeStampToString(model.getLastMessagTimestamp()));


                        holder.itemView.setOnClickListener( v ->{
                            //chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelInten(intent,otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_row,parent,false);
        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder{
        TextView userNameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;
        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_msg_text);
            lastMessageTime = itemView.findViewById(R.id.last_msg_time_text);
            profilePic = itemView.findViewById(R.id.profle_pic_image_view);
        }
    }
}
