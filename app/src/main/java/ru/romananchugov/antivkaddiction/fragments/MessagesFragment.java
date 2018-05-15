package ru.romananchugov.antivkaddiction.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import ru.romananchugov.antivkaddiction.R;

/**
 * Created by romananchugov on 15.05.2018.
 */

@SuppressLint("ValidFragment")
public class MessagesFragment extends Fragment {

    private TextView messagesTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.messages_fragment, container, false);

        messagesTextView = v.findViewById(R.id.tv_messages);

        VKRequest request = VKApi.messages().get();

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                messagesTextView.setText(response.json.toString());
            }
        });

        return v;
    }
}
