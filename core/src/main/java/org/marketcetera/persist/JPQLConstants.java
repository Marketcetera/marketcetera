package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Contains constants that are used for creating JPQL queries.
 * Its highly recommended that these constants be used for
 * constructing JPQL queries anywhere in the code that
 * uses the persistence infrastructure
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface JPQLConstants {
    static final String SELECT = "select";               //$NON-NLS-1$
    static final String FROM = "from";                   //$NON-NLS-1$
    static final String JOIN = "join";                   //$NON-NLS-1$
    static final String FETCH_JOIN = "left join fetch";  //$NON-NLS-1$
    static final String WHERE = "where";                 //$NON-NLS-1$
    static final String AND = "and";                     //$NON-NLS-1$
    static final String NOT = "not";                     //$NON-NLS-1$
    static final String IN = "in";                       //$NON-NLS-1$
    static final String LIKE = "like";                   //$NON-NLS-1$
    static final String ESCAPE = "escape";               //$NON-NLS-1$
    static final String ORDER_BY = "order by";           //$NON-NLS-1$
    static final String ASC = "asc";                     //$NON-NLS-1$
    static final String DESC = "desc";                   //$NON-NLS-1$
    static final String S = " ";                         //$NON-NLS-1$
    static final String L = "(";                         //$NON-NLS-1$
    static final String R = ")";                         //$NON-NLS-1$
    static final String EQUALS = "=";                    //$NON-NLS-1$
    static final String GREATER_THAN = ">";              //$NON-NLS-1$
    static final String LESS_THAN = "<";                 //$NON-NLS-1$
    static final String DOT = ".";                       //$NON-NLS-1$
    static final String COUNT_ALL = "count(*)";          //$NON-NLS-1$
    static final String PARAMETER_PREFIX = ":";          //$NON-NLS-1$
    static final String QUOTE = "'";                     //$NON-NLS-1$
    static final String DELETE = "delete";               //$NON-NLS-1$
}
