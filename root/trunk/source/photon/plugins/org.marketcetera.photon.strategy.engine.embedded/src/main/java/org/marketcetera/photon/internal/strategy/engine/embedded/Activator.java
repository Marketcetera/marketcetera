package org.marketcetera.photon.internal.strategy.engine.embedded;

import org.marketcetera.module.DataFlowID;
import org.marketcetera.photon.module.IDataFlowLabelProvider;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * Manages the lifecycle of this bundle.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(IDataFlowLabelProvider.class.getName(),
                new IDataFlowLabelProvider() {
                    @Override
                    public String getLabel(DataFlowID dataFlowId) {
                        try {
                            /*
                             * Naively assume an integer data flow id means the
                             * data originated from a strategy being run on the
                             * embedded engine.
                             */
                            Integer.parseInt(dataFlowId.getValue());
                            return Messages.EMBEDDED_ENGINE_IMPL__NAME
                                    .getText();
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                }, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

}
