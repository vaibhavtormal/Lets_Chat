package com.example.letschat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.letschat.adapter.RecentChatRecylerAdapter;
import com.example.letschat.adapter.SearchRecylcerAdapter;
import com.example.letschat.model.ChatRoomModel;
import com.example.letschat.model.UserModel;
import com.example.letschat.utils.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    RecentChatRecylerAdapter adapter;

    public ChatFragment() {

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_chat, container, false);
      recyclerView =view.findViewById(R.id.recycler_view);
      setupRecyclerView();

      return view;
    }

    void setupRecyclerView(){


        Query query = FireBaseUtil.allChatRoomCollectionReference()
                .whereArrayContains("userId",FireBaseUtil.currentUserId())
                .orderBy("lastMessageTimmestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query,ChatRoomModel.class).build();


        adapter = new RecentChatRecylerAdapter(options,getContext()) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter!= null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter!= null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter!= null)
            adapter.notifyDataSetChanged();//if crash change to startlisting
    }
}