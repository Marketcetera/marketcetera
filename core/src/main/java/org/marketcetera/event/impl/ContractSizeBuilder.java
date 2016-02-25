package org.marketcetera.event.impl;

/* $License$ */

/**
 * Builds market data events that have a contract size.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ContractSizeBuilder<B>
{
    /**
     * Set the contract size value.
     *
     * @param inContractSize an <code>int</code> value
     * @return a <code>B</code> value
     */
    B withContractSize(int inContractSize);
}
