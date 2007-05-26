/**
 * 
 */
package org.marketcetera.photon.views.fixmessagedetail;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;

class FIXMessageDetailContentProvider implements IStructuredContentProvider {

	private FIXMessageDetailTableRow[] detailRows;

	public Object[] getElements(Object inputElement) {
		if (detailRows == null) {
			return new FIXMessageDetailTableRow[0];
		}
		return detailRows;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput == null || !(newInput instanceof Message)) {
			detailRows = null;
		}
		if (newInput == oldInput) {
			return;
		}
		Message newMessage = (Message) newInput;
		detailRows = createRowsFromMessage(newMessage);
	}

	private FIXMessageDetailTableRow[] createRowsFromMessage(Message fixMessage) {
		if (fixMessage == null) {
			return null;
		}
		ArrayList<FIXMessageDetailTableRow> rows = new ArrayList<FIXMessageDetailTableRow>();

		FIXDataDictionary fixDictionary = PhotonPlugin.getDefault()
				.getFIXDataDictionary();
		final int maxFIXFields = FIXMessageUtil.getMaxFIXFields();
		for (int fieldInt = 1; fieldInt < maxFIXFields; fieldInt++) {
			if (fixMessage.isSetField(fieldInt)) {
				String fieldValueActual = null;
				try {
					fieldValueActual = fixMessage.getString(fieldInt);
				} catch (Exception anyException) {
					// Do nothing
				}
				String fieldValueReadable = null;
				if (fieldValueActual != null) {
					try {
						fieldValueReadable = fixDictionary.getHumanFieldValue(
								fieldInt, fieldValueActual);
					} catch (Exception anyException) {
						// Do nothing
					}
				}
				String fieldNumber = Integer.toString(fieldInt);
				String fieldName = fixDictionary.getHumanFieldName(fieldInt);

				boolean required = FIXMessageUtil.isRequiredField(fixMessage,
						fieldInt);

				FIXMessageDetailTableRow newRow = new FIXMessageDetailTableRow(
						fieldName, fieldNumber, fieldValueActual,
						fieldValueReadable, required);
				rows.add(newRow);
			}
		}

		return rows.toArray(new FIXMessageDetailTableRow[0]);
	}
}