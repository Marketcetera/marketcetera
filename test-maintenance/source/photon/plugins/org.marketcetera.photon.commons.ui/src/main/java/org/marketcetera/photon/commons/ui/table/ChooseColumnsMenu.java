package org.marketcetera.photon.commons.ui.table;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Dynamic menu that allows columns to be added or removed.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ChooseColumnsMenu extends CompoundContributionItem {

	private static final int DEFAULT_WIDTH = 100;
	private static final String RESTORED_WIDTH = "restoredWidth"; //$NON-NLS-1$

	/**
	 * Interface for objects that have a table.
	 */
	@ClassVersion("$Id$")
	public interface IColumnProvider {

		/**
		 * @return the widget that contains the columns, either a {@link Table} or a {@link Tree}.
		 */
		Control getColumnWidget();
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart();
		if (part instanceof IColumnProvider) {
			Control control = ((IColumnProvider) part).getColumnWidget();
			if (control instanceof Table) {
				Table table = (Table) control;
				final int columnCount = table.getColumnCount();
				if (columnCount > 0) {
					final IContributionItem[] items = new IContributionItem[columnCount];
					Action action;
					for (int i = 0; i < columnCount; i++) {
						final TableColumn column = table.getColumn(table.getColumnOrder()[i]);
						action = new Action(column.getText(), SWT.CHECK) {
							public void run() {
								if (!isChecked()) {
									column.setData(RESTORED_WIDTH, column.getWidth());
									column.setResizable(false);
									column.setWidth(0);
								} else {
									column.setResizable(true);
									final Object restoredWidth = column.getData(RESTORED_WIDTH);
									int width = (restoredWidth instanceof Integer) ? (Integer) restoredWidth
											: DEFAULT_WIDTH;
									column.setWidth(width);
								}
							}
						};
						action.setChecked(column.getWidth() != 0);
						items[i] = new ActionContributionItem(action);
					}
					return items;
				}
			} else if (control instanceof Tree) {
				Tree tree = (Tree) control;
				final int columnCount = tree.getColumnCount();
				if (columnCount > 0) {
					// skip the first column, assuming the tree node column should not be hidden
					final IContributionItem[] items = new IContributionItem[columnCount-1];
					Action action;
					for (int i = 1; i < columnCount; i++) {
						final TreeColumn column = tree.getColumn(tree.getColumnOrder()[i]);
						action = new Action(column.getText(), SWT.CHECK) {
							public void run() {
								if (!isChecked()) {
									column.setData(RESTORED_WIDTH, column.getWidth());
									column.setResizable(false);
									column.setWidth(0);
								} else {
									column.setResizable(true);
									final Object restoredWidth = column.getData(RESTORED_WIDTH);
									int width = (restoredWidth instanceof Integer) ? (Integer) restoredWidth
											: DEFAULT_WIDTH;
									column.setWidth(width);
								}
							}
						};
						action.setChecked(column.getWidth() != 0);
						items[i-1] = new ActionContributionItem(action);
					}
					return items;
				}
			}
		}
		return new IContributionItem[] {};
	}
}