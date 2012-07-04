package org.marketcetera.core;

import org.marketcetera.core.marketdata.IFeedComponent;

/**
 * @author Toli Kuznets
 * @version $Id: IFeedComponentListener.java 16063 2012-01-31 18:21:55Z colin $
 */
public interface IFeedComponentListener {
    public void feedComponentChanged(IFeedComponent component);
}
