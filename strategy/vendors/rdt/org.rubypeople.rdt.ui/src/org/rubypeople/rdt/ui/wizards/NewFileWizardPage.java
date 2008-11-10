package org.rubypeople.rdt.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.codemanipulation.StubUtility;
import org.rubypeople.rdt.internal.ui.dialogs.TextFieldNavigationHandler;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.Separator;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringDialogField;

public class NewFileWizardPage extends NewContainerWizardPage {

	private StringDialogField fScriptNameDialogField;
	private IRubyScript fCreatedScript;
	
	private final static String PAGE_NAME= "NewFileWizardPage"; //$NON-NLS-1$
	
	public NewFileWizardPage() {
		super(PAGE_NAME);
		
		setTitle(NewWizardMessages.NewFileWizardPage_title); 
		setDescription(NewWizardMessages.NewFileWizardPage_description); 
		
		fScriptNameDialogField= new StringDialogField();
//		fScriptNameDialogField.setDialogFieldListener(adapter);
		fScriptNameDialogField.setLabelText(getScriptNameLabel()); 
		fScriptNameDialogField.setText(getDefaultScriptName());
	}
	
	private String getDefaultScriptName() {		
		return "file.rb";// TODO Translate?
	}

	/**
	 * Returns the label that is used for the script name input field.
	 * 
	 * @return the label that is used for the script name input field.
	 * @since 3.2
	 */
	protected String getScriptNameLabel() {
		return NewWizardMessages.NewFileWizardPage_scriptname_label;
	}

	public void createScript(IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}

		monitor.beginTask(NewWizardMessages.NewTypeWizardPage_operationdesc, 8); 
		
		ISourceFolder pack= getSourceFolder();

		monitor.worked(1);
		
		try {				
			String lineDelimiter= StubUtility.getLineDelimiterUsed(pack.getRubyProject());
				
			String cuName= getRubyScriptName();
			String contents = "if __FILE__ == $0" + lineDelimiter + "  # TODO Generated stub" + lineDelimiter  + "end";
			fCreatedScript = pack.createRubyScript(cuName, contents, false, new SubProgressMonitor(monitor, 2)); //$NON-NLS-1$
		} finally {

			monitor.done();
		}
		
	}

	/**
	 * Returns the resource handle that corresponds to the ruby script that was or
	 * will be created or modified.
	 * @return A resource or null if the page contains illegal values.
	 * @since 1.0
	 */
	public IResource getModifiedResource() {
		ISourceFolder pack= getSourceFolder();
		if (pack != null) {
			String cuName= getRubyScriptName();
			return pack.getRubyScript(cuName).getResource();
		}
		return null;
	}
	
	/**
	 * Returns the type name entered into the type input field.
	 * 
	 * @return the type name
	 */
	public String getRubyScriptName() {
		return fScriptNameDialogField.getText();
	}

	public IRubyElement getCreatedScript() {
		return fCreatedScript;
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		
		int nColumns= 4;
		
		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;		
		composite.setLayout(layout);
		
		// pick & choose the wanted UI components
		createContainerControls(composite, nColumns);					
		createSeparator(composite, nColumns);
		createScriptNameControls(composite, nColumns);
		
		setControl(composite);
			
		Dialog.applyDialogFont(composite);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IRubyHelpContextIds.NEW_FILE_WIZARD_PAGE);			
	}
	
	/**
	 * Creates the controls for the type name field. Expects a <code>GridLayout</code> with at 
	 * least 2 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createScriptNameControls(Composite composite, int nColumns) {
		fScriptNameDialogField.doFillIntoGrid(composite, nColumns - 1);
		DialogField.createEmptySpace(composite);
		
		Text text= fScriptNameDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		TextFieldNavigationHandler.install(text);
	}	
	
	/**
	 * Creates a separator line. Expects a <code>GridLayout</code> with at least 1 column.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createSeparator(Composite composite, int nColumns) {
		(new Separator(SWT.SEPARATOR | SWT.HORIZONTAL)).doFillIntoGrid(composite, nColumns, convertHeightInCharsToPixels(1));		
	}
	
	/**
	 * The wizard owning this page is responsible for calling this method with the
	 * current selection. The selection is used to initialize the fields of the wizard 
	 * page.
	 * 
	 * @param selection used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		IRubyElement jelem= getInitialRubyElement(selection);
		initContainerPage(jelem);
//		doStatusUpdate();
	}

}
