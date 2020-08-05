package com.dengage.android.sample;

import com.google.gson.annotations.SerializedName;

public class StoryMessage {

    @SerializedName("mediaUrl")
    public String mediaUrl = "";

    @SerializedName("contactKey")
    public String contactKey = "";

    public String getContactKey() {
        return contactKey;
    }

    public void setContactKey(String contactKey) {
        this.contactKey = contactKey;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
