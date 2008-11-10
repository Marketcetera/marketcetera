package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * A Suggestion for a single order. Instances of this type
 * can be created via {@link Factory#createOrderSingleSuggestion()}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface OrderSingleSuggestion extends OrderSingle, Suggestion {
}
