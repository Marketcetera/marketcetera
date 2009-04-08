/*******************************************************************************
 * Copyright (c) 2004, 2005 Jean-Michel Lemieux, Jeff McAffer and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Hyperbola is an RCP application developed for the book 
 *     Eclipse Rich Client Platform - 
 *         Designing, Coding, and Packaging Java Applications 
 *
 * Contributors:
 *     Jean-Michel Lemieux and Jeff McAffer - initial implementation
 *******************************************************************************/
package org.marketcetera.photon.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.actions.ConnectionDetails;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Login dialog, which prompts for the user's account info, and has Login and
 * Cancel buttons.
 */
public class LoginDialog
    extends Dialog
    implements Messages
{

    private static final String ORS_URL = "href=\"http://www.marketcetera.com/masha/docs?version=1.5.0&qualifier=authentication\""; //$NON-NLS-1$

    private Combo userIdText;

	private Text passwordText;

	private ConnectionDetails connectionDetails;

	private HashMap<String,ConnectionDetails> savedDetails = new HashMap<String,ConnectionDetails>();

	private Image[] images;

	private static final String SAVED = "saved-connections"; //$NON-NLS-1$
	private static final String LAST_USER = "prefs_last_connection"; //$NON-NLS-1$
	
	private Shell mShell;

	public LoginDialog(Shell parentShell) {
		super(parentShell);
		loadDescriptors();
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
        mShell = newShell;
		newShell.setText(ORS_LOGIN_LABEL.getText());
		// load the image from the product definition
		IProduct product = Platform.getProduct();
		if (product != null) {
			String[] imageURLs = parseCSL(product
					.getProperty(IProductConstants.WINDOW_IMAGES));
			if (imageURLs.length > 0) {
				images = new Image[imageURLs.length];
				for (int i = 0; i < imageURLs.length; i++) {
					String url = imageURLs[i];
					ImageDescriptor descriptor = AbstractUIPlugin
							.imageDescriptorFromPlugin(product
									.getDefiningBundle().getSymbolicName(), url);
					images[i] = descriptor.createImage(true);
				}
				newShell.setImages(images);
			}
		}
	}
	
	public static String[] parseCSL(String csl) {
		if (csl == null)
			return null;

		StringTokenizer tokens = new StringTokenizer(csl, ","); //$NON-NLS-1$
		ArrayList<String> array = new ArrayList<String>(10);
		while (tokens.hasMoreTokens())
			array.add(tokens.nextToken().trim());

		return (String[]) array.toArray(new String[array.size()]);
	}

	public boolean close() {
		if (images != null) {
			for (int i = 0; i < images.length; i++)
				images[i].dispose();
		}
		return super.close();
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Label accountLabel = new Label(composite, SWT.NONE);
		accountLabel.setText(ACCOUNT_DETAILS_LABEL.getText());
		accountLabel.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.CENTER, false, false, 2, 1));

		Label userIdLabel = new Label(composite, SWT.NONE);
		userIdLabel.setText(MENU_USER_ID_LABEL.getText());
		userIdLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		userIdText = new Combo(composite, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				false);
		gridData.widthHint = convertHeightInCharsToPixels(20);
		userIdText.setLayoutData(gridData);
		userIdText.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				ConnectionDetails d = (ConnectionDetails) savedDetails
						.get(userIdText.getText());
				if (d != null) {
					passwordText.setText(d.getPassword());
				}
			}
		});

		Label passwordLabel = new Label(composite, SWT.NONE);
		passwordLabel.setText(MENU_PASSWORD_LABEL.getText());
		passwordLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		String lastUser = "admin"; //$NON-NLS-1$
		if (connectionDetails != null)
			lastUser = connectionDetails.getUserId();
		initializeUsers(lastUser);
		Link link = new Link(mShell, 
		                     SWT.BORDER);
		link.setText(ORS_LOGIN_HELP_URL.getText(ORS_URL));
		link.addListener(SWT.Selection, 
		                 new Listener() {
            public void handleEvent(Event event) 
            {
                // execute the native action associated with a URL
                Program.launch(event.text);
            }
        });
		
		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) 
	{
		Button removeCurrentUser = createButton(parent,
		                                        IDialogConstants.CLIENT_ID,
		                                        MENU_CLEAR_LABEL.getText(),
		                                        false);
		removeCurrentUser.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				savedDetails.remove(userIdText.getText());
				initializeUsers(""); //$NON-NLS-1$
			}
		});
		createButton(parent,
		             IDialogConstants.OK_ID,
		             MENU_LOGIN_LABEL.getText(),
		             true);
		createButton(parent,
		             IDialogConstants.CANCEL_ID,
		             IDialogConstants.CANCEL_LABEL,
		             false);
	}

	protected void initializeUsers(String defaultUser) {
		userIdText.removeAll();
		passwordText.setText(""); //$NON-NLS-1$
		for (Iterator<String> it = savedDetails.keySet().iterator(); it.hasNext();)
			userIdText.add(it.next());
		int index = Math.max(userIdText.indexOf(defaultUser), 0);
		userIdText.select(index);
	}

	protected void okPressed() {
		if (connectionDetails.getUserId().equals("")) { //$NON-NLS-1$
			MessageDialog.openError(getShell(),
			                        INVALID_USER_ID.getText(),
			                        USER_ID_MUST_NOT_BE_BLANK.getText());
			return;
		}
		super.okPressed();
	}

	protected void buttonPressed(int buttonId) {
		String userId = userIdText.getText();
		String password = passwordText.getText();
		connectionDetails = new ConnectionDetails(userId, password);
		savedDetails.put(userId, connectionDetails);
		if (buttonId == IDialogConstants.OK_ID
				|| buttonId == IDialogConstants.CANCEL_ID)
			saveDescriptors();
		super.buttonPressed(buttonId);
	}

	public void saveDescriptors() {
		Preferences preferences = new ConfigurationScope()
				.getNode(PhotonPlugin.ID);
		preferences.put(LAST_USER, connectionDetails.getUserId());
		Preferences connections = preferences.node(SAVED);
		for (Iterator<String> it = savedDetails.keySet().iterator(); it.hasNext();) {
			String name = it.next();
			ConnectionDetails d = (ConnectionDetails) savedDetails.get(name);
			Preferences connection = connections.node(name);
			connection.put(PASSWORD_LABEL.getText(),
			               d.getPassword());
		}
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

	}

	private void loadDescriptors() {
		try {
			Preferences preferences = new ConfigurationScope()
					.getNode(PhotonPlugin.ID);
			Preferences connections = preferences.node(SAVED);
			String[] userNames = connections.childrenNames();
			for (int i = 0; i < userNames.length; i++) {
				String userName = userNames[i];
				Preferences node = connections.node(userName);
				savedDetails.put(userName,
				                 new ConnectionDetails(userName,
				                                       node.get(PASSWORD_LABEL.getText(),
				                                                ""))); //$NON-NLS-1$
			}
			connectionDetails = (ConnectionDetails) savedDetails
					.get(preferences.get(LAST_USER, "")); //$NON-NLS-1$
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the connection details entered by the user, or <code>null</code>
	 * if the dialog was canceled.
	 */
	public ConnectionDetails getConnectionDetails() {
		return connectionDetails;
	}
}
