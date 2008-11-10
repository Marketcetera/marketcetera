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
package org.rubypeople.rdt.internal.corext.util;

import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.corext.util.TypeInfo.TypeInfoAdapter;
import org.rubypeople.rdt.internal.ui.util.StringMatcher;
import org.rubypeople.rdt.ui.dialogs.ITypeInfoFilterExtension;

public class TypeInfoFilter {
	
	private static class PatternMatcher {
		
		private String fPattern;
		private int fMatchKind;
		private StringMatcher fStringMatcher;

		private static final char END_SYMBOL= '<';
		private static final char ANY_STRING= '*';
		private static final char BLANK= ' ';
		
		public PatternMatcher(String pattern, boolean ignoreCase) {
			this(pattern, SearchPattern.R_EXACT_MATCH | SearchPattern.R_PREFIX_MATCH |
				SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CAMELCASE_MATCH);
		}
		
		public PatternMatcher(String pattern, int allowedModes) {
			initializePatternAndMatchKind(pattern);
			fMatchKind= fMatchKind & allowedModes;
			if (fMatchKind == SearchPattern.R_PATTERN_MATCH) {
				fStringMatcher= new StringMatcher(fPattern, true, false);
			}
		}
		
		public String getPattern() {
			return fPattern;
		}
		
		public int getMatchKind() {
			return fMatchKind;
		}
		
		public boolean matches(String text) {
			switch (fMatchKind) {
				case SearchPattern.R_PATTERN_MATCH:
					return fStringMatcher.match(text);
				case SearchPattern.R_EXACT_MATCH:
					return fPattern.equalsIgnoreCase(text);
				case SearchPattern.R_CAMELCASE_MATCH:
					if (SearchPattern.camelCaseMatch(fPattern, text)) {
						return true;
					}
					// fall through to prefix match if camel case failed (bug 137244)
				default:
					return Strings.startsWithIgnoreCase(text, fPattern);
			}
		}
		
		private void initializePatternAndMatchKind(String pattern) {
			int length= pattern.length();
			if (length == 0) {
				fMatchKind= SearchPattern.R_EXACT_MATCH;
				fPattern= pattern;
				return;
			}
			char last= pattern.charAt(length - 1);
			
			if (pattern.indexOf('*') != -1 || pattern.indexOf('?') != -1) {
				fMatchKind= SearchPattern.R_PATTERN_MATCH;
				switch (last) {
					case END_SYMBOL:
						fPattern= pattern.substring(0, length - 1);
						break;
					case BLANK:
						fPattern= pattern.trim();
						break;
					case ANY_STRING:
						fPattern= pattern;
						break;
					default:
						fPattern= pattern + ANY_STRING;
				}
				return;
			}
			
			if (last == END_SYMBOL) {
				fMatchKind= SearchPattern.R_EXACT_MATCH;
				fPattern= pattern.substring(0, length - 1);
				return;
			}
			
			if (last == BLANK) {
				fMatchKind= SearchPattern.R_EXACT_MATCH;
				fPattern= pattern.trim();
				return;
			}
			
			if (SearchUtils.isCamelCasePattern(pattern)) {
				fMatchKind= SearchPattern.R_CAMELCASE_MATCH;
				fPattern= pattern;
				return;
			}
			
			fMatchKind= SearchPattern.R_PREFIX_MATCH;
			fPattern= pattern;
		}		
	}
	
	private String fText;
	private IRubySearchScope fSearchScope;
	private boolean fIsWorkspaceScope;
	private int fElementKind;
	private ITypeInfoFilterExtension fFilterExtension;
	private TypeInfoAdapter fAdapter= new TypeInfoAdapter();

	private PatternMatcher fNamespaceMatcher;
	private PatternMatcher fNameMatcher;
		
	public TypeInfoFilter(String text, IRubySearchScope scope, int elementKind, ITypeInfoFilterExtension extension) {
		fText= text;
		fSearchScope= scope;
		fIsWorkspaceScope= fSearchScope.equals(SearchEngine.createWorkspaceScope());
		fElementKind= elementKind;
		fFilterExtension= extension;
		
		int index= text.lastIndexOf("::"); //$NON-NLS-1$
		if (index == -1) {
			fNameMatcher= new PatternMatcher(text, true);
		} else {
			fNamespaceMatcher= new PatternMatcher(text.substring(0, index), true);
			String name= text.substring(index + 2);
			if (name.length() == 0)
				name= "*"; //$NON-NLS-1$
			fNameMatcher= new PatternMatcher(name, true);
		}
	}

	public String getText() {
		return fText;
	}

	public boolean isSubFilter(String text) {
		if (! fText.startsWith(text))
			return false;
		if (text.endsWith("::") && !text.equals(fText)) { // last search ended with "::", so it returned no results. Force a new search, not look at cache
			return false;
		}
		return fText.indexOf("::", text.length()) == -1;
	}

	public boolean isCamelCasePattern() {
		return fNameMatcher.getMatchKind() == SearchPattern.R_CAMELCASE_MATCH;
	}

	public String getPackagePattern() {
		if (fNamespaceMatcher == null)
			return null;
		return fNamespaceMatcher.getPattern();
	}

	public String getNamePattern() {
		return fNameMatcher.getPattern();
	}

	public int getSearchFlags() {
		if (fNamespaceMatcher != null) {
			int matchKind = fNamespaceMatcher.getMatchKind();
			int nameKind = fNameMatcher.getMatchKind();
			// FIXME I have no idea what to do here. We need to change the match rule for teh simple name and the enclosing type names independently the way the API is set up, but here we can only pass in one flag
			if (matchKind == SearchPattern.R_CAMELCASE_MATCH || nameKind == SearchPattern.R_CAMELCASE_MATCH) return SearchPattern.R_CAMELCASE_MATCH;
			if (matchKind == SearchPattern.R_PATTERN_MATCH || nameKind == SearchPattern.R_PATTERN_MATCH) return SearchPattern.R_PATTERN_MATCH;
			if (matchKind == SearchPattern.R_PREFIX_MATCH || nameKind == SearchPattern.R_PREFIX_MATCH) return SearchPattern.R_PREFIX_MATCH;
		}
		return fNameMatcher.getMatchKind();
	}

	public boolean matchesRawNamePattern(TypeInfo type) {
		return Strings.startsWithIgnoreCase(type.getTypeName(), fNameMatcher.getPattern());
	}

	public boolean matchesCachedResult(TypeInfo type) {
		if (!(matchesNamespace(type) && matchesFilterExtension(type)))
			return false;
		return matchesName(type);
	}
	
	public boolean matchesHistoryElement(TypeInfo type) {
		if (!(matchesNamespace(type) && matchesModifiers(type) && matchesScope(type) && matchesFilterExtension(type)))
			return false;
		return matchesName(type);
	}

	public boolean matchesFilterExtension(TypeInfo type) {
		if (fFilterExtension == null)
			return true;
		fAdapter.setInfo(type);
		return fFilterExtension.select(fAdapter);
	}
	
	private boolean matchesName(TypeInfo type) {
		return fNameMatcher.matches(type.getTypeName());
	}

	private boolean matchesNamespace(TypeInfo type) {
		if (fNamespaceMatcher == null)
			return true;
		// We should use the type container name here. However this
		// require support from JDT/Core. See
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=120534
		return fNamespaceMatcher.matches(type.getEnclosingName());
	}

	private boolean matchesScope(TypeInfo type) {
		if (fIsWorkspaceScope)
			return true;
		return type.isEnclosed(fSearchScope);
	}
	
	private boolean matchesModifiers(TypeInfo type) {
		if (fElementKind == IRubySearchConstants.TYPE)
			return true;
		boolean isModule= type.isModule();
		switch (fElementKind) {
			case IRubySearchConstants.CLASS:
				return !isModule;
			case IRubySearchConstants.MODULE:
				return isModule;
		}
		return false;
	}
}
