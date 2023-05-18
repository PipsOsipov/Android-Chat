package com.pips_corp.justchat;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ApiService {
    @POST("send")
    Call<String> sendMsg(@HeaderMap HashMap<String, String> headers, @Body String messageBody);
}
