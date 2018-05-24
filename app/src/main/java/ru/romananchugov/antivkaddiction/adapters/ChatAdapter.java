package ru.romananchugov.antivkaddiction.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
    private static final int INPUT_MESSAGE = 1;
    private static final int OUTPUT_MESSAGE = 2;

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
        int messageType = 0;
        switch (viewType){
            case INPUT_MESSAGE:
                messageType = R.layout.input_message_view;
                break;
            case OUTPUT_MESSAGE:
                messageType = R.layout.output_message_view;
                break;
        }

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(messageType, parent, false);

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
        try {
            JSONObject messageObject = messagesJsonArray.getJSONObject(position);
            if(messageObject.getInt("out") == 1){
                return OUTPUT_MESSAGE;
            }else{
                return INPUT_MESSAGE;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

                    //from_id - чьё это сообщение
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
        private TextView messageBody;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout)itemView;
            messageBody = linearLayout.findViewById(R.id.tv_message);
        }

        public void bind(int position){
            try {
                JSONObject messageObject = messagesJsonArray.getJSONObject(position);

                if(messageObject.has("body")) {
                    messageBody.setText(messageObject.getString("body"));
                }
                if(messageObject.has("attachments")){
                    messageBody.setText("attachments(coming soon)");
                }
                if(messageObject.has("fwd_messages")){
                    messageBody.setText("fwd_messages(coming soon)");
                }
                if(messageObject.has("body") && messageObject.has("fwd_messages")){
                    messageBody.setText("body with fwd_messages(coming soon)");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
