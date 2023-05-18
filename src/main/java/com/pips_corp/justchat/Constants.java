package com.pips_corp.justchat;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NAME = "name";
    public static final String KEY_PREFERENCED_NAME = "justchatPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_FCM_TOKEN = "fcmtoken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAIBILITY = "avaibility";
    public static final String REMOTE_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REG_IDS = "registration_ids";
    public static HashMap<String, String> remoteMsgHeaders = null;
    public static  HashMap<String,String> getRemoteMsgHeaders(){
        if (remoteMsgHeaders == null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(REMOTE_AUTHORIZATION,"key=AAAA0qVGK0Y:APA91bFNBTcLfOtihS-VO9mMb1kIW0NQFsD8KiNsndtMMzMunO0lvdlTtVb-M7oswKoozpgAo_Se3mFFhaAcInAzBVjdz2yrRBpHEBsiNvSeigMtNqU68tWbftrYA7czfgJ9I8cC_Dhf");
            remoteMsgHeaders.put(REMOTE_MSG_CONTENT_TYPE, "application/json");
        }
        return remoteMsgHeaders;
    }
}
