package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.corext.util.RDocUtil;
import org.rubypeople.rdt.internal.ui.text.HTMLPrinter;
import org.rubypeople.rdt.internal.ui.text.HTMLTextPresenter;
import org.rubypeople.rdt.internal.ui.text.ruby.IInformationControlExtension4;

public class CommentHoverProvider extends AbstractRubyEditorTextHover implements IInformationProviderExtension2 {

	/**
	 * The hover control creator.
	 * 
	 * @since 1.0
	 */
	private IInformationControlCreator fHoverControlCreator;
	/**
	 * The presentation control creator.
	 * 
	 * @since 1.0
	 */
	private IInformationControlCreator fPresenterControlCreator;
	
	/*
	 * @see RubyElementHover
	 */
	protected String getHoverInfo(IRubyElement[] result) {

		StringBuffer buffer= new StringBuffer();
		String contents = RDocUtil.getHTMLDocumentation(result);
		if (contents != null)
			buffer.append(contents);

		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}

		return null;
	}

	/*
	 * @see IInformationProviderExtension2#getInformationPresenterControlCreator()
	 * @since 1.0
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null) {
			fPresenterControlCreator= new AbstractReusableInformationControlCreator() {

				/*
				 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
				 */
				public IInformationControl doCreateInformationControl(Shell parent) {
					int shellStyle= SWT.RESIZE | SWT.TOOL;
					int style= SWT.V_SCROLL | SWT.H_SCROLL;
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent, shellStyle, style);
					else
						return new DefaultInformationControl(parent, shellStyle, style, new HTMLTextPresenter(false));
				}
			};
		}
		return fPresenterControlCreator;
	}
	
	/*
	 * @see ITextHoverExtension#getHoverControlCreator()
	 * @since 1.0
	 */
	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null) {
			fHoverControlCreator= new AbstractReusableInformationControlCreator() {
				
				/*
				 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
				 */
				public IInformationControl doCreateInformationControl(Shell parent) {
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent, SWT.TOOL | SWT.NO_TRIM, SWT.NONE, getTooltipAffordanceString());
					else
						return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true), getTooltipAffordanceString());
				}
				
				/*
				 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#canReuse(org.eclipse.jface.text.IInformationControl)
				 */
				public boolean canReuse(IInformationControl control) {
					boolean canReuse= super.canReuse(control);
					if (canReuse && control instanceof IInformationControlExtension4)
						((IInformationControlExtension4)control).setStatusText(getTooltipAffordanceString());
					return canReuse;
						
				}
			};
		}
		return fHoverControlCreator;
	}
}