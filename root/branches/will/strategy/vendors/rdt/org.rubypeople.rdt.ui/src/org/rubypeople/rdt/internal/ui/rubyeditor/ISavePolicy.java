/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.rubypeople.rdt.core.IRubyScript;

public interface ISavePolicy {

	/**
	 * 
	 */
	void preSave(IRubyScript unit);

	/**
	 * Returns the compilation unit in which the argument has been changed. If
	 * the argument is not changed, the returned result is <code>null</code>.
	 */
	IRubyScript postSave(IRubyScript unit);
}
