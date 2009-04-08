package org.marketcetera.photon.commons.ui.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.commons.ui.table.ChooseColumnsMenu.IColumnProvider;
import org.marketcetera.photon.test.SWTTestUtil;

/* $License$ */

/**
 * Test {@link ChooseColumnsMenu}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class ChooseColumnsMenuTest {

	private static final String MOCK_VIEW = "org.marketcetera.photon.commons.ui.table.ChooseColumnsMenuTest$MockTableView"; //$NON-NLS-1$

	private static final String RESTORED_WIDTH = "restoredWidth"; //$NON-NLS-1$

	private static ChooseColumnsMenuTestCase sCurrent = null;

	@Before
	public void setUp() {
		sCurrent = null;
	}

	@After
	public void tearDown() {
		sCurrent = null;
	}

	@Test
	public void withoutColumns() throws Exception {
		sCurrent = new ChooseColumnsMenuTestCase() {
			@Override
			protected Table createControl(Composite parent) {
				return new Table(parent, SWT.NONE);
			}

			@Override
			void validate(ChooseColumnsMenu fixture) {
				assertEquals(0, fixture.getContributionItems().length);
			}
		};
		sCurrent.run();
	}

	@Test
	public void oneColumn() throws Exception {
		final String columnName = "C1";
		final int columnWidth = 101;
		sCurrent = new ChooseColumnsMenuTestCase() {

			private TableColumn column1;

			@Override
			protected Table createControl(Composite parent) {
				final Table table = new Table(parent, SWT.NONE);
				column1 = new TableColumn(table, SWT.NONE);
				column1.setText(columnName);
				column1.setWidth(columnWidth);
				return table;
			}

			@Override
			void validate(ChooseColumnsMenu fixture) {
				// initial state
				IContributionItem[] menuItems = fixture.getContributionItems();
				assertEquals(1, menuItems.length);
				IAction action = ((ActionContributionItem) menuItems[0])
						.getAction();
				assertEquals(columnName, action.getText());
				assertTrue(action.isChecked());
				assertEquals(columnWidth, column1.getWidth());
				validateVisible();

				// simulate uncheck
				action.setChecked(false);
				action.run();
				SWTTestUtil.delay(100, TimeUnit.MILLISECONDS);
				validateHidden();

				// new state
				menuItems[0].dispose();
				menuItems = fixture.getContributionItems();
				assertEquals(1, menuItems.length);
				action = ((ActionContributionItem) menuItems[0]).getAction();
				assertEquals(columnName, action.getText());
				assertFalse(action.isChecked());

				// simulate check
				action.setChecked(true);
				action.run();
				validateVisible();
			}

			private void validateVisible() {
				assertEquals(columnWidth, column1.getWidth());
				assertTrue(column1.getResizable());
			}

			private void validateHidden() {
				assertEquals(columnWidth, column1.getData(RESTORED_WIDTH));
				assertEquals(0, column1.getWidth());
				assertFalse(column1.getResizable());
			}
		};
		sCurrent.run();
	}

	@Test
	public void withoutTreeColumns() throws Exception {
		sCurrent = new ChooseColumnsMenuTestCase() {
			@Override
			protected Tree createControl(Composite parent) {
				return new Tree(parent, SWT.NONE);
			}

			@Override
			void validate(ChooseColumnsMenu fixture) {
				assertEquals(0, fixture.getContributionItems().length);
			}
		};
		sCurrent.run();
	}

	@Test
	public void oneTreeColumn() throws Exception {
		final String columnName = "C1";
		final int columnWidth = 101;
		sCurrent = new ChooseColumnsMenuTestCase() {

			private TreeColumn column1;

			@Override
			protected Tree createControl(Composite parent) {
				final Tree tree = new Tree(parent, SWT.NONE);
				new TreeColumn(tree, SWT.NONE); //node column
				column1 = new TreeColumn(tree, SWT.NONE);
				column1.setText(columnName);
				column1.setWidth(columnWidth);
				return tree;
			}

			@Override
			void validate(ChooseColumnsMenu fixture) {
				// initial state
				IContributionItem[] menuItems = fixture.getContributionItems();
				assertEquals(1, menuItems.length);
				IAction action = ((ActionContributionItem) menuItems[0])
						.getAction();
				assertEquals(columnName, action.getText());
				assertTrue(action.isChecked());
				assertEquals(columnWidth, column1.getWidth());
				validateVisible();

				// simulate uncheck
				action.setChecked(false);
				action.run();
				SWTTestUtil.delay(100, TimeUnit.MILLISECONDS);
				validateHidden();

				// new state
				menuItems[0].dispose();
				menuItems = fixture.getContributionItems();
				assertEquals(1, menuItems.length);
				action = ((ActionContributionItem) menuItems[0]).getAction();
				assertEquals(columnName, action.getText());
				assertFalse(action.isChecked());

				// simulate check
				action.setChecked(true);
				action.run();
				validateVisible();
			}

			private void validateVisible() {
				assertEquals(columnWidth, column1.getWidth());
				assertTrue(column1.getResizable());
			}

			private void validateHidden() {
				assertEquals(columnWidth, column1.getData(RESTORED_WIDTH));
				assertEquals(0, column1.getWidth());
				assertFalse(column1.getResizable());
			}
		};
		sCurrent.run();
	}

	/**
	 * Helper class for test cases.
	 */
	private abstract class ChooseColumnsMenuTestCase {

		abstract Control createControl(Composite parent);

		void run() throws Exception {
			final IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			IViewPart part = activePage.showView(MOCK_VIEW);

			validate(new ChooseColumnsMenu());

			activePage.hideView(part);
		}

		abstract void validate(ChooseColumnsMenu fixture);

	}

	/**
	 * Dumb view to host table/tree for ChooseColumnMenu.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
	 */
	public static class MockColumnView extends ViewPart implements
			IColumnProvider {

		private Control mControl;

		@Override
		public void createPartControl(Composite parent) {
			mControl = sCurrent.createControl(parent);
		}

		@Override
		public void setFocus() {
			mControl.setFocus();
		}

		@Override
		public Control getColumnWidget() {
			return mControl;
		}

	}

}
