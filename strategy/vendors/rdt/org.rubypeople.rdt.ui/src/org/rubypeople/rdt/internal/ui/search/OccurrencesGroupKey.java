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

public class OccurrencesGroupKey extends RubyElementLine {
	private boolean fIsWriteAccess;
	private boolean fIsVariable;
	
	/**
	 * Create a new occurrences group key.
	 * 
	 * @param element either an IRubyScript
	 * @param line the line number
	 * @param lineContents the line contents
	 * @param isWriteAccess <code>true</code> if it groups writable occurrences
	 * @param isVariable <code>true</code> if it groups variable occurrences
	 */
	public OccurrencesGroupKey(IRubyElement element, int line, String lineContents, boolean isWriteAccess, boolean isVariable) {
		super(element, line, lineContents);
		fIsWriteAccess= isWriteAccess;
		fIsVariable= isVariable;
	}

	public boolean isVariable() {
		return fIsVariable;
	}

	public boolean isWriteAccess() {
		return fIsWriteAccess;
	}

	public void setWriteAccess(boolean isWriteAccess) {
		fIsWriteAccess= isWriteAccess;
	}
}
