package org.marketcetera.photon.internal.strategy;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.marketcetera.photon.commons.emf.EMFFilePersistence;
import org.marketcetera.photon.commons.emf.IEMFPersistence;
import org.marketcetera.photon.strategy.StrategyUI;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.sa.ui.StrategyAgentEnginesSupport;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Manages the {@link IStrategyEngines} service provided by this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
class StrategyEnginesSupport
        extends StrategyAgentEnginesSupport
{
    /**
     * Persistence service that only saves {@link StrategyAgentEngine}.
     *
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since 2.0.0
     */
    @ClassVersion("$Id$")
    private final static class Persistence
            implements IEMFPersistence
    {
        @Override
        public void save(Collection<? extends EObject> objects)
                throws IOException
        {
            persistenceDelegate.save(Collections2.filter(objects,
                                                         Predicates.instanceOf(StrategyAgentEngine.class)));
        }
        @Override
        public List<? extends EObject> restore()
                throws IOException
        {
            if(!persistenceFile.exists()) {
                FileUtils.writeLines(persistenceFile,
                                     Lists.newArrayList("<strategyagent:StrategyAgentEngine xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:strategyagent=\"http://www.marketcetera.org/photon/strategy/engine/strategyagent/1.0\" name=\"Local SE\" jmsUrl=\"tcp://localhost:61617\" webServiceHostname=\"localhost\" webServicePort=\"8998\"/>"));
            }
            return persistenceDelegate.restore();
        }
        /**
         * file that holds persisted strategy engines
         */
        private final File persistenceFile = Platform.getStateLocation(Platform.getBundle(StrategyUI.PLUGIN_ID)).append(PERSISTENCE_FILE_NAME).toFile();
        /**
         * persistence delegate object
         */
        private final IEMFPersistence persistenceDelegate = new EMFFilePersistence(persistenceFile);
    };
    /**
     * Create a new StrategyEnginesSupport instance.
     *
     * @param inContext a <code>BundleContext</code> that contains the context with which to register the service
     * @throws IllegalArgumentException if context is null
     * @throws IllegalStateException if called from a non UI thread
     */
    public StrategyEnginesSupport(BundleContext inContext)
    {
        super(inContext,
              new Persistence());
    }
    @Override
    protected void initList(List<StrategyEngine> inEngines)
    {
        super.initList(inEngines);
    }
    /**
     * name of file that holds persisted engines
     */
    private static final String PERSISTENCE_FILE_NAME = "engines.xml"; //$NON-NLS-1$
}
