package org.rubypeople.rdt.internal.ui.text.hyperlinks;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.util.Util;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.OpenActionUtil;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.internal.ui.text.RubyWordFinder;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.text.hyperlinks.IHyperlinkProvider;

public class RubyElementsHyperlinkProvider implements IHyperlinkProvider {

	public RubyElementsHyperlinkProvider() {}
	
	class LazyRubyHyperlink implements IHyperlink {
		private IRegion fRegion;
		private IRubyScript script;

		public LazyRubyHyperlink(IRubyScript script, IRegion region) {
			fRegion = region;
			this.script = script;
		}

		public IRegion getHyperlinkRegion() {
			return fRegion;
		}

		public String getHyperlinkText() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getTypeLabel() {
			return "ruby";
		}

		public void open() {
			try {
				IRubyElement[] elements = SelectionConverter.codeResolve(script, fRegion.getOffset(), fRegion.getLength());
				if (elements != null && elements.length > 0) {	
					if (elements.length > 1) {
					IRubyElement selected = OpenActionUtil.selectRubyElement(elements, Display.getDefault().getActiveShell(), "Please select instance", "Please select which definition you woudl like to open.");
					if (selected != null)
						OpenActionUtil.open(selected, true);
					} else {
						OpenActionUtil.open(elements[0], true);
					}
				}
			} catch (PartInitException e) {
				RubyPlugin.log(e);
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
		}
	}

	public IHyperlink getHyperlink(IEditorInput input, ITextViewer textViewer, Node node, IRegion region, boolean canShowMultipleHyperlinks) {
		if (!inCode(textViewer, region)) return null;
		IRegion newRegion = RubyWordFinder.findWord(textViewer.getDocument(), region.getOffset());
		try {
			IWorkingCopyManager manager = RubyPlugin.getDefault().getWorkingCopyManager();
			IRubyScript script = manager.getWorkingCopy(input);
			if (script != null) {
				// Check the "word" and if it's a keyword or operator then don't offer a link
				String contents = textViewer.getDocument().get(newRegion.getOffset(), newRegion.getLength());
				if (Util.isOperator(contents) || Util.isKeyword(contents)) {
					return null;
				}				
				return new LazyRubyHyperlink(script, newRegion);
			}
		} catch (Exception e) {
			RubyPlugin.log(e);
		}
		return null;
	}

	private boolean inCode(ITextViewer textViewer, IRegion region) {
		try {
			ITypedRegion[] regions= TextUtilities.computePartitioning(textViewer.getDocument(), IRubyPartitions.RUBY_PARTITIONING, region.getOffset(), region.getLength(), false);
			if (regions == null) return false;
			for (int i = 0; i < regions.length; i++) {
				String type = regions[i].getType();
				if (type.equals(IDocument.DEFAULT_CONTENT_TYPE)) {
					return true;
				}
			}
		} catch (BadLocationException e1) {
			// ignore
		}
		return false;
	}
}