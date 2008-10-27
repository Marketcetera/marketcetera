package org.marketcetera.photon.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class ChooseColumnsMenu extends CompoundContributionItem {

	private static final int DEFAULT_WIDTH = 100;
	private static final String RESTORED_WIDTH = "restoredWidth"; //$NON-NLS-1$

	/**
	 * Interface for objects that have a table.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	public interface ITableProvider {

		/**
		 * @return this object's table
		 */
		Table getTable();
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		IWorkbenchPart part = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (part instanceof ITableProvider) {
			Table table = ((ITableProvider) part).getTable();
			if (table != null) {
				final int columnCount = table.getColumnCount();
				if (columnCount > 0) {
					final IContributionItem[] items = new IContributionItem[columnCount];
					Action action;
					for (int i = 0; i < columnCount; i++) {
						final TableColumn column = table.getColumn(table
								.getColumnOrder()[i]);
						action = new Action(column.getText(), SWT.CHECK) {
							public void run() {
								if (!isChecked()) {
									column.setData(RESTORED_WIDTH, column
											.getWidth());
									column.setResizable(false);
									column.setWidth(0);
								} else {
									column.setResizable(true);
									final Object restoredWidth = column
											.getData(RESTORED_WIDTH);
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
			}
		}
		return new IContributionItem[] {};
	}
}