/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bob Foster - The color manager idea; XSWT top-level node idea; some other 
 *                        important stuff
 *     David Orme (ASC) - Rewrote: switched to a reflection-based implementation
 ******************************************************************************/

package com.swtworkbench.community.xswt.dataparser.parsers;

import java.lang.reflect.Array;
import java.util.StringTokenizer;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

/**
 * Class ArrayDataParser.  
 * 
 * @author hallvard
 */
public class ArrayDataParser extends NonDisposableDataParser {

	private String delimiters = " \t\r\n";
	
	public ArrayDataParser() {
	}
	public ArrayDataParser(String delimiters) {
		this();
		this.delimiters = delimiters;
	}
	
	private String parenthesis = "(,)[ ]{;}<|>";
	
    public Object parse(String source, Class klass, IDataParserContext context) throws XSWTException {
    	Class elementClass = klass.getComponentType();
    	String delimiters = this.delimiters;
    	if (source.length() > 1) {
    		char first = source.charAt(0), last = source.charAt(source.length() - 1);
    		for (int i = 0; i < parenthesis.length(); i += 3) {
	    		if (first == parenthesis.charAt(i) && last == parenthesis.charAt(i + 2)) {
	    			delimiters = parenthesis.substring(i + 1, i + 2);
	    			source = source.substring(1, source.length() - 1);
	    			break;
	    		}
    		}
    	}
        StringTokenizer stringTokenizer = new StringTokenizer(source, delimiters);
        int tokens = stringTokenizer.countTokens();
        Object array = Array.newInstance(elementClass, tokens);
        for (int i = 0; i < tokens; i++) {
        	String token = stringTokenizer.nextToken();
        	if (delimiters == this.delimiters) {
        		token = token.trim();
        	}
        	Object o = context.parse(token, elementClass);
        	try {
				Array.set(array, i, o);
			} catch (RuntimeException e) {
				throw new XSWTException("Couldn't set array element " + i + " of " + array.getClass() + " to " + o, e);
			}
        }
        return array;
    }
}
