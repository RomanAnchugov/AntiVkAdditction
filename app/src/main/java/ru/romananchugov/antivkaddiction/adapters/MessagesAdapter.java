package ru.romananchugov.antivkaddiction.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import ru.romananchugov.antivkaddiction.R;

/**
 * Created by romananchugov on 16.05.2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private static final String TAG = MessagesAdapter.class.getSimpleName();

    private VKList<VKApiDialog> messagesList;
    private int offset = 0;
    private int count = 200;


    public MessagesAdapter(VKList<VKApiDialog> messageList){
        this.messagesList = messageList;
        loadNewDialogs();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.
                from(parent.getContext()).inflate(R.layout.messages_item, parent, false);
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
        VKRequest request = VKApi.messages()
                .getDialogs(VKParameters.from(VKApiConst.COUNT, count, VKApiConst.OFFSET, offset));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiGetDialogResponse messagesResponse = (VKApiGetDialogResponse) response.parsedModel;
                messagesList.addAll(messagesResponse.items);
                notifyDataSetChanged();
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
            messageTitle = linearLayout.findViewById(R.id.tv_message_title);
            messageBody = linearLayout.findViewById(R.id.tv_message_body);
        }

        public void bind(final int position){
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
        }
    }
}
