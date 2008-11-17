package org.rubypeople.rdt.internal.core.search.matching;

import org.rubypeople.rdt.core.IField;

public class VariableLocator extends PatternLocator {
	protected VariablePattern pattern;

	public VariableLocator(VariablePattern pattern) {
		super(pattern);

		this.pattern = pattern;
	}
	
	protected int getAccuracy(IField field) {
		if (!this.pattern.findDeclarations)
			return IMPOSSIBLE_MATCH;

		// Verify method name
		if (!matchesName(this.pattern.name, field.getElementName().toCharArray()))
			return IMPOSSIBLE_MATCH;

		// field declaration may match pattern
		return ACCURATE_MATCH;
	}
}
