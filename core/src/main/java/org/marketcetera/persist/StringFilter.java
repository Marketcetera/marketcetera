package org.marketcetera.persist;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Instances of this class represent a text
 * filter that can be used to filter results of
 * queries by text expressions.
 * The filter string may include <code>'?'</code> to
 * match a single character and <code>'*'</code> to
 * match multiple characters.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class StringFilter implements Serializable {
    private static final long serialVersionUID = 5764655491804375079L;

    /**
     * Create an instance suppling the filter text.
     *
     * @param value the filter text. the value cannot be null
     *
     * @throws PersistenceException if the specified string filter
     * is not valid
     */
    public StringFilter(String value) {
        if(value == null) {
            throw new NullPointerException();
        }
        if(!VALIDATOR.matcher(value).matches()) {
            throw new PersistenceException(Messages.INVALID_STRING_FILTER.getText(value,
                                                                                  VALIDATOR.toString()));
        }
        this.value = value;
    }

    /**
     * The filter text.
     *
     * @return the filter text.
     */
    public String getValue() {
        return value;
    }

    /**
     * The '?' special character. When used
     * within a filter, it matches exactly one
     * character
     */
    public static final char MATCH_ONE = '?';

    /**
     * The '*' special character. When used within
     * a filter, it matches 0 or more characters.
     */
    public static final char MATCH_MANY = '*';

    public String toString() {
        return "StringFilter{" + //$NON-NLS-1$
                "value='" + value + '\'' + //$NON-NLS-1$
                '}';
    }

    /**
     * Allow for letters, digits, space, ? & & chars
     */
    static final Pattern VALIDATOR =
            Pattern.compile("^[\\p{L}\\p{Nd}\\?\\* ]+$"); //$NON-NLS-1$

    private final String value;
}
