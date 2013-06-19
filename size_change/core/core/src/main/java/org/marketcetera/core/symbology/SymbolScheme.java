package org.marketcetera.core.symbology;

/**
 * @version $Id: SymbolScheme.java 16063 2012-01-31 18:21:55Z colin $
 */
public enum SymbolScheme {
    BLOOMBERG("B"), RIC("R"), ISIN("I"), SEDOL("S"), HYPERFEED("H"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    REUTERS_BRIDGE("RB"), BASIC("X"); //$NON-NLS-1$ //$NON-NLS-2$
    String abbrev;

    SymbolScheme(String abbrev)
    {
        this.abbrev = abbrev;
    }
    public String toString(){ return abbrev;}
}
