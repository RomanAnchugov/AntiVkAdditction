package ru.romananchugov.antivkaddiction.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private static final int ACTION_MESSAGE = 3;

    private int messagesCount = 50;
    private int pastOffset = 0;
    private int offset = 0;
    private long chatId;
    private JSONArray messagesJsonArray;
    private MainActivity mainActivity;

    public ChatAdapter(long chatId, MainActivity mainActivity) {
        this.chatId = chatId;
        this.mainActivity = mainActivity;
        loadMessages();
        messagesJsonArray = new JSONArray();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int messageType = 0;
        switch (viewType) {
            case INPUT_MESSAGE:
                messageType = R.layout.input_message_view;
                break;
            case OUTPUT_MESSAGE:
                messageType = R.layout.output_message_view;
                break;
            case ACTION_MESSAGE:
                messageType = R.layout.action_message_view;
                break;
        }

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(messageType, parent, false);

        return new ViewHolder(linearLayout, viewType);
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
        JSONObject messageObject = null;
        try {
            messageObject = messagesJsonArray.getJSONObject(position);
            if (messageObject.has("action")) {
                return ACTION_MESSAGE;
            }
            if (messageObject.getInt("out") == 1) {
                return OUTPUT_MESSAGE;
            }
            if (messageObject.getInt("out") == 0) {
                return INPUT_MESSAGE;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return position;
    }

    public void loadMessages() {
        final VKRequest request = new VKRequest("messages.getHistory"
                , VKParameters.from(
                "user_id", chatId,
                VKApiConst.COUNT, messagesCount,
                VKApiConst.OFFSET, offset * messagesCount));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    JSONArray responseJson = response.json.getJSONObject("response").getJSONArray("items");
                    if (pastOffset != offset) {
                        for (int i = 0; i < responseJson.length(); i++) {
                            messagesJsonArray.put(responseJson.get(i));
                        }
                    } else {
                        messagesJsonArray = responseJson;
                    }
                    Log.i(TAG, "onComplete: " + messagesJsonArray.toString());
                    Log.i(TAG, "complete: notifyer");
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void increaseOffset() {
        pastOffset = offset;
        offset++;
    }

    public void clearOffset() {
        offset = 0;
        pastOffset = offset;
    }

    public String loadUserInfo(int userId, final TextView messageUser) {
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private int viewType;

        private LinearLayout linearLayout;

        private TextView messageBody;
        private TextView messageUser;
        private TextView messageTime;
        private ImageView attachment;

        private TextView actionMessage;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            linearLayout = (LinearLayout) itemView;

            //users messages
            messageUser = linearLayout.findViewById(R.id.tv_message_user);
            messageBody = linearLayout.findViewById(R.id.tv_message);
            messageTime = linearLayout.findViewById(R.id.tv_message_time);
            attachment = linearLayout.findViewById(R.id.iv_message_attachment);

            //action message
            actionMessage = linearLayout.findViewById(R.id.tv_action_message);
        }

        public void bind(int position) {
            JSONObject messageObject = null;
            try {
                messageObject = (JSONObject) messagesJsonArray.get(position);

                Log.i(TAG, "bind: " + messageObject.toString());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Date date = new Date(messageObject.getInt("date"));

                if (messageUser != null) {
                    if (messageObject.has("chat_id")) {
                        loadUserInfo(messageObject.getInt("from_id"), messageUser);
                        Random random = new Random();
                        messageUser.setTextColor(Color.rgb(random.nextInt(255),
                                random.nextInt(255),
                                random.nextInt(255)));
                    } else if (mainActivity.getSupportActionBar() != null) {
                        messageUser.setText(mainActivity.getSupportActionBar().getTitle());
                    }

                }

                if(viewType == INPUT_MESSAGE || viewType == OUTPUT_MESSAGE) {
                    if (messageTime != null) {
                        messageTime.setText(simpleDateFormat.format(date));
                    }
                    if (messageObject.has("body") && messageBody != null) {
                        messageBody.setText(messageObject.getString("body"));
                    }
                    if (messageObject.has("attachments") && messageBody != null) {
                        messageBody.setText("attachments(coming soon)");
                        attachment.setVisibility(View.VISIBLE);
                        attachment.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_test));
                    }
                    if (messageObject.has("fwd_messages") && messageBody != null) {
                        messageBody.setText("fwd_messages(coming soon)");
                    }
                    if (messageObject.has("body") && messageObject.has("fwd_messages") && messageBody != null) {
                        messageBody.setText("body with fwd_messages(coming soon)");
                    }
                }
                if(viewType == ACTION_MESSAGE){
                    actionMessage.setText(messageObject.getString("action") + " "
                            + messageObject.getString("action_text"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
