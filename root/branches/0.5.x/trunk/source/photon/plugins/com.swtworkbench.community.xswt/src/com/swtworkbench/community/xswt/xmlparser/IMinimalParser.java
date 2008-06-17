package com.swtworkbench.community.xswt.xmlparser;

import java.io.InputStream;
import java.io.Reader;

import com.swtworkbench.community.xswt.XSWTException;

public interface IMinimalParser extends IMinimalOM {
	
	public void setPosition(Object element, int line, int column);
	/**
	 * Builds an object tree from an InputStream
	 * @param input InputStream to read XML document from
	 * @return root element object
	 */
	public Object build(InputStream input) throws XSWTException;
	/**
	 * Builds an object tree from a Reader
	 * @param input InputStream to read XML document from
	 * @return root element object
	 */
	public Object build(Reader input) throws XSWTException;
}
