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
@ClassVersion("$Id$")
public interface JPQLConstants {
    static final String SELECT = "select";
    static final String FROM = "from";
    static final String JOIN = "join";
    static final String FETCH_JOIN = "left join fetch";
    static final String WHERE = "where";
    static final String AND = "and";
    static final String NOT = "not";
    static final String IN = "in";
    static final String LIKE = "like";
    static final String ESCAPE = "escape";
    static final String ORDER_BY = "order by";
    static final String ASC = "asc";
    static final String DESC = "desc";
    static final String S = " ";
    static final String L = "(";
    static final String R = ")";
    static final String EQUALS = "=";
    static final String DOT = ".";
    static final String COUNT_ALL = "count(*)";
    static final String PARAMETER_PREFIX = ":";
    static final String QUOTE = "'";
    static final String DELETE = "delete";
}
