package com.swtworkbench.community.xswt.xmlparser;

public interface IMinimalOM {
	
	/**
	 * Tells whether element may be regarded as an element, by the other methods.
	 * @param element
	 * @return true if element may be regarded as an element, false otherwise
	 */
	public boolean isElement(Object element);
	/**
	 * Returns the name of the element
	 * @param element
	 * @return the name of the element
	 */
	public String getElementName(Object element);
	/**
	 * Returns the namespace (URI) of the element
	 * @param element
	 * @return the namespace URI of the element, as a String
	 */
	public String getElementNamespace(Object element);

	/**
	 * Returns the number of children of element
	 * @param element
	 * @return the number of children of element
	 */
	public int getChildElementCount(Object element);
	/**
	 * Returns the child element of element at the given position 
	 * @param element
	 * @return the child element of element at the given position
	 */
	public Object getChildElement(Object element, int i);
	
	/**
	 * Returns the number of attributes of element
	 * @param element
	 * @return the number of children of element
	 */
	public int getAttributeCount(Object element);
	
	/**
	 * Returns the name of the given attribute of element
	 * @param element
	 * @return the name of the given attribute of element
	 */
	public String getAttributeName(Object element, int i);
	/**
	 * Returns the namespace (URI) of the given attribute of element
	 * @param element
	 * @return the namespace (URI as a String) of the given attribute of element
	 */
	public String getAttributeNamespace(Object element, int i);
	/**
	 * Returns the text value of the given attribute of element
	 * @param element
	 * @return the text value of the given attribute of element
	 */
	public String getAttributeValue(Object element, int i);

	/**
	 * Returns the text content of element
	 * @param element
	 * @return the text content of element
	 */
	public String getElementText(Object element);
}
