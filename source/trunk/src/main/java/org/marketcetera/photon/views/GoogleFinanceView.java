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

public class GoogleFinanceView extends ViewPart {
	private static final String GOOGLE_BROWSER = "GOOGLE_BROWSER"; //$NON-NLS-1$

	public static String ID = "org.marketcetera.photon.views.GoogleFinanceView"; //$NON-NLS-1$

	public static MessageFormat GOOGLE_URL_FORMAT = new MessageFormat(
			"http://finance.google.com/finance?q={0}&client=Marketcetera+Photon+0.5"); //$NON-NLS-1$

	private Browser browser;

	private String location;

	private String DISABLE_SCRIPT = "var allForms, thisForm;"
			+ "allForms = document.getElementsByTagName('form');"
			+ "for (var i = 0; i < allForms.length; i++) {"
			+ "    thisForm = allForms[i];"
			+ "    if (thisForm.name == \"f\"){"
			+ "	 	thisForm.visibility = 'hidden';"
			+ "	 	thisForm.style.visibility = 'hidden';" + "	 }" + "}";


	public GoogleFinanceView() {
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
		browser = new Browser(parent, SWT.NONE);
		browser.addProgressListener(new ProgressListener() {
			public void completed(ProgressEvent event) {
				if (browser.getUrl().startsWith(
						"http://finance.google.com/finance?q="))
					browser.execute(DISABLE_SCRIPT);
			}

			public void changed(ProgressEvent event) {
			}

		});
		if (location != null) {
			browseTo(location);
		}
	}

	public String formatURL(String symbol) {
		return GOOGLE_URL_FORMAT.format(new Object[] { symbol });
	}

	public void browseTo(String symbol) {
		String theURL = formatURL(symbol);
		browser.setUrl(theURL);
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}


	public void goToSecurityHome() {
		if (!browser.getUrl().equals(formatURL(location))) {
			browseTo(location);
		}
	}

}
