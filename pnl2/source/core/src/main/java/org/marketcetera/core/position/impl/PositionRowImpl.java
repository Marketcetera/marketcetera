package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.marketcetera.core.position.PositionRow;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Simple implementation of {@link PositionRow}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PositionRowImpl extends PositionRowBaseImpl implements PositionRow {

    private final String account;
    private final String symbol;
    private final String traderId;

    /**
     * Constructor, providing all static fields.
     * 
     * @param account
     * @param symbol
     * @param traderId
     * @param incomingPosition
     */
    public PositionRowImpl(String account, String symbol, String traderId,
            BigDecimal incomingPosition) {
        super(incomingPosition);
        this.account = account;
        this.symbol = symbol;
        this.traderId = traderId;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getTraderId() {
        return traderId;
    }

}
