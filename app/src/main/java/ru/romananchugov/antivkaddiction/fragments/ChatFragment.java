package ru.romananchugov.antivkaddiction.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.romananchugov.antivkaddiction.R;
import ru.romananchugov.antivkaddiction.adapters.ChatAdapter;

/**
 * Created by romananchugov on 20.05.2018.
 */

@SuppressLint("ValidFragment")
public class ChatFragment extends Fragment {

    private long userId;

    private RecyclerView chatMessagesRecycler;
    private ChatAdapter adapter;

    public ChatFragment(long userId){
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_fragment, container, false);
        adapter = new ChatAdapter(userId);
        chatMessagesRecycler = v.findViewById(R.id.rv_chat_messages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        chatMessagesRecycler.setLayoutManager(linearLayoutManager);
        chatMessagesRecycler.setAdapter(adapter);


        return v;
    }
}
