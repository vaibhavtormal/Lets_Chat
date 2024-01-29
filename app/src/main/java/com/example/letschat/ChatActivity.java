package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.letschat.adapter.ChatRecyclerAdapter;
import com.example.letschat.adapter.SearchRecylcerAdapter;
import com.example.letschat.model.ChatMessageModel;
import com.example.letschat.model.ChatRoomModel;
import com.example.letschat.model.UserModel;
import com.example.letschat.utils.AndroidUtil;
import com.example.letschat.utils.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    String chatRoomId;
    ChatRoomModel chatRoomModel;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backButton;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //get userModel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatRoomId = FireBaseUtil.getChatRoomID(FireBaseUtil.currentUserId(),otherUser.getUserId());
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backButton = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_user_name);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profle_pic_image_view);



        FireBaseUtil.getOtherProfilepicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);
                    }
                });

        backButton.setOnClickListener((v)->{
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());



        sendMessageBtn.setOnClickListener((v ->{
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        }));
        getOnCreateChatRoomModel();
        setupChatRecyclerView();
    }
    void setupChatRecyclerView(){
        Query query = FireBaseUtil.getChatroomMessageReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();


        adapter = new ChatRecyclerAdapter(options,getApplicationContext()) ;
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }
    void sendMessageToUser(String message){

        chatRoomModel.setLastMessagTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSendId(FireBaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);
        FireBaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FireBaseUtil.currentUserId(),Timestamp.now());
        FireBaseUtil.getChatroomMessageReference(chatRoomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                      if(task.isSuccessful()){
                          messageInput.setText("");
                      }
                    }
                });
    }
    void getOnCreateChatRoomModel(){
        FireBaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if(chatRoomModel ==null){
                    //first time chat
               chatRoomModel = new ChatRoomModel(
                       chatRoomId,
                       Arrays.asList(FireBaseUtil.currentUserId(),otherUser.getUserId()),
                       Timestamp.now(),
                       ""
               );
               FireBaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);
                }
            }
        });

    }
}