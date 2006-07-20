package org.marketcetera.symbology;

/**
 * @author Graham Miller
 * @version $Id$
 */
public enum SymbolScheme {
    BLOOMBERG("B"), RIC("R"), ISIN("I"), SEDOL("S"), HYPERFEED("H"),
    REUTERS_BRIDGE("RB"), BASIC("X");
    String abbrev;

    SymbolScheme(String abbrev)
    {
        this.abbrev = abbrev;
    }
    public String toString(){ return abbrev;}
}