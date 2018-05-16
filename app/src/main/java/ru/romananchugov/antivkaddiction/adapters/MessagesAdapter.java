package ru.romananchugov.antivkaddiction.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKList;

import ru.romananchugov.antivkaddiction.R;

/**
 * Created by romananchugov on 16.05.2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    public static final String TAG = MessagesAdapter.class.getSimpleName();

    VKList<VKApiDialog> messageList;

    public MessagesAdapter(VKList<VKApiDialog> messageList){
        this.messageList = messageList;
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
        return messageList.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;
        private TextView messageTitile;
        private TextView messageBody;

        public ViewHolder(View itemView) {
            super(itemView);

            linearLayout = (LinearLayout)itemView;
            messageTitile = linearLayout.findViewById(R.id.tv_message_title);
            messageBody = linearLayout.findViewById(R.id.tv_message_body);
        }

        public void bind(int position){
            if(!messageList.get(position).message.title.equals("")) {
                messageTitile.setText(messageList.get(position).message.title);
            }else{
                messageTitile.setText(linearLayout.getResources()
                        .getString(R.string.int_placeholder, messageList.get(position).message.user_id));
            }

            if(!messageList.get(position).message.body.equals("")) {
                messageBody.setText(messageList.get(position).message.body);
            }else{
                messageBody.setText(R.string.message_not_support);
            }
            //Log.i(TAG, "bind: " + messageList.get(position).message.title);
        }
    }
}
