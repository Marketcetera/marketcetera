package org.marketcetera.modules.async;

import org.marketcetera.core.module.ModuleFactory;
import org.marketcetera.core.module.ModuleCreationException;
import org.marketcetera.core.module.ModuleURN;

/* $License$ */
/**
 * Provider that instantiates modules that emit received data in a thread that
 * is different from the thread on which the data is received..
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:async:simple</code></td></tr>
 * <tr><th>Cardinality:</th><td>Multi-Instance</td></tr>
 * <tr><th>Auto-Instantiated:</th><td>Yes</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>{@link ModuleURN}</td></tr>
 * <tr><th>Module Type</th><td>{@link SimpleAsyncProcessor}</td></tr>
 * </table>
 *
 * @see SimpleAsyncProcessor
 * @version $Id: SimpleAsyncProcessorFactory.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public final class SimpleAsyncProcessorFactory extends ModuleFactory {
    /**
     * Creates an instance.
     *
     */
    public SimpleAsyncProcessorFactory() {
        super(PROVIDER_URN, Messages.PROVIDER_DESCRIPTION, true, true,
                ModuleURN.class);
    }

    @Override
    public SimpleAsyncProcessor create(Object... inParameters)
            throws ModuleCreationException {
        return new SimpleAsyncProcessor((ModuleURN)inParameters[0]);
    }

    /**
     * The Provider URN.
     */
    public static final ModuleURN PROVIDER_URN =
            new ModuleURN("metc:async:simple");  //$NON-NLS-1$

}