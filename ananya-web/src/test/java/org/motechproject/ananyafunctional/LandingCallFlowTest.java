package org.motechproject.ananyafunctional;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananyafunctional.framework.CallFlow;
import org.motechproject.ananyafunctional.framework.MyWebClient;
import org.w3c.dom.NodeList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class LandingCallFlowTest {

    private MyWebClient myWebClient;

    @Before
    public void setUp() {
        myWebClient = new MyWebClient();
    }


    @Test
    public void shouldGetLandingPage() throws Exception {
        CallFlow callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/vxml/jobaid/landing/");
        
        assertEquals("/ananya/vxml/jobaid/enter/",callFlow.readString("/vxml/form/block/goto/@next"));

        callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/vxml/certificatecourse/landing/");
        assertEquals("/ananya/vxml/certificatecourse/enter/",callFlow.readString("/vxml/form/block/goto/@next"));
    }


    @Test
    public void shouldGetEntryPageForJobAid() throws Exception {

        CallFlow callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/vxml/jobaid/enter/");

        assertEquals("'/ananya/dynamic/js/caller_data.js?callerId=' + session.connection.remote.uri", callFlow.readString("/vxml/script/@srcexpr"));

        NodeList links = callFlow.readNode("/vxml/form/block/if/goto");
        assertEquals("/ananya/vxml/jobaid.vxml", links.item(0).getAttributes().item(0).getTextContent());
        assertEquals("/ananya/vxml/jobaid/register", links.item(1).getAttributes().item(0).getTextContent());

        NodeList prompts = callFlow.readNode("/vxml/form/block/if/block/audio");
        assertEquals(3, prompts.getLength());
    }


    @Test
    public void shouldGetEntryPageForCertificateCourse() throws Exception {

        CallFlow callFlow = myWebClient.getCallFlow("http://localhost:9979/ananya/vxml/certificatecourse/enter/");

        assertEquals("'/ananya/dynamic/js/caller_data.js?callerId=' + session.connection.remote.uri", callFlow.readString("/vxml/script/@srcexpr"));
        assertEquals("/ananya/js/landing.js", callFlow.readString("/vxml/script/@src"));
        assertEquals("true", callFlow.readString("/vxml/property[@name='bargein']/@value"));

        String controller = "/vxml/form[@id='controller']";
        assertEquals("flow", callFlow.readString(controller + "/block/goto/@expr"));
        assertNotNull(callFlow.readNode(controller + "/block/prompt/audio"));

        String registeredWithBookmark = "/vxml/form[@id='registered_bookmark_present']";
        assertNotNull(callFlow.readNode(registeredWithBookmark + "/block/goto"));

        String registeredWithoutBookmark = "/vxml/form[@id='registered_bookmark_absent']";
        assertEquals(1,callFlow.readNode(registeredWithoutBookmark + "/field[@name='repeat']/prompt/audio").getLength());
        assertEquals(2, callFlow.readNode(registeredWithoutBookmark + "/field[@name='repeat']/grammar/rule/one-of/item").getLength());
        NodeList links = callFlow.readNode(registeredWithoutBookmark + "/field[@name='repeat']/filled/if/goto");
        assertEquals("#controller",links.item(0).getAttributes().getNamedItem("next").getTextContent());
        assertEquals("/ananya/vxml/certificatecourse.vxml",links.item(1).getAttributes().getNamedItem("next").getTextContent());

        String unregistered = "/vxml/form[@id='unregistered']";
        assertEquals(2,callFlow.readNode(unregistered + "/field[@name='repeat']/prompt/audio").getLength());
        assertEquals(2, callFlow.readNode(unregistered + "/field[@name='repeat']/grammar/rule/one-of/item").getLength());
        links = callFlow.readNode(unregistered + "/field[@name='repeat']/filled/if/goto");
        assertEquals("#controller",links.item(0).getAttributes().getNamedItem("next").getTextContent());
        assertEquals("/ananya/vxml/certificatecourse/register",links.item(1).getAttributes().getNamedItem("next").getTextContent());
    }
}
