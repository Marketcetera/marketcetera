package org.marketcetera.systemmodel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;

import quickfix.field.Side;
import quickfix.field.TimeInForce;
import quickfix.field.OrdType;

/* $License$ */
/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since $Release$
 * @version $Id: $
 */
@ClassVersion("$Id: $") //$NON-NLS-1$
public interface Order
{
    public OrderID getID();
    public Date getTransactTime();
    public MSymbol getSymbol();
    public BigDecimal getQuantity();
    public BigDecimal getPrice();
    public Side getSide();
    public TimeInForce getTimeInForce();
    public OrdType getOrderType();
    public String getAccount();
    public Map<String,String> getCustomFields();
}
