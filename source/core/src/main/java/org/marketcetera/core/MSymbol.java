package org.marketcetera.core;

import org.marketcetera.symbology.SymbolScheme;
import org.marketcetera.symbology.Exchange;
import org.marketcetera.symbology.Exchanges;

/**
 * @author Graham Miller
 * @version $Id$
 */
public class MSymbol {
    private String baseSymbol;
    private String fullSymbol;
    private SymbolScheme scheme;
    private final String cachedString;
    private final int cachedHashCode;
    private String exchangeString;

    public MSymbol(String fullSymbol){
        this(fullSymbol, SymbolScheme.BASIC);
    }

    public MSymbol(String fullSymbol, SymbolScheme scheme) {
        this.fullSymbol = fullSymbol;
        if (fullSymbol == null || scheme == null){
            throw new IllegalArgumentException("Symbol and scheme must not be null");
        }
        if (scheme == SymbolScheme.BASIC){
            String [] symbolSplit = fullSymbol.split("\\.");
            this.baseSymbol = symbolSplit[0];
            this.exchangeString = symbolSplit.length > 1 ? symbolSplit[symbolSplit.length - 1] : null;
        } else {
            this.baseSymbol = fullSymbol;
        }
        this.scheme = scheme;
        cachedString = toStringHelper();
        cachedHashCode = cachedString.hashCode();
    }

    public String toString(){
        return cachedString;
    }

    protected String toStringHelper(){
        if (getScheme() != SymbolScheme.BASIC){
            return (getFullSymbol() +"("+getScheme() +")");
        }
        return (getFullSymbol());
    }

    public String getFullSymbol() {
        return fullSymbol;
    }

    // TODO: change me back to public
    public String getBaseSymbol() {
        return baseSymbol;
    }

    public SymbolScheme getScheme() {
        return scheme;
    }

    public boolean equals(Object obj){
        if (obj instanceof MSymbol) {
            MSymbol aSymbol = (MSymbol) obj;
            return (aSymbol.scheme.equals(getScheme()) && aSymbol.baseSymbol.equals(getBaseSymbol()));
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

   

}
