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

import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.ISpellCheckPreferenceKeys;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.ISpellChecker;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.ISpellEvent;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.ISpellEventListener;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
 * Internal abstract spelling engine, subclasses provide a content-type specific implementation.
 *
 * @since 3.1
 */
public abstract class SpellingEngine implements ISpellingEngine {

	/**
	 * {@link ISpellEvent}listener that forwards events as
	 * {@link org.eclipse.ui.texteditor.spelling.SpellingProblem}.
	 */
	protected static class SpellEventListener implements ISpellEventListener {

		/** Spelling problem collector */
		private ISpellingProblemCollector fCollector;

		/**
		 * Initialize with the given spelling problem collector.
		 *
		 * @param collector the spelling problem collector
		 */
		public SpellEventListener(ISpellingProblemCollector collector) {
			super();
			fCollector= collector;
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.spelling.engine.ISpellEventListener#handle(org.eclipse.jdt.internal.ui.text.spelling.engine.ISpellEvent)
		 */
		public void handle(ISpellEvent event) {
			fCollector.accept(new RubySpellingProblem(event));
		}
	}

	/*
	 * @see org.eclipse.ui.texteditor.spelling.ISpellingEngine#check(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IRegion[], org.eclipse.ui.texteditor.spelling.SpellingContext, org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void check(IDocument document, IRegion[] regions, SpellingContext context, ISpellingProblemCollector collector, IProgressMonitor monitor) {
		IPreferenceStore preferences= PreferenceConstants.getPreferenceStore();
		if (collector != null) {
			Locale locale= getLocale(preferences);
			ISpellChecker checker= SpellCheckEngine.getInstance().createSpellChecker(locale, preferences);
			if (checker != null)
				check(document, regions, checker, locale, collector, monitor);
		}
	}

	/**
	 * Spell-checks the given document regions with the given arguments.
	 *
	 * @param document the document
	 * @param regions the regions
	 * @param checker the spell checker
	 * @param locale the locale
	 * @param collector the spelling problem collector
	 * @param monitor the progress monitor, can be <code>null</code>
	 */
	protected abstract void check(IDocument document, IRegion[] regions, ISpellChecker checker, Locale locale, ISpellingProblemCollector collector, IProgressMonitor monitor);

	/**
	 * Returns the current locale of the spelling preferences.
	 *
	 * @return The current locale of the spelling preferences
	 */
	private Locale getLocale(IPreferenceStore preferences) {
		Locale defaultLocale= SpellCheckEngine.getDefaultLocale();
		String locale= preferences.getString(ISpellCheckPreferenceKeys.SPELLING_LOCALE);
		if (locale.equals(defaultLocale.toString()))
			return defaultLocale;

		if (locale.length() >= 5)
			return new Locale(locale.substring(0, 2), locale.substring(3, 5));

		return defaultLocale;
	}
}
