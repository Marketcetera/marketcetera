package org.marketcetera.photon.views;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class WebBrowserView extends ViewPart {

	public static String ID = "org.marketcetera.photon.views.WebBrowserView"; //$NON-NLS-1$

	public static MessageFormat GOOGLE_URL_FORMAT = new MessageFormat(
			"http://finance.google.com/finance?q={0}&client=Marketcetera+Photon"); //$NON-NLS-1$

	private Browser browser;

	private String location;

	public WebBrowserView() {
		super();
		// TODO Auto-generated constructor stub
	}


	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite)
	 */
	@Override
	public void init(IViewSite site) throws PartInitException {
		setSite(site);
	}



	@Override
	public void createPartControl(Composite parent) {
		try {
		browser = new Browser(parent, SWT.NONE);
		if (location != null) {
			browser.setUrl(location);
		}
		} catch (Throwable th){
			th.printStackTrace();
		}
	}

	public String formatGoogleURL(String symbol) {
		return GOOGLE_URL_FORMAT.format(new Object[] { symbol });
	}

	public void browseToGoogleFinanceForSymbol(String symbol) {
		if (browser != null){
			location = formatGoogleURL(symbol);
			browser.setUrl(location);
		}
	}

	@Override
	public void setFocus() {
		if (browser != null)
			browser.setFocus();
	}



}
