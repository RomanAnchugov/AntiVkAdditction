package ru.romananchugov.antivkaddiction.adapters;

import android.graphics.Color;
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
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import ru.romananchugov.antivkaddiction.MainActivity;
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
    private MainActivity mainActivity;

    public ChatAdapter(long chatId, MainActivity mainActivity){
        this.chatId = chatId;
        this.mainActivity = mainActivity;
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

    public String loadUserInfo(int userId, final TextView messageUser){
        final VKRequest request = VKApi.users()
                .get(VKParameters.from(VKApiConst.USER_IDS, userId));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                messageUser.setText(user.first_name + " " + user.last_name);
            }
        });
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;
        private TextView messageBody;
        private TextView messageUser;
        private TextView messageTime;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout)itemView;
            messageUser = null;
            messageBody = linearLayout.findViewById(R.id.tv_message);
            if(!(linearLayout.findViewById(R.id.tv_message_user) == null)){
                messageUser = linearLayout.findViewById(R.id.tv_message_user);
            }
            messageTime = linearLayout.findViewById(R.id.tv_message_time);
        }

        public void bind(int position){
            try {
                JSONObject messageObject = messagesJsonArray.getJSONObject(position);
                //Log.i(TAG, "bind: " + messageObject.toString());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Date date = new Date(messageObject.getInt("date"));

                if(messageUser != null){
                    if(messageObject.has("chat_id")) {
                        loadUserInfo(messageObject.getInt("from_id"), messageUser);
                        Random random = new Random();
                        messageUser.setTextColor(Color.rgb(random.nextInt(255),
                                random.nextInt(255),
                                random.nextInt(255)));
                    }else if(mainActivity.getSupportActionBar() != null){
                        messageUser.setText(mainActivity.getSupportActionBar().getTitle());
                    }

                }

                messageTime.setText(simpleDateFormat.format(date));
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
