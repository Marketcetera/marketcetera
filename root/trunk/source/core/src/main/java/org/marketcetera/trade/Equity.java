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
 * @version $Id: Equity.java 10804 2009-10-08 18:00:15Z will $
 * @since $Release$
 */
@ClassVersion("$Id: Equity.java 10804 2009-10-08 18:00:15Z will $")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Equity extends Instrument {

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

    /**
     * Always returns {@link SecurityType#CommonStock}.
     * 
     * @return {@link SecurityType#CommonStock}
     */
    @Override
    public SecurityType getSecurityType() {
        return SecurityType.CommonStock;
    }

    @Override
    public int hashCode() {
        return  mSymbol.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Equity)) {
            return false;
        }
        Equity other = (Equity) obj;
        return mSymbol.equals(other.mSymbol);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("symbol", mSymbol) //$NON-NLS-1$
                .toString();
    }
    private static final long serialVersionUID = 1L;
}