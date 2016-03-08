package org.marketcetera.event.impl;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the underlying event builder supports the attributes necessary to build currency events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id: CurrencyEventBuilder.java")
public interface CurrencyEventBuilder<B>
        extends ProviderSymbolBuilder<B>, DeliveryTypeBuilder<B>, ContractSizeBuilder<B>
{
}
