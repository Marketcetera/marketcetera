/*******************************************************************************
 * Copyright (c) 2005 RadRails.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rubypeople.rdt.ui.text.ansi;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides a method to parse a given String that may be ANSI encoded into tokens
 * which represent each ANSI formatted substring.
 * 
 * @author Johannes Holzer <Johannes.Holzer@gmail.com>
 *
 */
public class ANSIParser {
	/** ASCII-Code of the ESC-Symbol marking the possible beginning of an ANSI Sequence */
	public final static byte ESC = 27;
	// states
	private final static byte TEXT = 0;
	/** the ANSI_ESC_START state is reached when an ESC-Symbol is detected */
	private final static byte ANSI_ESC_START = 1; 
	/** the ANSI state is reached when ESC was followed by [ */ 
	private final static byte ANSI = 2;
	
	public List<ANSIToken> parse(String s) {
		if (s == null)
			return null;
		
		List<ANSIToken> tokens = new LinkedList<ANSIToken>();
		ANSIToken t = new ANSIToken();
		int state = TEXT;
		char[] c = s.toCharArray();
		StringBuffer ansiBuffer = new StringBuffer();
		/* 
		 * An ANSI-Sequence looks something like this:
		 * ESC[3;2mThis is textESC[0m
		 */
		for (int i = 0; i < c.length; i++) {
			switch (state) {
			case TEXT:
				if (c[i] == ESC) 
					state = ANSI_ESC_START;
				else
					t.add(c[i]);
				break;
			case ANSI_ESC_START:
				if (c[i] == '[') {
					state = ANSI;
					tokens.add(t);
					t = new ANSIToken();
				} else {
					state = TEXT;
				}
				break;
			case ANSI:
				if (c[i] == 'm')
					state = TEXT;
				if (c[i] == 'm' || c[i] == ';') {
					if (ansiBuffer.length() > 0) {
						try {
							int value = Integer.parseInt(ansiBuffer.toString());
							t.addProperty(value);
						} catch (NumberFormatException e) {
							// ignore
						}
					}
					ansiBuffer = new StringBuffer();
				} else {
					ansiBuffer.append(c[i]);
				}
				break;
			}
			
		}
		tokens.add(t);
		
		return tokens;
	}
	
	
}
