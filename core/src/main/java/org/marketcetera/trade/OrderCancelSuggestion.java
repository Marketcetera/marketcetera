package org.marketcetera.trade;

/* $License$ */

/**
 * Contains a suggestion for a cancel order.
 * 
 * <p>Instances of this type can be created via {@link Factory#createOrderCancelSuggestion()}
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OrderCancelSuggestion
        extends Suggestion,HasOrderCancel
{
    /**
     * Set the cancel order value.
     *
     * @param inOrderCancel the suggested order.
     */
    void setOrderCancel(OrderCancel inOrderCancel);
}
