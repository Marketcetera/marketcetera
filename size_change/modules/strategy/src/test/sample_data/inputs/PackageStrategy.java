package org.someorg.someapp;

import org.marketcetera.strategy.java.Strategy;

/* $License$ */

/**
 * Tests a Java strategy that is not in the default package.
 *
 * @version $Id: PackageStrategy.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class PackageStrategy
        extends Strategy
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStart()
     */
    @Override
    public void onStart()
    {
        setProperty("onStart",
                    Long.toString(System.currentTimeMillis()));
    }
}
