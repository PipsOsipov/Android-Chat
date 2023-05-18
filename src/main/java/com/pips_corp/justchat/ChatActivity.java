package com.pips_corp.justchat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pips_corp.justchat.databinding.ActivityChatBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private User receiveUser;
    private List<ChatMsg> chatMsgs;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversationId = null;
    private Boolean isAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadRD();
        setListeners();
        init();
        listenMsgs();
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMsgs = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMsgs, getBitmapString(receiveUser.image), preferenceManager.getString(Constants.KEY_USER_ID));
        binding.chatRecycler.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMsg(){
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,receiveUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputMsg.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if (conversationId != null){
            updateConversion(binding.inputMsg.getText().toString());
        }else {
            HashMap<String,Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receiveUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME, receiveUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiveUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMsg.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        if (!isAvailable){
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiveUser.token);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, binding.inputMsg.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REG_IDS, tokens);

                sendNotification(body.toString());
            }catch (Exception e){
                showTaost(e.getMessage());
            }
        }
        binding.inputMsg.setText(null);
    }

    private void showTaost(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody) {
        ApiClient.getClient().create(ApiService.class).sendMsg(Constants.getRemoteMsgHeaders(), messageBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()){
                    try {
                        if (response.body() != null){
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") == 1){
                                JSONObject error = (JSONObject) results.get(0);
                                showTaost(error.getString("error"));
                                return;
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    showTaost("Notification sent");
                }else {
                    showTaost("Error" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showTaost(t.getMessage());
            }
        });

    }

    private void listenAvailabilityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(
                receiveUser.id
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null){
                if (value.getLong(Constants.KEY_AVAIBILITY) != null){
                    int availability = Objects.requireNonNull(
                            value.getLong(Constants.KEY_AVAIBILITY)
                    ).intValue();
                    isAvailable = availability == 1;
                }
                receiveUser.token = value.getString(Constants.KEY_FCM_TOKEN);
                if (receiveUser.image == null){
                    receiveUser.image = value.getString(Constants.KEY_IMAGE);
                    chatAdapter.setReceiverProfImg(getBitmapString(receiveUser.image));
                    chatAdapter.notifyItemRangeChanged(0,chatMsgs.size());
                }
            }
            if (isAvailable){
                binding.textAvailability.setVisibility(View.VISIBLE);
            }else {
                binding.textAvailability.setVisibility(View.GONE);
            }
        });
    }

    private void listenMsgs(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiveUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,receiveUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

   @SuppressLint("NotifyDataSetChanged")
   private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
       if (error != null){
           return;
       }
       if (value != null){
           int count = chatMsgs.size();
           for (DocumentChange documentChange : value.getDocumentChanges()){
               if (documentChange.getType() == DocumentChange.Type.ADDED){
                   ChatMsg chatMsg = new ChatMsg();
                   chatMsg.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                   chatMsg.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                   chatMsg.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                   chatMsg.dateTime = getDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                   chatMsg.dateObj = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                   chatMsgs.add(chatMsg);
               }
           }
           chatMsgs.sort(Comparator.comparing(obj -> obj.dateObj));
           if (count == 0) {
               chatAdapter.notifyDataSetChanged();
           }else {
               chatAdapter.notifyItemRangeInserted(chatMsgs.size(), chatMsgs.size());
               binding.chatRecycler.smoothScrollToPosition(chatMsgs.size() - 1);
           }
           binding.chatRecycler.setVisibility(View.VISIBLE);
       }
       binding.progBar.setVisibility(View.GONE);
       if (conversationId == null){
           checkConvesion();
       }
   };

    private Bitmap getBitmapString(String encimage){
        if (encimage != null){
            byte[] bytes = Base64.decode(encimage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }else {
            return null;
        }
    }


    private void loadRD(){
        receiveUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.nameChat.setText(receiveUser.name);
    }
    private void setListeners(){
        binding.imageBack.setOnClickListener(view -> onBackPressed());
        binding.sendLayout.setOnClickListener(view -> sendMsg());
    }
    private String getDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }

    private void updateConversion(String message){
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversationId);
        documentReference.update( Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP, new Date());
    }

    private void checkConvesion(){
        if (chatMsgs.size() != 0){
            checkConvesionRemote(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiveUser.id
            );
            checkConvesionRemote(
                    receiveUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkConvesionRemote(String sendeId, String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,sendeId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size()>0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId= documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}