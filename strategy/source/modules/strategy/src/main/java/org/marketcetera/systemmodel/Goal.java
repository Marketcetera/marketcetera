package org.marketcetera.systemmodel;

import java.math.BigDecimal;

import org.marketcetera.core.MSymbol;

/* $License$ */
/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since $Release$
 * @version $Id: $
 */
public interface Goal
{
    public String getName();
    public MSymbol getSymbol();
    public BigDecimal getQuantity();
}
