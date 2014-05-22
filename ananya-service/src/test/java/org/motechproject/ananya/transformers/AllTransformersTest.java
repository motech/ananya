package org.motechproject.ananya.transformers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.contract.BaseRequest;
import org.motechproject.ananya.contract.BaseServiceRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllTransformersTest {
    @Mock
    private CallerIdTransformer callerIdTransformer;
    @Mock
    private CalledNumberTransformer calledNumberTransformer;
    private AllTransformers allTransformers;

    @Before
    public void setUp(){
        initMocks(this);
        allTransformers = new AllTransformers(callerIdTransformer,calledNumberTransformer);
    }

    @Test
    public void shouldCallAllTransformersToActOnBaseRequest(){
        BaseRequest request = mock(BaseServiceRequest.class);

        allTransformers.process(request);

        verify(callerIdTransformer).transform(request);
        verify(calledNumberTransformer).transform(request);
    }
}
