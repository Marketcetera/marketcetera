package org.marketcetera.trade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Identifies an equity.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Equity implements Instrument {

    private final String mSymbol;

    /**
     * Constructor.
     * 
     * @param symbol
     *            symbol
     * @throws IllegalArgumentException
     *             if symbol is null or whitespace
     */
    public Equity(String symbol) {
        symbol = StringUtils.trimToNull(symbol);
        Validate.notNull(symbol);
        mSymbol = symbol;
    }

    /**
     * Parameterless constructor for use only by JAXB.
     */
    protected Equity() {
        mSymbol = null;
    }

    /**
     * Returns equity symbol.
     * 
     * @return the equity symbol, never null
     */
    @Override
    public String getSymbol() {
        return mSymbol;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Equity))
            return false;
        Equity otherEquity = (Equity) obj;
        return mSymbol.equals(otherEquity.getSymbol());
    }

    @Override
    public int hashCode() {
        return mSymbol.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("symbol", mSymbol) //$NON-NLS-1$
                .toString();
    }
}
