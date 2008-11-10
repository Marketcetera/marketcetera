package org.rubypeople.rdt.internal.core.parser;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class IgnoreMarkersContentHandler implements ContentHandler {

	public static final String ID = "id";
	public static final String OFFSET = "offset";
	public static final String END_OFFSET = "end";
	public static final String RESOURCE = "resource";
	public static final String WARNING = "warning";
	public static final String ROOT = "warnings";
	
	private StringBuffer data;
	private Collection<IgnoreMarker> markers;
	
	private int id;
	private int offset;
	private int endOffset;
	private IResource resource;

	public void endDocument() throws SAXException {
		// Do nothing
	}

	public void startDocument() throws SAXException {
		markers = new ArrayList<IgnoreMarker>();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		for (int i = start; i < start + length; i++) {
			data.append(ch[i]);
		}
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// Do nothing
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		// Do nothing
	}

	public void skippedEntity(String name) throws SAXException {
		// Do nothing
	}

	public void setDocumentLocator(Locator locator) {
		// Do nothing
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		// Do nothing
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// Do nothing
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (qName.equals(RESOURCE)) {
			IPath proj = Path.fromPortableString(data.toString());
			resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(proj);
		} else if (qName.equals(ID)) {
			id = Integer.parseInt(data.toString());
		} else if (qName.equals(OFFSET)) {
			offset = Integer.parseInt(data.toString());
		} else if (qName.equals(END_OFFSET)) {
			endOffset = Integer.parseInt(data.toString());
		} else if (qName.equals(WARNING)) {
			if (resource != null)
				markers.add(new IgnoreMarker(resource, id, offset, endOffset));
		}
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		data = new StringBuffer();
	}

	public Collection<IgnoreMarker> getIgnoreMarkers() {
		return markers;
	}

}
