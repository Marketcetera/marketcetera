package org.rubypeople.rdt.core.search;

import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.compiler.parser.ScannerHelper;
import org.rubypeople.rdt.internal.core.LocalVariable;
import org.rubypeople.rdt.internal.core.search.MethodPatternParser;
import org.rubypeople.rdt.internal.core.search.indexing.IIndexConstants;
import org.rubypeople.rdt.internal.core.search.matching.ConstructorPattern;
import org.rubypeople.rdt.internal.core.search.matching.FieldPattern;
import org.rubypeople.rdt.internal.core.search.matching.InternalSearchPattern;
import org.rubypeople.rdt.internal.core.search.matching.LocalVariablePattern;
import org.rubypeople.rdt.internal.core.search.matching.MatchLocator;
import org.rubypeople.rdt.internal.core.search.matching.MethodPattern;
import org.rubypeople.rdt.internal.core.search.matching.OrPattern;
import org.rubypeople.rdt.internal.core.search.matching.QualifiedTypeDeclarationPattern;
import org.rubypeople.rdt.internal.core.search.matching.TypeDeclarationPattern;
import org.rubypeople.rdt.internal.core.search.matching.TypeReferencePattern;
import org.rubypeople.rdt.internal.core.util.CharOperation;

public abstract class SearchPattern extends InternalSearchPattern {
//	 Rules for pattern matching: (exact, prefix, pattern) [ | case sensitive]
	/**
	 * Match rule: The search pattern matches exactly the search result,
	 * that is, the source of the search result equals the search pattern.
	 */
	public static final int R_EXACT_MATCH = 0;

	/**
	 * Match rule: The search pattern is a prefix of the search result.
	 */
	public static final int R_PREFIX_MATCH = 0x0001;

	/**
	 * Match rule: The search pattern contains one or more wild cards ('*' or '?'). 
	 * A '*' wild-card can replace 0 or more characters in the search result.
	 * A '?' wild-card replaces exactly 1 character in the search result.
	 */
	public static final int R_PATTERN_MATCH = 0x0002;

	/**
	 * Match rule: The search pattern contains a regular expression.
	 */
	public static final int R_REGEXP_MATCH = 0x0004;

	/**
	 * Match rule: The search pattern matches the search result only if cases are the same.
	 * Can be combined to previous rules, e.g. {@link #R_EXACT_MATCH} | {@link #R_CASE_SENSITIVE}
	 */
	public static final int R_CASE_SENSITIVE = 0x0008;

	/**
	 * Match rule: The search pattern matches search results as raw/parameterized types/methods with same erasure.
	 * This mode has no effect on other java elements search.<br>
	 * Type search example:
	 * 	<ul>
	 * 	<li>pattern: <code>List&lt;Exception&gt;</code></li>
	 * 	<li>match: <code>List&lt;Object&gt;</code></li>
	 * 	</ul>
	 * Method search example:
	 * 	<ul>
	 * 	<li>declaration: <code>&lt;T&gt;foo(T t)</code></li>
	 * 	<li>pattern: <code>&lt;Exception&gt;foo(new Exception())</code></li>
	 * 	<li>match: <code>&lt;Object&gt;foo(new Object())</code></li>
	 * 	</ul>
	 * Can be combined to all other match rules, e.g. {@link #R_CASE_SENSITIVE} | {@link #R_ERASURE_MATCH}
	 * This rule is not activated by default, so raw types or parameterized types with same erasure will not be found
	 * for pattern List&lt;String&gt;,
	 * Note that with this pattern, the match selection will be only on the erasure even for parameterized types.
	 * @since 3.1
	 */
	public static final int R_ERASURE_MATCH = 0x0010;

	/**
	 * Match rule: The search pattern matches search results as raw/parameterized types/methods with equivalent type parameters.
	 * This mode has no effect on other java elements search.<br>
	 * Type search example:
	 * <ul>
	 * 	<li>pattern: <code>List&lt;Exception&gt;</code></li>
	 * 	<li>match:
	 * 		<ul>
	 * 		<li><code>List&lt;? extends Throwable&gt;</code></li>
	 * 		<li><code>List&lt;? super RuntimeException&gt;</code></li>
	 * 		<li><code>List&lt;?&gt;</code></li>
	 *			</ul>
	 * 	</li>
	 * 	</ul>
	 * Method search example:
	 * 	<ul>
	 * 	<li>declaration: <code>&lt;T&gt;foo(T t)</code></li>
	 * 	<li>pattern: <code>&lt;Exception&gt;foo(new Exception())</code></li>
	 * 	<li>match:
	 * 		<ul>
	 * 		<li><code>&lt;? extends Throwable&gt;foo(new Exception())</code></li>
	 * 		<li><code>&lt;? super RuntimeException&gt;foo(new Exception())</code></li>
	 * 		<li><code>foo(new Exception())</code></li>
	 *			</ul>
	 * 	</ul>
	 * Can be combined to all other match rules, e.g. {@link #R_CASE_SENSITIVE} | {@link #R_EQUIVALENT_MATCH}
	 * This rule is not activated by default, so raw types or equivalent parameterized types will not be found
	 * for pattern List&lt;String&gt;,
	 * This mode is overridden by {@link  #R_ERASURE_MATCH} as erasure matches obviously include equivalent ones.
	 * That means that pattern with rule set to {@link #R_EQUIVALENT_MATCH} | {@link  #R_ERASURE_MATCH}
	 * will return same results than rule only set with {@link  #R_ERASURE_MATCH}.
	 * @since 3.1
	 */
	public static final int R_EQUIVALENT_MATCH = 0x0020;

	/**
	 * Match rule: The search pattern matches exactly the search result,
	 * that is, the source of the search result equals the search pattern.
	 * @since 3.1
	 */
	public static final int R_FULL_MATCH = 0x0040;

	/**
	 * Match rule: The search pattern contains a Camel Case expression.
	 * <br>
	 * Examples:
	 * <ul>
	 * 	<li><code>NPE</code> type string pattern will match
	 * 		<code>NullPointerException</code> and <code>NpPermissionException</code> types,</li>
	 * 	<li><code>NuPoEx</code> type string pattern will only match
	 * 		<code>NullPointerException</code> type.</li>
	 * </ul>
	 * @see CharOperation#camelCaseMatch(char[], char[]) for a detailed explanation
	 * of Camel Case matching.
	 *<br>
	 * Can be combined to {@link #R_PREFIX_MATCH} match rule. For example,
	 * when prefix match rule is combined with Camel Case match rule,
	 * <code>"nPE"</code> pattern will match <code>nPException</code>.
	 *<br>
	 * Match rule {@link #R_PATTERN_MATCH} may also be combined but both rules
	 * will not be used simultaneously as they are mutually exclusive.
	 * Used match rule depends on whether string pattern contains specific pattern 
	 * characters (e.g. '*' or '?') or not. If it does, then only Pattern match rule
	 * will be used, otherwise only Camel Case match will be used.
	 * For example, with <code>"NPE"</code> string pattern, search will only use
	 * Camel Case match rule, but with <code>N*P*E*</code> string pattern, it will 
	 * use only Pattern match rule.
	 * 
	 * @since 3.2
	 */
	public static final int R_CAMELCASE_MATCH = 0x0080;

	private static final int MODE_MASK = R_EXACT_MATCH | R_PREFIX_MATCH | R_PATTERN_MATCH | R_REGEXP_MATCH;
	
	private int matchRule;

	/**
	 * Creates a search pattern with the rule to apply for matching index keys. 
	 * It can be exact match, prefix match, pattern match or regexp match.
	 * Rule can also be combined with a case sensitivity flag.
	 * 
	 * @param matchRule one of {@link #R_EXACT_MATCH}, {@link #R_PREFIX_MATCH}, {@link #R_PATTERN_MATCH},
	 * 	{@link #R_REGEXP_MATCH}, {@link #R_CAMELCASE_MATCH} combined with one of following values:
	 * 	{@link #R_CASE_SENSITIVE}, {@link #R_ERASURE_MATCH} or {@link #R_EQUIVALENT_MATCH}.
	 *		e.g. {@link #R_EXACT_MATCH} | {@link #R_CASE_SENSITIVE} if an exact and case sensitive match is requested, 
	 *		{@link #R_PREFIX_MATCH} if a prefix non case sensitive match is requested or {@link #R_EXACT_MATCH} | {@link #R_ERASURE_MATCH}
	 *		if a non case sensitive and erasure match is requested.<br>
	 * 	Note that {@link #R_ERASURE_MATCH} or {@link #R_EQUIVALENT_MATCH} have no effect
	 * 	on non-generic types/methods search.<br>
	 * 	Note also that default behavior for generic types/methods search is to find exact matches.
	 */
	public SearchPattern(int matchRule) {
		this.matchRule = matchRule;
		// Set full match implicit mode
		if ((matchRule & (R_EQUIVALENT_MATCH | R_ERASURE_MATCH )) == 0) {
			this.matchRule |= R_FULL_MATCH;
		}
	}	
	
	/**
	 * Returns a blank pattern that can be used as a record to decode an index key.
	 * <p>
	 * Implementors of this method should return a new search pattern that is going to be used
	 * to decode index keys.
	 * </p>
	 * 
	 * @return a new blank pattern
	 * @see #decodeIndexKey(char[])
	 */
	public abstract SearchPattern getBlankPattern();
	
	/**
	 * Decode the given index key in this pattern. The decoded index key is used by 
	 * {@link #matchesDecodedKey(SearchPattern)} to find out if the corresponding index entry 
	 * should be considered.
	 * <p>
	 * This method should be re-implemented in subclasses that need to decode an index key.
	 * </p>
	 * 
	 * @param key the given index key
	 */
	public void decodeIndexKey(char[] key) {
		// called from findIndexMatches(), override as necessary
	}
	
	/**
	 * Returns a key to find in relevant index categories, if null then all index entries are matched.
	 * The key will be matched according to some match rule. These potential matches
	 * will be further narrowed by the match locator, but precise match locating can be expensive,
	 * and index query should be as accurate as possible so as to eliminate obvious false hits.
	 * <p>
	 * This method should be re-implemented in subclasses that need to narrow down the
	 * index query.
	 * </p>
	 * 
	 * @return an index key from this pattern, or <code>null</code> if all index entries are matched.
	 */
	public char[] getIndexKey() {
		return null; // called from queryIn(), override as necessary
	}
	/**
	 * Returns an array of index categories to consider for this index query.
	 * These potential matches will be further narrowed by the match locator, but precise
	 * match locating can be expensive, and index query should be as accurate as possible
	 * so as to eliminate obvious false hits.
	 * <p>
	 * This method should be re-implemented in subclasses that need to narrow down the
	 * index query.
	 * </p>
	 * 
	 * @return an array of index categories
	 */
	public char[][] getIndexCategories() {
		return CharOperation.NO_CHAR_CHAR; // called from queryIn(), override as necessary
	}
	
	/**
	 * Returns the rule to apply for matching index keys. Can be exact match, prefix match, pattern match or regexp match.
	 * Rule can also be combined with a case sensitivity flag.
	 * 
	 * @return one of R_EXACT_MATCH, R_PREFIX_MATCH, R_PATTERN_MATCH, R_REGEXP_MATCH combined with R_CASE_SENSITIVE,
	 *   e.g. R_EXACT_MATCH | R_CASE_SENSITIVE if an exact and case sensitive match is requested, 
	 *   or R_PREFIX_MATCH if a prefix non case sensitive match is requested.
	 * [TODO (frederic) I hope R_ERASURE_MATCH doesn't need to be on this list. Because it would be a breaking API change.]
	 */	
	public final int getMatchRule() {
		return this.matchRule;
	}
	/**
	 * Returns whether this pattern matches the given pattern (representing a decoded index key).
	 * <p>
	 * This method should be re-implemented in subclasses that need to narrow down the
	 * index query.
	 * </p>
	 * 
	 * @param decodedPattern a pattern representing a decoded index key
	 * @return whether this pattern matches the given pattern
	 */
	public boolean matchesDecodedKey(SearchPattern decodedPattern) {
		return true; // called from findIndexMatches(), override as necessary if index key is encoded
	}

	public static SearchPattern createPattern(int elementType, String stringPattern, int limitTo, int matchRule) {
		switch (elementType) {
		case IRubyElement.TYPE:
			return createTypePattern(stringPattern, limitTo, matchRule, IIndexConstants.TYPE_SUFFIX);
		case IRubyElement.METHOD:
			return createMethodOrConstructorPattern(stringPattern, limitTo, matchRule, false/*not a constructor*/);
		case IRubyElement.FIELD:
		case IRubyElement.CONSTANT:
		case IRubyElement.GLOBAL:
		case IRubyElement.CLASS_VAR:
		case IRubyElement.INSTANCE_VAR:
			return createFieldPattern(stringPattern, limitTo, matchRule);
		default:
			break;
		}
		return null;
	}
	
	/**
	 * Field pattern are formed by [declaringType.]name[ type]
	 * e.g. java.lang.String.serialVersionUID long
	 *		field*
	 */
	private static SearchPattern createFieldPattern(String patternString, int limitTo, int matchRule) {
		String fieldName = patternString;
		if (fieldName == null) return null;

		char[] fieldNameChars = fieldName.toCharArray();
		if (fieldNameChars.length == 1 && fieldNameChars[0] == '*') fieldNameChars = null;
			
		char[] declaringTypeQualification = null, declaringTypeSimpleName = null;
		char[] typeQualification = null, typeSimpleName = null;

		// Create field pattern
		boolean findDeclarations = false;
		boolean readAccess = false;
		boolean writeAccess = false;
		switch (limitTo) {
			case IRubySearchConstants.DECLARATIONS :
				findDeclarations = true;
				break;
			case IRubySearchConstants.REFERENCES :
				readAccess = true;
				writeAccess = true;
				break;
			case IRubySearchConstants.READ_ACCESSES :
				readAccess = true;
				break;
			case IRubySearchConstants.WRITE_ACCESSES :
				writeAccess = true;
				break;
			case IRubySearchConstants.ALL_OCCURRENCES :
				findDeclarations = true;
				readAccess = true;
				writeAccess = true;
				break;
		}
		return new FieldPattern(
				findDeclarations,
				readAccess,
				writeAccess,
				fieldNameChars,
				declaringTypeQualification,
				declaringTypeSimpleName,
				matchRule);
	}
	
	/**
	 * Returns whether the given name matches the given pattern.
	 * <p>
	 * This method should be re-implemented in subclasses that need to define how
	 * a name matches a pattern.
	 * </p>
	 * 
	 * @param pattern the given pattern, or <code>null</code> to represent "*"
	 * @param name the given name
	 * @return whether the given name matches the given pattern
	 */
	public boolean matchesName(char[] pattern, char[] name) {
		if (pattern == null) return true; // null is as if it was "*"
		if (name != null) {
			boolean isCaseSensitive = (this.matchRule & R_CASE_SENSITIVE) != 0;
			boolean isCamelCase = (this.matchRule & R_CAMELCASE_MATCH) != 0;
			int matchMode = this.matchRule & MODE_MASK;
			boolean sameLength = pattern.length == name.length;
			boolean canBePrefix = name.length >= pattern.length;
			boolean matchFirstChar = !isCaseSensitive || pattern.length == 0 || (name.length > 0 &&  pattern[0] == name[0]);
			if (isCamelCase && matchFirstChar && CharOperation.camelCaseMatch(pattern, name)) {
				return true;
			}
			switch (matchMode) {
				case R_EXACT_MATCH :
				case R_FULL_MATCH :
					if (!isCamelCase) {
						if (sameLength && matchFirstChar) {
							return CharOperation.equals(pattern, name, isCaseSensitive);
						}
						break;
					}
					// fall through next case to match as prefix if camel case failed
				case R_PREFIX_MATCH :
					if (canBePrefix && matchFirstChar) {
						return CharOperation.prefixEquals(pattern, name, isCaseSensitive);
					}
					break;

				case R_PATTERN_MATCH :
					if (!isCaseSensitive)
						pattern = CharOperation.toLowerCase(pattern);
					return CharOperation.match(pattern, name, isCaseSensitive);

				case R_REGEXP_MATCH :
					// TODO (frederic) implement regular expression match
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Type pattern are formed by [qualification '.']type [typeArguments].
	 * e.g. java.lang.Object
	 *		Runnable
	 *		List&lt;String&gt;
	 *
	 * @since 3.1
	 *		Type arguments can be specified to search references to parameterized types.
	 * 	and look as follow: '&lt;' { [ '?' {'extends'|'super'} ] type ( ',' [ '?' {'extends'|'super'} ] type )* | '?' } '&gt;'
	 * 	Please note that:
	 * 		- '*' is not valid inside type arguments definition &lt;&gt;
	 * 		- '?' is treated as a wildcard when it is inside &lt;&gt; (ie. it must be put on first position of the type argument)
	 */
	private static SearchPattern createTypePattern(String patternString, int limitTo, int matchRule, char indexSuffix) {
		char[] typePart = null;
		if (patternString != null) typePart = patternString.toCharArray();
		char[] typeChars = null;
		char[] qualificationChars = null;
		// get qualification name
		int lastDotPosition = CharOperation.lastIndexOf("::", typePart);
		if (lastDotPosition >= 0) {
			qualificationChars = CharOperation.subarray(typePart, 0, lastDotPosition);
			if (qualificationChars.length == 1 && qualificationChars[0] == '*')
				qualificationChars = null;
			typeChars = CharOperation.subarray(typePart, lastDotPosition+2, typePart.length);
		} else {
			typeChars = typePart;
		}
		if (typeChars != null && typeChars.length == 1 && typeChars[0] == '*') {
			typeChars = null;
		}
		switch (limitTo) {
			case IRubySearchConstants.DECLARATIONS : // cannot search for explicit member types
				return new QualifiedTypeDeclarationPattern(qualificationChars, typeChars, indexSuffix, matchRule);
			case IRubySearchConstants.REFERENCES :
				return new TypeReferencePattern(qualificationChars, typeChars, matchRule);
//			case IRubySearchConstants.IMPLEMENTORS : 
//				return new SuperTypeReferencePattern(qualificationChars, typeChars, SuperTypeReferencePattern.ONLY_SUPER_INTERFACES, indexSuffix, matchRule);
			case IRubySearchConstants.ALL_OCCURRENCES :
				return new OrPattern(
					new QualifiedTypeDeclarationPattern(qualificationChars, typeChars, indexSuffix, matchRule),// cannot search for explicit member types
					new TypeReferencePattern(qualificationChars, typeChars, matchRule));
		}
		return null;
	}

	/**
	 * Method pattern are formed by:<br>
	 * 	[declaringType '.'] selector ['(' parameterNames ')']
	 *		<br>e.g.<ul>
	 *			<li>java.lang.Runnable.run() void</li>
	 *			<li>main(*)</li>
	 *			<li>&lt;String&gt;toArray(String[])</li>
	 *		</ul>
	 * Constructor pattern are formed by:<br>
	 *		[declaringQualification '.'] type ['(' parameterNames ')']
	 *		<br>e.g.<ul>
	 *			<li>java.lang.Object()</li>
	 *			<li>Main(*)</li>
	 *			<li>&lt;Exception&gt;Sample(Exception)</li>
	 *		</ul>
	 * Type arguments have the same pattern that for type patterns
	 * @see #createTypePattern(String,int,int,char)
	 */
	private static SearchPattern createMethodOrConstructorPattern(String patternString, int limitTo, int matchRule, boolean isConstructor) {
		MethodPatternParser parser = new MethodPatternParser();
		parser.parse(patternString);
		char[] selectorChars = parser.getSelector();		
		char[][] parameterNames = parser.getParameterNames();		
		char[] declaringTypeSimpleName = parser.getTypeSimpleName();		
		char[] declaringTypeQualification = parser.getQualifiedTypeName();
		
		// Create method/constructor pattern
		boolean findDeclarations = true;
		boolean findReferences = true;
		switch (limitTo) {
			case IRubySearchConstants.DECLARATIONS :
				findReferences = false;
				break;
			case IRubySearchConstants.REFERENCES :
				findDeclarations = false;
				break;
			case IRubySearchConstants.ALL_OCCURRENCES :
				break;
		}
		if (isConstructor) {
			return new ConstructorPattern(
					findDeclarations,
					findReferences,
					declaringTypeSimpleName, 
					declaringTypeQualification,
					parameterNames,
					matchRule);
		} else {
			return new MethodPattern(
					findDeclarations,
					findReferences,
					selectorChars,
					declaringTypeQualification,
					declaringTypeSimpleName,
					parameterNames,
					matchRule);
		}
	}

	/**
	 * Answers true if the pattern matches the given name using CamelCase rules, or false otherwise. 
	 * CamelCase matching does NOT accept explicit wild-cards '*' and '?' and is inherently case sensitive.
	 * <br>
	 * CamelCase denotes the convention of writing compound names without spaces, and capitalizing every term.
	 * This function recognizes both upper and lower CamelCase, depending whether the leading character is capitalized
	 * or not. The leading part of an upper CamelCase pattern is assumed to contain a sequence of capitals which are appearing
	 * in the matching name; e.g. 'NPE' will match 'NullPointerException', but not 'NewPerfData'. A lower CamelCase pattern
	 * uses a lowercase first character. In Java, type names follow the upper CamelCase convention, whereas method or field
	 * names follow the lower CamelCase convention.
	 * <br>
	 * The pattern may contain lowercase characters, which will be match in a case sensitive way. These characters must
	 * appear in sequence in the name. For instance, 'NPExcep' will match 'NullPointerException', but not 'NullPointerExCEPTION'
	 * or 'NuPoEx' will match 'NullPointerException', but not 'NoPointerException'.
	 * <br><br>
	 * Examples:
	 * <ol>
	 * <li><pre>
	 *    pattern = "NPE"
	 *    name = NullPointerException / NoPermissionException
	 *    result => true
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    pattern = "NuPoEx"
	 *    name = NullPointerException
	 *    result => true
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    pattern = "npe"
	 *    name = NullPointerException
	 *    result => false
	 * </pre>
	 * </li>
	 * </ol>
	 * @see CharOperation#camelCaseMatch(char[], char[])
	 * 	Implementation has been entirely copied from this method except for array lengthes
	 * 	which were obviously replaced with calls to {@link String#length()}.
	 * 
	 * @param pattern the given pattern
	 * @param name the given name
	 * @return true if the pattern matches the given name, false otherwise
	 * @since 3.2
	 */
	public static final boolean camelCaseMatch(String pattern, String name) {
		if (pattern == null)
			return true; // null pattern is equivalent to '*'
		if (name == null)
			return false; // null name cannot match

		return camelCaseMatch(pattern, 0, pattern.length(), name, 0, name.length());
	}
	
	/**
	 * Answers true if a sub-pattern matches the subpart of the given name using CamelCase rules, or false otherwise.  
	 * CamelCase matching does NOT accept explicit wild-cards '*' and '?' and is inherently case sensitive. 
	 * Can match only subset of name/pattern, considering end positions as non-inclusive.
	 * The subpattern is defined by the patternStart and patternEnd positions.
	 * <br>
	 * CamelCase denotes the convention of writing compound names without spaces, and capitalizing every term.
	 * This function recognizes both upper and lower CamelCase, depending whether the leading character is capitalized
	 * or not. The leading part of an upper CamelCase pattern is assumed to contain a sequence of capitals which are appearing
	 * in the matching name; e.g. 'NPE' will match 'NullPointerException', but not 'NewPerfData'. A lower CamelCase pattern
	 * uses a lowercase first character. In Java, type names follow the upper CamelCase convention, whereas method or field
	 * names follow the lower CamelCase convention.
	 * <br>
	 * The pattern may contain lowercase characters, which will be match in a case sensitive way. These characters must
	 * appear in sequence in the name. For instance, 'NPExcep' will match 'NullPointerException', but not 'NullPointerExCEPTION'
	 * or 'NuPoEx' will match 'NullPointerException', but not 'NoPointerException'.
	 * <br><br>
	 * Examples:
	 * <ol>
	 * <li><pre>
	 *    pattern = "NPE"
	 *    patternStart = 0
	 *    patternEnd = 3
	 *    name = NullPointerException
	 *    nameStart = 0
	 *    nameEnd = 20
	 *    result => true
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    pattern = "NPE"
	 *    patternStart = 0
	 *    patternEnd = 3
	 *    name = NoPermissionException
	 *    nameStart = 0
	 *    nameEnd = 21
	 *    result => true
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    pattern = "NuPoEx"
	 *    patternStart = 0
	 *    patternEnd = 6
	 *    name = NullPointerException
	 *    nameStart = 0
	 *    nameEnd = 20
	 *    result => true
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    pattern = "NuPoEx"
	 *    patternStart = 0
	 *    patternEnd = 6
	 *    name = NoPermissionException
	 *    nameStart = 0
	 *    nameEnd = 21
	 *    result => false
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    pattern = "npe"
	 *    patternStart = 0
	 *    patternEnd = 3
	 *    name = NullPointerException
	 *    nameStart = 0
	 *    nameEnd = 20
	 *    result => false
	 * </pre>
	 * </li>
	 * </ol>
	 * @see CharOperation#camelCaseMatch(char[], int, int, char[], int, int)
	 * 	Implementation has been entirely copied from this method except for array lengthes
	 * 	which were obviously replaced with calls to {@link String#length()} and
	 * 	for array direct access which were replaced with calls to {@link String#charAt(int)}.
	 * 
	 * @param pattern the given pattern
	 * @param patternStart the start index of the pattern, inclusive
	 * @param patternEnd the end index of the pattern, exclusive
	 * @param name the given name
	 * @param nameStart the start index of the name, inclusive
	 * @param nameEnd the end index of the name, exclusive
	 * @return true if a sub-pattern matches the subpart of the given name, false otherwise
	 * @since 3.2
	 */
	public static final boolean camelCaseMatch(String pattern, int patternStart, int patternEnd, String name, int nameStart, int nameEnd) {
		if (name == null)
			return false; // null name cannot match
		if (pattern == null)
			return true; // null pattern is equivalent to '*'
		if (patternEnd < 0) 	patternEnd = pattern.length();
		if (nameEnd < 0) nameEnd = name.length();

		if (patternEnd <= patternStart) return nameEnd <= nameStart;
		if (nameEnd <= nameStart) return false;
		// check first pattern char
		if (name.charAt(nameStart) != pattern.charAt(patternStart)) {
			// first char must strictly match (upper/lower)
			return false;
		}

		char patternChar, nameChar;
		int iPattern = patternStart;
		int iName = nameStart;

		// Main loop is on pattern characters
		while (true) {

			iPattern++;
			iName++;

			if (iPattern == patternEnd) {
				// We have exhausted pattern, so it's a match
				return true;
			}

			if (iName == nameEnd){
				// We have exhausted name (and not pattern), so it's not a match 
				return false;
			}

			// For as long as we're exactly matching, bring it on (even if it's a lower case character)
			if ((patternChar = pattern.charAt(iPattern)) == name.charAt(iName)) {
				continue;
			}

			// If characters are not equals, then it's not a match if patternChar is lowercase
			if (patternChar < ScannerHelper.MAX_OBVIOUS) {
				if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[patternChar] & ScannerHelper.C_UPPER_LETTER) == 0) {
					return false;
				}
			}
			else if (Character.isJavaIdentifierPart(patternChar) && !Character.isUpperCase(patternChar)) {
				return false;
			}

			// patternChar is uppercase, so let's find the next uppercase in name
			while (true) {
				if (iName == nameEnd){
		            //	We have exhausted name (and not pattern), so it's not a match
					return false;
				}

				nameChar = name.charAt(iName);

				if (nameChar < ScannerHelper.MAX_OBVIOUS) {
					if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[nameChar] & (ScannerHelper.C_LOWER_LETTER | ScannerHelper.C_SPECIAL | ScannerHelper.C_DIGIT)) != 0) {
						// nameChar is lowercase    
						iName++;
					// nameChar is uppercase...
					} else  if (patternChar != nameChar) {
						//.. and it does not match patternChar, so it's not a match
						return false;
					} else {
						//.. and it matched patternChar. Back to the big loop
						break;
					}
				}
				else if (Character.isJavaIdentifierPart(nameChar) && !Character.isUpperCase(nameChar)) {
					// nameChar is lowercase    
					iName++;
				// nameChar is uppercase...
				} else  if (patternChar != nameChar) {
					//.. and it does not match patternChar, so it's not a match
					return false;
				} else {
					//.. and it matched patternChar. Back to the big loop
					break;
				}
			}
			// At this point, either name has been exhausted, or it is at an uppercase letter.
			// Since pattern is also at an uppercase letter
		}
	}

	/**
	 * Validate compatibility between given string pattern and match rule.
	 *<br>
	 * Optimized (ie. returned match rule is modified) combinations are:
	 * <ul>
	 * 	<li>{@link #R_PATTERN_MATCH} without any '*' or '?' in string pattern:
	 * 		pattern match bit is unset,
	 * 	</li>
	 * 	<li>{@link #R_PATTERN_MATCH} and {@link #R_PREFIX_MATCH}  bits simultaneously set:
	 * 		prefix match bit is unset,
	 * 	</li>
	 * 	<li>{@link #R_PATTERN_MATCH} and {@link #R_CAMELCASE_MATCH}  bits simultaneously set:
	 * 		camel case match bit is unset,
	 * 	</li>
	 * 	<li>{@link #R_CAMELCASE_MATCH} with invalid combination of uppercase and lowercase characters:
	 * 		camel case match bit is unset and replaced with prefix match pattern,
	 * 	</li>
	 * 	<li>{@link #R_CAMELCASE_MATCH} combined with {@link #R_PREFIX_MATCH} and {@link #R_CASE_SENSITIVE}
	 * 		bits is reduced to only {@link #R_CAMELCASE_MATCH} as Camel Case search is already prefix and case sensitive,
	 * 	</li>
	 * </ul>
	 *<br>
	 * Rejected (ie. returned match rule -1) combinations are:
	 * <ul>
	 * 	<li>{@link #R_REGEXP_MATCH} with any other match mode bit set,
	 * 	</li>
	 * </ul>
	 *
	 * @param stringPattern The string pattern
	 * @param matchRule The match rule
	 * @return Optimized valid match rule or -1 if an incompatibility was detected.
	 * @since 3.2
	 */
	public static int validateMatchRule(String stringPattern, int matchRule) {

		// Verify Regexp match rule
		if ((matchRule & R_REGEXP_MATCH) != 0) {
			if ((matchRule & R_PATTERN_MATCH) != 0 || (matchRule & R_PREFIX_MATCH) != 0 || (matchRule & R_CAMELCASE_MATCH) != 0) {
				return -1;
			}
		}

		// Verify Pattern match rule
		int starIndex = stringPattern.indexOf('*');
		int questionIndex = stringPattern.indexOf('?');
		if (starIndex < 0 && questionIndex < 0) {
			// reset pattern match bit if any
			matchRule &= ~R_PATTERN_MATCH;
		} else {
			// force Pattern rule
			matchRule |= R_PATTERN_MATCH;
		}
		if ((matchRule & R_PATTERN_MATCH) != 0) {
			// remove Camel Case and Prefix match bits if any
			matchRule &= ~R_CAMELCASE_MATCH;
			matchRule &= ~R_PREFIX_MATCH;
		}

		// Verify Camel Case match rule
		if ((matchRule & R_CAMELCASE_MATCH) != 0) {
			// Verify sting pattern validity
			int length = stringPattern.length();
			boolean validCamelCase = true;
			boolean uppercase = false;
			for (int i=0; i<length && validCamelCase; i++) {
				char ch = stringPattern.charAt(i);
				validCamelCase = ScannerHelper.isJavaIdentifierStart(ch);
				// at least one uppercase character is need in CamelCase pattern
				// (see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=136313)
				if (!uppercase) uppercase = ScannerHelper.isUpperCase(ch);
			}
			validCamelCase = validCamelCase && uppercase;
			// Verify bits compatibility
			if (validCamelCase) {
				if ((matchRule & R_PREFIX_MATCH) != 0) {
					if ((matchRule & R_CASE_SENSITIVE) != 0) {
						// This is equivalent to Camel Case match rule
						matchRule &= ~R_PREFIX_MATCH;
						matchRule &= ~R_CASE_SENSITIVE;
					}
				}
			} else {
				matchRule &= ~R_CAMELCASE_MATCH;
				if ((matchRule & R_PREFIX_MATCH) == 0) {
					matchRule |= R_PREFIX_MATCH;
					matchRule |= R_CASE_SENSITIVE;
				}
			}
		}
		return matchRule;
	}

	/**
	 * Returns a search pattern based on a given string pattern. The string patterns support '*' wild-cards.
	 * The remaining parameters are used to narrow down the type of expected results.
	 *
	 * <br>
	 *	Examples:
	 *	<ul>
	 * 		<li>search for case insensitive references to <code>Object</code>:
	 *			<code>createSearchPattern("Object", TYPE, REFERENCES, false);</code></li>
	 *  	<li>search for case sensitive references to exact <code>Object()</code> constructor:
	 *			<code>createSearchPattern("java.lang.Object()", CONSTRUCTOR, REFERENCES, true);</code></li>
	 *  	<li>search for implementers of <code>java.lang.Runnable</code>:
	 *			<code>createSearchPattern("java.lang.Runnable", TYPE, IMPLEMENTORS, true);</code></li>
	 *  </ul>
	 * @param stringPattern the given pattern
	 * @param searchFor determines the nature of the searched elements
	 *	<ul>
	 * 	<li>{@link IRubySearchConstants#CLASS}: only look for classes</li>
	 *		<li>{@link IRubySearchConstants#INTERFACE}: only look for interfaces</li>
	 * 	<li>{@link IRubySearchConstants#ENUM}: only look for enumeration</li>
	 *		<li>{@link IRubySearchConstants#ANNOTATION_TYPE}: only look for annotation type</li>
	 * 	<li>{@link IRubySearchConstants#CLASS_AND_ENUM}: only look for classes and enumerations</li>
	 *		<li>{@link IRubySearchConstants#CLASS_AND_INTERFACE}: only look for classes and interfaces</li>
	 * 	<li>{@link IRubySearchConstants#TYPE}: look for all types (ie. classes, interfaces, enum and annotation types)</li>
	 *		<li>{@link IRubySearchConstants#FIELD}: look for fields</li>
	 *		<li>{@link IRubySearchConstants#METHOD}: look for methods</li>
	 *		<li>{@link IRubySearchConstants#CONSTRUCTOR}: look for constructors</li>
	 *		<li>{@link IRubySearchConstants#PACKAGE}: look for packages</li>
	 *	</ul>
	 * @param limitTo determines the nature of the expected matches
	 *	<ul>
	 * 	<li>{@link IRubySearchConstants#DECLARATIONS}: will search declarations matching
	 * 			with the corresponding element. In case the element is a method, declarations of matching
	 * 			methods in subtypes will also be found, allowing to find declarations of abstract methods, etc.<br>
	 * 			Note that additional flags {@link IRubySearchConstants#IGNORE_DECLARING_TYPE} and
	 * 			{@link IRubySearchConstants#IGNORE_RETURN_TYPE} are ignored for string patterns.
	 * 			This is due to the fact that client may omit to define them in string pattern to have same behavior.
	 * 	</li>
	 *		 <li>{@link IRubySearchConstants#REFERENCES}: will search references to the given element.</li>
	 *		 <li>{@link IRubySearchConstants#ALL_OCCURRENCES}: will search for either declarations or
	 *				references as specified above.
	 *		</li>
	 *		 <li>{@link IRubySearchConstants#IMPLEMENTORS}: for types, will find all types
	 *				which directly implement/extend a given interface.
	 *				Note that types may be only classes or only interfaces if {@link IRubySearchConstants#CLASS } or
	 *				{@link IRubySearchConstants#INTERFACE} is respectively used instead of {@link IRubySearchConstants#TYPE}.
	 *		</li>
	 *	</ul>
	 * @param matchRule one of {@link #R_EXACT_MATCH}, {@link #R_PREFIX_MATCH}, {@link #R_PATTERN_MATCH},
	 * 	{@link #R_REGEXP_MATCH}, {@link #R_CAMELCASE_MATCH} combined with one of following values:
	 * 	{@link #R_CASE_SENSITIVE}, {@link #R_ERASURE_MATCH} or {@link #R_EQUIVALENT_MATCH}.
	 *		e.g. {@link #R_EXACT_MATCH} | {@link #R_CASE_SENSITIVE} if an exact and case sensitive match is requested, 
	 *		{@link #R_PREFIX_MATCH} if a prefix non case sensitive match is requested or {@link #R_EXACT_MATCH} | {@link #R_ERASURE_MATCH}
	 *		if a non case sensitive and erasure match is requested.<br>
	 * 	Note that {@link #R_ERASURE_MATCH} or {@link #R_EQUIVALENT_MATCH} have no effect
	 * 	on non-generic types/methods search.<br>
	 * 	Note also that default behavior for generic types/methods search is to find exact matches.
	 * @return a search pattern on the given string pattern, or <code>null</code> if the string pattern is ill-formed
	 */
	public static SearchPattern createPattern(String stringPattern, int searchFor, int limitTo, int matchRule) {
		if (stringPattern == null || stringPattern.length() == 0) return null;

		if ((matchRule = validateMatchRule(stringPattern, matchRule)) == -1) {
			return null;
		}

		// Ignore additional nature flags
//		limitTo &= ~(IRubySearchConstants.IGNORE_DECLARING_TYPE+IRubySearchConstants.IGNORE_RETURN_TYPE);

		switch (searchFor) {
			case IRubySearchConstants.CLASS:
				return createTypePattern(stringPattern, limitTo, matchRule, IIndexConstants.CLASS_SUFFIX);
			case IRubySearchConstants.MODULE:
				return createTypePattern(stringPattern, limitTo, matchRule, IIndexConstants.MODULE_SUFFIX);
			case IRubySearchConstants.TYPE:
				return createTypePattern(stringPattern, limitTo, matchRule, IIndexConstants.TYPE_SUFFIX);
			case IRubySearchConstants.METHOD:
				return createMethodOrConstructorPattern(stringPattern, limitTo, matchRule, false/*not a constructor*/);
			case IRubySearchConstants.CONSTRUCTOR:
				return createMethodOrConstructorPattern(stringPattern, limitTo, matchRule, true/*constructor*/);
			case IRubySearchConstants.FIELD:
				return createFieldPattern(stringPattern, limitTo, matchRule);
		}
		return null;
	}

	/**
	 * Returns a search pattern based on a given Ruby element. 
	 * The pattern is used to trigger the appropriate search, and can be parameterized as follows:
	 *
	 * @param element the Ruby element the search pattern is based on
	 * @param limitTo determines the nature of the expected matches
	 *	<ul>
	 * 	<li>{@link IRubySearchConstants#DECLARATIONS}: will search declarations matching
	 * 			with the corresponding element. In case the element is a method, declarations of matching
	 * 			methods in subtypes will also be found, allowing to find declarations of abstract methods, etc.
	 *				Some additional flags may be specified while searching declaration:
	 *				<ul>
	 *					<li>{@link IRubySearchConstants#IGNORE_DECLARING_TYPE}: declaring type will be ignored
	 *							during the search.<br>
	 *							For example using following test case:
	 *					<pre>
	 *                  class A { A method() { return null; } }
	 *                  class B extends A { B method() { return null; } }
	 *                  class C { A method() { return null; } }
	 *					</pre>
	 *							search for <code>method</code> declaration with this flag
	 *							will return 2 matches: in A and in C
	 *					</li>
	 *					<li>{@link IRubySearchConstants#IGNORE_RETURN_TYPE}: return type will be ignored
	 *							during the search.<br>
	 *							Using same example, search for <code>method</code> declaration with this flag
	 *							will return 2 matches: in A and in B.
	 *					</li>
	 *				</ul>
	 *				Note that these two flags may be combined and both declaring and return types can be ignored
	 *				during the search. Then, using same example, search for <code>method</code> declaration
	 *				with these 2 flags will return 3 matches: in A, in B  and in C
	 * 	</li>
	 *		 <li>{@link IRubySearchConstants#REFERENCES}: will search references to the given element.</li>
	 *		 <li>{@link IRubySearchConstants#ALL_OCCURRENCES}: will search for either declarations or
	 *				references as specified above.
	 *		</li>
	 *		 <li>{@link IRubySearchConstants#IMPLEMENTORS}: for types, will find all types
	 *				which directly implement/extend a given interface.
	 *		</li>
	 *	</ul>
	 * @param matchRule one of {@link #R_EXACT_MATCH}, {@link #R_PREFIX_MATCH}, {@link #R_PATTERN_MATCH},
	 * 	{@link #R_REGEXP_MATCH}, {@link #R_CAMELCASE_MATCH} combined with one of following values:
	 * 	{@link #R_CASE_SENSITIVE}, {@link #R_ERASURE_MATCH} or {@link #R_EQUIVALENT_MATCH}.
	 *		e.g. {@link #R_EXACT_MATCH} | {@link #R_CASE_SENSITIVE} if an exact and case sensitive match is requested, 
	 *		{@link #R_PREFIX_MATCH} if a prefix non case sensitive match is requested or {@link #R_EXACT_MATCH} |{@link #R_ERASURE_MATCH}
	 *		if a non case sensitive and erasure match is requested.<br>
	 * 	Note that {@link #R_ERASURE_MATCH} or {@link #R_EQUIVALENT_MATCH} have no effect on non-generic types
	 * 	or methods search.<br>
	 * 	Note also that default behavior for generic types or methods is to find exact matches.
	 * @return a search pattern for a Ruby element or <code>null</code> if the given element is ill-formed
	 * @since 1.0
	 */
	public static SearchPattern createPattern(IRubyElement element, int limitTo, int matchRule) {
		SearchPattern searchPattern = null;
		int lastDot;
		boolean ignoreDeclaringType = true;
//		boolean ignoreReturnType = false;
		int maskedLimitTo = limitTo /*& ~(IRubySearchConstants.IGNORE_DECLARING_TYPE+IJavaSearchConstants.IGNORE_RETURN_TYPE)*/;
		if (maskedLimitTo == IRubySearchConstants.DECLARATIONS || maskedLimitTo == IRubySearchConstants.ALL_OCCURRENCES) {
			ignoreDeclaringType = (limitTo & IRubySearchConstants.IGNORE_DECLARING_TYPE) != 0;
//			ignoreReturnType = (limitTo & IRubySearchConstants.IGNORE_RETURN_TYPE) != 0;
		}
		char[] declaringSimpleName = null;
		char[] declaringQualification = null;
		switch (element.getElementType()) {
			case IRubyElement.FIELD :
			case IRubyElement.INSTANCE_VAR :
			case IRubyElement.CONSTANT :
			case IRubyElement.CLASS_VAR :
				IField field = (IField) element; 
				if (!ignoreDeclaringType) {
					IType declaringClass = field.getDeclaringType();
					declaringSimpleName = declaringClass.getElementName().toCharArray();
					declaringQualification = declaringClass.getSourceFolder().getElementName().toCharArray();
					char[][] enclosingNames = enclosingTypeNames(declaringClass);
					if (enclosingNames.length > 0) {
						declaringQualification = CharOperation.concat(declaringQualification, CharOperation.concatWith(enclosingNames, '.'), '.');
					}
				}
				char[] name = field.getElementName().toCharArray();
				
				// Create field pattern
				boolean findDeclarations = false;
				boolean readAccess = false;
				boolean writeAccess = false;
				switch (maskedLimitTo) {
					case IRubySearchConstants.DECLARATIONS :
						findDeclarations = true;
						break;
					case IRubySearchConstants.REFERENCES :
						readAccess = true;
						writeAccess = true;
						break;
					case IRubySearchConstants.READ_ACCESSES :
						readAccess = true;
						break;
					case IRubySearchConstants.WRITE_ACCESSES :
						writeAccess = true;
						break;
					case IRubySearchConstants.ALL_OCCURRENCES :
						findDeclarations = true;
						readAccess = true;
						writeAccess = true;
						break;
				}
				searchPattern = 
					new FieldPattern(
						findDeclarations,
						readAccess,
						writeAccess,
						name, 
						declaringQualification, 
						declaringSimpleName, 
						matchRule);
				break;
			case IRubyElement.IMPORT_DECLARATION :
				String elementName = element.getElementName();
				lastDot = elementName.lastIndexOf('.');
				if (lastDot == -1) return null; // invalid import declaration
				IImportDeclaration importDecl = (IImportDeclaration)element;
				searchPattern = 
						createTypePattern(
							elementName.substring(lastDot+1).toCharArray(),
							elementName.substring(0, lastDot).toCharArray(),
							null,
							null,
							null,
							maskedLimitTo,
							matchRule);
				break;
			case IRubyElement.LOCAL_VARIABLE :
				LocalVariable localVar = (LocalVariable) element;
				boolean findVarDeclarations = false;
				boolean findVarReadAccess = false;
				boolean findVarWriteAccess = false;
				switch (maskedLimitTo) {
					case IRubySearchConstants.DECLARATIONS :
						findVarDeclarations = true;
						break;
					case IRubySearchConstants.REFERENCES :
						findVarReadAccess = true;
						findVarWriteAccess = true;
						break;
					case IRubySearchConstants.READ_ACCESSES :
						findVarReadAccess = true;
						break;
					case IRubySearchConstants.WRITE_ACCESSES :
						findVarWriteAccess = true;
						break;
					case IRubySearchConstants.ALL_OCCURRENCES :
						findVarDeclarations = true;
						findVarReadAccess = true;
						findVarWriteAccess = true;
						break;
				}
				searchPattern = 
					new LocalVariablePattern(
						findVarDeclarations,
						findVarReadAccess,
						findVarWriteAccess,
						localVar,
						matchRule);
				break;
			case IRubyElement.METHOD :
				IMethod method = (IMethod) element;
				boolean isConstructor= method.isConstructor();

				IType declaringClass = method.getDeclaringType();
				if (ignoreDeclaringType) {
					if (isConstructor) declaringSimpleName = declaringClass.getElementName().toCharArray();
				} else {
					declaringSimpleName = declaringClass.getElementName().toCharArray();
					declaringQualification = declaringClass.getSourceFolder().getElementName().toCharArray();
					char[][] enclosingNames = enclosingTypeNames(declaringClass);
					if (enclosingNames.length > 0) {
						declaringQualification = CharOperation.concat(declaringQualification, CharOperation.concatWith(enclosingNames, '.'), '.');
					}
				}
				char[] selector = method.getElementName().toCharArray();
				String[] parameterNames;
				try {
					parameterNames = method.getParameterNames();
				} catch (RubyModelException e) {
					return null;
				}
				int paramCount = parameterNames.length;
				char[][] parameterSimpleNames = new char[paramCount][];
				for (int i = 0; i < paramCount; i++) {					
					parameterSimpleNames[i] = parameterNames[i].toCharArray();
				}

				// Create method/constructor pattern
				boolean findMethodDeclarations = true;
				boolean findMethodReferences = true;
				switch (maskedLimitTo) {
					case IRubySearchConstants.DECLARATIONS :
						findMethodReferences = false;
						break;
					case IRubySearchConstants.REFERENCES :
						findMethodDeclarations = false;
						break;
					case IRubySearchConstants.ALL_OCCURRENCES :
						break;
				}
				if (isConstructor) {
					searchPattern =
						new ConstructorPattern(
							findMethodDeclarations,
							findMethodReferences,
							declaringSimpleName, 
							declaringQualification, 
							parameterSimpleNames,
							method,
							matchRule);
				} else {
					searchPattern =
						new MethodPattern(
							findMethodDeclarations,
							findMethodReferences,
							selector, 
							declaringQualification, 
							declaringSimpleName, 
							parameterSimpleNames,
							method,
							matchRule);
				}
				break;
			case IRubyElement.TYPE :
				IType type = (IType)element;
				searchPattern = 	createTypePattern(
							type.getElementName().toCharArray(), 
							type.getSourceFolder().getElementName().toCharArray(),
							ignoreDeclaringType ? null : enclosingTypeNames(type),
							null,
							type,
							maskedLimitTo,
							matchRule);
				break;
//			case IRubyElement.SOURCE_FOLDER :
//				searchPattern = createFolderPattern(element.getElementName(), maskedLimitTo, matchRule);
//				break;
		}
		if (searchPattern != null)
			MatchLocator.setFocus(searchPattern, element);
		return searchPattern;
	}
	
	private static SearchPattern createTypePattern(char[] simpleName, char[] packageName, char[][] enclosingTypeNames, String typeSignature, IType type, int limitTo, int matchRule) {
		switch (limitTo) {
			case IRubySearchConstants.DECLARATIONS :
				return new TypeDeclarationPattern(
					packageName, 
					enclosingTypeNames, 
					simpleName, 
					IIndexConstants.TYPE_SUFFIX,
					matchRule);
			case IRubySearchConstants.REFERENCES :
//				if (type != null) {
					return new TypeReferencePattern(
						CharOperation.concatWith(enclosingTypeNames, "::"), 
						simpleName,
//						type,
						matchRule);
//				}
//				return new TypeReferencePattern(
//					CharOperation.concatWith(enclosingTypeNames, "::"), 
//					simpleName,
//					typeSignature,
//					matchRule);
//			case IRubySearchConstants.IMPLEMENTORS : 
//				return new SuperTypeReferencePattern(
//					CharOperation.concatWith(packageName, enclosingTypeNames, '.'), 
//					simpleName,
//					SuperTypeReferencePattern.ONLY_SUPER_INTERFACES,
//					matchRule);
			case IRubySearchConstants.ALL_OCCURRENCES :
				return new OrPattern(
					new TypeDeclarationPattern(
						packageName, 
						enclosingTypeNames, 
						simpleName, 
						IIndexConstants.TYPE_SUFFIX,
						matchRule), 
//					(type != null)
						/*?*/ new TypeReferencePattern(
							CharOperation.concatWith(enclosingTypeNames, "::"), 
							simpleName,
//							type,
							matchRule)
//						: new TypeReferencePattern(
//							CharOperation.concatWith(enclosingTypeNames, "::"), 
//							simpleName,
//							typeSignature,
//							matchRule)
				);
		}
		return null;
	}
	
	/**
	 * Returns the enclosing type names of the given type.
	 */
	private static char[][] enclosingTypeNames(IType type) {
		IRubyElement parent = type.getParent();
		switch (parent.getElementType()) {
			case IRubyElement.SCRIPT:
				return CharOperation.NO_CHAR_CHAR;
			case IRubyElement.FIELD:
			case IRubyElement.METHOD:
				IType declaringClass = ((IMember) parent).getDeclaringType();
				return CharOperation.arrayConcat(
					enclosingTypeNames(declaringClass),
					new char[][] {declaringClass.getElementName().toCharArray(), IIndexConstants.ONE_STAR});
			case IRubyElement.TYPE:
				return CharOperation.arrayConcat(
					enclosingTypeNames((IType)parent), 
					parent.getElementName().toCharArray());
			default:
				return null;
		}
	}
	
}
