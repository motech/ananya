package org.motechproject.ananyafunctional.framework;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class CallFlow {
    private Document doc;
    private XPath xPath;
    private String vxml;

    public CallFlow(String vxml) {
        this.vxml = vxml;
        init();
    }

    private void init() {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(vxml));
            this.doc = db.parse(is);

            xPath = XPathFactory.newInstance().newXPath();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object read(String expression, QName returnType) throws XPathExpressionException {
        XPathExpression xPathExpression = xPath.compile(expression);
        return xPathExpression.evaluate(doc, returnType);
    }

    public String vxml() {
        return vxml;
    }
}
