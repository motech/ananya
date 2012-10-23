package org.motechproject.ananya.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class TestUtils {
    public static <T> T fromXml(Class className, String xmlString) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(className);
        Unmarshaller u = jc.createUnmarshaller();
        return (T) u.unmarshal(new StringReader(xmlString));
    }
}
