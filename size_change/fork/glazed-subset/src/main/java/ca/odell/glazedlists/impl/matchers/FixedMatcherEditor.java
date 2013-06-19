/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists.impl.matchers;

import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;

/**
 * A {@link ca.odell.glazedlists.matchers.MatcherEditor} whose {@link ca.odell.glazedlists.matchers.Matcher} never changes.
 *
 */
public final class FixedMatcherEditor<E> extends AbstractMatcherEditor<E> {

    /**
     * Create a {@link FixedMatcherEditor} for the specified {@link ca.odell.glazedlists.matchers.Matcher}.
     */
    public FixedMatcherEditor(Matcher<E> matcher) {
        super.fireChanged(matcher);
    }
}