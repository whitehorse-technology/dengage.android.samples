package com.dengage.android.sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class StoryRequest {
    @SerializedName("channel")
    public String channel = "";

    @SerializedName("contactKey")
    public String contactKey = "";

    @SerializedName("accountGuid")
    public String accountGuid = "";

    @SerializedName("deviceId")
    public String deviceId = "";

    public Map<String, Object> extraParams;

    public String getContactKey() {
        return contactKey;
    }

    public void setContactKey(String contactKey) {
        this.contactKey = contactKey;
    }

    public String getAccountGuid() {
        return accountGuid;
    }

    public void setAccountGuid(String accountGuid) {
        this.accountGuid = accountGuid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Map<String, Object> getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(Map<String, Object> extraParams) {
        this.extraParams = extraParams;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
