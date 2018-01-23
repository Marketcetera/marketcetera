package org.marketcetera.marketdata.core.request;

import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.marketdata.Content;

/* $License$ */

/**
 * Represents the most basic market data request element.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@XmlRootElement
public interface MarketDataRequestAtom
{
    /**
     * Gets the symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getSymbol();
    /**
     * Gets the exchange value.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getExchange();
    /**
     * Indicates if the symbol represents an instrument or an underlying instrument.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isUnderlyingSymbol();
    /**
     * Gets the content value.
     *
     * @return a <code>Content</code> value
     */
    public Content getContent();
}
