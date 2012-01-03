package org.motechproject.ananyafunctional.framework;

import org.apache.commons.lang.StringUtils;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
public abstract class BaseCallFlowTest extends XMLTestCase{

    private static final String VXML_ROOT = "xmlns=\"http://www.w3.org/2001/vxml\"\n"+
            "      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
            "      xsi:schemaLocation=\"http://www.w3.org/2001/vxml http://www.w3.org/TR/voicexml21/vxml.xsd\"";

    protected String transform(String vxml){
        return StringUtils.remove(vxml, VXML_ROOT);
    }
}
