package org.motechproject.ananya.transformers;

import org.motechproject.ananya.request.BaseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllTransformers {

    private List<Transformer> transformers = new ArrayList<Transformer>();

    @Autowired
    public AllTransformers(CallerIdTransformer callerIdTransformer, CalledNumberTransformer calledNumberTransformer) {
        transformers.add(callerIdTransformer);
        transformers.add(calledNumberTransformer);
    }

    public void process(BaseRequest request) {
        for (Transformer transformer : transformers)
            transformer.transform(request);
    }
}

