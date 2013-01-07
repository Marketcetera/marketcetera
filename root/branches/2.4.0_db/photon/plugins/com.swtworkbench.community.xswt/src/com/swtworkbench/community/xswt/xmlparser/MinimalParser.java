package com.swtworkbench.community.xswt.xmlparser;

import java.util.ArrayList;
import java.util.List;

import com.swtworkbench.community.xswt.XSWT;

public abstract class MinimalParser implements IMinimalParser {

	protected static class Element {
		String name;
		String text;
		Element[] children;
		Attribute[] attributes;
		private int line, column;
		
		public String uri() {
			return null;
		}
		public String toString() {
			String namestring = (uri() != null ? ":" : "") + name;
			String result = "<" + namestring;
			if (attributes != null) {
				for (int i = 0; i < attributes.length; i++) {
					result += " " + attributes[i].toString();
				}
			}
			if (children != null && children.length > 0) {
				result += ">..." + children.length + " elements...</" + namestring + ">";
			} else if (text != null) {
				result += ">" + text + "</" + namestring + ">";
			} else {
				result  +="/>";
			}
			return result + " @ " + line + "," + column;
		}
	}
	private static class UriElement extends Element{
		private String uri;
		public String uri() {
			return uri;
		}
	}
	private static class XswtElement extends Element{
		public String uri() {
			return XSWT.XSWT_NS;
		}
	}
	
	protected Element createElement(String uri, String name) {
		Element element = null;
		if (uri == null || "".equals(uri)) {
			element = new Element();
		} else if (XSWT.XSWT_NS.equals(uri)) {
			element = new XswtElement();
		} else {
			element = new UriElement();
			((UriElement)element).uri = uri;
		}
		element.name = name;
		return element;
	}

	protected static class Attribute {
		String name;
		String value;
		public String uri() {
			return null;
		}
		public String toString() {
			return (uri() != null ? ":" : "") + name + "=" + value;
		}
	}
	private static class UriAttribute extends Attribute {
		private String uri;
		public String uri() {
			return uri;
		}
	}
	private static class XswtAttribute extends Attribute {
		public String uri() {
			return XSWT.XSWT_NS;
		}
	}
	
	protected Attribute createAttribute(String uri, String name) {
		Attribute attr = null;
		if (uri == null || "".equals(uri)) {
			attr = new Attribute();
		} else if (XSWT.XSWT_NS.equals(uri)) {
			attr = new XswtAttribute();
		} else {
			attr = new UriAttribute();
			((UriAttribute)attr).uri = uri;
		}
		attr.name = name;
		return attr ;
	}

	// methods from IMinimalParser
	
	public boolean isElement(Object element) {
		return element instanceof Element;
	}

	public String getElementName(Object element) {
		return ((Element)element).name;
	}

	public String getElementNamespace(Object element) {
		return ((Element)element).uri();
	}

	public int getChildElementCount(Object element) {
		return ((Element)element).children.length;
	}

	public Object getChildElement(Object element, int i) {
		return ((Element)element).children[i];
	}

	public int getAttributeCount(Object element) {
		return ((Element)element).attributes.length;
	}

	public String getAttributeName(Object element, int i) {
		return ((Element)element).attributes[i].name;
	}

	public String getAttributeNamespace(Object element, int i) {
		return ((Element)element).attributes[i].uri();
	}

	public String getAttributeValue(Object element, int i) {
		return ((Element)element).attributes[i].value;
	}

	public String getElementText(Object element) {
		return ((Element)element).text;
	}

	// helper methods for building an Element tree
	
	private transient List elements;
	
	protected void startDocument() {
		elements = new ArrayList();
	}
	
	protected Element startElement(String uri, String name) {
		Element element = createElement(uri, name);
		elements.add(element);
		return element;
	}

	private static Element[] EMPTY_ELEMENT_ARRAY = new Element[0]; 
	
	protected Element endElement() {
		int size = elements.size(), count = 0;
		while (count < size) {
			Element element = (Element)elements.get(size - count - 1);
			if (element.children == null) {
				break;
			}
			count++;
		}
		Element[] children = (count == 0 ? EMPTY_ELEMENT_ARRAY : new Element[count]);
		for (; count > 0; count--) {
			children[count - 1] = (Element)elements.remove(elements.size() - 1);;
		}
		Element element = (Element)elements.get(elements.size() - 1);
		element.children = children;
		return element;
	}
	
	Object endDocument() {
		Element element = (Element)elements.get(0);
		elements = null;
		return element;
	}

	public void setPosition(Object element, int line, int column) {
		if (element instanceof Element) {
			((Element)element).line = line;
			((Element)element).column = column;
		}
	}
}
