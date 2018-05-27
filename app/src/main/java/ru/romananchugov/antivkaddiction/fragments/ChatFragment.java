package ru.romananchugov.antivkaddiction.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import ru.romananchugov.antivkaddiction.MainActivity;
import ru.romananchugov.antivkaddiction.R;
import ru.romananchugov.antivkaddiction.adapters.ChatAdapter;

/**
 * Created by romananchugov on 20.05.2018.
 */

@SuppressLint("ValidFragment")
public class ChatFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ChatFragment.class.getSimpleName();

    private long chatId;
    private String chatName;

    private RecyclerView chatMessagesRecycler;
    private ChatAdapter adapter;

    private EditText messageInput;
    private ImageButton messageSender;

    private MainActivity mainActivity;

    public ChatFragment(long chatId, String chatName, MainActivity mainActivity){
        this.chatName = chatName;
        this.chatId = chatId;
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_fragment, container, false);
        adapter = new ChatAdapter(chatId, mainActivity);
        chatMessagesRecycler = v.findViewById(R.id.rv_chat_messages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        chatMessagesRecycler.setLayoutManager(linearLayoutManager);
        chatMessagesRecycler.setAdapter(adapter);
        chatMessagesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(-1)){
                    adapter.increaseOffset();
                    adapter.loadMessages();
                }
            }
        });

        messageInput = v.findViewById(R.id.et_chat_input);
        messageSender = v.findViewById(R.id.ib_chat_sender);
        messageSender.setOnClickListener(this);

        if(mainActivity != null){
            mainActivity.setTitle(chatName);
        }

        return v;
    }

    private void sendMessage(){
        String messageText = messageInput.getText().toString();
        long chatId = this.chatId;
        VKRequest request;

        //если чат
        if(chatId > 2000000000){
            chatId -= 2000000000;
            request =  new VKRequest("messages.send"
                    , VKParameters.from("chat_id", chatId, "message", messageText));
        }else{//если диалог
            request =  new VKRequest("messages.send"
                    , VKParameters.from("user_id", chatId, "message", messageText));
        }

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                adapter.loadMessages();
            }

            @Override
            public void onError(VKError error) {
                Log.i(TAG, "onError: " + error.toString());
            }
        });

        messageInput.setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_chat_sender:
                sendMessage();
                break;
        }
    }


}
