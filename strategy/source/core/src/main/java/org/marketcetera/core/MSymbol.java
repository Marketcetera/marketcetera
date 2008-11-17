package org.marketcetera.core;

import org.marketcetera.symbology.Exchange;
import org.marketcetera.symbology.Exchanges;
import org.marketcetera.symbology.SymbolScheme;
import org.marketcetera.trade.SecurityType;
import org.apache.commons.lang.ObjectUtils;

/**
 * Represents a Security's trading symbol.
 * <p>
 * Do note that the class currently generates hash codes in a way that
 * will lead to inefficiecies if the class is  
 *
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MSymbol {
    private String baseSymbol;
    private String fullSymbol;
    private SymbolScheme scheme;
    private final String cachedString;
    private final int cachedHashCode;
    private String exchangeString;
    private final SecurityType mSecurityType;

    /**
     * Creates a new instance.
     *
     * @param fullSymbol the symbol value.
     */
    public MSymbol(String fullSymbol){
        this(fullSymbol, SymbolScheme.BASIC);
    }

    /**
     * Creates a new instance.
     *
     * @param fullSymbol the symbol value.
     * @param inSecurityType the security type.
     */
    public MSymbol(String fullSymbol, SecurityType inSecurityType){
        this(fullSymbol, SymbolScheme.BASIC, inSecurityType);
    }

    /**
     * Creates a new instance.
     *
     * @param fullSymbol the symbol value.
     * @param scheme the symbol scheme.
     */
    public MSymbol(String fullSymbol, SymbolScheme scheme) {
        this(fullSymbol, scheme, null);
    }

    /**
     * Creates a new instance.
     *
     * @param fullSymbol the symbol value.
     * @param scheme the symbol scheme.
     * @param inSecurityType the security type.
     */
    public MSymbol(String fullSymbol, SymbolScheme scheme, SecurityType inSecurityType) {
        this.fullSymbol = fullSymbol;
        mSecurityType = inSecurityType;
        if (fullSymbol == null || scheme == null){
            throw new IllegalArgumentException(Messages.ERROR_NULL_MSYMBOL.getText());
        }
        if (scheme == SymbolScheme.BASIC){
            String [] symbolSplit = fullSymbol.split("\\."); //$NON-NLS-1$
            this.baseSymbol = symbolSplit[0];
            this.exchangeString = symbolSplit.length > 1 ? symbolSplit[symbolSplit.length - 1] : null;
        } else {
            this.baseSymbol = fullSymbol;
        }
        this.scheme = scheme;
        cachedString = toStringHelper();
        cachedHashCode = cachedString.hashCode();
    }

    @Override
    public String toString(){
        return cachedString;
    }

    protected String toStringHelper(){
        if (getScheme() != SymbolScheme.BASIC){
            return (getFullSymbol() +"("+getScheme() +")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return (getFullSymbol());
    }

    public String getFullSymbol() {
        return fullSymbol;
    }

    public String getBaseSymbol() {
        return baseSymbol;
    }

    public SymbolScheme getScheme() {
        return scheme;
    }

    public boolean equals(Object obj){
        if (obj instanceof MSymbol) {
            MSymbol aSymbol = (MSymbol) obj;
            return (aSymbol.scheme.equals(getScheme()) &&
                    aSymbol.baseSymbol.equals(getBaseSymbol()) &&
                    ObjectUtils.equals(getSecurityType(), aSymbol.getSecurityType()));
        }
        return false;
    }

    public int hashCode() {
        return cachedHashCode;
    }

    public boolean hasExchange()
    {
        return exchangeString != null;
    }

    public String getExchangeString()
    {
        return exchangeString;
    }

    public Exchange getExchange()
    {
        return Exchanges.getExchange(exchangeString);
    }

    /**
     * Returns the Security Type for this Symbol.
     *
     * @return the security type of this symbol.
     */
    public SecurityType getSecurityType() {
        return mSecurityType;
    }
}
