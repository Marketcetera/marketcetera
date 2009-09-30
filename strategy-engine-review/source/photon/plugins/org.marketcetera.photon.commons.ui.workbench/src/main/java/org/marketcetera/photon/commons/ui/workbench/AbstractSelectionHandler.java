package org.marketcetera.photon.commons.ui.workbench;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.commons.ui.JFaceUtils;
import org.marketcetera.photon.commons.ui.JFaceUtils.IUnsafeRunnableWithProgress;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Abstract handler that computes the selection (with an optional filter) and
 * calls the abstract {@link #process(T, SubMonitor)} for each item in the
 * selection.
 * <p>
 * If an exception is thrown, an error dialog will be presented to the user, see
 * {@link ProgressUtils#runModalWithErrorDialog(org.eclipse.jface.window.IShellProvider, IRunnableWithProgress, I18NBoundMessage)}.
 * <p>
 * This should be used when you want to process a homogeneous subset of the
 * selection, e.g. start all selected items that are not already started.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AbstractSelectionHandler<T> extends SafeHandler {

    private final Class<T> mClazz;
    private final Predicate<T> mPredicate;
    private final I18NBoundMessage mFailureMessage;

    /**
     * Constructor. The class parameter will be used to filter items of the
     * correct type.
     * 
     * @param clazz
     *            the type of items to be processed
     * @param failureMessage
     *            the message to log if the operation fails.
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public AbstractSelectionHandler(Class<T> clazz,
            I18NBoundMessage failureMessage) {
        this(clazz, failureMessage, Predicates.<T> alwaysTrue());
    }

    /**
     * Constructor providing a predicate for additional filtering.
     * 
     * @param clazz
     *            the type of items to be processed
     * @param failureMessage
     *            the message to log if the operation fails.
     * @param predicate
     *            the predicate for filtering
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public AbstractSelectionHandler(Class<T> clazz,
            I18NBoundMessage failureMessage, Predicate<T> predicate) {
        mClazz = clazz;
        mFailureMessage = failureMessage;
        mPredicate = predicate;
    }

    @Override
    public void executeSafely(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event);
        final List<T> selected = Collections.synchronizedList(Lists
                .<T> newArrayList());
        for (Object item : selection.toList()) {
            if (mClazz.isInstance(item)) {
                T cast = mClazz.cast(item);
                if (mPredicate.apply(cast)) {
                    selected.add(cast);
                }
            }
        }
        if (selected.isEmpty()) {
            return;
        }
        final IRunnableWithProgress operation = JFaceUtils
                .safeRunnableWithProgress(new IUnsafeRunnableWithProgress() {
                    @Override
                    public void run(IProgressMonitor monitor) throws Exception {
                        SubMonitor progress = SubMonitor.convert(monitor,
                                selected.size());
                        for (T item : selected) {
                            ModalContext.checkCanceled(progress);
                            process(item, progress.newChild(1));
                        }
                    }
                });
        ProgressUtils.runModalWithErrorDialog(HandlerUtil
                .getActiveWorkbenchWindowChecked(event), operation,
                mFailureMessage);
    }

    /**
     * Processes a single item. Implementations should use the monitor to set
     * the current task name, but should not call
     * {@link IProgressMonitor#worked} or {@link IProgressMonitor#done()}.
     * 
     * @param item
     *            the current item
     * @param monitor
     *            the progress monitor to use for reporting progress to the
     *            user. It is the caller's responsibility to call done() on the
     *            given monitor. Will not be null.
     * @throws InterruptedException
     *             if the operation detects a request to cancel, using
     *             <code>IProgressMonitor.isCanceled()</code>, it should exit by
     *             throwing <code>InterruptedException</code>
     * @throws Exception
     *             if an exception occurs during the operation
     */
    protected abstract void process(T item, IProgressMonitor monitor)
            throws Exception;
}
