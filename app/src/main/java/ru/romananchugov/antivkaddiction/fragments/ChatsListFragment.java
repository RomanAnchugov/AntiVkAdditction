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

import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKList;

import ru.romananchugov.antivkaddiction.MainActivity;
import ru.romananchugov.antivkaddiction.R;
import ru.romananchugov.antivkaddiction.adapters.ChatsListAdapter;

/**
 * Created by romananchugov on 15.05.2018.
 */

@SuppressLint("ValidFragment")
public class ChatsListFragment extends Fragment {
    private static final String TAG = ChatsListFragment.class.getSimpleName();

    private VKList<VKApiDialog> messagesList;
    private RecyclerView messagesRecycler;
    private ChatsListAdapter adapter;

    public ChatsListFragment(MainActivity mainActivity){
        messagesList = new VKList<>();
        adapter = new ChatsListAdapter(messagesList, mainActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chats_list_fragment, container, false);

        messagesRecycler = v.findViewById(R.id.rv_chats);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        messagesRecycler.setAdapter(adapter);
        messagesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1)){
                    adapter.increaseOffset();
                    adapter.loadNewDialogs();
                }
            }
        });

        return v;
    }
}
