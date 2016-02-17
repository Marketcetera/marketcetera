package org.marketcetera.event.impl;

import org.marketcetera.trade.FutureType;
import org.marketcetera.trade.FutureUnderlyingAssetType;
import org.marketcetera.trade.StandardType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the underlying event builder supports the attributes necessary to build future events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public interface FutureEventBuilder<B>
        extends ProviderSymbolBuilder<B>, DeliveryTypeBuilder<B>, ContractSizeBuilder<B>
{
    /**
     * Set the future type value.
     *
     * @param inFutureType a <code>FutureType</code> value
     * @return a <code>B</code> value
     */
    B withFutureType(FutureType inFutureType);
    /**
     * Set the standard type value.
     *
     * @param inStandardType a <code>StandardType</code> value
     * @return a <code>B</code> value
     */
    B withStandardType(StandardType inStandardType);
    /**
     * Set the underlying asset type value.
     *
     * @param inUnderlyingAssetType a <code>FutureUnderlyingAssetType</code> value
     * @return a <code>B</code> value
     */
    B withUnderlyingAssetType(FutureUnderlyingAssetType inUnderlyingAssetType);
}
