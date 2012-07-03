package org.motechproject.ananya.transformers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.request.BaseRequest;
import org.motechproject.ananya.request.BaseServiceRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;

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
