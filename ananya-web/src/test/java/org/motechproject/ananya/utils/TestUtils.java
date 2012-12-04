package org.motechproject.ananya.utils;

import org.codehaus.jackson.map.ObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;

public class TestUtils {
    public static <T> T fromXml(Class className, String xmlString) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(className);
        Unmarshaller u = jc.createUnmarshaller();
        return (T) u.unmarshal(new StringReader(xmlString));
    }

    public static <T> T fromJson(String jsonString, Class<T> subscriberResponseClass) {
        ObjectMapper mapper = new ObjectMapper();
        T serializedObject = null;
        try {
            serializedObject = mapper.readValue(jsonString, subscriberResponseClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializedObject;
    }
}
