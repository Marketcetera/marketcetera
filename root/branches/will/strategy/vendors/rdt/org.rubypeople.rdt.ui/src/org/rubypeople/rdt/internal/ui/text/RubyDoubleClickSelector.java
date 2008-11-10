package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;

public class RubyDoubleClickSelector implements ITextDoubleClickStrategy {

    public void doubleClicked(ITextViewer text) {
        int position = text.getSelectedRange().x;
        if (position < 0) return;

        IRegion region = RubyWordFinder.findWord(text.getDocument(), position);
        if (region != null && region.getLength() != 0 ) text.setSelectedRange(region.getOffset(), region.getLength());
    }
}