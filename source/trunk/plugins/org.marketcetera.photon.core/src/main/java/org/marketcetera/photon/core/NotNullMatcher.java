/**
 * 
 */
package org.marketcetera.photon.core;

import org.marketcetera.core.ClassVersion;

import ca.odell.glazedlists.matchers.Matcher;

@ClassVersion("$Id$")
public final class NotNullMatcher implements Matcher<MessageHolder> {
	public boolean matches(MessageHolder arg0) {
		return arg0 != null;
	}
}