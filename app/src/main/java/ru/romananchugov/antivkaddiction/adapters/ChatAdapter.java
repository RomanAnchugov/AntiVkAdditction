package ru.romananchugov.antivkaddiction.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.romananchugov.antivkaddiction.R;

/**
 * Created by romananchugov on 20.05.2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = ChatAdapter.class.getSimpleName();

    private long chatId;
    private JSONArray messagesJsonArray;

    public ChatAdapter(long chatId){
        this.chatId = chatId;
        loadMessages();
        messagesJsonArray = new JSONArray();
    }


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
        return messagesJsonArray.length();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void loadMessages(){
        final VKRequest request = new VKRequest("messages.getHistory"
                , VKParameters.from("user_id", chatId, VKApiConst.COUNT, 200));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    messagesJsonArray = null;
                    messagesJsonArray = response.json.getJSONObject("response").getJSONArray("items");

                    Log.i(TAG, "onComplete: " + messagesJsonArray.length());

//                    for(int i = 0; i < messagesJsonArray.length(); i++){
//                        Log.i(TAG, "onComplete: " + messagesJsonArray.get(i).toString());
//                    }

                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;
        private TextView inMessage;
        private TextView outMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout)itemView;
            inMessage = linearLayout.findViewById(R.id.tv_in_message);
            outMessage = linearLayout.findViewById(R.id.tv_out_message);
        }

        public void bind(int position){
            try {
                JSONObject messageObject = messagesJsonArray.getJSONObject(position);
                Log.i(TAG, "bind: " + messageObject.toString());
                if(messageObject.getInt("out") == 1){
                    inMessage.setVisibility(View.GONE);
                    outMessage.setVisibility(View.VISIBLE);
                    outMessage.setText(messageObject.getString("body"));
                }else{
                    inMessage.setVisibility(View.VISIBLE);
                    inMessage.setText(messageObject.getString("body"));
                    outMessage.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
