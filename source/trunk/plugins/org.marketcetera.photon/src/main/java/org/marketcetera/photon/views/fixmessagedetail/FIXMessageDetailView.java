package org.marketcetera.photon.views.fixmessagedetail;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.ui.TableComparatorChooser;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

public class FIXMessageDetailView extends ViewPart implements IFIXMessageDetail {

	public static final String ID = "org.marketcetera.photon.views.FIXMessageDetailView";

	private final int MESSAGE_TEXT_WIDTH_HINT = 80;

	private FormToolkit formToolkit;

	private ScrolledForm form = null;

	private Table messageTable;

	private IndexedTableViewer messageViewer;

	private Composite messageTextComposite;

	private Text messageText;

	private Clipboard clipboard;

	private TableComparatorChooser<FIXMessageDetailTableRow> comparatorChooser;

	private FIXMessageDetailLabelProvider labelProvider;

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

		createLayoutDataForOuterWidgets();

		clipboard = new Clipboard(getSite().getShell().getDisplay());
	}

	@Override
	public void dispose() {
		if (clipboard != null && !clipboard.isDisposed()) {
			clipboard.dispose();
		}
	}

	private void createLayoutDataForOuterWidgets() {
		{
			FormData layoutData = new FormData();
			layoutData.left = new FormAttachment(0);
			layoutData.top = new FormAttachment(0);
			layoutData.right = new FormAttachment(100);
			layoutData.bottom = new FormAttachment(75);
			messageTable.setLayoutData(layoutData);
		}
		{
			FormData layoutData = new FormData();
			layoutData.left = new FormAttachment(0);
			layoutData.top = new FormAttachment(messageTable);
			layoutData.right = new FormAttachment(100);
			layoutData.bottom = new FormAttachment(100);
			messageTextComposite.setLayoutData(layoutData);
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
		messageViewer = new IndexedTableViewer(messageTable);
		getSite().setSelectionProvider(messageViewer);
		labelProvider = new FIXMessageDetailLabelProvider(messageTable);
		messageViewer.setLabelProvider(labelProvider);
		messageViewer
				.setContentProvider(new EventListContentProvider<FIXMessageDetailTableRow>());
	}

	private Label messageDetailLabel;

	private void createMessageDetailComposite() {
		messageTextComposite = getFormToolkit().createComposite(
				getDefaultOuterParent(), SWT.BORDER);

		messageDetailLabel = new Label(messageTextComposite, SWT.WRAP);
		messageDetailLabel.setBackground(getDefaultOuterParent()
				.getBackground());
		messageDetailLabel.setText("Full message:");

		Button copyRawMessageButton = new Button(messageTextComposite, SWT.FLAT);
		copyRawMessageButton.setText("Copy Message");
		copyRawMessageButton
				.setToolTipText("Copy the raw FIX message below to the clipboard.");

		Button copyTableButton = new Button(messageTextComposite, SWT.FLAT);
		copyTableButton.setText("Copy Table");
		copyTableButton
				.setToolTipText("Copy the formatted table above to the clipboard.");

		messageText = getFormToolkit().createText(messageTextComposite, "",
				SWT.V_SCROLL | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		createLayoutDataForMessageDetailComposite(messageDetailLabel,
				copyRawMessageButton, copyTableButton);
		addMessageDetailCompositeListeners(copyRawMessageButton,
				copyTableButton);
	}

	private void createLayoutDataForMessageDetailComposite(
			Label fullMessageLabel, Button copyRawMessageButton,
			Button copyTableButton) {
		{
			FormLayout layout = new FormLayout();
			messageTextComposite.setLayout(layout);
		}
		{
			FormData data = new FormData();
			data.left = new FormAttachment(0);
			data.top = new FormAttachment(0);
			data.right = new FormAttachment(copyRawMessageButton);
			// Two rows of text are possible when there's a preferences link.
			data.height = 2 * EclipseUtils.getTextAreaSize(fullMessageLabel,
					null, 1, 1.2).y;
			fullMessageLabel.setLayoutData(data);
		}
		{
			FormData data = new FormData();
			data.right = new FormAttachment(copyTableButton);
			data.top = new FormAttachment(0);
			copyRawMessageButton.setLayoutData(data);
		}
		{
			FormData data = new FormData();
			data.right = new FormAttachment(100);
			data.top = new FormAttachment(0);
			copyTableButton.setLayoutData(data);
		}
		{
			FormData data = new FormData();
			data.left = new FormAttachment(0);
			data.right = new FormAttachment(100);
			data.bottom = new FormAttachment(100);
			data.top = new FormAttachment(fullMessageLabel);
			data.width = MESSAGE_TEXT_WIDTH_HINT;
			data.height = 40;
			messageText.setLayoutData(data);
		}
	}

	private void addMessageDetailCompositeListeners(
			Button copyRawMessageButton, Button copyTableButton) {
		copyRawMessageButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String textStr = messageText.getText();
				if (textStr != null && textStr.length() > 0) {
					messageText.selectAll();
					messageText.copy();
					messageText.setSelection(0, 0);
				}
			}
		});

		copyTableButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String textStr = getTableAsFormattedString();
				copyToClipboard(textStr);
			}
		});
	}

	private String getTableAsFormattedString() {
		TableItem[] tableItems = messageTable.getItems();
		if (tableItems == null) {
			return "";
		}
		StringBuilder tableAsString = new StringBuilder();
		for (TableItem tableItem : tableItems) {
			Object dataObj = tableItem.getData();
			if (dataObj instanceof FIXMessageDetailTableRow) {
				FIXMessageDetailTableRow row = (FIXMessageDetailTableRow) dataObj;
				String rowAsStr = row.toFormattedString();
				tableAsString.append(rowAsStr);
			}
		}
		return tableAsString.toString();
	}

	private void copyToClipboard(String textToCopy) {
		if (textToCopy == null) {
			return;
		}
		final Transfer[] dataTypes = { TextTransfer.getInstance() };
		Object[] data = { textToCopy };
		clipboard.setContents(data, dataTypes);
	}

	public void showMessage(Message fixMessage) {
		PhotonPlugin.getMainConsoleLogger().debug(
				getClass().getName() + ": " + fixMessage);

		ArrayList<FIXMessageDetailTableRow> rows = createRowsFromMessage(fixMessage);

		BasicEventList<FIXMessageDetailTableRow> eventList = new BasicEventList<FIXMessageDetailTableRow>();
		eventList.addAll(rows);
		SortedList<FIXMessageDetailTableRow> sortedList = new SortedList<FIXMessageDetailTableRow>(
				eventList);

		if (comparatorChooser != null) {
			comparatorChooser.removeSortIndicators();
			comparatorChooser.dispose();
			comparatorChooser = null;
		}
		comparatorChooser = new TableComparatorChooser<FIXMessageDetailTableRow>(
				messageTable, labelProvider, sortedList, false);
		// This code sets an initial sort order and is currently disabled.
		// final int initialSortIndex =
		// FIXMessageDetailColumnType.Tag.getIndex();
		// comparatorChooser.appendComparator(initialSortIndex, 0, false);
		// comparatorChooser.updateSortIndicatorIcon(initialSortIndex);

		messageViewer.setInput(sortedList);

		String messageTextStr = getFIXMessageDisplayString(fixMessage);
		messageText.setText(messageTextStr);
	}

	private String getHumanFieldValueSafely(FIXDataDictionary fixDictionary,
			int fieldNumber, String fieldValueActual) {
		String fieldValueReadable = null;
		if (fieldValueActual != null) {
			try {
				fieldValueReadable = fixDictionary.getHumanFieldValue(
						fieldNumber, fieldValueActual);
			} catch (Exception anyException) {
				// Do nothing
			}
		}
		return fieldValueReadable;
	}

	private ArrayList<FIXMessageDetailTableRow> createRowsFromMessage(
			Message fixMessage) {
		if (fixMessage == null) {
			return null;
		}

		ArrayList<FIXMessageDetailTableRow> rows = new ArrayList<FIXMessageDetailTableRow>();
		FIXDataDictionary fixDictionary = PhotonPlugin.getDefault()
				.getFIXDataDictionary();
		// Show all fields that are set on the message.
		for (int fieldNumber = 1; fieldNumber < FIXMessageUtil
				.getMaxFIXFields(); ++fieldNumber) {
			addFIXMessageDetailTableRow(fixMessage, fieldNumber, fixDictionary,
					rows);
		}

		return rows;
	}

	private void addFIXMessageDetailTableRow(Message fixMessage,
			int fieldNumber, FIXDataDictionary fixDictionary,
			ArrayList<FIXMessageDetailTableRow> rows) {

		if (fixMessage.isSetField(fieldNumber)) {
			String fieldValueActual = null;
			try {
				fieldValueActual = fixMessage.getString(fieldNumber);
			} catch (Exception anyException) {
				// Do nothing
			}
			String fieldValueReadable = getHumanFieldValueSafely(fixDictionary,
					fieldNumber, fieldValueActual);
			String fieldName = fixDictionary.getHumanFieldName(fieldNumber);

			boolean required = FIXMessageUtil.isRequiredField(fixMessage,
					fieldNumber);

			FIXMessageDetailTableRow newRow = new FIXMessageDetailTableRow(
					fieldName, fieldNumber, fieldValueActual,
					fieldValueReadable, required);
			rows.add(newRow);
		}
	}
	
	private String getFIXMessageDisplayString(Message fixMessage) {
		String messageTextStr = "";
		if (fixMessage != null) {
			String fixStr = fixMessage.toString();
			if (fixStr != null) {
				fixStr = fixStr.replace('\001', ' ');
			}
			messageTextStr = fixStr;
		}
		return messageTextStr;
	}
}
