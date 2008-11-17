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
package org.rubypeople.rdt.internal.core.search.matching;

import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.core.RubyScript;

public class OrLocator extends PatternLocator {

	protected PatternLocator[] patternLocators;

	public OrLocator(OrPattern pattern) {
		super(pattern);

		SearchPattern[] patterns = pattern.patterns;
		int length = patterns.length;
		this.patternLocators = new PatternLocator[length];
		for (int i = 0; i < length; i++)
			this.patternLocators[i] = PatternLocator
					.patternLocator(patterns[i]);
	}

	@Override
	public void reportMatches(RubyScript script, MatchLocator locator) {
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			PatternLocator patternLocator = this.patternLocators[i];
			patternLocator.reportMatches(script, locator);
		}
	}
}
