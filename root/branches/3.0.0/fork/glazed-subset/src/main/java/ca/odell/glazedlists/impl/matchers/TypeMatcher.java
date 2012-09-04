package ca.odell.glazedlists.impl.matchers;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * Matches only items that are one of the given class types.
 *
 */
public class TypeMatcher<E> implements Matcher<E> {

    private final Class[] classes;

    public TypeMatcher(Class... classes) {
        this.classes = classes;
    }

    public boolean matches(E item) {
        if (item == null) return false;

        final Class target = item.getClass();
        for (int i = 0; i < classes.length; i++)
            if (classes[i].isAssignableFrom(target))
                return true;

        return false;
    }
}