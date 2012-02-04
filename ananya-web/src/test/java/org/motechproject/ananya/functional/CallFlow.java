package org.motechproject.ananya.functional;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
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

    public NodeList readNode(String expression) throws XPathExpressionException {
        XPathExpression xPathExpression = xPath.compile(expression);
        return (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
    }

    public String readString(String expression) throws XPathExpressionException {
        XPathExpression xPathExpression = xPath.compile(expression);
        return (String) xPathExpression.evaluate(doc, XPathConstants.STRING);
    }

    public String vxml() {
        return vxml;
    }
}
