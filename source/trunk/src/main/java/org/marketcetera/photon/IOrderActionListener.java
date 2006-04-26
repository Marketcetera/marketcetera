package org.marketcetera.photon;

import org.marketcetera.core.AccountID;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.symbology.Exchange;

@ClassVersion("$Id$")
public interface IOrderActionListener {
    void orderActionTaken(String symbol, Exchange exchange, String currency, AccountID id);
}
