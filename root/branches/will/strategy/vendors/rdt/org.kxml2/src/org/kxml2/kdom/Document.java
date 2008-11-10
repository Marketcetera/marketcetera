/* kXML 2
 *
 * Copyright (C) 2000, 2001, 2002 
 *               Stefan Haustein
 *               D-46045 Oberhausen (Rhld.),
 *               Germany. All Rights Reserved.
 *
 * The contents of this file are subject to the "Common Public
 * License" (CPL); you may not use this file except in compliance
 * with the License.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License.
 *
 * Thanks to Paul Palaszewski, Wilhelm Fitzpatrick, 
 * Eric Foster-Johnson, Michael Angel, and Liam Quinn for providing various
 * fixes and hints for the KXML 1 parser.
 * */

package org.kxml2.kdom;

import java.io.*;

import org.xmlpull.v1.*;
/** The document consists of some legacy events and a single root
    element. This class basically adds some consistency checks to
    Node. */

public class Document extends Node {

    protected int rootIndex = -1;
    String encoding;
    Boolean standalone;

    /** returns "#document" */

    public String getEncoding () {
        return encoding;
    }
    
    public void setEncoding(String enc) {
        this.encoding = enc;
    }
    
    public void setStandalone (Boolean standalone) {
        this.standalone = standalone;
    }
    
    public Boolean getStandalone() {
        return standalone;
    }


    public String getName() {
        return "#document";
    }

    /** Adds a child at the given index position. Throws
    an exception when a second root element is added */

    public void addChild(int index, int type, Object child) {
        if (type == ELEMENT) {
            if (rootIndex != -1)
                throw new RuntimeException("Only one document root element allowed");

            rootIndex = index;
        }
        else if (rootIndex >= index)
            rootIndex++;

        super.addChild(index, type, child);
    }

    /** reads the document and checks if the last event
    is END_DOCUMENT. If not, an exception is thrown.
    The end event is consumed. For parsing partial
        XML structures, consider using Node.parse (). */

    public void parse(XmlPullParser parser)
        throws IOException, XmlPullParserException {

		parser.require(XmlPullParser.START_DOCUMENT, null, null);
		parser.nextToken ();        	

        encoding = parser.getInputEncoding();
        standalone = (Boolean)parser.getProperty ("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone");
        
        super.parse(parser);

        if (parser.getEventType() != XmlPullParser.END_DOCUMENT)
            throw new RuntimeException("Document end expected!");

    }

    public void removeChild(int index) {
        if (index == rootIndex)
            rootIndex = -1;
        else if (index < rootIndex)
            rootIndex--;

        super.removeChild(index);
    }

    /** returns the root element of this document. */

    public Element getRootElement() {
        if (rootIndex == -1)
            throw new RuntimeException("Document has no root element!");

        return (Element) getChild(rootIndex);
    }
    
    
    /** Writes this node to the given XmlWriter. For node and document,
        this method is identical to writeChildren, except that the
        stream is flushed automatically. */

    public void write(XmlSerializer writer)
        throws IOException {
        
        writer.startDocument(encoding, standalone);
        writeChildren(writer);
        writer.endDocument();
    }
    
    
}