package org.marketcetera.photon.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CopyMessagesAction extends Action {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
	private final Table table;
	private final Clipboard clipboard;

	public CopyMessagesAction(Clipboard clipboard, Table table, String text) {
		super(text);
		this.clipboard = clipboard;
		this.table = table;
	}
	
	public void run() {
		TableItem[] selection = table.getSelection();
		table.getColumnCount();
		if (selection.length > 0){
			TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new Object[] {asText(selection, table.getColumnCount())},
					new Transfer[] { textTransfer});
		} else {
			clipboard.clearContents();
		}
	}

	public static String asText(TableItem[] selection, int numColumns) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < selection.length; i++) {
			buf.append(asText(selection[i], numColumns));
			buf.append(LINE_SEPARATOR);
		}
		return buf.toString();
	}

	private static String asText(TableItem item, int numColumns) {
		StringBuffer buffer = new StringBuffer();
		for (int i =0; i < numColumns; i++){
			String colText = item.getText(i);
			buffer.append(colText);
			if (i < numColumns-1){
				buffer.append('\t');
			}
		}
		return buffer.toString();
	}
}
