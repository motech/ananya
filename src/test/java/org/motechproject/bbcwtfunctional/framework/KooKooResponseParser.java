package org.motechproject.bbcwtfunctional.framework;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.motechproject.bbcwtfunctional.testdata.ivrreponse.Hangup;
import org.motechproject.bbcwtfunctional.testdata.ivrreponse.IVRResponse;

public class KooKooResponseParser {
    private static XStream xStream;

    static {
        xStream = new XStream(new DomDriver());
        xStream.processAnnotations(IVRResponse.class);
        xStream.alias("hangup", Hangup.class);
    }

    public static IVRResponse fromXml(String xml) {
        try {
            return (IVRResponse) xStream.fromXML(xml);
        } catch (XStreamException exception) {
            System.out.println(xml);
            throw exception;
        }
    }
}
