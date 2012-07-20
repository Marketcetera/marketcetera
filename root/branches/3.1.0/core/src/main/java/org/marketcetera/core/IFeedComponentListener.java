package org.marketcetera.core;

import org.marketcetera.marketdata.IFeedComponent;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
public interface IFeedComponentListener {
    public void feedComponentChanged(IFeedComponent component);
}
