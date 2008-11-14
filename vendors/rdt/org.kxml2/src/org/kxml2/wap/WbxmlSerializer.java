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


package org.kxml2.wap;

import java.io.*;
import java.util.*;

import org.xmlpull.v1.*;

//import com.sun.xml.parser.Parser;

/** a class for converting ("binary encoding") XML to WBXML. 
 *  Todo: 
 *  <ul>
 *  <li>Add support for processing instructions 
 *  <li>Add support for tag and attribute tables
 *  <li>Add support for WBXML extensions
 *  </ul>
 */

public class WbxmlSerializer implements XmlSerializer {

    Hashtable stringTable = new Hashtable();

    OutputStream out;

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    ByteArrayOutputStream stringTableBuf = new ByteArrayOutputStream();

    String pending;
    int depth;
    String name;
    String namespace;
    Vector attributes = new Vector();

    Hashtable attrStartTable = new Hashtable();
    Hashtable attrValueTable = new Hashtable();
    Hashtable tagTable = new Hashtable();


    public XmlSerializer attribute(String namespace, String name, String value) {
        attributes.addElement(name);
        attributes.addElement(value);
        return this;
    }


    public void cdsect (String cdsect) throws IOException{
        text (cdsect);
    }



    /* silently ignore comment */

    public void comment (String comment) {
    }

    
    public void docdecl (String docdecl) {
        throw new RuntimeException ("Cannot write docdecl for WBXML");
    }


    public void entityRef (String er) {
        throw new RuntimeException ("EntityReference not supported for WBXML");
    }
    
    public int getDepth() {
    	return depth;
    }


    public boolean getFeature (String name) {
        return false;
    }
    
	public String getNamespace() {
		throw new RuntimeException("NYI");
	}
	
	public String getName() {
		throw new RuntimeException("NYI");
	}
	
	public String getPrefix(String nsp, boolean create) {
        throw new RuntimeException ("NYI");
    }
    
    
    public Object getProperty (String name) {
        return null;
    }

    public void ignorableWhitespace (String sp) {
    }
    

    public void endDocument() throws IOException {
        writeInt(out, stringTableBuf.size());

        // write StringTable

        out.write(stringTableBuf.toByteArray());

        // write buf 

        out.write(buf.toByteArray());

        // ready!

        out.flush();
    }


    /** ATTENTION: flush cannot work since Wbxml documents cannot
    need buffering. Thus, this call does nothing. */

    public void flush() {
    }


    public void checkPending(boolean degenerated) throws IOException {
        if (pending == null)
            return;

        int len = attributes.size();

        Integer idx = (Integer) tagTable.get(pending);

        // if no entry in known table, then add as literal
        if (idx == null) {
            buf.write(
                len == 0
                    ? (degenerated ? Wbxml.LITERAL : Wbxml.LITERAL_C)
                    : (degenerated ? Wbxml.LITERAL_A : Wbxml.LITERAL_AC));

            writeStrT(pending);
        }
        else {
            buf.write(
                len == 0
                    ? (degenerated ? idx.intValue() : idx.intValue() | 64)
                    : (degenerated
                        ? idx.intValue() | 128
                        : idx.intValue() | 192));

        }

        for (int i = 0; i < len;) {
            idx = (Integer) attrStartTable.get(attributes.elementAt(i));
            if (idx == null) {
                buf.write(Wbxml.LITERAL);
                writeStrT((String) attributes.elementAt(i));
            }
            else {
                buf.write(idx.intValue());
            }
            idx = (Integer) attrValueTable.get(attributes.elementAt(++i));
            if (idx == null) {
                buf.write(Wbxml.STR_I);
                writeStrI(buf, (String) attributes.elementAt(i));
            }
            else {
                buf.write(idx.intValue());
            }
            ++i;
        }

        if (len > 0)
            buf.write(Wbxml.END);

        pending = null;
        attributes.removeAllElements();
    }


    public void processingInstruction(String pi) {
        throw new RuntimeException ("PI NYI");
    }


    public void setFeature(String name, boolean value) {
        throw new IllegalArgumentException ("unknown feature "+name);
    }
        


    public void setOutput (Writer writer) {
        throw new RuntimeException ("Wbxml requires an outputstream, no writer");
    }

    public void setOutput (OutputStream out, String encoding) throws IOException {
        
        if (encoding != null) throw new IllegalArgumentException ("encoding not supported for WBXML");
        
        this.out = out;

        buf = new ByteArrayOutputStream();
        stringTableBuf = new ByteArrayOutputStream();

        // ok, write header 
    }


    public void setPrefix(String prefix, String nsp) {
        throw new RuntimeException("NYI");
    }

    public void setProperty(String property, Object value) {
        throw new IllegalArgumentException ("unknown property "+property);
    }

    
    public void startDocument(String s, Boolean b) throws IOException{
        out.write(0x01); // version
        out.write(0x01); // unknown or missing public identifier
        out.write(0x04); // iso-8859-1
    }


    public XmlSerializer startTag(String namespace, String name) throws IOException {

        if (namespace != null && !"".equals(namespace)) 
            throw new RuntimeException ("NSP NYI");

        //current = new State(current, prefixMap, name);

        checkPending(false);
        pending = name;
		depth++;
		
        return this;
    }

    public XmlSerializer text(char[] chars, int start, int len) throws IOException {

        checkPending(false);

        buf.write(Wbxml.STR_I);
        writeStrI(buf, new String(chars, start, len));

        return this;
    }

    public XmlSerializer text(String text) throws IOException {

        checkPending(false);

        buf.write(Wbxml.STR_I);
        writeStrI(buf, text);

        return this;
    }
    
    

    public XmlSerializer endTag(String namespace, String name) throws IOException {

//        current = current.prev;

        if (pending != null)
            checkPending(true);
        else
            buf.write(Wbxml.END);

		depth--;

        return this;
    }

    /** currently ignored! */

    public void writeLegacy(int type, String data) {
    }

    // ------------- internal methods --------------------------

    static void writeInt(OutputStream out, int i) throws IOException {
        byte[] buf = new byte[5];
        int idx = 0;

        do {
            buf[idx++] = (byte) (i & 0x7f);
            i = i >> 7;
        }
        while (i != 0);

        while (idx > 1) {
            out.write(buf[--idx] | 0x80);
        }
        out.write(buf[0]);
    }

    static void writeStrI(OutputStream out, String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            out.write((byte) s.charAt(i));
        }
        out.write(0);
    }

    void writeStrT(String s) throws IOException {

        Integer idx = (Integer) stringTable.get(s);

        if (idx == null) {
            idx = new Integer(stringTableBuf.size());
            stringTable.put(s, idx);
            writeStrI(stringTableBuf, s);
            stringTableBuf.flush();
        }

        writeInt(buf, idx.intValue());
    }

    /** Sets the tag table for a given page.
     *	The first string in the array defines tag 5, the second tag 6 etc.
     *  Currently, only page 0 is supported
     */
    public void setTagTable(int page, String[] tagTable) {
        // clear entries in tagTable!
        for (int i = 0; i < tagTable.length; i++) {
            if (tagTable[i] != null) {
                Integer idx = new Integer(i + 5);
                this.tagTable.put(tagTable[i], idx);
            }
        }
        if (page != 0)
            throw new RuntimeException("code pages curr. not supp.");
    }

    /** Sets the attribute start Table for a given page.
     *	The first string in the array defines attribute 
     *  5, the second attribute 6 etc.
     *  Currently, only page 0 is supported. Please use the 
     *  character '=' (without quote!) as delimiter 
     *  between the attribute name and the (start of the) value 
     */
    public void setAttrStartTable(int page, String[] attrStartTable) {
        // clear entries in this.table!
        for (int i = 0; i < attrStartTable.length; i++) {
            if (attrStartTable[i] != null) {
                Integer idx = new Integer(i + 5);
                this.attrStartTable.put(attrStartTable[i], idx);
            }
        }
        if (page != 0)
            throw new RuntimeException("code pages curr. not supp.");
    }

    /** Sets the attribute value Table for a given page.
     *	The first string in the array defines attribute value 0x85, 
     *  the second attribute value 0x86 etc.
     *  Currently, only page 0 is supported.
     */
    public void setAttrValueTable(int page, String[] attrValueTable) {
        // clear entries in this.table!
        for (int i = 0; i < attrValueTable.length; i++) {
            if (attrValueTable[i] != null) {
                Integer idx = new Integer(i + 0x085);
                this.attrValueTable.put(attrValueTable[i], idx);
            }
        }
        if (page != 0)
            throw new RuntimeException("code pages curr. not supp.");
    }
}
