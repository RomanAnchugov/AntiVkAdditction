package ru.romananchugov.antivkaddiction.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.romananchugov.antivkaddiction.MainActivity;
import ru.romananchugov.antivkaddiction.R;
import ru.romananchugov.antivkaddiction.fragments.ChatFragment;

/**
 * Created by romananchugov on 16.05.2018.
 */

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder> {
    private static final String TAG = ChatsListAdapter.class.getSimpleName();

    private MainActivity mainActivity;
    private VKList<VKApiDialog> messagesList;
    private ArrayList<Integer> chatIdsArray;
    private int offset = 0;
    private int count = 20;


    public ChatsListAdapter(VKList<VKApiDialog> messageList, MainActivity mainActivity){
        this.messagesList = messageList;
        this.mainActivity = mainActivity;
        chatIdsArray = new ArrayList<>();
        loadNewDialogs();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.
                from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return messagesList.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void loadNewDialogs(){
        final VKRequest request = VKApi.messages()
                .getDialogs(VKParameters.from(VKApiConst.COUNT, count, VKApiConst.OFFSET, offset));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiGetDialogResponse messagesResponse = (VKApiGetDialogResponse) response.parsedModel;
                messagesList.addAll(messagesResponse.items);

                //finding chatId for getHistory
                try {
                    JSONArray dialogsJsonArray = response.json.getJSONObject("response").getJSONArray("items");
                    for(int i = 0; i < dialogsJsonArray.length(); i++){
                        JSONObject currentMessage = dialogsJsonArray.getJSONObject(i).getJSONObject("message");
                        Integer chatId = currentMessage.getInt("user_id");
                        if(currentMessage.has("chat_id")){
                            chatId = currentMessage.getInt("chat_id") + 2000000000;
                        }
                        chatIdsArray.add(chatId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.i(TAG, "onError: " + error.toString());
            }
        });
    }

    public void increaseOffset(){
        offset += count;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;
        private TextView messageTitle;
        private TextView messageBody;

        public ViewHolder(View itemView) {
            super(itemView);

            linearLayout = (LinearLayout)itemView;
            messageTitle = linearLayout.findViewById(R.id.tv_chat_title);
            messageBody = linearLayout.findViewById(R.id.tv_chat_body);
        }

        public void bind(final int position){
            Log.i(TAG, "bind: " + messagesList.get(position).message.toString());
            //MESSAGE TITLE BINDING
            if(!messagesList.get(position).message.title.equals("")) {
                messageTitle.setText(messagesList.get(position).message.title);
            }else{
                final VKRequest request = VKApi.users()
                        .get(VKParameters.from(VKApiConst.USER_IDS, messagesList.get(position).message.user_id));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                        messageTitle.setText(user.first_name + " " + user.last_name);
                    }
                });
            }

            //MESSAGE BODY BINDING
            if(!messagesList.get(position).message.body.equals("")) {
                messageBody.setText(messagesList.get(position).message.body);
            }else{
                messageBody.setText(R.string.message_not_support);
            }

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mainActivity.hideBottomNav();
                    if(mainActivity.getSupportActionBar() != null) {
                        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }

                    FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft = ft.replace(R.id.fragment_container,
                            new ChatFragment(chatIdsArray.get(position)
                                    , messageTitle.getText().toString()
                                    , mainActivity));
                    ft.commit();
                }
            });
        }
    }
}
