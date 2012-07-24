/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists.impl.matchers;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * A simple {@link ca.odell.glazedlists.matchers.Matcher} implementation that inverts the result of another
 * {@link ca.odell.glazedlists.matchers.Matcher Matcher's} {@link ca.odell.glazedlists.matchers.Matcher#matches(Object)} method.
 *
 * @author <a href="mailto:rob@starlight-systems.com">Rob Eden</a>
 */
public class NotMatcher<E> implements Matcher<E> {
	private Matcher<E> parent;

	public NotMatcher(Matcher<E> parent) {
		if (parent == null ) throw new IllegalArgumentException("parent cannot be null");
		this.parent = parent;
	}

    /** {@inheritDoc} */
	public boolean matches(E item) {
		return !parent.matches(item);
	}

    /** {@inheritDoc} */
	public String toString() {
		return "[NotMatcher parent:" + parent + "]";
	}
}