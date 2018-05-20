package ru.romananchugov.antivkaddiction.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.romananchugov.antivkaddiction.R;

/**
 * Created by romananchugov on 20.05.2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);

        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;
        private TextView chatMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout)itemView;
            chatMessage = linearLayout.findViewById(R.id.tv_chat_message);
        }

        public void bind(int position){
            chatMessage.setText(position + "");
        }
    }
}
