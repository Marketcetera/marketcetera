package org.marketcetera.core.trade;

/* $License$ */

/**
 * Indicates that the implementer can produce a CFI code.
 *
 * @version $Id$
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
