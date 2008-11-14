package org.xmlpull.v1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Define an interface to serialziation of XML Infoset.
 * This interface abstracts away if serialized XML is XML 1.0 comaptible text or
 * other formats of XML 1.0 serializations (such as binary XML for example with WBXML).
 *
 * <p><b>PLEASE NOTE:</b> This interface will be part of XmlPull 1.2 API.
 * It is included as basis for discussion. It may change in any way.
 *
 * <p>Exceptions that may be thrown are: IOException or runtime exception
 * (more runtime exceptions can be thrown but are not declared and as such
 * have no semantics defined for this interface):
 * <ul>
 * <li><em>IllegalArgumentException</em> - for almost all methods to signal that
 *     argument is illegal
 * <li><em>IllegalStateException</em> - to signal that call has good arguments but
 *     is not expected here (violation of contract) and for features/properties
 *    when requesting setting unimplemented feature/property
 *    (UnsupportedOperationException would be better but it is not in MIDP)
 *  </ul>
 *
 * <p><b>NOTE:</b> writing  CDSECT, ENTITY_REF, IGNORABLE_WHITESPACE,
 *  PROCESSING_INSTRUCTION, COMMENT, and DOCDECL in some implementations
 * may not be supported (for example when serializing to WBXML).
 * In such case IllegalStateException will be thrown and it is recommened
 * to use an optional feature to signal that implementation is not
 * supporting this kind of output.
 */

public interface XmlSerializer {

    /**
     * Set feature identified by name (recommended to be URI for uniqueness).
     * Some well known optional features are defined in
     * <a href="http://www.xmlpull.org/v1/doc/features.html">
     * http://www.xmlpull.org/v1/doc/features.html</a>.
     *
     * If feature is not recocgnized or can not be set
     * then IllegalStateException MUST be thrown.
     *
     * @exception IllegalStateException If the feature is not supported or can not be set
     */
    public void setFeature(String name,
                           boolean state)
        throws IllegalArgumentException, IllegalStateException;


    /**
     * Return the current value of the feature with given name.
     * <p><strong>NOTE:</strong> unknown features are <string>always</strong> returned as false.
     *
     * @param name The name of feature to be retrieved.
     * @return The value of named feature.
     * @exception IllegalArgumentException if feature string is null
     */
    public boolean getFeature(String name);


    /**
     * Set the value of a property.
     * (the property name is recommened to be URI for uniqueness).
     * Some well known optional properties are defined in
     * <a href="http://www.xmlpull.org/v1/doc/properties.html">
     * http://www.xmlpull.org/v1/doc/properties.html</a>.
     *
     * If property is not recocgnized or can not be set
     * then IllegalStateException MUST be thrown.
     *
     * @exception IllegalStateException if the property is not supported or can not be set
     */
    public void setProperty(String name,
                            Object value)
        throws IllegalArgumentException, IllegalStateException;

    /**
     * Look up the value of a property.
     *
     * The property name is any fully-qualified URI. I
     * <p><strong>NOTE:</strong> unknown properties are <string>always</strong> returned as null
     *
     * @param name The name of property to be retrieved.
     * @return The value of named property.
     */
    public Object getProperty(String name);


    /**
     * Set to use binary output stream with given encoding.
     */
    public void setOutput (OutputStream os, String encoding)
        throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Set the output to the given writer;
     * <p><b>WARNING</b> no information about encoding is available!
     */
    public void setOutput (Writer writer)
        throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Write &lt;?xml declaration with encoding (if encoding not null)
     * and standalone flag (if standalone not null)
     * This method can only be called just after setOutput.
     */
    public void startDocument (String encoding, Boolean standalone)
        throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Finish writing. All unclosed start tags will be closed and output
     * will be flushed. After calling this method no more output can be
     * serialized until next call to setOutput()
     */
    public void endDocument ()
        throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Binds the given prefix to the given namespace.
     * This call is valid for the next element including child elements.
     * The prefix and namespace MUST be always declared even if prefix
     * is not used in element (startTag() or attribute()) - for XML 1.0
     * it must result in declaring <code>xmlns:prefix='namespace'</code>
     * (or <code>xmlns:prefix="namespace"</code> depending what character is used
     * to quote attribute value).
     *
     * <p><b>NOTE:</b> this method MUST be called directly before startTag()
     *   and if anything but startTag() or setPrefix() is called next there will be exception.
     * <p><b>NOTE:</b> prefixes "xml" and "xmlns" are already bound
     *   and can not be redefined see:
     * <a href="http://www.w3.org/XML/xml-names-19990114-errata#NE05">Namespaces in XML Errata</a>.
     * <p><b>NOTE:</b> to set default namespace use as prefix empty string.
     *
     * @argument prefix must be not null (or IllegalArgumentException is thrown)
     * @argument namespace must be not null
     */
    public void setPrefix (String prefix, String namespace)
        throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Return namespace that corresponds to given prefix
     * If there is no prefix bound to this namespace return null
     * but if generatePrefix is false then return generated prefix.
     *
     * <p><b>NOTE:</b> if the prefix is empty string "" and defualt namespace is bound
     * to this prefix then empty string ("") is returned.
     *
     * <p><b>NOTE:</b> prefixes "xml" and "xmlns" are already bound
     *   will have values as defined
     * <a href="http://www.w3.org/TR/REC-xml-names/">Namespaces in XML specification</a>
     */
    public String getPrefix (String namespace, boolean generatePrefix)
        throws IllegalArgumentException;

    /**
     * Writes a start tag with the given namespace and name.
     * If there is no prefix defined for the given namespace,
     * a prefix will be defined automatically.
     * The explicit prefixes for namespaces can be established by calling setPrefix()
     * immediately before this method.
     * If namespace is null no namespace prefix is printed but just name.
     * If namespace is empty string then serialzier will make sure that
     * default empty namespace is declared (in XML 1.0 xmlns='').
     */
    public XmlSerializer startTag (String namespace, String name)
        throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Write an attribute. Calls to attribute() MUST follow a call to
     * startTag() immediately. If there is no prefix defined for the
     * given namespace, a prefix will be defined automatically.
     * If namespace is null or empty string
     * no namespace prefix is printed but just name.
     */
    public XmlSerializer attribute (String namespace, String name, String value)
        throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Write end tag. Repetition of namespace and name is just for avoiding errors.
     * <p><b>Background:</b> in kXML endTag had no arguments, and non matching tags were
     *  very difficult to find...
     * If namespace is null no namespace prefix is printed but just name.
     * If namespace is empty string then serialzier will make sure that
     * default empty namespace is declared (in XML 1.0 xmlns='').
     */
    public XmlSerializer endTag (String namespace, String name)
        throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Writes text, where special XML chars are escaped automatically
     */
    public XmlSerializer text (String text)
        throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Writes text, where special XML chars are escaped automatically
     */
    public XmlSerializer text (char [] buf, int start, int len)
        throws IOException, IllegalArgumentException, IllegalStateException;

    public void cdsect (String text)
        throws IOException, IllegalArgumentException, IllegalStateException;
    public void entityRef (String text)  throws IOException,
        IllegalArgumentException, IllegalStateException;
    public void processingInstruction (String text)
        throws IOException, IllegalArgumentException, IllegalStateException;
    public void comment (String text)
        throws IOException, IllegalArgumentException, IllegalStateException;
    public void docdecl (String text)
        throws IOException, IllegalArgumentException, IllegalStateException;
    public void ignorableWhitespace (String text)
        throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Write all pending output to the stream.
     * If method startTag() or attribute() was called then start tag is closed (final &gt;)
     * before flush() is called on underlying output stream.
     *
     * <p><b>NOTE:</b> if there is need to close start tag
     * (so no more attribute() calls are allowed) but without flushinging output
     * call method text() with empty string (text("")).
     *
     */
    public void flush ()
        throws IOException;

}

