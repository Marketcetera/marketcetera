package com.aptana.rdt.internal.core.gems;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SourceURLContentHandler implements ContentHandler {

	private HashSet<String> urls;
	
	private StringBuffer data;

	public void characters(char[] ch, int start, int length) throws SAXException {
		for (int i = start; i < start + length; i++) {
			data.append(ch[i]);
		}
	}

	public void endDocument() throws SAXException {
		// do nothing
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (qName.equals("url")) {
			urls.add(data.toString());
		}
	}

	public void endPrefixMapping(String arg0) throws SAXException {
		// do nothing
	}

	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// do nothing
	}

	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// do nothing
	}

	public void setDocumentLocator(Locator arg0) {
		// do nothing
	}

	public void skippedEntity(String arg0) throws SAXException {
		// do nothing
	}

	public void startDocument() throws SAXException {
		urls = new HashSet<String>();
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		data = new StringBuffer();
	}

	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		// do nothing
	}

	public Set<String> getURLs() {
		return urls;
	}
}
