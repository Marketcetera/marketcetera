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
package org.rubypeople.rdt.internal.ui.text.folding;

import org.eclipse.osgi.util.NLS;

/**
 * @since 0.8.0
 */
class FoldingMessages extends NLS {

	private static final String BUNDLE_NAME= FoldingMessages.class.getName();
	private FoldingMessages() {
	}
	
	public static String DefaultRubyFoldingPreferenceBlock_title;
	public static String DefaultRubyFoldingPreferenceBlock_comments;
	public static String DefaultRubyFoldingPreferenceBlock_innerTypes;
	public static String DefaultRubyFoldingPreferenceBlock_methods;
	public static String EmptyRubyFoldingPreferenceBlock_emptyCaption;

	static {
		NLS.initializeMessages(BUNDLE_NAME, FoldingMessages.class);
	}
}
