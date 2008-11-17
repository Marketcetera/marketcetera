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

/**
 * Preference keys for the comment spell-checker.
 *
 * @since 3.0
 */
public interface ISpellCheckPreferenceKeys {

	/**
	 * A named preference that controls whether words containing digits should
	 * be skipped during spell-checking.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String SPELLING_IGNORE_DIGITS= "spelling_ignore_digits"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether mixed case words should be
	 * skipped during spell-checking.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String SPELLING_IGNORE_MIXED= "spelling_ignore_mixed"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether sentence capitalization should
	 * be ignored during spell-checking.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String SPELLING_IGNORE_SENTENCE= "spelling_ignore_sentence"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether upper case words should be
	 * skipped during spell-checking.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String SPELLING_IGNORE_UPPER= "spelling_ignore_upper"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether urls should be ignored during
	 * spell-checking.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String SPELLING_IGNORE_URLS= "spelling_ignore_urls"; //$NON-NLS-1$

	/**
	 * A named preference that controls the locale used for spell-checking.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public final static String SPELLING_LOCALE= "spelling_locale"; //$NON-NLS-1$

	/**
	 * A named preference that controls the number of proposals offered during
	 * spell-checking.
	 * <p>
	 * Value is of type <code>Integer</code>.
	 * </p>
	 */
	public final static String SPELLING_PROPOSAL_THRESHOLD= "spelling_proposal_threshold"; //$NON-NLS-1$

	/**
	 * A named preference that specifies the workspace user dictionary.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public final static String SPELLING_USER_DICTIONARY= "spelling_user_dictionary"; //$NON-NLS-1$

	/**
	 * A named preference that specifies whether spelling dictionaries are available to content assist.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String SPELLING_ENABLE_CONTENTASSIST= "spelling_enable_contentassist"; //$NON-NLS-1$
}
