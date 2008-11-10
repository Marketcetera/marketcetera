package org.rubypeople.rdt.ui.text.hyperlinks;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.jruby.ast.Node;

public interface IHyperlinkProvider {
	public IHyperlink getHyperlink(IEditorInput input,ITextViewer textViewer, Node node, IRegion region, boolean canShowMultipleHyperlinks);
}
