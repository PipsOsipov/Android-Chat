package com.pips_corp.justchat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pips_corp.justchat.databinding.ContainerRecentConversionBinding;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversionViewHolder>{

    private final List<ChatMsg> chatMsgs;

    private final ConversionListener conversionListener;

    public ConversationAdapter(List<ChatMsg> chatMsgs, ConversionListener conversionListener) {
        this.chatMsgs = chatMsgs;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMsgs.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMsgs.size();
    }



    class ConversionViewHolder extends RecyclerView.ViewHolder{

        ContainerRecentConversionBinding binding;

        ConversionViewHolder(ContainerRecentConversionBinding containerRecentConversionBinding){
            super(containerRecentConversionBinding.getRoot());
            binding = containerRecentConversionBinding;
        }

        void setData(ChatMsg chatMsg){
            binding.imageProfile.setImageBitmap(getConversionImage(chatMsg.conversionImage));
            binding.textName.setText(chatMsg.conversionName);
            binding.textRecentMessage.setText(chatMsg.message);
            binding.getRoot().setOnClickListener(view -> {
                User user = new User();
                user.id = chatMsg.conversionId;
                user.name = chatMsg.conversionName;
                user.image = chatMsg.conversionImage;
                conversionListener.onConversionClicked(user);
            });
        }
    }

    private Bitmap getConversionImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
    }
}
