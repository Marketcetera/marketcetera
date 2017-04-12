package org.marketcetera.tensorflow.model;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.tensorflow.Messages;

/* $License$ */

/**
 * Provides a module that invokes an existing TensorFlow model.
 *
 * <p>The factory has the following characteristics.
 * <table>
 * <tr><th>Provider URN:</th><td><code>metc:tensorflow:model</code></td></tr>
 * <tr><th>Cardinality:</th><td>Multi-Instance</td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>Yes</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>String: the name of an existing model</td></tr>
 * <tr><th>Module Type:</th><td>{@link TensorFlowModelModule}</td></tr>
 * </table></p>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TensorFlowModelModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new TensorFlowModelModuleFactory instance.
     */
    public TensorFlowModelModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.MODEL_PROVIDER_DESCRIPTION,
              true,
              false,
              String.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        if(inParameters == null || inParameters.length != 1) {
            
        }
        String modelName = String.valueOf(inParameters[0]);
        return new TensorFlowModelModule(new ModuleURN(PROVIDER_URN,
                                                       modelName),
                                         modelName);
    }
    /**
     * tensor flow module provider URN
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:tensorflow:model");  //$NON-NLS-1$
}
