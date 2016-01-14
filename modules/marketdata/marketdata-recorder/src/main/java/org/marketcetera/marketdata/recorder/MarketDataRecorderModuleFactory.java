package org.marketcetera.marketdata.recorder;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.springframework.context.ApplicationContext;

/* $License$ */

/**
 * Provides a market data recorder module factory implementation.
 * <p>
 * The factory accepts the following parameters when creating new instances of the strategy
 * <ol>
 * <li>String: directory to record to</li>
 * </ol>
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:mdata:recorder</code></td></tr>
 * <tr><th>Cardinality:</th><td>Multi-Instance</td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>No</td></tr>
 * <tr><th>Instantiation Arguments:</th><td><code>String</code>: See above for details.</td></tr>
 * <tr><th>Module Type:</th><td>{@link MarketDataRecorderModule}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRecorderModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new MarketDataRecorderModuleFactory instance.
     */
    public MarketDataRecorderModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.FILERECORDER_PROVIDER_DESCRIPTION,
              true,
              false,
              String.class,
              ApplicationContext.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        if(inParameters == null || inParameters.length != 2) {
            throw new ModuleCreationException(Messages.PARAMETER_COUNT_ERROR);
        }
        String directoryName = String.valueOf(inParameters[0]);
        ApplicationContext applicationContext = (ApplicationContext)inParameters[1];
        return new MarketDataRecorderModule(directoryName,
                                            applicationContext);
    }
    /**
     * unique provider URN for the receiver module
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:recorder");  //$NON-NLS-1$
}
