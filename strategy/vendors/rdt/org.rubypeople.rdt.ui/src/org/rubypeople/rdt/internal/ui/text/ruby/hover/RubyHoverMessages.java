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
package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

class RubyHoverMessages extends NLS {

	private static final String BUNDLE_NAME= RubyHoverMessages.class.getName();
	private RubyHoverMessages() {
	}

	public static String RubyTextHover_makeStickyHint;
	public static String RubyTextHover_createTextHover;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, RubyHoverMessages.class);
	}
	
	/**
	 * Gets a string from the resource bundle and formats it with the argument
	 * 
	 * @param key	the string used to get the bundle value, must not be null
	 * @since 0.8.0
	 */
	public static String getFormattedString(String key, Object arg) {
		return MessageFormat.format(key, new Object[] { arg });
	}

}
