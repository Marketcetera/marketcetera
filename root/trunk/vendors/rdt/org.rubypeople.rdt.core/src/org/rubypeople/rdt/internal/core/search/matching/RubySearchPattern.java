package org.rubypeople.rdt.internal.core.search.matching;

import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.core.search.indexing.IIndexConstants;

public class RubySearchPattern extends SearchPattern implements IIndexConstants {

	/*
	 * Whether this pattern is case sensitive.
	 */
	boolean isCaseSensitive;

	/*
	 * Whether this pattern is camel case.
	 */
	boolean isCamelCase;
	
	/**
	 * One of following pattern value:
	 * <ul>
	 * 	<li>{@link #R_EXACT_MATCH}</li>
	 *		<li>{@link #R_PREFIX_MATCH}</li>
	 *		<li>{@link #R_PATTERN_MATCH}</li>
	 *		<li>{@link #R_REGEXP_MATCH}</li>
	 *		<li>{@link #R_CAMELCASE_MATCH}</li>
	 * </ul>
	 */
	int matchMode;

	/**
	 * One of {@link #R_ERASURE_MATCH}, {@link #R_EQUIVALENT_MATCH}, {@link #R_FULL_MATCH}.
	 */
	int matchCompatibility;
	
	/**
	 * Mask used on match rule for match mode.
	 */
	public static final int MATCH_MODE_MASK = R_EXACT_MATCH | R_PREFIX_MATCH | R_PATTERN_MATCH | R_REGEXP_MATCH;

	/**
	 * Mask used on match rule for generic relevance.
	 */
	public static final int MATCH_COMPATIBILITY_MASK = R_ERASURE_MATCH | R_EQUIVALENT_MATCH | R_FULL_MATCH;
	
	protected RubySearchPattern(int patternKind, int matchRule) {
		super(matchRule);
		((InternalSearchPattern)this).kind = patternKind;
		// Use getMatchRule() instead of matchRule as super constructor may modify its value
		// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=81377
		int rule = getMatchRule();
		this.isCaseSensitive = (rule & R_CASE_SENSITIVE) != 0;
		this.isCamelCase = (rule & R_CAMELCASE_MATCH) != 0;
		this.matchCompatibility = rule & MATCH_COMPATIBILITY_MASK;
		this.matchMode = rule & MATCH_MODE_MASK;
	}
	
	public SearchPattern getBlankPattern() {
		return null;
	}
	
	int getMatchMode() {
		return this.matchMode;
	}
	
	boolean isCamelCase() {
		return this.isCamelCase;
	}

	boolean isCaseSensitive () {
		return this.isCaseSensitive;
	}
	
	protected StringBuffer print(StringBuffer output) {
		output.append(", "); //$NON-NLS-1$
		if (this.isCamelCase) {
			output.append("camel case + "); //$NON-NLS-1$
		}
		switch(getMatchMode()) {
			case R_EXACT_MATCH : 
				output.append("exact match,"); //$NON-NLS-1$
				break;
			case R_PREFIX_MATCH :
				output.append("prefix match,"); //$NON-NLS-1$
				break;
			case R_PATTERN_MATCH :
				output.append("pattern match,"); //$NON-NLS-1$
				break;
			case R_REGEXP_MATCH :
				output.append("regexp match, "); //$NON-NLS-1$
				break;
		}
		if (isCaseSensitive())
			output.append(" case sensitive"); //$NON-NLS-1$
		else
			output.append(" case insensitive"); //$NON-NLS-1$
		if ((this.matchCompatibility & R_ERASURE_MATCH) != 0) {
			output.append(", erasure only"); //$NON-NLS-1$
		}
		if ((this.matchCompatibility & R_EQUIVALENT_MATCH) != 0) {
			output.append(", equivalent oronly"); //$NON-NLS-1$
		}
		return output;
	}
}
