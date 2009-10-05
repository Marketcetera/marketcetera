package org.marketcetera.photon.internal.strategy;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.marketcetera.photon.commons.emf.EMFFilePersistence;
import org.marketcetera.photon.commons.emf.IEMFPersistence;
import org.marketcetera.photon.strategy.StrategyUI;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.embedded.EmbeddedEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.strategyagent.ui.StrategyAgentEnginesSupport;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/* $License$ */

/**
 * Manages the {@link IStrategyEngines} service provided by this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class StrategyEnginesSupport extends StrategyAgentEnginesSupport {

    private static final String PERSISTENCE_FILE_NAME = "engines.xml"; //$NON-NLS-1$

    /**
     * Persistence service that only saves {@link StrategyAgentEngine}.
     */
    @ClassVersion("$Id$")
    private final static class Persistence implements IEMFPersistence {

        private final IEMFPersistence mDelegate = new EMFFilePersistence(
                Platform.getStateLocation(
                        Platform.getBundle(StrategyUI.PLUGIN_ID)).append(
                        PERSISTENCE_FILE_NAME).toFile());

        @Override
        public void save(Collection<? extends EObject> objects)
                throws IOException {
            mDelegate.save(Collections2.filter(objects, Predicates
                    .instanceOf(StrategyAgentEngine.class)));

        }

        @Override
        public List<? extends EObject> restore() throws IOException {
            return mDelegate.restore();
        }
    };

    /**
     * Constructor.
     * 
     * @param context
     *            the context with which to register the service
     * @throws IllegalArgumentException
     *             if context is null
     * @throws IllegalStateException
     *             if called from a non UI thread
     */
    public StrategyEnginesSupport(BundleContext context) {
        super(context, new Persistence());
    }

    @Override
    protected void initList(List<StrategyEngine> engines) {
        /*
         * Put the static embedded engine at the top of the list.
         */
        StrategyEngine mEngine = EmbeddedEngine.createEngine(getGuiExecutor(),
                true);
        engines.add(mEngine);
        super.initList(engines);
    }
}