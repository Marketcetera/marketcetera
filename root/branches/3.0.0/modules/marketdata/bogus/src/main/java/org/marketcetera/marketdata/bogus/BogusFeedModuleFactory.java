package org.marketcetera.marketdata.bogus;

import static org.marketcetera.marketdata.bogus.Messages.PROVIDER_DESCRIPTION;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.module.ModuleCreationException;
import org.marketcetera.core.module.ModuleFactory;
import org.marketcetera.core.module.ModuleURN;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * <code>ModuleFactory</code> implementation for the <code>BogusFeed</code> market data provider.
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:mdata:bogus</code></td></tr>
 * <tr><th>Cardinality:</th><td>Singleton</td></tr>
 * <tr><th>Instance URN:</th><td><code>metc:mdata:bogus:single</code></td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>No</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>None</td></tr>
 * <tr><th>Module Type:</th><td>{@link BogusFeedModule}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BogusFeedModuleFactory.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: BogusFeedModuleFactory.java 16063 2012-01-31 18:21:55Z colin $")  //$NON-NLS-1$
public class BogusFeedModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new BogusFeedModuleFactory instance.
     */
    public BogusFeedModuleFactory()
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
    public BogusFeedModule create(Object... inArg0)
            throws ModuleCreationException
    {
        try {
            return new BogusFeedModule();
        } catch (CoreException e) {
            throw new ModuleCreationException(e.getI18NBoundMessage());
        }
    }
    public static final String IDENTIFIER = "bogus";  //$NON-NLS-1$
    /**
     * unique provider URN for the bogus feed market data provider
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN for the bogus feed market data provider
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
