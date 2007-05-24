package org.marketcetera.photon.views;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXDataDictionary;

import quickfix.InvalidMessage;
import quickfix.Message;

public class FIXMessageDetailView extends ViewPart implements IFIXMessageDetail {

	private FormToolkit formToolkit;

	private ScrolledForm form = null;

	private Table messageTable;

	private TableViewer messageViewer;

	private FormToolkit getFormToolkit() {
		if (formToolkit == null) {
			formToolkit = new FormToolkit(Display.getCurrent());
		}
		return formToolkit;
	}

	private Composite getDefaultOuterParent() {
		return form.getBody();
	}

	@Override
	public void createPartControl(Composite parent) {
		createOuterForm(parent);
		createFieldTable();
		createMessageDetailComposite();

		createLayoutDataForWidgets();

		addMockMessage();
	}

	private void createLayoutDataForWidgets() {
		{
			FormData layoutData = new FormData();
			layoutData.left = new FormAttachment(0);
			layoutData.top = new FormAttachment(0);
			layoutData.right = new FormAttachment(100);
			layoutData.bottom = new FormAttachment(80);
			messageTable.setLayoutData(layoutData);
		}
	}

	@Override
	public void setFocus() {
	}

	private void createOuterForm(Composite parent) {
		form = getFormToolkit().createScrolledForm(parent);
		form.setLayout(new FillLayout());
		form.getBody().setLayout(new FormLayout());
	}

	private void createFieldTable() {
		messageTable = getFormToolkit().createTable(getDefaultOuterParent(),
				SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		messageTable.setHeaderVisible(true);
		messageViewer = new TableViewer(messageTable);
		getSite().setSelectionProvider(messageViewer);
		FIXMessageDetailLabelProvider labelProvider = new FIXMessageDetailLabelProvider(
				messageTable);
		messageViewer.setLabelProvider(labelProvider);
		messageViewer.setContentProvider(new FIXMessageDetailContentProvider());
	}
	
	private void createMessageDetailComposite() {

	}

	public void showMessage(Message fixMessage) {
		PhotonPlugin.getMainConsoleLogger().debug(
				getClass().getName() + ": " + fixMessage);
		messageViewer.setInput(fixMessage);
	}

	// This is temporary - this view is in development.
	private void addMockMessage() {
		final String messageAsStr = "8=FIX.4.29=26235=852=20070524-13:01:456=011=1180011613001-leprechaun/192.168.11.21814=017=1175740904625-toli-net-01/127.0.0.120=031=032=039=854=155=AMD58=This OMS was setup to work with FIX.4.2 and cannot handle a FIX.4.4 message.60=20070524-13:01:45150=8151=010=104";
		Message message = null;
		try {
			message = new Message(messageAsStr);
		} catch (InvalidMessage invalid) {
			// Do nothing
		}
		showMessage(message);
	}

	private static class FIXMessageDetailLabelProvider implements
			ITableLabelProvider {
		private Table underlyingTable;

		public FIXMessageDetailLabelProvider(Table underlyingTable) {
			this.underlyingTable = underlyingTable;
			createTableColumns();
		}

		private void createZeroFirstColumn() {
			TableColumn zeroFirstColumn = new TableColumn(underlyingTable,
					SWT.LEFT);
			zeroFirstColumn.setWidth(0);
			zeroFirstColumn.setResizable(false);
			zeroFirstColumn.setMoveable(false);
			zeroFirstColumn.setText("");
			zeroFirstColumn.setImage(null);
		}

		private void createTableColumns() {
			createZeroFirstColumn();
			FIXMessageDetailColumnType[] columns = FIXMessageDetailColumnType.values();
			for (FIXMessageDetailColumnType columnInfo : columns) {
				int alignment = SWT.CENTER;
				if (columnInfo.getIndex() == 0) {
					alignment = SWT.LEFT;
				} else if (columnInfo.getIndex() == columns.length - 1) {
					alignment = SWT.RIGHT;
				}
				TableColumn column = new TableColumn(underlyingTable, alignment);
				column.setText(columnInfo.getName());
				int width = getColumnWidth(columnInfo);
				column.setWidth(width);
			}
		}
		
		public int getColumnWidth(FIXMessageDetailColumnType columnInfo) {
			if(columnInfo == FIXMessageDetailColumnType.Field) {
				return getColumnWidthHint(20);
			} else if(columnInfo == FIXMessageDetailColumnType.Tag) {
				return getColumnWidthHint(10);
			} else if(columnInfo == FIXMessageDetailColumnType.Value) {
				return getColumnWidthHint(42);
			}
			return getColumnWidthHint(15);
		}
		
		private int getColumnWidthHint(int numChars) {
			return EclipseUtils.getTextAreaSize(underlyingTable, null, numChars, 1.0).x;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if(element == null || !(element instanceof FIXMessageDetailRow)) {
				return null;
			}
			FIXMessageDetailRow row = (FIXMessageDetailRow) element;
			return row.getColumnValue(columnIndex);
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}

	public enum FIXMessageDetailColumnType {
		// The declaration order must match the column index.
		// There is a hidden first column at index zero.
		Field("Field", 1), Tag("Tag", 2), Value("Value", 3);

		private String name;

		private int index;

		private FIXMessageDetailColumnType(String name, int index) {
			this.name = name;
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}
	};
	
	private static class FIXMessageDetailContentProvider implements
			IStructuredContentProvider {

		private FIXMessageDetailRow[] detailRows;
		
		public Object[] getElements(Object inputElement) {
			if(detailRows == null) {
				return new FIXMessageDetailRow[0];
			}
			return detailRows;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if(newInput == null || !(newInput instanceof Message)) {
				detailRows = null;
			}
			if(newInput == oldInput) {
				return;
			}
			Message newMessage = (Message) newInput;
			detailRows = createRowsFromMessage(newMessage);
		}
		
		private FIXMessageDetailRow[] createRowsFromMessage(Message fixMessage) {
			if(fixMessage == null) {
				return null;
			}
			ArrayList<FIXMessageDetailRow> rows = new ArrayList<FIXMessageDetailRow>();
			
			FIXDataDictionary fixDictionary = PhotonPlugin.getDefault().getFIXDataDictionary();
			final int maxFIXFields = 2000;// todo: FIXMessageUtil.getMaxFIXFields();
			for (int fieldInt = 1; fieldInt < maxFIXFields; fieldInt++){
				if(fixMessage.isSetField(fieldInt)){
					String fieldValueActual = null;
					try {
						fieldValueActual = fixMessage.getString(fieldInt);
					} catch(Exception anyException) {
						// Do nothing
					}
					String fieldValueReadable = null;
					if(fieldValueActual != null) {
						try {
							fieldValueReadable = fixDictionary.getHumanFieldValue(fieldInt, fieldValueActual);
						} catch(Exception anyException) {
							// Do nothing
						}
					}
					String fieldNumber = Integer.toString(fieldInt);
					String fieldName = fixDictionary.getHumanFieldName(fieldInt);
					
					String fieldValue = "";
					if(fieldValueActual != null) {
						fieldValue += fieldValueActual; 
					}
					if(fieldValueReadable != null) {
						fieldValue += " (" + fieldValueReadable + ")"; 
					}
					
					FIXMessageDetailRow newRow = new FIXMessageDetailRow(fieldName, fieldNumber, fieldValue);
					rows.add(newRow);
				}
			}
			
			return rows.toArray(new FIXMessageDetailRow[0]);
		}
	}
	
	private static class FIXMessageDetailRow {
		private String field;
		private String tag;
		private String value;
		
		public FIXMessageDetailRow(String field, String tag, String value) {
			super();
			this.field = field;
			this.tag = tag;
			this.value = value;
		}
		
		public String getColumnValue(int whichColumn) {
			if(whichColumn == 1) {
				return field;
			} else if(whichColumn == 2) {
				return tag;
			} else if(whichColumn == 3) {
				return value;
			}
			return null;
		}
		
		public String getColumnValue(FIXMessageDetailColumnType whichColumn) {
			if(whichColumn == FIXMessageDetailColumnType.Field) {
				return field;
			} else if(whichColumn == FIXMessageDetailColumnType.Tag) {
				return tag;
			} else if(whichColumn == FIXMessageDetailColumnType.Value) {
				return value;
			}
			return null;
		}
	}
}
