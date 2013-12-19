package org.marketcetera.marketdata.neo.request;

import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.marketdata.Content;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the most basic market data request element.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequestAtom.java 16375 2012-11-19 21:02:22Z colin $
 * @since $Release$
 */
@XmlRootElement
@ClassVersion("$Id$")
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
