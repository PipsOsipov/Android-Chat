package com.pips_corp.justchat;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pips_corp.justchat.databinding.RecievedMsgBinding;
import com.pips_corp.justchat.databinding.SendMsgBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMsg> chatMsgs;
    private final String senderId;

    private Bitmap receiverProfImg;


    public static final int VEIW_TYPE_SENT = 1;
    public static final int VEIW_TYPE_RECEIVED = 2;

    public void setReceiverProfImg(Bitmap bitmap){
        receiverProfImg = bitmap;
    }

    public ChatAdapter( List<ChatMsg> chatMsgs, Bitmap receicvedProfImg, String senderId) {
        this.chatMsgs = chatMsgs;
        this.receiverProfImg = receicvedProfImg;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VEIW_TYPE_SENT){
            return new SentMessageViewHolder(
                    SendMsgBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else {
            return new ReceiveMessageViewHolder(
                    RecievedMsgBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VEIW_TYPE_SENT){
            ((SentMessageViewHolder)holder).setData(chatMsgs.get(position));
        }else {
            ((ReceiveMessageViewHolder) holder).setData(chatMsgs.get(position), receiverProfImg);
        }
    }

    @Override
    public int getItemCount() {
        return chatMsgs.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMsgs.get(position).senderId.equals(senderId)){
            return VEIW_TYPE_SENT;
        }else {
            return VEIW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{

        private final SendMsgBinding binding;

        SentMessageViewHolder(SendMsgBinding sendMsgBinding){
            super(sendMsgBinding.getRoot());
            binding = sendMsgBinding;
        }

        void setData(ChatMsg chatMsg){
            binding.msgText.setText(chatMsg.message);
            binding.msgTime.setText(chatMsg.dateTime);
        }
    }

    static class ReceiveMessageViewHolder extends RecyclerView.ViewHolder{
        private final RecievedMsgBinding binding;

        ReceiveMessageViewHolder(RecievedMsgBinding recievedMsgBinding){
            super(recievedMsgBinding.getRoot());
            binding = recievedMsgBinding;
        }
        void setData(ChatMsg chatMsg, Bitmap receiverProfImg){
            binding.textmsg.setText(chatMsg.message);
            binding.msgDateTime.setText(chatMsg.dateTime);
            if (receiverProfImg != null){
                binding.profImg.setImageBitmap(receiverProfImg);
            }
        }
    }
}
