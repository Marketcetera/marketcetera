/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.text.spelling;

import java.util.LinkedList;
import java.util.Locale;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.DefaultSpellChecker;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.ISpellCheckIterator;

import com.ibm.icu.text.BreakIterator;

/**
 * Iterator to spell-check ruby comment regions.
 *
 * @since 3.0
 */
public class SpellCheckIterator implements ISpellCheckIterator/*, IJavaDocTagConstants, IHtmlTagConstants*/ {

	/** The content of the region */
	private final String fContent;

	/** The line delimiter */
	private final String fDelimiter;

	/** The last token */
	private String fLastToken= null;

	/** The next break */
	private int fNext= 1;

	/** The offset of the region */
	private final int fOffset;

	/** The predecessor break */
	private int fPredecessor;

	/** The previous break */
	private int fPrevious= 0;

	/** The sentence breaks */
	private final LinkedList fSentenceBreaks= new LinkedList();

	/** Does the current word start a sentence? */
	private boolean fStartsSentence= false;

	/** The successor break */
	private int fSuccessor;

	/** The word iterator */
	private final BreakIterator fWordIterator;

	/**
	 * Creates a new spell check iterator.
	 *
	 * @param document the document containing the specified partition
	 * @param region the region to spell-check
	 * @param locale the locale to use for spell-checking
	 */
	public SpellCheckIterator(IDocument document, IRegion region, Locale locale) {
		this(document, region, locale, BreakIterator.getWordInstance(locale));
	}

	/**
	 * Creates a new spell check iterator.
	 *
	 * @param document the document containing the specified partition
	 * @param region the region to spell-check
	 * @param locale the locale to use for spell-checking
	 * @param breakIterator the break-iterator
	 */
	public SpellCheckIterator(IDocument document, IRegion region, Locale locale, BreakIterator breakIterator) {
		fOffset= region.getOffset();
		fWordIterator= breakIterator;
		fDelimiter= TextUtilities.getDefaultLineDelimiter(document);

		String content;
		try {

			content= document.get(region.getOffset(), region.getLength());
//			if (content.startsWith(NLSElement.TAG_PREFIX))
//				content= ""; //$NON-NLS-1$

		} catch (Exception exception) {
			content= ""; //$NON-NLS-1$
		}
		fContent= content;

		fWordIterator.setText(content);
		fPredecessor= fWordIterator.first();
		fSuccessor= fWordIterator.next();

		final BreakIterator iterator= BreakIterator.getSentenceInstance(locale);
		iterator.setText(content);

		int offset= iterator.current();
		while (offset != BreakIterator.DONE) {

			fSentenceBreaks.add(new Integer(offset));
			offset= iterator.next();
		}
	}

	/*
	 * @see org.eclipse.spelling.done.ISpellCheckIterator#getBegin()
	 */
	public final int getBegin() {
		return fPrevious + fOffset;
	}

	/*
	 * @see org.eclipse.spelling.done.ISpellCheckIterator#getEnd()
	 */
	public final int getEnd() {
		return fNext + fOffset - 1;
	}

	/*
	 * @see java.util.Iterator#hasNext()
	 */
	public final boolean hasNext() {
		return fSuccessor != BreakIterator.DONE;
	}

	/**
	 * Does the specified token consist of at least one letter and digits
	 * only?
	 *
	 * @param begin the begin index
	 * @param end the end index
	 * @return <code>true</code> iff the token consists of digits and at
	 *         least one letter only, <code>false</code> otherwise
	 */
	protected final boolean isAlphaNumeric(final int begin, final int end) {

		char character= 0;

		boolean letter= false;
		for (int index= begin; index < end; index++) {

			character= fContent.charAt(index);
			if (Character.isLetter(character))
				letter= true;

			if (!Character.isLetterOrDigit(character))
				return false;
		}
		return letter;
	}

	/**
	 * Was the last token a Javadoc tag tag?
	 *
	 * @param tags the javadoc tags to check
	 * @return <code>true</code> iff the last token was a Javadoc tag,
	 *         <code>false</code> otherwise
	 */
	protected final boolean isJavadocToken(final String[] tags) {

		if (fLastToken != null) {

			for (int index= 0; index < tags.length; index++) {

				if (fLastToken.equals(tags[index]))
					return true;
			}
		}
		return false;
	}

	/**
	 * Is the current token a single letter token surrounded by
	 * non-whitespace characters?
	 *
	 * @param begin the begin index
	 * @return <code>true</code> iff the token is a single letter token,
	 *         <code>false</code> otherwise
	 */
	protected final boolean isSingleLetter(final int begin) {

		if (begin > 0 && begin < fContent.length() - 1)
			return Character.isWhitespace(fContent.charAt(begin - 1)) && Character.isLetter(fContent.charAt(begin)) && Character.isWhitespace(fContent.charAt(begin + 1));

		return false;
	}

	/**
	 * Does the specified token look like an URL?
	 *
	 * @param begin the begin index
	 * @return <code>true</code> iff this token look like an URL,
	 *         <code>false</code> otherwise
	 */
	protected final boolean isUrlToken(final int begin) {

		for (int index= 0; index < DefaultSpellChecker.URL_PREFIXES.length; index++) {

			if (fContent.startsWith(DefaultSpellChecker.URL_PREFIXES[index], begin))
				return true;
		}
		return false;
	}

	/**
	 * Does the specified token consist of whitespace only?
	 *
	 * @param begin the begin index
	 * @param end the end index
	 * @return <code>true</code> iff the token consists of whitespace
	 *         only, <code>false</code> otherwise
	 */
	protected final boolean isWhitespace(final int begin, final int end) {

		for (int index= begin; index < end; index++) {

			if (!Character.isWhitespace(fContent.charAt(index)))
				return false;
		}
		return true;
	}

	/*
	 * @see java.util.Iterator#next()
	 */
	public final Object next() {

		String token= nextToken();
		while (token == null && fSuccessor != BreakIterator.DONE)
			token= nextToken();

		fLastToken= token;

		return token;
	}

	/**
	 * Advances the end index to the next word break.
	 */
	protected final void nextBreak() {

		fNext= fSuccessor;
		fPredecessor= fSuccessor;

		fSuccessor= fWordIterator.next();
	}

	/**
	 * Returns the next sentence break.
	 *
	 * @return the next sentence break
	 */
	protected final int nextSentence() {
		return ((Integer) fSentenceBreaks.getFirst()).intValue();
	}

	/**
	 * Determines the next token to be spell-checked.
	 *
	 * @return the next token to be spell-checked, or <code>null</code>
	 *         iff the next token is not a candidate for spell-checking.
	 */
	protected String nextToken() {

		String token= null;

		fPrevious= fPredecessor;
		fStartsSentence= false;

		nextBreak();

		boolean update= false;
		if (fNext - fPrevious > 0) {

			/*if (fSuccessor != BreakIterator.DONE && fContent.charAt(fPrevious) == JAVADOC_TAG_PREFIX) {

				nextBreak();
				if (Character.isLetter(fContent.charAt(fPrevious + 1))) {
					update= true;
					token= fContent.substring(fPrevious, fNext);
				} else
					fPredecessor= fNext;

			} else if (fSuccessor != BreakIterator.DONE && fContent.charAt(fPrevious) == HTML_TAG_PREFIX && (Character.isLetter(fContent.charAt(fNext)) || fContent.charAt(fNext) == '/')) {

				if (fContent.startsWith(HTML_CLOSE_PREFIX, fPrevious))
					nextBreak();

				nextBreak();

//				if (fSuccessor != BreakIterator.DONE && fContent.charAt(fNext) == HTML_TAG_POSTFIX) {
//
//					nextBreak();
//					if (fSuccessor != BreakIterator.DONE) {
//						update= true;
//						token= fContent.substring(fPrevious, fNext);
//					}
//				}
			} else*/ if (!isWhitespace(fPrevious, fNext) && isAlphaNumeric(fPrevious, fNext)) {

				if (isUrlToken(fPrevious))
					skipTokens(fPrevious, ' ');
				/*else if (isJavadocToken(JAVADOC_PARAM_TAGS))
					fLastToken= null;
				else if (isJavadocToken(JAVADOC_REFERENCE_TAGS)) {
					fLastToken= null;
					skipTokens(fPrevious, fDelimiter.charAt(0));
				}*/ else if (fNext - fPrevious > 1 || isSingleLetter(fPrevious))
					token= fContent.substring(fPrevious, fNext);

				update= true;
			}
		}

		if (update && fSentenceBreaks.size() > 0) {

			if (fPrevious >= nextSentence()) {

				while (fSentenceBreaks.size() > 0 && fPrevious >= nextSentence())
					fSentenceBreaks.removeFirst();

				fStartsSentence= (fLastToken == null) || (token != null);
			}
		}
		return token;
	}

	/*
	 * @see java.util.Iterator#remove()
	 */
	public final void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Skip the tokens until the stop character is reached.
	 *
	 * @param begin the begin index
	 * @param stop the stop character
	 */
	protected final void skipTokens(final int begin, final char stop) {

		int end= begin;

		while (end < fContent.length() && fContent.charAt(end) != stop)
			end++;

		if (end < fContent.length()) {

			fNext= end;
			fPredecessor= fNext;

			fSuccessor= fWordIterator.following(fNext);
		} else
			fSuccessor= BreakIterator.DONE;
	}

	/*
	 * @see org.eclipse.spelling.done.ISpellCheckIterator#startsSentence()
	 */
	public final boolean startsSentence() {
		return fStartsSentence;
	}
}
