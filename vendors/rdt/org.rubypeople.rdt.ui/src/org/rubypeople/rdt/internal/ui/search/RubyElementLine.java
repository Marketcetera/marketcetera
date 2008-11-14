/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.search;

import org.rubypeople.rdt.core.IRubyElement;

public class RubyElementLine {
	private IRubyElement fElement;
	private int fLine;
	private String fLineContents;
	
	/**
	 * @param element either an IRubyScript
	 */
	public RubyElementLine(IRubyElement element, int line, String lineContents) {
		fElement= element;
		fLine= line;
		fLineContents= lineContents;
	}
	
	public IRubyElement getRubyElement() {
		return fElement;
	}
	
	public int getLine() {
		return fLine;
	}
	
	public String getLineContents() {
		return fLineContents;
	}
}
