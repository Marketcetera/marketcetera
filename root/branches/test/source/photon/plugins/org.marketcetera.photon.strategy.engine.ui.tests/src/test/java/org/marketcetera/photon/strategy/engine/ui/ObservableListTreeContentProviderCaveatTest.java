package org.marketcetera.photon.strategy.engine.ui;

import static org.junit.Assert.assertTrue;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Validates certain undocumented caveats ("features") of
 * {@link ObservableListTreeContentProvider}. If these tests ever fail, then the
 * documentation/implementation for {@link StrategyEnginesContentProvider} can
 * be updated.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(SimpleUIRunner.class)
public class ObservableListTreeContentProviderCaveatTest extends PhotonTestBase {

    @Test
    @UI
    public void getElementsCannotBeCalledWithoutComparer() throws Exception {
        new ExpectedFailure<AssertionFailedException>(
                "assertion failed: Getter called on disposed observable", false) {
            @Override
            public void run() throws Exception {
                Shell s = new Shell();
                try {
                    TreeViewer v = new TreeViewer(s);
                    ITreeContentProvider cp = createFixture();
                    v.setContentProvider(cp);
                    WritableList input = WritableList
                            .withElementType(String.class);
                    /*
                     * The following four lines together cause the failure.
                     * First, setInput causes an internal node to be created for
                     * the "input" WritableList and an internal HashMap (named
                     * "elementNodes") maps "input" to this node. The second
                     * line adds an element to the list (changing its hash
                     * code). Then getElements(input) corrupts the map by
                     * creating a new internal node for the same key since the
                     * hash code is different. The final v.setInput(null) causes
                     * the exception since it iterates over the internal nodes
                     * and disposes them. The WritableList thus ends up getting
                     * disposed twice.
                     */
                    v.setInput(input);
                    input.add("ABC");
                    cp.getElements(input);
                    v.setInput(null);
                } finally {
                    s.close();
                }
            }
        };
    }

    @Test
    @UI
    public void inputIsDisposedOnContentChange() throws Exception {
        Shell s = new Shell();
        TreeViewer v = new TreeViewer(s);
        ITreeContentProvider cp = createFixture();
        v.setContentProvider(cp);
        WritableList input = WritableList.withElementType(String.class);
        v.setInput(input);
        v.setInput(null);
        assertTrue(input.isDisposed());
        s.close();
    }

    private ITreeContentProvider createFixture() {
        return new ObservableListTreeContentProvider(new IObservableFactory() {
            @Override
            public IObservable createObservable(Object target) {
                if (target instanceof IObservableList) {
                    return (IObservable) target;
                }
                return null;
            }
        }, new TreeStructureAdvisor() {
        });
    }
}
