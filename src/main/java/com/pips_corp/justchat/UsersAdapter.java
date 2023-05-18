package com.pips_corp.justchat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pips_corp.justchat.databinding.UserContainerBinding;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<User> users;
    private final UserListener userListener;

    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserContainerBinding userContainerBinding = UserContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent, false);
        return new UserViewHolder(userContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUSerData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    class UserViewHolder extends RecyclerView.ViewHolder{

        UserContainerBinding binding;

        UserViewHolder(UserContainerBinding userContainerBinding) {
            super(userContainerBinding.getRoot());
            binding = userContainerBinding;
        }

        void setUSerData(User user){
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));
            binding.getRoot().setOnClickListener(view -> userListener.onUserCliked(user));
        }
    }
    private Bitmap getUserImage(String encodingImage){
        byte[] bytes = Base64.decode(encodingImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
