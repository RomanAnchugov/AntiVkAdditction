package ru.romananchugov.antivkaddiction.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKList;

import ru.romananchugov.antivkaddiction.R;
import ru.romananchugov.antivkaddiction.adapters.MessagesAdapter;

/**
 * Created by romananchugov on 15.05.2018.
 */

@SuppressLint("ValidFragment")
public class MessagesFragment extends Fragment {

    private VKList<VKApiDialog> messagesList;
    private RecyclerView messagesRecycler;
    private MessagesAdapter adapter;

    public MessagesFragment(){
        messagesList = new VKList<>();
        adapter = new MessagesAdapter(messagesList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.messages_fragment, container, false);

        messagesRecycler = v.findViewById(R.id.rv_messages);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        messagesRecycler.setAdapter(adapter);

        VKRequest request = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, "100"));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiGetDialogResponse messagesResponse = (VKApiGetDialogResponse) response.parsedModel;
                messagesList.addAll(messagesResponse.items);
                adapter.notifyDataSetChanged();
            }
        });

        return v;
    }
}
