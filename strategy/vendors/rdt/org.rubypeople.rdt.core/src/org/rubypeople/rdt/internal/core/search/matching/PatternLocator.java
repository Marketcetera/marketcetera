package org.rubypeople.rdt.internal.core.search.matching;

import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.search.indexing.IIndexConstants;
import org.rubypeople.rdt.internal.core.util.CharOperation;

public class PatternLocator implements IIndexConstants {
	
	// store pattern info
	protected int matchMode;
	protected boolean isCaseSensitive;
	protected boolean isCamelCase;
	protected boolean isEquivalentMatch;
	protected boolean isErasureMatch;
	protected boolean mustResolve;
	protected boolean mayBeGeneric;
	
	/* match levels */
	public static final int IMPOSSIBLE_MATCH = 0;
	public static final int INACCURATE_MATCH = 1;
	public static final int POSSIBLE_MATCH = 2;
	public static final int ACCURATE_MATCH = 3;
	public static final int ERASURE_MATCH = 4;

	// Possible rule match flavors
	// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=79866
	public static final int EXACT_FLAVOR = 0x0010;
	public static final int PREFIX_FLAVOR = 0x0020;
	public static final int PATTERN_FLAVOR = 0x0040;
	public static final int REGEXP_FLAVOR = 0x0080;
	public static final int CAMELCASE_FLAVOR = 0x0100;
	public static final int SUPER_INVOCATION_FLAVOR = 0x0200;
	public static final int SUB_INVOCATION_FLAVOR = 0x0400;
	public static final int OVERRIDDEN_METHOD_FLAVOR = 0x0800;
	public static final int MATCH_LEVEL_MASK = 0x0F;
	public static final int FLAVORS_MASK = ~MATCH_LEVEL_MASK;
	
	/* match container */
	public static final int COMPILATION_UNIT_CONTAINER = 1;
	public static final int CLASS_CONTAINER = 2;
	public static final int METHOD_CONTAINER = 4;
	public static final int FIELD_CONTAINER = 8;
	public static final int ALL_CONTAINER =
		COMPILATION_UNIT_CONTAINER | CLASS_CONTAINER | METHOD_CONTAINER | FIELD_CONTAINER;
	
	public PatternLocator(SearchPattern pattern) {
		int matchRule = pattern.getMatchRule();
		this.isCaseSensitive = (matchRule & SearchPattern.R_CASE_SENSITIVE) != 0;
		this.isCamelCase = (matchRule & SearchPattern.R_CAMELCASE_MATCH) != 0;
		this.isErasureMatch = (matchRule & SearchPattern.R_ERASURE_MATCH) != 0;
		this.isEquivalentMatch = (matchRule & SearchPattern.R_EQUIVALENT_MATCH) != 0;
		this.matchMode = matchRule & RubySearchPattern.MATCH_MODE_MASK;
		this.mustResolve = ((InternalSearchPattern)pattern).mustResolve;
	}

	/**
	 * Returns the type(s) of container for this pattern.
	 * It is a bit combination of types, denoting compilation unit, class declarations, field declarations or method declarations.
	 */
	protected int matchContainer() {
		// override if the pattern can be more specific
		return ALL_CONTAINER;
	}
	
	public static PatternLocator patternLocator(SearchPattern pattern) {
		switch (((InternalSearchPattern)pattern).kind) { // XXX Implement all pattern types (not just methods, fields, and type declarations)
			case IIndexConstants.TYPE_REF_PATTERN :
				return new TypeReferenceLocator((TypeReferencePattern) pattern);
			case IIndexConstants.TYPE_DECL_PATTERN :
				return new TypeDeclarationLocator((TypeDeclarationPattern) pattern);
//			case IIndexConstants.SUPER_REF_PATTERN : FIXME Implement these commented out pattern locators!
//				return new SuperTypeReferenceLocator((SuperTypeReferencePattern) pattern);
//			case IIndexConstants.CONSTRUCTOR_PATTERN :
//				return new ConstructorLocator((ConstructorPattern) pattern);
			case IIndexConstants.FIELD_PATTERN :
				return new FieldLocator((FieldPattern) pattern);
			case IIndexConstants.METHOD_PATTERN :
				return new MethodLocator((MethodPattern) pattern);
			case IIndexConstants.OR_PATTERN :
				return new OrLocator((OrPattern) pattern);
			case IIndexConstants.LOCAL_VAR_PATTERN :
				return new LocalVariableLocator((LocalVariablePattern) pattern);
		}
		return null;
	}
	
	/*
	 * Clear caches
	 */
	protected void clear() {
		// nothing to clear by default
	}

	public void reportMatches(RubyScript script, MatchLocator locator) {
		// TODO Auto-generated method stub
		// override in specific locators!
	}
	
	/**
	 * Returns whether the given name matches the given pattern.
	 */
	protected boolean matchesName(char[] pattern, char[] name) {
		if (pattern == null) return true; // null is as if it was "*"
		if (name == null) return false; // cannot match null name
		return matchNameValue(pattern, name) != IMPOSSIBLE_MATCH;
	}
	
	/**
	 * Return how the given name matches the given pattern.
	 * @see "https://bugs.eclipse.org/bugs/show_bug.cgi?id=79866"
	 * 
	 * @param pattern
	 * @param name
	 * @return Possible values are:
	 * <ul>
	 * 	<li> {@link #ACCURATE_MATCH}</li>
	 * 	<li> {@link #IMPOSSIBLE_MATCH}</li>
	 * 	<li> {@link #POSSIBLE_MATCH} which may be flavored with following values:
	 * 		<ul>
	 * 		<li>{@link #EXACT_FLAVOR}: Given name is equals to pattern</li>
	 * 		<li>{@link #PREFIX_FLAVOR}: Given name prefix equals to pattern</li>
	 * 		<li>{@link #CAMELCASE_FLAVOR}: Given name matches pattern as Camel Case</li>
	 * 		<li>{@link #PATTERN_FLAVOR}: Given name matches pattern as Pattern (ie. using '*' and '?' characters)</li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 */
	protected int matchNameValue(char[] pattern, char[] name) {
		if (pattern == null) return ACCURATE_MATCH; // null is as if it was "*"
		if (name == null) return IMPOSSIBLE_MATCH; // cannot match null name
		if (name.length == 0) { // empty name
			if (pattern.length == 0) { // can only matches empty pattern
				return ACCURATE_MATCH;
			}
			return IMPOSSIBLE_MATCH;
		} else if (pattern.length == 0) {
			return IMPOSSIBLE_MATCH; // need to have both name and pattern length==0 to be accurate
		}
		boolean matchFirstChar = !this.isCaseSensitive || pattern[0] == name[0];
		boolean sameLength = pattern.length == name.length;
		boolean canBePrefix = name.length >= pattern.length;
		if (this.isCamelCase && matchFirstChar && CharOperation.camelCaseMatch(pattern, name)) {
			return POSSIBLE_MATCH;
		}
		switch (this.matchMode) {
			case SearchPattern.R_EXACT_MATCH:
				if (!this.isCamelCase) {
					if (sameLength && matchFirstChar && CharOperation.equals(pattern, name, this.isCaseSensitive)) {
						return POSSIBLE_MATCH | EXACT_FLAVOR;
					}
					break;
				}
				// fall through next case to match as prefix if camel case failed
			case SearchPattern.R_PREFIX_MATCH:
				if (canBePrefix && matchFirstChar && CharOperation.prefixEquals(pattern, name, this.isCaseSensitive)) {
					return POSSIBLE_MATCH;
				}
				break;
			case SearchPattern.R_PATTERN_MATCH:
				if (!this.isCaseSensitive) {
					pattern = CharOperation.toLowerCase(pattern);
				}
				if (CharOperation.match(pattern, name, this.isCaseSensitive)) {
					return POSSIBLE_MATCH;
				}
				break;
			case SearchPattern.R_REGEXP_MATCH :
				// TODO (frederic) implement regular expression match
				break;
		}
		return IMPOSSIBLE_MATCH;
	}
}
