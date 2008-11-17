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

package org.rubypeople.rdt.internal.ui.text.spelling.engine;

import java.util.Locale;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Interface for spell-check engines.
 *
 * @since 3.0
 */
public interface ISpellCheckEngine {

	/**
	 * Creates a configured instance of a spell-checker that uses the
	 * appropriate dictionaries.
	 *
	 * @param locale
	 *                   The locale to get the spell checker for
	 * @param store
	 *                   The preference store for the spell-checker
	 * @return A configured instance of a spell checker, or <code>null</code>
	 *               iff no dictionary could be found for that locale
	 */
	ISpellChecker createSpellChecker(Locale locale, IPreferenceStore store);

	/**
	 * Returns the current locale of the spell check engine.
	 *
	 * @return The current locale of the engine
	 */
	Locale getLocale();

	/**
	 * Registers a dictionary for all locales available on the platform.
	 * <p>
	 * This call is equivalent to calling <code>registerDictionary(Locale,ISpellDictionary)</code>
	 * for each of the locales returned by <code>Locale.getAvailableLocales()</code>.
	 * </p>
	 *
	 * @param dictionary
	 *                   The dictionary to register
	 */
	void registerDictionary(ISpellDictionary dictionary);

	/**
	 * Registers a dictionary tuned for the specified locale with this engine.
	 *
	 * @param locale
	 *                   The locale to register the dictionary with
	 * @param dictionary
	 *                   The dictionary to register
	 */
	void registerDictionary(Locale locale, ISpellDictionary dictionary);

	/**
	 * Unloads the spell check engine and its associated components.
	 * <p>
	 * All registered dictionaries are unloaded and the engine unregisters for
	 * preference changes. After a new call to <code>getSpellChecker(Locale)</code>,
	 * it registers again for preference changes. The dictionaries perform lazy
	 * loading, that is to say on the next query they reload their associated
	 * word lists.
	 */
	void unload();

	/**
	 * Unregisters a dictionary previously registered either by a call to
	 * <code>registerDictionary(Locale,ISpellDictionary)</code> or <code>registerDictionary(ISpellDictionary)</code>.
	 * If the dictionary was not registered before, nothing happens.
	 *
	 * @param dictionary
	 *                   The dictionary to unregister
	 */
	void unregisterDictionary(ISpellDictionary dictionary);
}
