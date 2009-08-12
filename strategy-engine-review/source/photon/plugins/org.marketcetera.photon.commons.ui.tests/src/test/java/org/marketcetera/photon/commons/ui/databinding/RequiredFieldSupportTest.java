package org.marketcetera.photon.commons.ui.databinding;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.IViewerObservableList;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link RequiredFieldSupport}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class RequiredFieldSupportTest {

    private ApplicationWindow mWindow;

    @Before
    @UI
    public void before() {
        mWindow = new ApplicationWindow(null) {
            @Override
            protected Control createContents(Composite parent) {
                Composite c = new Composite(parent, SWT.NONE);
                Text text = new Text(c, SWT.NONE);
                IObservableValue value = SWTObservables.observeText(text,
                        SWT.Modify);
                DataBindingContext dbc = new DataBindingContext();
                RequiredFieldSupport.initFor(dbc, value, "Text");
                ListViewer list = new ListViewer(c);
                list.setContentProvider(new ArrayContentProvider());
                list.setInput(new Object[] { "item 1", "item 2" });
                IViewerObservableList set = ViewersObservables
                        .observeMultiSelection(list);
                RequiredFieldSupport.initFor(dbc, set, "list item");
                TableViewer table = new TableViewer(c);
                table.setContentProvider(new ArrayContentProvider());
                table.setInput(new Object[] { "item 1", "item 2" });
                IViewerObservableValue single = ViewersObservables
                        .observeSingleSelection(table);
                RequiredFieldSupport.initFor(dbc, single, "table item");
                GridLayoutFactory.swtDefaults().generateLayout(c);
                return c;
            }
        };
        mWindow.open();
    }

    @After
    @UI
    public void after() {
        mWindow.close();
    }

    @Test
    @UI
    public void testValidation() throws Exception {
        new ExpectedFailure<Exception>("context cannot be null") {
            @Override
            protected void run() throws Exception {
                RequiredFieldSupport.initFor(null, null, null);
            }
        };
        new ExpectedFailure<Exception>("target cannot be null") {
            @Override
            protected void run() throws Exception {
                RequiredFieldSupport.initFor(new DataBindingContext(), null,
                        null);
            }
        };
        new ExpectedFailure<Exception>("description cannot be null") {
            @Override
            protected void run() throws Exception {
                RequiredFieldSupport.initFor(new DataBindingContext(),
                        new WritableValue(), null);
            }
        };
        final Realm badRealm = new Realm() {
            public boolean isCurrent() {
                return false;
            };
        };
        new ExpectedFailure<Exception>(
                "must be called from the validation realm of context") {
            @Override
            protected void run() throws Exception {
                RequiredFieldSupport.initFor(new DataBindingContext(badRealm),
                        new WritableValue(), "");
            }
        };
        new ExpectedFailure<Exception>(
                "must be called from the realm of target") {
            @Override
            protected void run() throws Exception {
                RequiredFieldSupport.initFor(new DataBindingContext(),
                        new WritableValue(badRealm), "");
            }
        };
    }

    @Test
    public void testForSWTObservableValue() throws Throwable {
        SWTBot bot = new SWTBot();
        SWTBotText text = bot.text();
        SWTBotControlDecoration decoration = new SWTBotControlDecoration(text);
        final String message = getRequiredValueMessage("Text");
        decoration.assertRequired(message);
        text.setText("ABC");
        decoration.assertHidden();
        text.setText("");
        decoration.assertError(message);
    }

    @Test
    public void testForViewerObservableList() throws Throwable {
        SWTBot bot = new SWTBot();
        SWTBotList list = bot.list();
        SWTBotControlDecoration decoration = new SWTBotControlDecoration(list);
        final String message = getRequiredCollectionMessage("list item");
        decoration.assertRequired(message);
        list.select(0);
        decoration.assertHidden();
        list.select(new int[] { 0, 1 });
        decoration.assertHidden();
        list.unselect();
        decoration.assertError(message);
    }

    @Test
    public void testForViewerObservableValue() throws Throwable {
        SWTBot bot = new SWTBot();
        // workaround SWTBotTable bug (http://bugs.eclipse.org/285755)
        SWTBotTable table = new SWTBotTable(bot.table().widget) {
            @Override
            public void unselect() {
                super.unselect();
                notifySelect();
            }
        };
        SWTBotControlDecoration decoration = new SWTBotControlDecoration(table);
        final String message = getRequiredValueMessage("table item");
        decoration.assertRequired(message);
        table.select(0);
        decoration.assertHidden();
        table.select(0, 1);
        decoration.assertHidden();
        table.unselect();
        decoration.assertError(message);
    }

    /**
     * Returns the message use by {@link RequiredFieldSupport} for a missing
     * value.
     * 
     * @param description
     *            description of the missing value (same as description passed
     *            to
     *            {@link RequiredFieldSupport#initFor(DataBindingContext, org.eclipse.core.databinding.observable.IObservable, String)}
     * @return the formatted, localized message
     */
    public static String getRequiredValueMessage(String description) {
        return Messages.REQUIRED_FIELD_SUPPORT_MISSING_VALUE
                .getText(description);
    }

    /**
     * Returns the message use by {@link RequiredFieldSupport} for an empty
     * collection.
     * 
     * @param description
     *            description of the missing value (same as description passed
     *            to
     *            {@link RequiredFieldSupport#initFor(DataBindingContext, org.eclipse.core.databinding.observable.IObservable, String)}
     * @return the formatted, localized message
     */
    public static String getRequiredCollectionMessage(String description) {
        return Messages.REQUIRED_FIELD_SUPPORT_MISSING_COLLECTION
                .getText(description);
    }
}
