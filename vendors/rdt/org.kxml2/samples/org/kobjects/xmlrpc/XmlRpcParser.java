package org.kobjects.xmlrpc;

/**
 * @author Stefan Haustein
 *
 * <p>A simple XML RPC parser based on the XML PULL API,
 * intended to show the XMLPULL and KXml2 API usage with
 * a real application example.</p>
 * 
 * <ul>
 * <li>For the XML RPC specification, please refer to
 * <a href="http://www.xmlrpc.com/spec">http://www.xmlrpc.com/spec</a></li>
 * <li>For the XmlPullParser API specification, please refer to
 * <a href="http://xmlpull.org/">xmlpull.org</a></li>
 * <li>For information about kXML 2, please refer to
 * <a href="http://kxml.org/">kxml.org</a></li>
 * </ul>
 */

import java.util.*;
import java.io.*;
import org.xmlpull.v1.*;

public class XmlRpcParser {

    XmlPullParser parser;

    /** 
     * Creates a new XmlRpcParser, using the given XmlPullParser.
     */

    public XmlRpcParser(XmlPullParser parser) {
        this.parser = parser;
    }

    /** 
     * Parses an XML RPC method call response.
     * The return values are collected in a Vector.
     * 
     * @return The return values collected in a Vector.
     */

    public Vector parseResponse() throws XmlPullParserException, IOException {

        Vector result = new Vector();

        parser.nextTag();
        parser.require(parser.START_TAG, "", "methodResponse");
        parser.nextTag();
        parser.require(parser.START_TAG, "", "params");

        while (parser.nextTag() == parser.START_TAG) {
            parser.require(parser.START_TAG, "", "param");
            parser.nextTag();
            result.addElement(parseValue());
            parser.nextTag();
            parser.require(parser.END_TAG, "", "param");
        }

        parser.require(parser.END_TAG, "", "params");
        parser.nextTag();
        parser.require(parser.END_TAG, "", "methodResponse");
        parser.next();
        parser.require(parser.END_DOCUMENT, null, null);

        return result;
    }

    /** 
     * Parses an XML-RPC value element. Returns the
     * content of the element as a corresponding Java object. 
     * <p>
     * <b>precondition:</b> parser is on a "value" start tag<br />
     * <b>postcondition:</b> parser is on a "value" end tag</p>
     */

    Object parseValue() throws IOException, XmlPullParserException {
        parser.require(parser.START_TAG, "", "value"); // precondition
        parser.next();

        Object result;

        if (parser.getEventType() == parser.END_TAG)
            result = "";
        else if (parser.getEventType() == parser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        else {
            parser.require(parser.START_TAG, "", null);
            String name = parser.getName();

            if (name.equals("double"))
                result = new Double(parser.nextText());
            else if (name.equals("int") || name.equals("i4"))
                result = new Integer(parser.nextText());
            else if (name.equals("array"))
                result = parseArray();
            else if (name.equals("string"))
                result = parser.nextText();
            else if (name.equals("struct"))
                result = parseStruct();
            else
                throw new RuntimeException("unexpected element: " + name);

            parser.require(parser.END_TAG, "", name);
            parser.nextTag();
        }
        parser.require(parser.END_TAG, "", "value"); // postcond.
        return result;
    }

    /** Parses an XML-RPC array and returns it as a Java Vector
     *  
     *  <p>
     *  <b>Precondition:</b> On "array" start tag<br />
     *  <b>Postcondition:</b> On "array" end tag
     *  </p>
     */

    Vector parseArray() throws IOException, XmlPullParserException {
        Vector v = new Vector();
        parser.require(parser.START_TAG, "", "array");

        while (parser.nextTag() == parser.START_TAG)
            v.addElement(parseValue());

        parser.require(parser.END_TAG, "", "array");
        return v;
    }

    Hashtable parseStruct() throws IOException, XmlPullParserException {
        Hashtable struct = new Hashtable();
        parser.require(parser.START_TAG, "", "struct");
        while (parser.nextTag() == parser.START_TAG) {
            parser.require(parser.START_TAG, "", "member");
            parser.nextTag();
            parser.require(parser.START_TAG, "", "name");
            String name = parser.nextText();
            parser.require(parser.END_TAG, "", "name");
            parser.nextTag();
            struct.put(name, parseValue());
            parser.nextTag();
            parser.require(parser.END_TAG, "", "member");
        }
        parser.require(parser.END_TAG, "", "struct");
        return struct;
    }

    /** main method, temporarily included for simple testing only */


    public static void main(String[] argv)
        throws IOException, XmlPullParserException {

        String test =
            "<?xml version=\"1.0\"?>\n"
                + "<methodResponse><params>\n"
                + " <param>\n"
                + "  <value><string>South Dakota</string></value>\n"
                + " </param><param>\n"
                + "  <value><struct>\n"
                + "   <member><name>foo</name><value>bar</value></member>\n"
                + "   <member><name>v</name><value><array></array></value></member>\n"
                + "  </struct></value>\n"
                + " </param><param>\n"
                + "   <value><double>3.14</double></value>\n"
                + " </param>\n"
                + "</params></methodResponse>\n";

        System.out.println("test input:\n" + test);
        XmlPullParser xp = new org.kxml2.io.KXmlParser();
        xp.setInput(new java.io.StringReader(test));
        System.out.println(
            "parsing result: " + new XmlRpcParser(xp).parseResponse());

    }

}
