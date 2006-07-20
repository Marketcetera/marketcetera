/**
 * 
 */
package org.marketcetera.photon.model;

import ca.odell.glazedlists.matchers.Matcher;

final class NotNullMatcher implements Matcher<MessageHolder> {
	public boolean matches(MessageHolder arg0) {
		return arg0 != null;
	}
}