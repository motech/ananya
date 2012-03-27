package org.motechproject.ananya.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class TransferDataList {

    private List<TransferData> list;

    public TransferDataList(String jsonData) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TransferData.class, new TransferData());
        Gson gson = gsonBuilder.create();
        Type collectionType = new TypeToken<Collection<TransferData>>() {
        }.getType();
        list = gson.fromJson(jsonData, collectionType);
    }

    public List<TransferData> getAll() {
        return list;
    }


}
