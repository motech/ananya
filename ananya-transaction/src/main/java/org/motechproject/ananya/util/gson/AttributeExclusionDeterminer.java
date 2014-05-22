package org.motechproject.ananya.util.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.Arrays;
import java.util.List;

public class AttributeExclusionDeterminer implements ExclusionStrategy {
    private List<String> parameterNamesToExclude;

    public AttributeExclusionDeterminer(String... parameterNamesToExclude) {
        this.parameterNamesToExclude = Arrays.asList(parameterNamesToExclude);
    }

    @Override
    public boolean shouldSkipField(FieldAttributes
                                           fieldAttributes) {
        return parameterNamesToExclude.contains(fieldAttributes.getName());

    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}