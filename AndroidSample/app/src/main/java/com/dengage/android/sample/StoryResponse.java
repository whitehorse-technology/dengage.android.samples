package com.dengage.android.sample;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class StoryResponse {
    @SerializedName("message")
    public String message;

    @SerializedName("uniqueId")
    public String uniqueId;

    public transient StoryMessage innerMessage;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public StoryMessage getInnerMessage() {
        Log.d("DenPush", "Message: "+  this.message);
        return new Gson().fromJson(this.message, StoryMessage.class);
    }

    public StoryResponse[] fromJson(String response) {
        return new Gson().fromJson(response, StoryResponse[].class);
    }
}
