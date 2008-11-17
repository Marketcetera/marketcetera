package org.rubypeople.rdt.internal.corext.util;

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.search.SearchPattern;

public class SearchUtils {

    /**
     * Constant for use as matchRule in {@link SearchPattern#createPattern(IRubyElement, int, int)}
     * to get search behavior as of 3.1M3 (all generic instantiations are found).
     */
    public final static int GENERICS_AGNOSTIC_MATCH_RULE= SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE | SearchPattern.R_ERASURE_MATCH;

    
    /**
     * Returns whether the given pattern is a camel case pattern or not.
     * 
     * @param pattern the pattern to inspect
     * @return whether it is a camel case pattern or not
     */
	public static boolean isCamelCasePattern(String pattern) {
		return SearchPattern.validateMatchRule(
			pattern, 
			SearchPattern.R_CAMELCASE_MATCH) == SearchPattern.R_CAMELCASE_MATCH;
	}

}
