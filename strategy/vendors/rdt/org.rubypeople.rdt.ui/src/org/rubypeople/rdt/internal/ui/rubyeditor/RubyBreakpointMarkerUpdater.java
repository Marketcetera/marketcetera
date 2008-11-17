package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IMarkerUpdater;
import org.eclipse.ui.texteditor.MarkerUtilities;

public class RubyBreakpointMarkerUpdater implements IMarkerUpdater {

	private final static String[] ATTRIBUTES = { IMarker.LINE_NUMBER };

	public String getMarkerType() {
		return "org.rubypeople.rdt.debug.core.RubyBreakpointMarker";
	}

	public String[] getAttribute() {
		return ATTRIBUTES;
	}

	public boolean updateMarker(IMarker marker, IDocument document, Position position) {
		if (position == null) {
			return true;
		}

		if (position.isDeleted()) {
			return false;
		}

		try {
			// marker line numbers are 1-based
			MarkerUtilities.setLineNumber(marker, document.getLineOfOffset(position.getOffset()) + 1);
		} catch (BadLocationException x) {
		}

		return true;
	}

}
