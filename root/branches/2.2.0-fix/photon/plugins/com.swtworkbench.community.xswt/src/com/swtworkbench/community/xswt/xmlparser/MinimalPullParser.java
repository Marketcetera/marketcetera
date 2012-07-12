package com.swtworkbench.community.xswt.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.swtworkbench.community.xswt.XSWTException;

public class MinimalPullParser extends MinimalParser implements IMinimalParser {

	private XmlPullParser parser;
	
	public MinimalPullParser() {
		initParser();
	}

	private void initParser() {
		if (System.getProperty(XmlPullParserFactory.PROPERTY_NAME) != null) {
			try {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				//System.getProperty(XmlPullParserFactory.PROPERTY_NAME),
				// null);
				factory.setNamespaceAware(true);
				parser = factory.newPullParser();
			} catch (XmlPullParserException e) {
			}
		} else {
			parser = new KXmlParser();
		}
	}

	public Object build(InputStream input) throws XSWTException {
		try {
			parser.setInput(input, null);
			return build();
		} catch (Exception e) {
			throw new XSWTException(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
			}
		}
	}
	public Object build(Reader input) throws XSWTException {
		try {
			parser.setInput(input);
			return build();
		} catch (Exception e) {
			throw new XSWTException(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
			}
		}
	}
	private Object build() throws XmlPullParserException, IOException {
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		parser.require(XmlPullParser.START_DOCUMENT, null, null);
		startDocument();
		parser.nextToken();
		try {
			int eventType = parser.getEventType();
			Element lastElement = null;
			// start parsing loop
			do {
				switch (eventType) {
				case org.xmlpull.v1.XmlPullParser.START_TAG: {
					Element element = startElement(parser.getNamespace(), parser.getName());
					setPosition(element, parser.getLineNumber(), parser.getColumnNumber());

					Attribute[] attrs = new Attribute[parser.getAttributeCount()];
					for (int i = 0; i < attrs.length; i++) {
						Attribute attr = createAttribute(parser.getAttributeNamespace(i), parser.getAttributeName(i));
						attr.value = parser.getAttributeValue(i);
						attrs[i] = attr;
					}
					element.attributes = attrs;
					lastElement = element;
					break;
				}
				case org.xmlpull.v1.XmlPullParser.TEXT: {
					if (lastElement != null) {
						String text = parser.getText(); // nextText();
						if (text != null) {
							text = text.trim();
							if (text.length() > 0) {
								lastElement.text = text;
							}
						}
					}
					break;
				}
				case org.xmlpull.v1.XmlPullParser.END_TAG: {
					lastElement = null;
					Element element = endElement();
					if (element.children.length > 0) {
						element.text = null;
					}
				}
				default: break;
				}
				eventType = parser.next();
			} while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT);
			return endDocument();
		} catch (IOException e) {
		}
		return null;
	}
}
