package org.marketcetera.core;

import java.util.Arrays;
import java.util.List;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides common behavior for <code>ContextClassProvider</code> instances.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class ContextClassAggregator
        implements ContextClassProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.ContextClassProvider#getContextClasses()
     */
    @Override
    public Class<?>[] getContextClasses()
    {
        return allClasses.toArray(new Class<?>[allClasses.size()]);
    }
    /**
     * Create a new ContextClassAggregator instance.
     */
    public ContextClassAggregator() {}
    /**
     * Create a new ContextClassAggregator instance.
     *
     * @param inProviders a <code>ContextClassProvider...</code> value
     */
    public ContextClassAggregator(ContextClassProvider...inProviders)
    {
        if(inProviders != null) {
            for(ContextClassProvider provider : inProviders) {
                allClasses.addAll(Arrays.asList(provider.getContextClasses()));
            }
        }
    }
    /**
     * Sets the context class providers to aggregate.
     *
     * @param inProviders a <code>List&lt;ContextClassProvider&gt;</code> value
     */
    public void setContextClassProviders(List<ContextClassProvider> inProviders)
    {
        allClasses.clear();
        if(inProviders != null) {
            for(ContextClassProvider provider : inProviders) {
                allClasses.addAll(Arrays.asList(provider.getContextClasses()));
            }
        }
    }
    /**
     * contains all classes
     */
    private final List<Class<?>> allClasses = Lists.newArrayList();
}
