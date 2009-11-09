package org.marketcetera.core.position;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The key elements available for grouping.
 * 
 * @see PositionRow#getGrouping()
 * @see PositionEngine#getGroupedData(Grouping...)
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public enum Grouping {
    Symbol {
        @Override
        public String get(PositionRow row) {
            return row.getSymbol();
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
