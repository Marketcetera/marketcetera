package org.rubypeople.rdt.internal.ui.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IStatus;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIException;
import org.rubypeople.rdt.internal.ui.RubyUIStatus;
import org.rubypeople.rdt.internal.ui.preferences.RubyEditorColoringConfigurationBlock.HighlightingColorListItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SyntaxColoringStore {

	private static final int CURRENT_VERSION = 1;

	/**
	 * Identifiers for the XML file.
	 */
	private final static String XML_NODE_ROOT = "colors"; //$NON-NLS-1$
	private final static String XML_NODE_SETTING = "setting"; //$NON-NLS-1$
	private final static String XML_ATTRIBUTE_VERSION = "version"; //$NON-NLS-1$
	private final static String XML_ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	private final static String XML_ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$

	public static void write(List<HighlightingColorListItem> colors, File file)
			throws RubyUIException {
		final OutputStream writer;
		try {
			writer = new FileOutputStream(file);
			try {
				writeToStream(colors, writer);
			} finally {
				try {
					writer.close();
				} catch (IOException e) { /* ignore */
				}
			}
		} catch (IOException e) {
			throw createException(e, "Problems serializing the profiles to XML");
		}
	}

	private static void writeToStream(List<HighlightingColorListItem> colors,
			OutputStream stream) throws RubyUIException {
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.newDocument();

			final Element rootElement = document.createElement(XML_NODE_ROOT);
			rootElement.setAttribute(XML_ATTRIBUTE_VERSION, Integer.toString(CURRENT_VERSION));
			document.appendChild(rootElement);

			for (HighlightingColorListItem item : colors) {
				addKeyValuePair(document, rootElement, item.getColorKey(), "");
				addKeyValuePair(document, rootElement, item.getBackgroundKey(), "");
				addKeyValuePair(document, rootElement, item.getBackgroundEnabledKey(), "false");
				addKeyValuePair(document, rootElement, item.getBoldKey(), "false");
				addKeyValuePair(document, rootElement, item.getItalicKey(), "false");
				addKeyValuePair(document, rootElement, item.getStrikethroughKey(), "false");
				addKeyValuePair(document, rootElement, item.getUnderlineKey(), "false");
			}

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			transformer.transform(new DOMSource(document), new StreamResult(stream));
		} catch (TransformerException e) {
			throw createException(e, "Problems serializing the profiles to XML");
		} catch (ParserConfigurationException e) {
			throw createException(e, "Problems serializing the profiles to XML");
		}
	}

	private static void addKeyValuePair(final Document document, final Element rootElement, String key, String defaultValue) {
		String value = RubyPlugin.getDefault().getPreferenceStore().getString(key);
		if (value == null || value.trim().length() == 0) {
			value = defaultValue;
		}
		final Element setting = document.createElement(XML_NODE_SETTING);
		setting.setAttribute(XML_ATTRIBUTE_ID, key);
		setting.setAttribute(XML_ATTRIBUTE_VALUE, value);
		rootElement.appendChild(setting);
	}

	/*
	 * Creates a UI exception for logging purposes
	 */
	private static RubyUIException createException(Throwable t, String message) {
		return new RubyUIException(RubyUIStatus.createError(IStatus.ERROR, message, t));
	}

	public static Map<String, String> readFromFile(File file) throws RubyUIException {
		try {
			final FileInputStream reader = new FileInputStream(file);
			try {
				return readFromStream(new InputSource(reader));
			} finally {
				try {
					reader.close();
				} catch (IOException e) { /* ignore */
				}
			}
		} catch (IOException e) {
			throw createException(e, "Problems reading profiles from XML.");
		}
	}

	private static Map<String, String> readFromStream(InputSource inputSource) throws RubyUIException {
		final ProfileDefaultHandler handler = new ProfileDefaultHandler();
		try {
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			final SAXParser parser = factory.newSAXParser();
			parser.parse(inputSource, handler);
		} catch (SAXException e) {
			throw createException(e, "Problems reading profiles from XML.");
		} catch (IOException e) {
			throw createException(e, "Problems reading profiles from XML.");
		} catch (ParserConfigurationException e) {
			throw createException(e, "Problems reading profiles from XML.");
		}
		return handler.getProfiles();
	}
	
	 /**
     * A SAX event handler to parse the xml format for profiles. 
     */
    private final static class ProfileDefaultHandler extends DefaultHandler {
        
    	private Map fSettings;

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals(XML_NODE_SETTING)) {
                final String key= attributes.getValue(XML_ATTRIBUTE_ID);
                final String value= attributes.getValue(XML_ATTRIBUTE_VALUE);
                fSettings.put(key, value);
            } else if (qName.equals(XML_NODE_ROOT)) {
                fSettings= new HashMap(200);
            }
        }
        
        public void endElement(String uri, String localName, String qName) {            
        }
        
        public Map getProfiles() {
            return fSettings;
        }
    }
}
