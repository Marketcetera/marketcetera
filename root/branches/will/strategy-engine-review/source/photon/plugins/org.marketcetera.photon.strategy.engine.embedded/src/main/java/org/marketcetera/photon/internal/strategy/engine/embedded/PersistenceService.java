package org.marketcetera.photon.internal.strategy.engine.embedded;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.emf.ecore.EObject;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.emf.EMFFilePersistence;
import org.marketcetera.photon.commons.emf.IEMFPersistence;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Provides support for saving and restoring a list of strategies to a file.
 * <p>
 * This is not thread safe because it reads and writes EMF objects.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
                final List<? extends EObject> restored;
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
    public synchronized void save(List<? extends Strategy> strategies) throws IOException {
        ImmutableList<? extends Strategy> copy = ImmutableList
                .copyOf(strategies);
        Validate.noNullElements(copy, "strategies"); //$NON-NLS-1$
        if (mLoading) {
            return;
        }
        mEMFPersistence.save(ImmutableList.copyOf(strategies));
    }
}
