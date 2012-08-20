package org.marketcetera.core.position;

import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * The key elements available for grouping.
 * 
 * @see PositionRow#getGrouping()
 * @see PositionEngine#getGroupedData(Grouping...)
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: Grouping.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.5.0
 */
@ClassVersion("$Id: Grouping.java 16063 2012-01-31 18:21:55Z colin $")
public enum Grouping {
    Underlying {
        @Override
        public String get(PositionRow row) {
            return row.getUnderlying();
        }
    },
    Account {
        @Override
        public String get(PositionRow row) {
            return row.getAccount();
        }
    },
    Trader {
        @Override
        public String get(PositionRow row) {
            return row.getTraderId();
        }
    };

    /**
     * Extracts the value of the field represented by this grouping.
     * 
     * @param row
     *            the row to extract from
     * @return the value
     */
    public abstract String get(PositionRow row);
};
