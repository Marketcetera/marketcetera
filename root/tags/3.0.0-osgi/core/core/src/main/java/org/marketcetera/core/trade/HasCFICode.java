package org.marketcetera.core.trade;

/* $License$ */

/**
 * Indicates that the implementer can produce a CFI code.
 *
 * @version $Id: HasCFICode.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
public interface HasCFICode
{
    /**
     * Gets the CFI Code.
     *
     * @return a <code>char</code> value
     */
    public char getCfiCode();
}
