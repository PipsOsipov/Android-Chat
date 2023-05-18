package com.pips_corp.justchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pips_corp.justchat.databinding.ActivityUsersBinding;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getUsers();
        setListeners();
    }

    private void setListeners(){
        binding.arrowBack.setOnClickListener(view -> onBackPressed());
    }


    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if (currentId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token =queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size()>0){
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.recyclerUser.setAdapter(usersAdapter);
                            binding.recyclerUser.setVisibility(View.VISIBLE);
                        }else {
                            errorMsg();
                        }
                    }else {
                        errorMsg();
                    }
                });
    }


    private void errorMsg(){
        binding.errorMsg.setText(String.format("%s", "No user available"));
        binding.errorMsg.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressB.setVisibility(View.VISIBLE);
        } else {
            binding.progressB.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserCliked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}
