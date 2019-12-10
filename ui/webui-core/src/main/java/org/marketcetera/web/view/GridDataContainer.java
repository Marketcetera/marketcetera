package org.marketcetera.web.view;

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;

/* $License$ */

/**
 * Provides common behavior for data containers for {@link AbstractGridView}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class GridDataContainer<DataClazz>
        extends BeanItemContainer<DataClazz>
{
    /**
     * Create a new GridDataContainer instance.
     *
     * @param inType
     * @throws IllegalArgumentException
     */
    protected GridDataContainer(Class<? super DataClazz> inType)
            throws IllegalArgumentException
    {
        super(inType);
    }
    /**
     * Create a new GridDataContainer instance.
     *
     * @param inType
     * @param inCollection
     * @throws IllegalArgumentException
     */
    protected GridDataContainer(Class<? super DataClazz> inType,
                                Collection<? extends DataClazz> inCollection)
            throws IllegalArgumentException
    {
        super(inType,
              inCollection);
    }
    /**
     * Configure the data container as necessary before start.
     */
    protected abstract void configure();
    private static final long serialVersionUID = 8619929729440103508L;
}
