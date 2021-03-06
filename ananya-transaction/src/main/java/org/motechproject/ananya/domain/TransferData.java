package org.motechproject.ananya.domain;

import com.google.gson.*;

import java.lang.reflect.Type;


public class TransferData implements JsonDeserializer<TransferData> {
    private String token;
    private String type;
    private String data;

    public TransferData() {
    }

    public TransferData(String token, String type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public int tokenIntValue() {
        return Integer.parseInt(token);
    }

    public boolean isCCState() {
        return type.equals(TransferDataState.TYPE_CC_STATE);
    }

    public boolean isAudioTrackerState() {
        return type.equals(TransferDataState.TYPE_AUDIO_TRACKER);
    }

    public boolean isCallDuration(){
        return type.equals(TransferDataState.TYPE_CALL_DURATION);
    }

    @Override
    public TransferData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        TransferData transferData = new TransferData();
        transferData.token = jsonObject.get("token").getAsString();
        transferData.type = jsonObject.get("type").getAsString();
        transferData.data = jsonObject.get("data").toString();

        return transferData;
    }
}