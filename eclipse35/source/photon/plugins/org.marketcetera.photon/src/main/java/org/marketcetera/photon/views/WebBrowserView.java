package org.marketcetera.photon.views;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.RCPUtils;
import org.marketcetera.trade.MSymbol;

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class WebBrowserView
    extends ViewPart
    implements StatusTextListener, LocationListener, Messages
{

	public static final String ID = "org.marketcetera.photon.views.WebBrowserView"; //$NON-NLS-1$

	public static final MessageFormat GOOGLE_URL_FORMAT = new MessageFormat(
			"http://finance.google.com/finance?q={0}&client=Marketcetera+Photon"); //$NON-NLS-1$

	private Browser browser;

	private FormToolkit formToolkit;

	private Composite top;

	private Composite controlsComposite = null;

	private Button backButton = null;

	private Button forwardButton = null;

	private Text addressText = null;

	private Button goButton = null;

	private Label statusLabel = null;

	public WebBrowserView() {
		super();
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
        GridData statusLabelGridData = new GridData();
        statusLabelGridData.horizontalAlignment = GridData.FILL;
        statusLabelGridData.grabExcessHorizontalSpace = true;
        statusLabelGridData.horizontalIndent = 3;
        statusLabelGridData.verticalAlignment = GridData.CENTER;
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 5;
        gridLayout.marginHeight = 0;
        GridData browserGridData = new GridData();
        browserGridData.grabExcessHorizontalSpace = true;
        browserGridData.verticalAlignment = GridData.FILL;
        browserGridData.grabExcessVerticalSpace = true;
        browserGridData.horizontalAlignment = GridData.FILL;
        top = new Composite(parent, SWT.NONE);
        
		createControlsComposite();

		top.setLayout(gridLayout);
		browser = new Browser(top, SWT.NONE);
		browser.setLayoutData(browserGridData);
		statusLabel = new Label(top, SWT.NONE);
		statusLabel.setText(READY_LABEL.getText());
		statusLabel.setLayoutData(statusLabelGridData);
        
		browser.addStatusTextListener(this);
		browser.addLocationListener(this);
	}

	private void go()
	{
		String newLocation = addressText.getText();
		go(newLocation);
	}


	public void go(String newLocation) {
		try {
			new URL(newLocation);
			setUrl(newLocation);
		} catch (MalformedURLException e) {
			// maybe it's a symbol
			// just check to see if it's 10 characters or less
			if (newLocation.length()>0 && newLocation.length()<=10){
				browseToGoogleFinanceForSymbol(new MSymbol(newLocation));
			}
		}
	}

	private void setUrl(String newLocation) {
		try {
			browser.setUrl(newLocation);
		} catch (Exception ex) {
			String message = CANNOT_CONNECT_TO_URL.getText(newLocation);
			statusLabel.setText(RCPUtils.escapeAmpersands(message));
		}
	}

	public String formatGoogleURL(MSymbol symbol) {
		return GOOGLE_URL_FORMAT.format(new Object[] { symbol.getFullSymbol() });
	}

	public void browseToGoogleFinanceForSymbol(MSymbol symbol) {
		if (browser != null){
			String location = formatGoogleURL(symbol);
			setUrl(location);
		}
	}

	@Override
	public void setFocus() {
		if (browser != null)
			browser.setFocus();
	}

	/**
	 * This method initializes formToolkit	
	 * 	
	 * @return org.eclipse.ui.forms.widgets.FormToolkit	
	 */
	private FormToolkit getFormToolkit() {
		if (formToolkit == null) {
			formToolkit = new FormToolkit(Display.getCurrent());
		}
		return formToolkit;
	}



	/**
	 * This method initializes controlsComposite	
	 *
	 */
	private void createControlsComposite() {
		GridData controlsCompositeGridData = new GridData();
		controlsCompositeGridData.verticalAlignment = GridData.CENTER;
		controlsCompositeGridData.grabExcessHorizontalSpace = true;
		controlsCompositeGridData.horizontalAlignment = GridData.FILL;
		GridData addressTextGridData = new GridData();
		addressTextGridData.grabExcessHorizontalSpace = true;
		addressTextGridData.verticalAlignment = GridData.CENTER;
		addressTextGridData.horizontalAlignment = GridData.FILL;
		controlsComposite = new Composite(top,SWT.NONE);
		controlsComposite.setLayout(new GridLayout(4,false));
		controlsComposite.setLayoutData(controlsCompositeGridData);
		backButton = getFormToolkit().createButton(controlsComposite, null, SWT.PUSH|SWT.FLAT);
		backButton.setImage(PhotonPlugin.getImageDescriptor(IImageKeys.BROWSER_BACK).createImage());
		backButton.setEnabled(false);
		backButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				browser.back();
			}
		});
		forwardButton = getFormToolkit().createButton(controlsComposite, null, SWT.PUSH|SWT.FLAT);
		forwardButton.setImage(PhotonPlugin.getImageDescriptor(IImageKeys.BROWSER_FORWARD).createImage());
		forwardButton.setEnabled(false);
		forwardButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						browser.forward();
					}
				});
		addressText = getFormToolkit().createText(controlsComposite, null, SWT.SINGLE | SWT.BORDER);
		addressText.setLayoutData(addressTextGridData);
		addressText.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			public void keyReleased(org.eclipse.swt.events.KeyEvent e) {
				if (e.keyCode == SWT.CR){
					go();
				}
			}
		});
		goButton = getFormToolkit().createButton(controlsComposite, null, SWT.PUSH|SWT.FLAT);
		goButton.setImage(PhotonPlugin.getImageDescriptor(IImageKeys.BROWSER_GO).createImage());

		goButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				go();
			}
		});
	}



	public void changed(StatusTextEvent event) {
		statusLabel.setText(RCPUtils.escapeAmpersands(event.text));
		checkButtons();
	}



	public void changed(LocationEvent event) {
		addressText.setText(event.location);
		checkButtons();		
	}



	public void changing(LocationEvent event) {
		statusLabel.setText(RCPUtils.escapeAmpersands(event.location));
		checkButtons();		
	}

	private void checkButtons() {
		backButton.setEnabled(browser.isBackEnabled());
		forwardButton.setEnabled(browser.isForwardEnabled());
	}

}
