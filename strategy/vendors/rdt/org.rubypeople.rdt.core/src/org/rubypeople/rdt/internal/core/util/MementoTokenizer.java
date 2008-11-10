/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core.util;

import org.rubypeople.rdt.internal.core.RubyElement;

public class MementoTokenizer {
	private static final String COUNT = Character.toString(RubyElement.JEM_COUNT);
	private static final String JAVAPROJECT = Character.toString(RubyElement.JEM_RUBYPROJECT);
	private static final String PACKAGEFRAGMENTROOT = Character.toString(RubyElement.JEM_SOURCEFOLDERROOT);
	private static final String PACKAGEFRAGMENT = Character.toString(RubyElement.JEM_SOURCE_FOLDER);
	private static final String FIELD = Character.toString(RubyElement.JEM_FIELD);
	private static final String METHOD = Character.toString(RubyElement.JEM_METHOD);
	private static final String COMPILATIONUNIT = Character.toString(RubyElement.JEM_RUBYSCRIPT);
	private static final String TYPE = Character.toString(RubyElement.JEM_TYPE);
	private static final String IMPORTDECLARATION = Character.toString(RubyElement.JEM_IMPORTDECLARATION);
	private static final String LOCALVARIABLE = Character.toString(RubyElement.JEM_LOCALVARIABLE);

	private final char[] memento;
	private final int length;
	private int index = 0;
	
	public MementoTokenizer(String memento) {
		this.memento = memento.toCharArray();
		this.length = this.memento.length;
	}
	
	public boolean hasMoreTokens() {
		return this.index < this.length;
	}
	
	public String nextToken() {
		int start = this.index;
		StringBuffer buffer = null;
		switch (this.memento[this.index++]) {
			case RubyElement.JEM_ESCAPE:
				buffer = new StringBuffer();
				buffer.append(this.memento[this.index]);
				start = ++this.index;
				break;
			case RubyElement.JEM_COUNT:
				return COUNT;
			case RubyElement.JEM_RUBYPROJECT:
				return JAVAPROJECT;
			case RubyElement.JEM_SOURCEFOLDERROOT:
				return PACKAGEFRAGMENTROOT;
			case RubyElement.JEM_SOURCE_FOLDER:
				return PACKAGEFRAGMENT;
			case RubyElement.JEM_FIELD:
				return FIELD;
			case RubyElement.JEM_METHOD:
				return METHOD;
			case RubyElement.JEM_RUBYSCRIPT:
				return COMPILATIONUNIT;
			case RubyElement.JEM_TYPE:
				return TYPE;
			case RubyElement.JEM_IMPORTDECLARATION:
				return IMPORTDECLARATION;
			case RubyElement.JEM_LOCALVARIABLE:
				return LOCALVARIABLE;
		}
		loop: while (this.index < this.length) {
			switch (this.memento[this.index]) {
				case RubyElement.JEM_ESCAPE:
					if (buffer == null) buffer = new StringBuffer();
					buffer.append(this.memento, start, this.index - start);
					start = ++this.index;
					break;
				case RubyElement.JEM_COUNT:
				case RubyElement.JEM_RUBYPROJECT:
				case RubyElement.JEM_SOURCEFOLDERROOT:
				case RubyElement.JEM_SOURCE_FOLDER:
				case RubyElement.JEM_FIELD:
				case RubyElement.JEM_METHOD:
				case RubyElement.JEM_RUBYSCRIPT:
				case RubyElement.JEM_TYPE:
				case RubyElement.JEM_IMPORTDECLARATION:
				case RubyElement.JEM_LOCALVARIABLE:
					break loop;
			}
			this.index++;
		}
		if (buffer != null) {
			buffer.append(this.memento, start, this.index - start);
			return buffer.toString();
		} else {
			return new String(this.memento, start, this.index - start);
		}
	}
	
}
