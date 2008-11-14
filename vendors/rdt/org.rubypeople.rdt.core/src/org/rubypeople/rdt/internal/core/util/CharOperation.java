/*
 * Created on Jan 29, 2005
 */
package org.rubypeople.rdt.internal.core.util;

import org.rubypeople.rdt.internal.compiler.parser.ScannerHelper;



/**
 * @author Chris
 */
public class CharOperation {
	
	/**
	 * Constant for an empty char array
	 */
	public static final char[] NO_CHAR = new char[0];
	public static final String[] NO_STRINGS = new String[0];
	/**
	 * Constant for an empty char array with two dimensions.
	 */
	public static final char[][] NO_CHAR_CHAR = new char[0][];

	/**
	 * Answers the first index in the array for which the corresponding
	 * character is equal to toBeFound starting the search at index start.
	 * Answers -1 if no occurrence of this character is found. <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *   
	 *    
	 *     
	 *      
	 *          toBeFound = 'c'
	 *          array = { ' a', 'b', 'c', 'd' }
	 *          start = 2
	 *          result =&gt; 2
	 *       
	 *      
	 *     
	 *    
	 *   
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *   
	 *    
	 *     
	 *      
	 *          toBeFound = 'c'
	 *          array = { ' a', 'b', 'c', 'd' }
	 *          start = 3
	 *          result =&gt; -1
	 *       
	 *      
	 *     
	 *    
	 *   
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *   
	 *    
	 *     
	 *      
	 *          toBeFound = 'e'
	 *          array = { ' a', 'b', 'c', 'd' }
	 *          start = 1
	 *          result =&gt; -1
	 *       
	 *      
	 *     
	 *    
	 *   
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param toBeFound
	 *            the character to search
	 * @param array
	 *            the array to be searched
	 * @param start
	 *            the starting index
	 * @return the first index in the array for which the corresponding
	 *         character is equal to toBeFound, -1 otherwise
	 * @throws NullPointerException
	 *             if array is null
	 * @throws ArrayIndexOutOfBoundsException
	 *             if start is lower than 0
	 */
	public static final int indexOf(char toBeFound, char[] array, int start) {
		for (int i = start; i < array.length; i++)
			if (toBeFound == array[i])
				return i;
		return -1;
	}

	/**
	 * Answers a new array of characters with substitutions. No side-effect is
	 * operated on the original array, in case no substitution happened, then
	 * the result is the same as the original one. <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *   
	 *    
	 *        array = { 'a' , 'b', 'b', 'a', 'b', 'a' }
	 *        toBeReplaced = { 'b' }
	 *        replacementChar = { 'a', 'a' }
	 *        result =&gt; { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a' }
	 *     
	 *    
	 *   
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *   
	 *    
	 *        array = { 'a' , 'b', 'b', 'a', 'b', 'a' }
	 *        toBeReplaced = { 'c' }
	 *        replacementChar = { 'a' }
	 *        result =&gt; { 'a' , 'b', 'b', 'a', 'b', 'a' }
	 *     
	 *    
	 *   
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param array
	 *            the given array
	 * @param toBeReplaced
	 *            characters to be replaced
	 * @param replacementChars
	 *            the replacement characters
	 * @return a new array of characters with substitutions or the given array
	 *         if none
	 * @throws NullPointerException
	 *             if the given array is null
	 */
	public static final char[] replace(char[] array, char[] toBeReplaced,
			char[] replacementChars) {

		int max = array.length;
		int replacedLength = toBeReplaced.length;
		int replacementLength = replacementChars.length;

		int[] starts = new int[5];
		int occurrenceCount = 0;

		if (!equals(toBeReplaced, replacementChars)) {

			next: for (int i = 0; i < max; i++) {
				int j = 0;
				while (j < replacedLength) {
					if (i + j == max)
						continue next;
					if (array[i + j] != toBeReplaced[j++])
						continue next;
				}
				if (occurrenceCount == starts.length) {
					System.arraycopy(starts, 0,
							starts = new int[occurrenceCount * 2], 0,
							occurrenceCount);
				}
				starts[occurrenceCount++] = i;
			}
		}
		if (occurrenceCount == 0)
			return array;
		char[] result = new char[max + occurrenceCount
				* (replacementLength - replacedLength)];
		int inStart = 0, outStart = 0;
		for (int i = 0; i < occurrenceCount; i++) {
			int offset = starts[i] - inStart;
			System.arraycopy(array, inStart, result, outStart, offset);
			inStart += offset;
			outStart += offset;
			System.arraycopy(replacementChars, 0, result, outStart,
					replacementLength);
			inStart += replacedLength;
			outStart += replacementLength;
		}
		System.arraycopy(array, inStart, result, outStart, max - inStart);
		return result;
	}

	/**
	 * Answers true if the two arrays are identical character by character,
	 * otherwise false. The equality is case sensitive. <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *   
	 *       first = null
	 *       second = null
	 *       result =&gt; true
	 *    
	 *   
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *   
	 *       first = { }
	 *       second = null
	 *       result =&gt; false
	 *    
	 *   
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *   
	 *       first = { 'a' }
	 *       second = { 'a' }
	 *       result =&gt; true
	 *    
	 *   
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *   
	 *       first = { 'a' }
	 *       second = { 'A' }
	 *       result =&gt; false
	 *    
	 *   
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param first
	 *            the first array
	 * @param second
	 *            the second array
	 * @return true if the two arrays are identical character by character,
	 *         otherwise false
	 */
	public static final boolean equals(char[] first, char[] second) {
		if (first == second)
			return true;
		if (first == null || second == null)
			return false;
		if (first.length != second.length)
			return false;

		for (int i = first.length; --i >= 0;)
			if (first[i] != second[i])
				return false;
		return true;
	}

	/**
	 * Answers true if the pattern matches the filepath using the pathSepatator,
	 * false otherwise.
	 * 
	 * Path char[] pattern matching, accepting wild-cards '**', '*' and '?'
	 * (using Ant directory tasks conventions, also see
	 * "http://jakarta.apache.org/ant/manual/dirtasks.html#defaultexcludes").
	 * Path pattern matching is enhancing regular pattern matching in supporting
	 * extra rule where '**' represent any folder combination. Special rule: -
	 * foo\ is equivalent to foo\** When not case sensitive, the pattern is
	 * assumed to already be lowercased, the name will be lowercased character
	 * per character as comparing.
	 * 
	 * @param pattern
	 *            the given pattern
	 * @param filepath
	 *            the given path
	 * @param isCaseSensitive
	 *            to find out whether or not the matching should be case
	 *            sensitive
	 * @param pathSeparator
	 *            the given path separator
	 * @return true if the pattern matches the filepath using the pathSepatator,
	 *         false otherwise
	 */
	public static final boolean pathMatch(char[] pattern, char[] filepath,
			boolean isCaseSensitive, char pathSeparator) {

		if (filepath == null)
			return false; // null name cannot match
		if (pattern == null)
			return true; // null pattern is equivalent to '*'

		// offsets inside pattern
		int pSegmentStart = pattern[0] == pathSeparator ? 1 : 0;
		int pLength = pattern.length;
		int pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern,
				pSegmentStart + 1);
		if (pSegmentEnd < 0)
			pSegmentEnd = pLength;

		// special case: pattern foo\ is equivalent to foo\**
		boolean freeTrailingDoubleStar = pattern[pLength - 1] == pathSeparator;

		// offsets inside filepath
		int fSegmentStart, fLength = filepath.length;
		if (filepath[0] != pathSeparator) {
			fSegmentStart = 0;
		} else {
			fSegmentStart = 1;
		}
		if (fSegmentStart != pSegmentStart) {
			return false; // both must start
			// with a separator
			// or none.
		}
		int fSegmentEnd = CharOperation.indexOf(pathSeparator, filepath,
				fSegmentStart + 1);
		if (fSegmentEnd < 0)
			fSegmentEnd = fLength;

		// first segments
		while (pSegmentStart < pLength
				&& !(pSegmentEnd == pLength && freeTrailingDoubleStar || (pSegmentEnd == pSegmentStart + 2
						&& pattern[pSegmentStart] == '*' && pattern[pSegmentStart + 1] == '*'))) {

			if (fSegmentStart >= fLength)
				return false;
			if (!CharOperation.match(pattern, pSegmentStart, pSegmentEnd,
					filepath, fSegmentStart, fSegmentEnd, isCaseSensitive)) {
				return false;
			}

			// jump to next segment
			pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern,
					pSegmentStart = pSegmentEnd + 1);
			// skip separator
			if (pSegmentEnd < 0)
				pSegmentEnd = pLength;

			fSegmentEnd = CharOperation.indexOf(pathSeparator, filepath,
					fSegmentStart = fSegmentEnd + 1);
			// skip separator
			if (fSegmentEnd < 0)
				fSegmentEnd = fLength;
		}

		/* check sequence of doubleStar+segment */
		int pSegmentRestart;
		if ((pSegmentStart >= pLength && freeTrailingDoubleStar)
				|| (pSegmentEnd == pSegmentStart + 2
						&& pattern[pSegmentStart] == '*' && pattern[pSegmentStart + 1] == '*')) {
			pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern,
					pSegmentStart = pSegmentEnd + 1);
			// skip separator
			if (pSegmentEnd < 0)
				pSegmentEnd = pLength;
			pSegmentRestart = pSegmentStart;
		} else {
			if (pSegmentStart >= pLength)
				return fSegmentStart >= fLength; // true
			// if
			// filepath
			// is
			// done
			// too.
			pSegmentRestart = 0; // force fSegmentStart check
		}
		int fSegmentRestart = fSegmentStart;
		checkSegment: while (fSegmentStart < fLength) {

			if (pSegmentStart >= pLength) {
				if (freeTrailingDoubleStar)
					return true;
				// mismatch - restart current path segment
				pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern,
						pSegmentStart = pSegmentRestart);
				if (pSegmentEnd < 0)
					pSegmentEnd = pLength;

				fSegmentRestart = CharOperation.indexOf(pathSeparator,
						filepath, fSegmentRestart + 1);
				// skip separator
				if (fSegmentRestart < 0) {
					fSegmentRestart = fLength;
				} else {
					fSegmentRestart++;
				}
				fSegmentEnd = CharOperation.indexOf(pathSeparator, filepath,
						fSegmentStart = fSegmentRestart);
				if (fSegmentEnd < 0)
					fSegmentEnd = fLength;
				continue checkSegment;
			}

			/* path segment is ending */
			if (pSegmentEnd == pSegmentStart + 2
					&& pattern[pSegmentStart] == '*'
					&& pattern[pSegmentStart + 1] == '*') {
				pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern,
						pSegmentStart = pSegmentEnd + 1);
				// skip separator
				if (pSegmentEnd < 0)
					pSegmentEnd = pLength;
				pSegmentRestart = pSegmentStart;
				fSegmentRestart = fSegmentStart;
				if (pSegmentStart >= pLength)
					return true;
				continue checkSegment;
			}
			/* chech current path segment */
			if (!CharOperation.match(pattern, pSegmentStart, pSegmentEnd,
					filepath, fSegmentStart, fSegmentEnd, isCaseSensitive)) {
				// mismatch - restart current path segment
				pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern,
						pSegmentStart = pSegmentRestart);
				if (pSegmentEnd < 0)
					pSegmentEnd = pLength;

				fSegmentRestart = CharOperation.indexOf(pathSeparator,
						filepath, fSegmentRestart + 1);
				// skip separator
				if (fSegmentRestart < 0) {
					fSegmentRestart = fLength;
				} else {
					fSegmentRestart++;
				}
				fSegmentEnd = CharOperation.indexOf(pathSeparator, filepath,
						fSegmentStart = fSegmentRestart);
				if (fSegmentEnd < 0)
					fSegmentEnd = fLength;
				continue checkSegment;
			}
			// jump to next segment
			pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern,
					pSegmentStart = pSegmentEnd + 1);
			// skip separator
			if (pSegmentEnd < 0)
				pSegmentEnd = pLength;

			fSegmentEnd = CharOperation.indexOf(pathSeparator, filepath,
					fSegmentStart = fSegmentEnd + 1);
			// skip separator
			if (fSegmentEnd < 0)
				fSegmentEnd = fLength;
		}

		return (pSegmentRestart >= pSegmentEnd)
				|| (fSegmentStart >= fLength && pSegmentStart >= pLength)
				|| (pSegmentStart == pLength - 2
						&& pattern[pSegmentStart] == '*' && pattern[pSegmentStart + 1] == '*')
				|| (pSegmentStart == pLength && freeTrailingDoubleStar);
	}

	/**
	 * Answers true if the a sub-pattern matches the subpart of the given name,
	 * false otherwise. char[] pattern matching, accepting wild-cards '*' and
	 * '?'. Can match only subset of name/pattern. end positions are
	 * non-inclusive. The subpattern is defined by the patternStart and
	 * pattternEnd positions. When not case sensitive, the pattern is assumed to
	 * already be lowercased, the name will be lowercased character per
	 * character as comparing. <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *      pattern = { '?', 'b', '*' }
	 *      patternStart = 1
	 *      patternEnd = 3
	 *      name = { 'a', 'b', 'c' , 'd' }
	 *      nameStart = 1
	 *      nameEnd = 4
	 *      isCaseSensitive = true
	 *      result =&gt; true
	 *   
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *  
	 *      pattern = { '?', 'b', '*' }
	 *      patternStart = 1
	 *      patternEnd = 2
	 *      name = { 'a', 'b', 'c' , 'd' }
	 *      nameStart = 1
	 *      nameEnd = 2
	 *      isCaseSensitive = true
	 *      result =&gt; false
	 *   
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param pattern
	 *            the given pattern
	 * @param patternStart
	 *            the given pattern start
	 * @param patternEnd
	 *            the given pattern end
	 * @param name
	 *            the given name
	 * @param nameStart
	 *            the given name start
	 * @param nameEnd
	 *            the given name end
	 * @param isCaseSensitive
	 *            flag to know if the matching should be case sensitive
	 * @return true if the a sub-pattern matches the subpart of the given name,
	 *         false otherwise
	 */
	public static final boolean match(char[] pattern, int patternStart,
			int patternEnd, char[] name, int nameStart, int nameEnd,
			boolean isCaseSensitive) {

		if (name == null)
			return false; // null name cannot match
		if (pattern == null)
			return true; // null pattern is equivalent to '*'
		int iPattern = patternStart;
		int iName = nameStart;

		if (patternEnd < 0)
			patternEnd = pattern.length;
		if (nameEnd < 0)
			nameEnd = name.length;

		/* check first segment */
		char patternChar = 0;
		while ((iPattern < patternEnd)
				&& (patternChar = pattern[iPattern]) != '*') {
			if (iName == nameEnd)
				return false;
			if (patternChar != (isCaseSensitive ? name[iName] : Character
					.toLowerCase(name[iName]))
					&& patternChar != '?') {
				return false;
			}
			iName++;
			iPattern++;
		}
		/* check sequence of star+segment */
		int segmentStart;
		if (patternChar == '*') {
			segmentStart = ++iPattern; // skip star
		} else {
			segmentStart = 0; // force iName check
		}
		int prefixStart = iName;
		checkSegment: while (iName < nameEnd) {
			if (iPattern == patternEnd) {
				iPattern = segmentStart; // mismatch - restart current
				// segment
				iName = ++prefixStart;
				continue checkSegment;
			}
			/* segment is ending */
			if ((patternChar = pattern[iPattern]) == '*') {
				segmentStart = ++iPattern; // skip start
				if (segmentStart == patternEnd) {
					return true;
				}
				prefixStart = iName;
				continue checkSegment;
			}
			/* check current name character */
			if ((isCaseSensitive ? name[iName] : Character
					.toLowerCase(name[iName])) != patternChar
					&& patternChar != '?') {
				iPattern = segmentStart; // mismatch - restart current
				// segment
				iName = ++prefixStart;
				continue checkSegment;
			}
			iName++;
			iPattern++;
		}

		return (segmentStart == patternEnd)
				|| (iName == nameEnd && iPattern == patternEnd)
				|| (iPattern == patternEnd - 1 && pattern[iPattern] == '*');
	}

	/**
	 * Answers a new array which is a copy of the given array starting at the
	 * given start and ending at the given end. The given start is inclusive and
	 * the given end is exclusive. Answers null if start is greater than end, if
	 * start is lower than 0 or if end is greater than the length of the given
	 * array. If end equals -1, it is converted to the array length. <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li>
	 * 
	 * <pre>
	 *     array = { 'a' , 'b' }
	 *     start = 0
	 *     end = 1
	 *     result =&gt; { 'a' }
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *     array = { 'a', 'b' }
	 *     start = 0
	 *     end = -1
	 *     result =&gt; { 'a' , 'b' }
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param array
	 *            the given array
	 * @param start
	 *            the given starting index
	 * @param end
	 *            the given ending index
	 * @return a new array which is a copy of the given array starting at the
	 *         given start and ending at the given end
	 * @throws NullPointerException
	 *             if the given array is null
	 */
	public static final char[] subarray(char[] array, int start, int end) {
		if (end == -1)
			end = array.length;
		if (start > end)
			return null;
		if (start < 0)
			return null;
		if (end > array.length)
			return null;

		char[] result = new char[end - start];
		System.arraycopy(array, start, result, 0, end - start);
		return result;
	}

	/**
	 * Answers the concatenation of the two arrays inserting the separator
	 * character between the two arrays. It answers null if the two arrays are
	 * null. If the first array is null, then the second array is returned. If
	 * the second array is null, then the first array is returned. <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li>
	 * 
	 * <pre>
	 *     first = null
	 *     second = { 'a' }
	 *     separator = '/'
	 *     =&gt; result = { ' a' }
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *     first = { ' a' }
	 *     second = null
	 *     separator = '/'
	 *     =&gt; result = { ' a' }
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *     first = { ' a' }
	 *     second = { ' b' }
	 *     separator = '/'
	 *     =&gt; result = { ' a' , '/', 'b' }
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param first
	 *            the first array to concatenate
	 * @param second
	 *            the second array to concatenate
	 * @param separator
	 *            the character to insert
	 * @return the concatenation of the two arrays inserting the separator
	 *         character between the two arrays , or null if the two arrays are
	 *         null.
	 */
	public static final char[] concat(char[] first, char[] second,
			char separator) {
		if (first == null)
			return second;
		if (second == null)
			return first;

		int length1 = first.length;
		if (length1 == 0)
			return second;
		int length2 = second.length;
		if (length2 == 0)
			return first;

		char[] result = new char[length1 + length2 + 1];
		System.arraycopy(first, 0, result, 0, length1);
		result[length1] = separator;
		System.arraycopy(second, 0, result, length1 + 1, length2);
		return result;
	}

	/**
	 * Answers the last index in the array for which the corresponding character
	 * is equal to toBeFound starting from the end of the array. Answers -1 if
	 * no occurrence of this character is found. <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li>
	 * 
	 * <pre>
	 *     toBeFound = 'c'
	 *     array = { ' a', 'b', 'c', 'd' , 'c', 'e' }
	 *     result =&gt; 4
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *     toBeFound = 'e'
	 *     array = { ' a', 'b', 'c', 'd' }
	 *     result =&gt; -1
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param toBeFound
	 *            the character to search
	 * @param array
	 *            the array to be searched
	 * @return the last index in the array for which the corresponding character
	 *         is equal to toBeFound starting from the end of the array, -1
	 *         otherwise
	 * @throws NullPointerException
	 *             if array is null
	 */
	public static final int lastIndexOf(char toBeFound, char[] array) {
		for (int i = array.length; --i >= 0;)
			if (toBeFound == array[i])
				return i;
		return -1;
	}

	/**
	 * Answers true if the two arrays are identical character by character,
	 * otherwise false. The equality is case sensitive. <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li>
	 * 
	 * <pre>
	 *     first = null
	 *     second = null
	 *     result =&gt; true
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *     first = { { } }
	 *     second = null
	 *     result =&gt; false
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *     first = { { 'a' } }
	 *     second = { { 'a' } }
	 *     result =&gt; true
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *     first = { { 'A' } }
	 *     second = { { 'a' } }
	 *     result =&gt; false
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param first
	 *            the first array
	 * @param second
	 *            the second array
	 * @return true if the two arrays are identical character by character,
	 *         otherwise false
	 */
	public static final boolean equals(char[][] first, char[][] second) {
		if (first == second)
			return true;
		if (first == null || second == null)
			return false;
		if (first.length != second.length)
			return false;

		for (int i = first.length; --i >= 0;)
			if (!equals(first[i], second[i]))
				return false;
		return true;
	}

	public static final boolean equals(String[] first, String[] second) {
		if (first == second)
			return true;
		if (first == null || second == null)
			return false;
		if (first.length != second.length)
			return false;

		for (int i = first.length; --i >= 0;)
			if (!first[i].equals(second[i]))
				return false;
		return true;
	}

	/**
	 * Answers a hashcode for the array
	 * 
	 * @param array
	 *            the array for which a hashcode is required
	 * @return the hashcode
	 * @throws NullPointerException
	 *             if array is null
	 */
	public static final int hashCode(char[] array) {
		int length = array.length;
		int hash = length == 0 ? 31 : array[0];
		if (length < 8) {
			for (int i = length; --i > 0;)
				hash = (hash * 31) + array[i];
		} else {
			// 8 characters is enough to compute a decent hash code, don't waste
			// time examining every character
			for (int i = length - 1, last = i > 16 ? i - 16 : 0; i > last; i -= 2)
				hash = (hash * 31) + array[i];
		}
		return hash & 0x7FFFFFFF;
	}
	
	/**
	 * Answers the concatenation of the given array parts using the given separator between each part.
	 * <br>
	 * <br>
	 * For example:<br>
	 * <ol>
	 * <li><pre>
	 *    array = { { 'a' }, { 'b' } }
	 *    separator = '.'
	 *    => result = { 'a', '.', 'b' }
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    array = null
	 *    separator = '.'
	 *    => result = { }
	 * </pre></li>
	 * </ol>
	 * 
	 * @param array the given array
	 * @param separator the given separator
	 * @return the concatenation of the given array parts using the given separator between each part
	 */
	public static final char[] concatWith(char[][] array, char separator) {
		int length = array == null ? 0 : array.length;
		if (length == 0)
			return CharOperation.NO_CHAR;

		int size = length - 1;
		int index = length;
		while (--index >= 0) {
			if (array[index].length == 0)
				size--;
			else
				size += array[index].length;
		}
		if (size <= 0)
			return CharOperation.NO_CHAR;
		char[] result = new char[size];
		index = length;
		while (--index >= 0) {
			length = array[index].length;
			if (length > 0) {
				System.arraycopy(
					array[index],
					0,
					result,
					(size -= length),
					length);
				if (--size >= 0)
					result[size] = separator;
			}
		}
		return result;
	}

	public static final char[][] splitOn(char divider, char[] array) {
	int length = array == null ? 0 : array.length;
	if (length == 0)
		return NO_CHAR_CHAR;

	int wordCount = 1;
	for (int i = 0; i < length; i++)
		if (array[i] == divider)
			wordCount++;
	char[][] split = new char[wordCount][];
	int last = 0, currentWord = 0;
	for (int i = 0; i < length; i++) {
		if (array[i] == divider) {
			split[currentWord] = new char[i - last];
			System.arraycopy(
				array,
				last,
				split[currentWord++],
				0,
				i - last);
			last = i + 1;
		}
	}
	split[currentWord] = new char[length - last];
	System.arraycopy(array, last, split[currentWord], 0, length - last);
	return split;
}

	/**
 * Answers true if the given name starts with the given prefix, false otherwise.
 * The comparison is case sensitive.
 * <br>
 * <br>
 * For example:
 * <ol>
 * <li><pre>
 *    prefix = { 'a' , 'b' }
 *    name = { 'a' , 'b', 'b', 'a', 'b', 'a' }
 *    result => true
 * </pre>
 * </li>
 * <li><pre>
 *    prefix = { 'a' , 'c' }
 *    name = { 'a' , 'b', 'b', 'a', 'b', 'a' }
 *    result => false
 * </pre>
 * </li>
 * </ol>
 * 
 * @param prefix the given prefix
 * @param name the given name
 * @return true if the given name starts with the given prefix, false otherwise
 * @throws NullPointerException if the given name is null or if the given prefix is null
 */
public static final boolean prefixEquals(char[] prefix, char[] name) {

	int max = prefix.length;
	if (name.length < max)
		return false;
	for (int i = max;
		--i >= 0;
		) // assumes the prefix is not larger than the name
		if (prefix[i] != name[i])
			return false;
	return true;
}

	public static final boolean camelCaseMatch(char[] pattern, char[] name) {
	if (pattern == null)
		return true; // null pattern is equivalent to '*'
	if (name == null)
		return false; // null name cannot match

	return camelCaseMatch(pattern, 0, pattern.length, name, 0, name.length);
	}
	
	public static final boolean camelCaseMatch(char[] pattern, int patternStart, int patternEnd, char[] name, int nameStart, int nameEnd) {
		if (name == null)
			return false; // null name cannot match
		if (pattern == null)
			return true; // null pattern is equivalent to '*'
		if (patternEnd < 0) 	patternEnd = pattern.length;
		if (nameEnd < 0) nameEnd = name.length;

		if (patternEnd <= patternStart) return nameEnd <= nameStart;
		if (nameEnd <= nameStart) return false;
		// check first pattern char
		if (name[nameStart] != pattern[patternStart]) {
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
			if ((patternChar = pattern[iPattern]) == name[iName]) {
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

				nameChar = name[iName];
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

	public static final boolean equals(
	char[] first,
	char[] second,
	boolean isCaseSensitive) {

	if (isCaseSensitive) {
		return equals(first, second);
	}
	if (first == second)
		return true;
	if (first == null || second == null)
		return false;
	if (first.length != second.length)
		return false;

	for (int i = first.length; --i >= 0;)
		if (ScannerHelper.toLowerCase(first[i])
			!= ScannerHelper.toLowerCase(second[i]))
			return false;
	return true;
}

	public static final boolean prefixEquals(
	char[] prefix,
	char[] name,
	boolean isCaseSensitive) {

	int max = prefix.length;
	if (name.length < max)
		return false;
	if (isCaseSensitive) {
		for (int i = max;
			--i >= 0;
			) // assumes the prefix is not larger than the name
			if (prefix[i] != name[i])
				return false;
		return true;
	}

	for (int i = max;
		--i >= 0;
		) // assumes the prefix is not larger than the name
		if (ScannerHelper.toLowerCase(prefix[i])
			!= ScannerHelper.toLowerCase(name[i]))
			return false;
	return true;
}

	public static final boolean match(
	char[] pattern,
	char[] name,
	boolean isCaseSensitive) {

	if (name == null)
		return false; // null name cannot match
	if (pattern == null)
		return true; // null pattern is equivalent to '*'

	return match(
		pattern,
		0,
		pattern.length,
		name,
		0,
		name.length,
		isCaseSensitive);
}

	/**
 * Answers the result of a char[] conversion to lowercase. Answers null if the given chars array is null.
 * <br>
 * NOTE: If no conversion was necessary, then answers back the argument one.
 * <br>
 * <br>
 * For example:
 * <ol>
 * <li><pre>
 *    chars = { 'a' , 'b' }
 *    result => { 'a' , 'b' }
 * </pre>
 * </li>
 * <li><pre>
 *    array = { 'A', 'b' }
 *    result => { 'a' , 'b' }
 * </pre>
 * </li>
 * </ol>
 * 
 * @param chars the chars to convert
 * @return the result of a char[] conversion to lowercase
 */
final static public char[] toLowerCase(char[] chars) {
	if (chars == null)
		return null;
	int length = chars.length;
	char[] lowerChars = null;
	for (int i = 0; i < length; i++) {
		char c = chars[i];
		char lc = ScannerHelper.toLowerCase(c);
		if ((c != lc) || (lowerChars != null)) {
			if (lowerChars == null) {
				System.arraycopy(
					chars,
					0,
					lowerChars = new char[length],
					0,
					i);
			}
			lowerChars[i] = lc;
		}
	}
	return lowerChars == null ? chars : lowerChars;
}

	/**
 * Answers a new array which is a copy of the given array starting at the given start and 
 * ending at the given end. The given start is inclusive and the given end is exclusive.
 * Answers null if start is greater than end, if start is lower than 0 or if end is greater 
 * than the length of the given array. If end  equals -1, it is converted to the array length.
 * <br>
 * <br>
 * For example:
 * <ol>
 * <li><pre>
 *    array = { { 'a' } , { 'b' } }
 *    start = 0
 *    end = 1
 *    result => { { 'a' } }
 * </pre>
 * </li>
 * <li><pre>
 *    array = { { 'a' } , { 'b' } }
 *    start = 0
 *    end = -1
 *    result => { { 'a' }, { 'b' } }
 * </pre>
 * </li>
 * </ol>
 *  
 * @param array the given array
 * @param start the given starting index
 * @param end the given ending index
 * @return a new array which is a copy of the given array starting at the given start and 
 * ending at the given end
 * @throws NullPointerException if the given array is null
 */
public static final char[][] subarray(char[][] array, int start, int end) {
	if (end == -1)
		end = array.length;
	if (start > end)
		return null;
	if (start < 0)
		return null;
	if (end > array.length)
		return null;

	char[][] result = new char[end - start][];
	System.arraycopy(array, start, result, 0, end - start);
	return result;
}

/**
 * Answers the concatenation of the three arrays inserting the sep1 character between the 
 * first two arrays and sep2 between the last two.
 * It answers null if the three arrays are null.
 * If the first array is null, then it answers the concatenation of second and third inserting
 * the sep2 character between them.
 * If the second array is null, then it answers the concatenation of first and third inserting
 * the sep1 character between them.
 * If the third array is null, then it answers the concatenation of first and second inserting
 * the sep1 character between them.
 * <br>
 * <br>
 * For example:
 * <ol>
 * <li><pre>
 *    first = null
 *    sep1 = '/'
 *    second = { 'a' }
 *    sep2 = ':'
 *    third = { 'b' }
 *    => result = { ' a' , ':', 'b' }
 * </pre>
 * </li>
 * <li><pre>
 *    first = { 'a' }
 *    sep1 = '/'
 *    second = null
 *    sep2 = ':'
 *    third = { 'b' }
 *    => result = { ' a' , '/', 'b' }
 * </pre>
 * </li>
 * <li><pre>
 *    first = { 'a' }
 *    sep1 = '/'
 *    second = { 'b' }
 *    sep2 = ':'
 *    third = null
 *    => result = { ' a' , '/', 'b' }
 * </pre>
 * </li>
 * <li><pre>
 *    first = { 'a' }
 *    sep1 = '/'
 *    second = { 'b' }
 *    sep2 = ':'
 *    third = { 'c' }
 *    => result = { ' a' , '/', 'b' , ':', 'c' }
 * </pre>
 * </li>
 * </ol>
 * 
 * @param first the first array to concatenate
 * @param sep1 the character to insert
 * @param second the second array to concatenate
 * @param sep2 the character to insert
 * @param third the second array to concatenate
 * @return the concatenation of the three arrays inserting the sep1 character between the 
 * two arrays and sep2 between the last two.
 */
public static final char[] concat(
	char[] first,
	char sep1,
	char[] second,
	char sep2,
	char[] third) {
	if (first == null)
		return concat(second, third, sep2);
	if (second == null)
		return concat(first, third, sep1);
	if (third == null)
		return concat(first, second, sep1);

	int length1 = first.length;
	int length2 = second.length;
	int length3 = third.length;
	char[] result = new char[length1 + length2 + length3 + 2];
	System.arraycopy(first, 0, result, 0, length1);
	result[length1] = sep1;
	System.arraycopy(second, 0, result, length1 + 1, length2);
	result[length1 + length2 + 1] = sep2;
	System.arraycopy(third, 0, result, length1 + length2 + 2, length3);
	return result;
}

/**
 * Answers a new array with appending the suffix character at the end of the array.
 * <br>
 * <br>
 * For example:<br>
 * <ol>
 * <li><pre>
 *    array = { 'a', 'b' }
 *    suffix = 'c'
 *    => result = { 'a', 'b' , 'c' }
 * </pre>
 * </li>
 * <li><pre>
 *    array = null
 *    suffix = 'c'
 *    => result = { 'c' }
 * </pre></li>
 * </ol>
 * 
 * @param array the array that is concanated with the suffix character
 * @param suffix the suffix character
 * @return the new array
 */
public static final char[] append(char[] array, char suffix) {
	if (array == null)
		return new char[] { suffix };
	int length = array.length;
	System.arraycopy(array, 0, array = new char[length + 1], 0, length);
	array[length] = suffix;
	return array;
}

/**
 * If isCaseSensite is true, answers true if the two arrays are identical character
 * by character, otherwise false.
 * If it is false, answers true if the two arrays are identical character by 
 * character without checking the case, otherwise false.
 * <br>
 * <br>
 * For example:
 * <ol>
 * <li><pre>
 *    first = null
 *    second = null
 *    isCaseSensitive = true
 *    result => true
 * </pre>
 * </li>
 * <li><pre>
 *    first = { { } }
 *    second = null
 *    isCaseSensitive = true
 *    result => false
 * </pre>
 * </li>
 * <li><pre>
 *    first = { { 'A' } }
 *    second = { { 'a' } }
 *    isCaseSensitive = true
 *    result => false
 * </pre>
 * </li>
 * <li><pre>
 *    first = { { 'A' } }
 *    second = { { 'a' } }
 *    isCaseSensitive = false
 *    result => true
 * </pre>
 * </li>
 * </ol>
 * 
 * @param first the first array
 * @param second the second array
 * @param isCaseSensitive check whether or not the equality should be case sensitive
 * @return true if the two arrays are identical character by character according to the value
 * of isCaseSensitive, otherwise false
 */
public static final boolean equals(
	char[][] first,
	char[][] second,
	boolean isCaseSensitive) {

	if (isCaseSensitive) {
		return equals(first, second);
	}
	if (first == second)
		return true;
	if (first == null || second == null)
		return false;
	if (first.length != second.length)
		return false;

	for (int i = first.length; --i >= 0;)
		if (!equals(first[i], second[i], false))
			return false;
	return true;
}

public static char[][] splitOn(String divider, char[] key, int start, int last) {
	String newKey = new String(key);
	newKey = newKey.substring(start, last);
	String[] result = newKey.split(divider);
	char[][] resultEnd = new char[result.length][];
	for (int i = 0; i < resultEnd.length; i++) {
		resultEnd[i] = result[i].toCharArray();
	}
	return resultEnd;
}

public static char[][] splitOn(String divider, char[] key) {
	String newKey = new String(key);
	String[] result = newKey.split(divider);
	char[][] resultEnd = new char[result.length][];
	for (int i = 0; i < resultEnd.length; i++) {
		resultEnd[i] = result[i].toCharArray();
	}
	return resultEnd;
	
}

public static int occurencesOf(String toBeFound, char[] originalString) {
	String newKey = new String(originalString);
	int count = 0;
	int index = newKey.indexOf(toBeFound);
	while (index > -1) {
		count++;
		if (newKey.length() < index + toBeFound.length()) break;
		newKey = newKey.substring(index + toBeFound.length());
		index = newKey.indexOf(toBeFound);
	}
	return count;
}

public static int lastIndexOf(String toBeFound, char[] typePart) {
	if (typePart == null || typePart.length == 0) return -1;
	return new String(typePart).lastIndexOf(toBeFound);
}

/**
 * Answers the concatenation of the two arrays. It answers null if the two arrays are null.
 * If the first array is null, then the second array is returned.
 * If the second array is null, then the first array is returned.
 * <br>
 * <br>
 * For example:
 * <ol>
 * <li><pre>
 *    first = null
 *    second = { 'a' }
 *    => result = { ' a' }
 * </pre>
 * </li>
 * <li><pre>
 *    first = { ' a' }
 *    second = null
 *    => result = { ' a' }
 * </pre>
 * </li>
 * <li><pre>
 *    first = { ' a' }
 *    second = { ' b' }
 *    => result = { ' a' , ' b' }
 * </pre>
 * </li>
 * </ol>
 * 
 * @param first the first array to concatenate
 * @param second the second array to concatenate
 * @return the concatenation of the two arrays, or null if the two arrays are null.
 */
public static final char[] concat(char[] first, char[] second) {
	if (first == null)
		return second;
	if (second == null)
		return first;

	int length1 = first.length;
	int length2 = second.length;
	char[] result = new char[length1 + length2];
	System.arraycopy(first, 0, result, 0, length1);
	System.arraycopy(second, 0, result, length1, length2);
	return result;
}

public static char[] lastSegment(char[] typeName, String divider) {
	if (typeName == null) return NO_CHAR;
	char[][] result = splitOn(divider, typeName);
	return result[result.length - 1];
}

/**
 * Answers the concatenation of the two arrays. It answers null if the two arrays are null.
 * If the first array is null, then the second array is returned.
 * If the second array is null, then the first array is returned.
 * <br>
 * <br>
 * For example:
 * <ol>
 * <li><pre>
 *    first = null
 *    second = null
 *    => result = null
 * </pre>
 * </li>
 * <li><pre>
 *    first = { { ' a' } }
 *    second = null
 *    => result = { { ' a' } }
 * </pre>
 * </li>
 * <li><pre>
 *    first = null
 *    second = { { ' a' } }
 *    => result = { { ' a' } }
 * </pre>
 * </li>
 * <li><pre>
 *    first = { { ' b' } }
 *    second = { { ' a' } }
 *    => result = { { ' b' }, { ' a' } }
 * </pre>
 * </li>
 * </ol>
 * 
 * @param first the first array to concatenate
 * @param second the second array to concatenate
 * @return the concatenation of the two arrays, or null if the two arrays are null.
 */
public static final char[][] arrayConcat(char[][] first, char[][] second) {
	if (first == null)
		return second;
	if (second == null)
		return first;

	int length1 = first.length;
	int length2 = second.length;
	char[][] result = new char[length1 + length2][];
	System.arraycopy(first, 0, result, 0, length1);
	System.arraycopy(second, 0, result, length1, length2);
	return result;
}

/**
 * Answers a new array adding the second array at the end of first array.
 * It answers null if the first and second are null.
 * If the first array is null, then a new array char[][] is created with second.
 * If the second array is null, then the first array is returned.
 * <br>
 * <br>
 * For example:
 * <ol>
 * <li><pre>
 *    first = null
 *    second = { 'a' }
 *    => result = { { ' a' } }
 * </pre>
 * <li><pre>
 *    first = { { ' a' } }
 *    second = null
 *    => result = { { ' a' } }
 * </pre>
 * </li>
 * <li><pre>
 *    first = { { ' a' } }
 *    second = { ' b' }
 *    => result = { { ' a' } , { ' b' } }
 * </pre>
 * </li>
 * </ol>
 * 
 * @param first the first array to concatenate
 * @param second the array to add at the end of the first array
 * @return a new array adding the second array at the end of first array, or null if the two arrays are null.
 */
public static final char[][] arrayConcat(char[][] first, char[] second) {
	if (second == null)
		return first;
	if (first == null)
		return new char[][] { second };

	int length = first.length;
	char[][] result = new char[length + 1][];
	System.arraycopy(first, 0, result, 0, length);
	result[length] = second;
	return result;
}

public static char[] concatWith(char[][] enclosingTypeNames, String string) {
	if (enclosingTypeNames == null) return NO_CHAR;
	StringBuffer buffer = new StringBuffer();	
	for (int i = 0; i < enclosingTypeNames.length; i++) {
		char[] name = enclosingTypeNames[i];
		if (i > 0) buffer.append(string);
		buffer.append(name);		
	}
	return buffer.toString().toCharArray();
}

public static char[] concat(char[] one, char[] two, String separator) {
	StringBuffer buffer = new StringBuffer();
	buffer.append(one);
	buffer.append(separator);
	buffer.append(two);
	return buffer.toString().toCharArray();
}
}
