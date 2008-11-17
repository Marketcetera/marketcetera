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

package org.rubypeople.rdt.internal.ui.text.spelling;

import java.net.URL;
import java.util.Locale;

import org.rubypeople.rdt.internal.ui.text.spelling.engine.LocaleSensitiveSpellDictionary;

/**
 * Dictionary used by the spell reconciling strategy.
 *
 * @since 3.0
 */
public class SpellReconcileDictionary extends LocaleSensitiveSpellDictionary /*implements IJavaDocTagConstants, IHtmlTagConstants*/ {

	/**
	 * Creates a new locale sensitive spell dictionary.
	 *
	 * @param locale
	 *                   The locale for this dictionary
	 * @param location
	 *                   The location of the locale sensitive dictionaries
	 */
	public SpellReconcileDictionary(final Locale locale, final URL location) {
		super(locale, location);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.spelling.engine.ISpellDictionary#isCorrect(java.lang.String)
	 */
	public boolean isCorrect(final String word) {

//		final char character= word.charAt(0);
//		if (character != JAVADOC_TAG_PREFIX && character != HTML_TAG_PREFIX)
			return super.isCorrect(word);

//		return false;
	}
}
