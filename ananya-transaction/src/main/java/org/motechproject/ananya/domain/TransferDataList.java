package org.motechproject.ananya.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

    public List<TransferData> all() {
        return list;
    }

    public Integer maxToken() {
        return Collections.max(list, new Comparator<TransferData>() {
            @Override
            public int compare(TransferData transferData1, TransferData transferData2) {
                return transferData1.tokenIntValue() - transferData2.tokenIntValue();
            }
        }).tokenIntValue();
    }

    public void removeTokensOlderThan(final Integer token) {
        CollectionUtils.filter(list, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                TransferData transferData = (TransferData) o;
                return token < transferData.tokenIntValue();
            }
        });
    }
}