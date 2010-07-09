package org.marketcetera.photon.internal.strategy.engine.embedded;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.emf.EMFFilePersistence;
import org.marketcetera.photon.commons.emf.IEMFPersistence;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides support for saving and restoring a list of strategies to a file.
 * <p>
 * This is not thread safe because it reads and writes EMF objects.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@NotThreadSafe
@ClassVersion("$Id$")
public final class PersistenceService implements IPersistenceService {

    private final File mFile;
    private final IEMFPersistence mEMFPersistence;
    private boolean mLoading;

    /**
     * Constructor.
     * 
     * @param file
     *            the file to use for persistence
     * @throws IllegalArgumentException
     *             if file is null
     */
    public PersistenceService(File file) {
        Validate.notNull(file, "file"); //$NON-NLS-1$
        mFile = file;
        mEMFPersistence = new EMFFilePersistence(file);
    }

    @Override
    public synchronized void restore(StrategyEngineConnection connection) {
        Validate.notNull(connection, "connection"); //$NON-NLS-1$
        if (!mFile.exists()) {
            Messages.PERSISTENCE_SERVICE_NO_FILE.debug(this);
            return;
        } else {
            mLoading = true;
            try {
                final Collection<? extends EObject> restored;
                try {
                    restored = mEMFPersistence.restore();
                } catch (Exception e) {
                    Messages.PERSISTENCE_SERVICE_RESTORE_FAILED.error(this, e);
                    return;
                }
                for (EObject object : restored) {
                    if (object instanceof Strategy) {
                        final Strategy strategy = (Strategy) object;
                        try {
                            connection.deploy(strategy);
                        } catch (Exception e) {
                            Messages.PERSISTENCE_SERVICE_DEPLOY_FAILED.error(
                                    this, e, strategy.getInstanceName());
                        }
                    } else {
                        Messages.PERSISTENCE_SERVICE_UNEXPECTED_OBJECT.warn(
                                this, object.getClass());
                    }
                }
            } finally {
                mLoading = false;
            }
        }
    }

    @Override
    public synchronized void save(Collection<? extends Strategy> strategies)
            throws IOException {
        Validate.noNullElements(strategies, "strategies"); //$NON-NLS-1$
        if (mLoading) {
            return;
        }
        List<? extends Strategy> copy = Lists.newArrayList(strategies);
        for (Iterator<? extends Strategy> iterator = copy.iterator(); iterator
                .hasNext();) {
            Strategy strategy = (Strategy) iterator.next();
            if (StringUtils.isBlank(strategy.getScriptPath())) {
                Messages.PERSISTENCE_SERVICE_IGNORED_STRATEGY_WITH_NO_SCRIPT_PATH
                        .warn(this, strategy);
                iterator.remove();
            }
        }
        mEMFPersistence.save(copy);
    }
}
