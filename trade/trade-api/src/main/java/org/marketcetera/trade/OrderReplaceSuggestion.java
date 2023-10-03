package org.marketcetera.trade;

/* $License$ */

/**
 * Contains a suggestion for a replace order.
 * 
 * <p>Instances of this type can be created via {@link Factory#createOrderReplaceSuggestion()}
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OrderReplaceSuggestion
        extends Suggestion,HasOrderReplace
{
    /**
     * Set the replace order value.
     *
     * @param inOrderReplace the suggested order.
     */
    void setOrderReplace(OrderReplace inOrderReplace);
}
