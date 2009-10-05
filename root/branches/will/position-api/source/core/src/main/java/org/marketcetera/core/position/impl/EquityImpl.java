package org.marketcetera.core.position.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.position.AbstractInstrument;
import org.marketcetera.core.position.Equity;
import org.marketcetera.core.position.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implementation of {@link Equity}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EquityImpl extends AbstractInstrument implements Equity {

    private static final int TYPE_ID = 10;

    private final String mSymbol;

    /**
     * Constructor.
     * 
     * @param symbol
     *            symbol
     * @throws IllegalArgumentException
     *             if symbol is null or empty
     */
    public EquityImpl(String symbol) {
        Validate.notEmpty(symbol);
        mSymbol = symbol;
    }

    /**
     * Parameterless constructor for use only by JAXB.
     */
    protected EquityImpl() {
        mSymbol = null;
    }

    @Override
    public String getUnderlying() {
        return mSymbol;
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public String getSymbol() {
        return mSymbol;
    }
    
    @Override
    protected void enhanceHashCode(HashCodeBuilder builder) {
        builder.append(mSymbol);
    }

    @Override
    protected void refineCompareTo(CompareToBuilder builder,
            Instrument otherInstrument) {
        Equity otherEquity = (Equity) otherInstrument;
        builder.append(mSymbol, otherEquity.getSymbol());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("symbol", mSymbol) //$NON-NLS-1$
                .toString();
    }
}
