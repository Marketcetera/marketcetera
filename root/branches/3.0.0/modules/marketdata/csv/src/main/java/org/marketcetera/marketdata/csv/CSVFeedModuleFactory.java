package org.marketcetera.marketdata.csv;

import static org.marketcetera.marketdata.csv.Messages.PROVIDER_DESCRIPTION;

import org.marketcetera.core.module.ModuleFactory;
import org.marketcetera.core.module.ModuleCreationException;
import org.marketcetera.core.module.ModuleURN;
import org.marketcetera.core.CoreException;

/**
 * <code>ModuleFactory</code> implementation for the <code>CSVFeed</code> market data provider.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id: CSVFeedModuleFactory.java 16063 2012-01-31 18:21:55Z colin $
 */
public class CSVFeedModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new CSVFeedModuleFactory instance.
     */
    public CSVFeedModuleFactory()
    {
        super(PROVIDER_URN,
              PROVIDER_DESCRIPTION,
              false,
              false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public CSVFeedModule create(Object... inArg0)
            throws ModuleCreationException
    {
        try {
            return new CSVFeedModule();
        } catch (CoreException e) {
            throw new ModuleCreationException(e.getI18NBoundMessage());
        }
    }
    public static final String IDENTIFIER = "csv";  //$NON-NLS-1$
    /**
     * unique provider URN for the CSV feed market data provider
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN for the CSV feed market data provider
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
