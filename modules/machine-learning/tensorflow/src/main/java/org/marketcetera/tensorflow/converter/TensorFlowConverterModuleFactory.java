package org.marketcetera.tensorflow.converter;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.tensorflow.Messages;
import org.tensorflow.Tensor;

/* $License$ */

/**
 * Provider that will convert incoming data flow data to {@link Tensor} types.
 *
 * <p>The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:tensorflow:converter</code></td></tr>
 * <tr><th>Cardinality:</th><td>Single Instance</td></tr>
 * <tr><th>Auto-Instantiated:</th><td>Yes</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>n/a</td></tr>
 * <tr><th>Module Type:</th><td>{@link TensorFlowConverterModule}</td></tr>
 * </table></p>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TensorFlowConverterModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new TensorFlowModuleFactory instance.
     */
    public TensorFlowConverterModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.CONVERTER_PROVIDER_DESCRIPTION,
              false,
              true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        return new TensorFlowConverterModule(INSTANCE_URN);
    }
    /**
     * tensor flow module provider URN
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:tensorflow:converter");  //$NON-NLS-1$
    /**
     * tensor flow module instance URN
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN("metc:tensorflow:converter:single");  //$NON-NLS-1$
}
