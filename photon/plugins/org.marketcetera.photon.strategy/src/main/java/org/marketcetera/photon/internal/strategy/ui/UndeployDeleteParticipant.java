package org.marketcetera.photon.internal.strategy.ui;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.photon.strategy.StrategyUI;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;

/* $License$ */

/**
 * Undeploys local strategy deployments if they correspond to a file being
 * deleted.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class UndeployDeleteParticipant extends DeleteParticipant {

    private static final String PLATFORM_RESOURCE_URL_PREFIX = "platform:/resource"; //$NON-NLS-1$

    private final Provider<IStrategyEngines> mEnginesProvider;
    private volatile ImmutableList<DeployedStrategy> mAffected;
    private volatile String mResourcePath;

    /**
     * Constructor.
     * 
     * @param enginesProvider
     *            provides access to the strategy engines
     */
    @Inject
    public UndeployDeleteParticipant(Provider<IStrategyEngines> enginesProvider) {
        mEnginesProvider = enginesProvider;
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm,
            CheckConditionsContext context) throws OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        Change[] changes = new Change[mAffected.size()];
        for (int i = 0; i < changes.length; i++) {
            changes[i] = new UndeployChange(mAffected.get(i));
        }
        return new CompositeChange(
                Messages.UNDEPLOY_DELETE_PARTICIPANT_CHANGE_GROUP__DESCRIPTION
                        .getText(mResourcePath), changes);
    }

    @Override
    public String getName() {
        return Messages.UNDEPLOY_DELETE_PARTICIPANT__NAME.getText();
    }

    @Override
    protected boolean initialize(final Object element) {
        final IStrategyEngines engines = mEnginesProvider.get();
        if (engines != null) {
            final AtomicReference<CoreException> exception = new AtomicReference<CoreException>();
            PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
                @Override
                public void run() {
                    if (engines.getStrategyEngines().size() > 0) {
                        final Set<String> affectedFiles = Sets.newHashSet();
                        IResource resource = (IResource) element;
                        mResourcePath = resource.getFullPath().toString();
                        try {
                            resource.accept(new IResourceVisitor() {
                                @Override
                                public boolean visit(IResource resource)
                                        throws CoreException {
                                    if (resource instanceof IFile) {
                                        affectedFiles
                                                .add(PLATFORM_RESOURCE_URL_PREFIX
                                                        + ((IFile) resource)
                                                                .getFullPath()
                                                                .toString());
                                        return false;
                                    }
                                    return true;
                                }
                            });
                        } catch (CoreException e) {
                            exception.set(e);
                            return;
                        }
                        if (!affectedFiles.isEmpty()) {
                            List<DeployedStrategy> strategies = Lists
                                    .newLinkedList();
                            StrategyEngine embeddedEngine = (StrategyEngine) engines
                                    .getStrategyEngines().get(0);
                            for (DeployedStrategy strategy : embeddedEngine
                                    .getDeployedStrategies()) {
                                if (affectedFiles.contains(strategy
                                        .getScriptPath())) {
                                    strategies.add(strategy);
                                }
                            }
                            if (!strategies.isEmpty()) {
                                mAffected = ImmutableList.copyOf(strategies);
                            }
                        }
                    }
                }
            });
            CoreException caughtException = exception.get();
            if (caughtException != null) {
                // this is not expected to happen
                throw new IllegalStateException(caughtException);
            }
        }
        return mAffected != null;
    }

    /**
     * Undeploys a single strategy.
     */
    @ClassVersion("$Id$")
    private static class UndeployChange extends Change {

        private final DeployedStrategy mStrategy;

        /**
         * Constructor.
         * 
         * @param strategy
         *            the strategy to undeploy
         */
        public UndeployChange(DeployedStrategy strategy) {
            mStrategy = strategy;
        }

        @Override
        public Change perform(IProgressMonitor pm) throws CoreException {
            try {
                mStrategy.getEngine().getConnection().undeploy(mStrategy);
            } catch (Exception e) {
                throw new CoreException(new Status(IStatus.ERROR,
                        StrategyUI.PLUGIN_ID, e.getLocalizedMessage(), e));
            }
            // no undo support
            return null;
        }

        @Override
        public RefactoringStatus isValid(IProgressMonitor pm)
                throws CoreException, OperationCanceledException {
            return RefactoringStatus.create(Status.OK_STATUS);
        }

        @Override
        public void initializeValidationData(IProgressMonitor pm) {
        }

        @Override
        public String getName() {
            return Messages.UNDEPLOY_DELETE_PARTICIPANT_CHANGE__DESCRIPTION
                    .getText(mStrategy.getInstanceName(), mStrategy.getEngine()
                            .getName());
        }

        @Override
        public Object getModifiedElement() {
            return null;
        }

    }
}
