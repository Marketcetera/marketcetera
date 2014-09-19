package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Parent dialog class for report dialogs
 * 
 * @author milan
 *
 */
public class ReportDialog extends Dialog
{
	/** Empty value */
	protected static final String EMPTY_VALUE = ""; //$NON-NLS-1$
	
	/** Minimal size for the execution report table */
	protected static final int SIZE = 300;
	
	/** Custom description label */
	private final String fDescription;
	
	public ReportDialog(Shell parentShell, String description)
	{
		super(parentShell);
		fDescription = description;
	}

	@Override
	protected Control createContents(Composite parent)
	{
		// Initialization of the description label
		Composite labelComposite = createDescriptionLabel(parent);
		
		super.createContents(parent);
		
		return labelComposite;
	}
	
	/**
	 * Creates a description label
	 * 
	 * @param parent of type <code>Composite</code>
	 */
	protected Composite createDescriptionLabel(Composite parent)
	{
		Composite labelComposite = createComposite(parent, new GridData(GridData.FILL_HORIZONTAL));

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 10;
		gridLayout.marginLeft = 10;
		
		labelComposite.setLayout(gridLayout);

		Label descriptionLabel = new Label(labelComposite, SWT.WRAP);
		Font parentFont = parent.getFont();
		
		// Set font style
		FontData fontData = parentFont.getFontData()[0];
		fontData.setStyle(fontData.getStyle() | SWT.BOLD);
		
		descriptionLabel.setFont(new Font(parentFont.getDevice(), fontData));
		descriptionLabel.setText(fDescription);
		
		return labelComposite;
	}
	
	@Override
	protected boolean isResizable() 
	{
		return true;
	}
	
	/**
	 * Creates a default composite container
	 * 
	 * @param parent container of type <code>Composite</code>
	 * @return composite container of type <code>Composite</code>
	 */
	protected Composite createComposite(Composite parent, GridData gridData)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();

		composite.setLayoutData(gridData);
		composite.setLayout(gridLayout);

		return composite;
	}

}
