package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.actions.RemoveExecutionFieldAction;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.providers.ExecutionReportComparator;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.providers.ExecutionReportContentProvider;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.providers.ExecutionReportLabelProvider;

/**
 * Execution report table
 * 
 * @author milan
 *
 */
public class ExecutionReportViewer
{
	/** Parent dialog*/
	private final AddExecutionReportDialog fParentDialog;
	
	/** Execution report table */
	private TableViewer fExecutionReportViewer;
	
	public ExecutionReportViewer(AddExecutionReportDialog parentDialog)
	{
		fParentDialog = parentDialog;
	}
	
	public void createViewer(Composite parent, Object dataModel)
	{
		TableColumnLayout columnLayout = new TableColumnLayout();
		parent.setLayout(columnLayout);

		fExecutionReportViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		
		// Context menu
		registerContextMenu(fExecutionReportViewer);
		
		Table table = fExecutionReportViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		
		createColumn(Messages.ADD_EXECUTION_REPORT_DIALOG_TABLE_COLUMN_FIELD.getText(), fExecutionReportViewer, columnLayout, 20);
		createColumn(Messages.ADD_EXECUTION_REPORT_DIALOG_TABLE_COLUMN_VALUE.getText(), fExecutionReportViewer, columnLayout, 20);

		fExecutionReportViewer.setLabelProvider(new ExecutionReportLabelProvider());
		fExecutionReportViewer.setContentProvider(new ExecutionReportContentProvider());
		fExecutionReportViewer.setComparator(new ExecutionReportComparator());
		fExecutionReportViewer.setInput(dataModel);
	}

	/**
	 * Register the context menu for the viewer so that commands may be added to
	 * it.
	 */
	private void registerContextMenu(TableViewer tableViewer) 
	{
		MenuManager contextMenu = new MenuManager();
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new ContextMenuListener(contextMenu));		
		Table table = tableViewer.getTable();
		Menu menu = contextMenu.createContextMenu(table);
		table.setMenu(menu);
	}

	/**
	 * Fill context menu for the Reports table
	 * 
	 * @param contextMenu of type <code></code>
	 */
	private void fillContextMenu(MenuManager contextMenu) 
	{
		// Remove execution field action
		IAction cancelOrderAction = new RemoveExecutionFieldAction(fExecutionReportViewer, fParentDialog);
		contextMenu.add(cancelOrderAction);		
	}
	
	/**
	 * Creates a default TableViewer Column and appends it to
	 * <code>tableViewer</code>
	 * 
	 * @param columnDescription of type <code>String</code>
	 * @param tableViewer of type <code>TableViewer</code>
	 * @param columnLayout of type <code>TableColumnLayout</code>
	 * @param width of type <code>int</code>
	 * 
	 * @return created table column of type <code>TableColumn</code>
	 */
	protected TableColumn createColumn(String columnDescription, TableViewer tableViewer, TableColumnLayout columnLayout, int width)
	{
		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
		final TableColumn tableColumn = viewerColumn.getColumn();
		tableColumn.setText(columnDescription);
		columnLayout.setColumnData(tableColumn, new ColumnWeightData(width, width));
		
		return tableColumn;
	}
	
	/**
	 * Refresh table data
	 */
	public void refreshData()
	{
		fExecutionReportViewer.refresh();
	}

	/**
	 * Context menu listener class
	 * 
	 * @author milan
	 *
	 */
	private class ContextMenuListener implements IMenuListener
	{
		private MenuManager fContextMenu;
		
		public ContextMenuListener(MenuManager contextMenu) 
		{
			fContextMenu = contextMenu;
		}
		
		@Override
		public void menuAboutToShow(IMenuManager manager) 
		{
			if(fExecutionReportViewer.getTable().getSelectionCount() == 0)
				return;
			
			fillContextMenu(fContextMenu);
		}	
	}

	public void update() 
	{
		fExecutionReportViewer.refresh();
	}

	public void addSelectionChangedListener(
			ISelectionChangedListener iSelectionChangedListener) {
		fExecutionReportViewer.addSelectionChangedListener(iSelectionChangedListener);
	}
}
