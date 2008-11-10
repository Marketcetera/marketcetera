/*******************************************************************************
 * Copyright (c) 2005 RadRails.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rubypeople.rdt.ui.text.ansi;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/**
 * Represents a range of text that can be attributed with ANSI commands.
 * 
 * Currently, ANSI support is limited to styles and foreground colors.
 *
 * @author Johannes Holzer <Johannes.Holzer@gmail.com>
 *
 */
public class ANSIToken {
	
	final static byte BOLD = 1;
	final static byte UNDERSCORE = 4;
	
	final static byte BLACK = 30;
	final static byte BLUE = 34;
	public final static byte CYAN = 36;
	final static byte GREEN = 32;
	final static byte MAGENTA = 35;
	public final static byte RED = 31;
	public final static byte YELLOW = 33;
	
	
	private final static int INITIAL_TOKEN_LENGTH = 300;
	
	boolean bold = false;
	boolean underscore = false;
	
	int backgroundColor = 0; // ANSI value
	int foregroundColor = 0; // ANSI value
	
	/** text is dynamically expanded upon reaching the arrays limit (@see add) */
	char[] text = new char[INITIAL_TOKEN_LENGTH];
	int textPos = 0;
	
	/**
	 * Add text to the token
	 * 
	 * @param c character to add
	 */
	public void add(char c) {
		if (textPos >= text.length) {
			char[] tmp = new char[text.length + INITIAL_TOKEN_LENGTH];
			System.arraycopy(text, 0, tmp, 0, text.length);
			text = tmp;
		}
		text[textPos++] = c;	
	}
	
	/**
	 * Attaches an ANSI-Property to the token.
	 * 
	 * @param ansiProp the Property to add
	 */
	public void addProperty(int ansiProp) {
		if (ansiProp <= 8)
			setTextAttribute(ansiProp);
		else if (ansiProp <= 37)
			foregroundColor = ansiProp;
		else if (ansiProp <= 47)
			backgroundColor = ansiProp;
	}
	
	public boolean hasFontStyle() {
		return (bold || underscore);
	}
	
	/**
	 * Returns the previously attached ANSI text attributes as SWT font style
	 * 
	 * @return the associated SWT font style
	 */
	public int getFontStyle() {
		if (bold && underscore) return (SWT.BOLD + SWT.ITALIC);
		if (underscore) return SWT.ITALIC;
		if (bold) return SWT.BOLD;
		return 0;
	}

	public RGB getForegroundRGB() {
		switch(foregroundColor) {
		case BLACK:
			return new RGB(0, 0, 0);
		case RED:
			return new RGB(255, 0, 0);
		case GREEN:
			return new RGB(0, 255, 0);
		case YELLOW:
			return new RGB(255, 255, 0);
		case BLUE:
			return new RGB(0, 0, 255);
		case MAGENTA:
			return new RGB(255, 0, 255);
		case CYAN:
			return new RGB(0, 255, 255);
		default:
			return new RGB(0, 0, 0);
		}
	}
	
	/**
	 * Creates an ANSI-Sequence that is suitable to use as hashcode. 
	 * @return a complete ANSI-Sequence (e.g. <em>[1;34m</em>)
	 */
	public String getAnsi() {
		return "\\e[" + bold + ";" + underscore + ";" + foregroundColor + ";" + backgroundColor + "m";
	}
	
	public boolean hasForegroundColor() {
		return foregroundColor > 0;
	}

	public void setTextAttribute(int attr) {
		switch (attr) {
		case BOLD:
			bold = true;
			break;
		case UNDERSCORE:
			underscore = true;
			break;
		}
	}
	
	/**
	 * Retrieve the text that is stored in the token
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < textPos; i++)
			sb.append(text[i]);
		
		return sb.toString();
	}
}
