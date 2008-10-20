package org.marketcetera.systemmodel;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.core.MSymbol;

/* $License$ */
/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since $Release$
 * @version $Id: $
 */
public interface Position
{
    public Date asOf();
    public MSymbol getSymbol();
    public BigDecimal getQuantity();
}
