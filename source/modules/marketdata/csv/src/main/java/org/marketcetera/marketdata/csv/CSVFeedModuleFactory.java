package org.marketcetera.marketdata.csv;

import static org.marketcetera.marketdata.csv.Messages.PROVIDER_DESCRIPTION;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.core.CoreException;

/**
 * <code>ModuleFactory</code> implementation for the <code>CSVFeed</code> market data provider.
 * @author toli kuznets
 * @version $Id: CSVFeedModuleFactory.java 4348 2009-09-24 02:33:11Z toli $
 */
@ClassVersion("$Id: CSVFeedModuleFactory.java 4348 2009-09-24 02:33:11Z toli $")
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
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
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
