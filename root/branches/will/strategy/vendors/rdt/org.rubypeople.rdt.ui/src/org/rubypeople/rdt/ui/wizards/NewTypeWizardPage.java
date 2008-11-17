package org.rubypeople.rdt.ui.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.rubypeople.rdt.core.IBuffer;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyConventions;
import org.rubypeople.rdt.core.formatter.CodeFormatter;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.core.util.Util;
import org.rubypeople.rdt.internal.corext.codemanipulation.StubUtility;
import org.rubypeople.rdt.internal.corext.util.CodeFormatterUtil;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.dialogs.TextFieldNavigationHandler;
import org.rubypeople.rdt.internal.ui.dialogs.TypeSelectionDialog2;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.SuperModuleSelectionDialog;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.Separator;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringDialogField;

public abstract class NewTypeWizardPage extends NewContainerWizardPage {
	
	private static class InterfaceWrapper {
		public String interfaceName;

		public InterfaceWrapper(String interfaceName) {
			this.interfaceName= interfaceName;
		}

		public int hashCode() {
			return interfaceName.hashCode();
		}

		public boolean equals(Object obj) {
			return obj != null && getClass().equals(obj.getClass()) && ((InterfaceWrapper) obj).interfaceName.equals(interfaceName);
		}
	}
	
	private static class InterfacesListLabelProvider extends LabelProvider {
		private Image fInterfaceImage;
		
		public InterfacesListLabelProvider() {
			fInterfaceImage= RubyPluginImages.get(RubyPluginImages.IMG_OBJS_MODULE);
		}
		
		public String getText(Object element) {
			return ((InterfaceWrapper) element).interfaceName;
		}
		
		public Image getImage(Object element) {
			return fInterfaceImage;
		}
	}	

	private final static String PAGE_NAME= "NewTypeWizardPage"; //$NON-NLS-1$
	
	/** Field ID of the package input field. */
	protected final static String PACKAGE= PAGE_NAME + ".package";	 //$NON-NLS-1$
	/** Field ID of the enclosing type input field. */
	protected final static String ENCLOSING= PAGE_NAME + ".enclosing"; //$NON-NLS-1$
	/** Field ID of the enclosing type checkbox. */
	protected final static String ENCLOSINGSELECTION= ENCLOSING + ".selection"; //$NON-NLS-1$
	/** Field ID of the type name input field. */	
	protected final static String TYPENAME= PAGE_NAME + ".typename"; //$NON-NLS-1$
	/** Field ID of the super type input field. */
	protected final static String SUPER= PAGE_NAME + ".superclass"; //$NON-NLS-1$
	/** Field ID of the super interfaces input field. */
	protected final static String INTERFACES= PAGE_NAME + ".interfaces"; //$NON-NLS-1$
	/** Field ID of the method stubs check boxes. */
	protected final static String METHODS= PAGE_NAME + ".methods"; //$NON-NLS-1$
	
		
	/**
	 * a handle to the type to be created (does usually not exist, can be null)
	 */
	private IType fCurrType;
	private StringDialogField fTypeNameDialogField;
	
	private StringButtonDialogField fSuperClassDialogField;
	private ListDialogField fSuperModulesDialogField;
	
	private IType fCreatedType;
		
	protected IStatus fTypeNameStatus;
	protected IStatus fSuperClassStatus;
	protected IStatus fSuperModulesStatus;	

	private int fTypeKind;
	
	/**
	 * Constant to signal that the created type is a class.
	 * @since 0.9.0
	 */
	public static final int CLASS_TYPE = 1;
	
	/**
	 * Constant to signal that the created type is a interface.
	 * @since 0.9.0
	 */
	public static final int INTERFACE_TYPE = 2;
	
	/**
	 * Creates a new <code>NewTypeWizardPage</code>.
	 * 
	 * @param isClass <code>true</code> if a new class is to be created; otherwise
	 * an interface is to be created
	 * @param pageName the wizard page's name
	 */
	public NewTypeWizardPage(boolean isClass, String pageName) {
		this(isClass ? CLASS_TYPE : INTERFACE_TYPE, pageName);
	}
	
	/**
	 * Creates a new <code>NewTypeWizardPage</code>.
	 * 
	 * @param typeKind Signals the kind of the type to be created. Valid kinds are
	 * {@link #CLASS_TYPE}, {@link #INTERFACE_TYPE}
	 * @param pageName the wizard page's name
	 * @since 3.1
	 */
	public NewTypeWizardPage(int typeKind, String pageName) {
	    super(pageName);
	    fTypeKind= typeKind;

	    fCreatedType= null;
		
		TypeFieldsAdapter adapter= new TypeFieldsAdapter();
		
		fTypeNameDialogField= new StringDialogField();
		fTypeNameDialogField.setDialogFieldListener(adapter);
		fTypeNameDialogField.setLabelText(getTypeNameLabel()); 
		
		fSuperClassDialogField= new StringButtonDialogField(adapter);
		fSuperClassDialogField.setDialogFieldListener(adapter);
		fSuperClassDialogField.setLabelText(getSuperClassLabel()); 
		fSuperClassDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_superclass_button); 
		
		String[] addButtons= new String[] {
			NewWizardMessages.NewTypeWizardPage_interfaces_add, 
			/* 1 */ null,
			NewWizardMessages.NewTypeWizardPage_interfaces_remove
		}; 
		fSuperModulesDialogField= new ListDialogField(adapter, addButtons, new InterfacesListLabelProvider());
		fSuperModulesDialogField.setDialogFieldListener(adapter);
		fSuperModulesDialogField.setTableColumns(new ListDialogField.ColumnsDescription(1, false));
		fSuperModulesDialogField.setLabelText(getSuperModulesLabel());
		fSuperModulesDialogField.setRemoveButtonIndex(2);
							
		fTypeNameStatus= new StatusInfo();
		fSuperClassStatus= new StatusInfo();
		fSuperModulesStatus= new StatusInfo();
	}
	
	/**
	 * Returns the label that is used for the package input field.
	 * 
	 * @return the label that is used for the package input field.
	 * @since 3.2
	 */
	protected String getSourceFolderLabel() {
		return NewWizardMessages.NewTypeWizardPage_package_label;
	}
	
	/**
	 * Creates the controls for the type name field. Expects a <code>GridLayout</code> with at 
	 * least 2 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createTypeNameControls(Composite composite, int nColumns) {
		fTypeNameDialogField.doFillIntoGrid(composite, nColumns - 1);
		DialogField.createEmptySpace(composite);
		
		Text text= fTypeNameDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		TextFieldNavigationHandler.install(text);
	}
	
	/**
	 * Sets the focus on the type name input field.
	 */		
	protected void setFocus() {
		fTypeNameDialogField.setFocus();
	}
	
	/**
	 * Sets the type name input field's text to the given value. Method doesn't update
	 * the model.
	 * 
	 * @param name the new type name
	 * @param canBeModified if <code>true</code> the type name field is
	 * editable; otherwise it is read-only.
	 */	
	public void setTypeName(String name, boolean canBeModified) {
		fTypeNameDialogField.setText(name);
		fTypeNameDialogField.setEnabled(canBeModified);
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
	 * Returns the label that is used for the super interfaces input field.
	 * 
	 * @return the label that is used for the super interfaces input field.
	 * @since 3.2
	 */
	protected String getSuperModulesLabel() {
	    if (fTypeKind != INTERFACE_TYPE)
	        return NewWizardMessages.NewTypeWizardPage_interfaces_class_label; 
	    return NewWizardMessages.NewTypeWizardPage_interfaces_ifc_label; 
	}
	
	/**
	 * Returns the label that is used for the type name input field.
	 * 
	 * @return the label that is used for the type name input field.
	 * @since 3.2
	 */
	protected String getTypeNameLabel() {
		return NewWizardMessages.NewTypeWizardPage_typename_label;
	}
	
	/**
	 * Returns the label that is used for the super class input field.
	 * 
	 * @return the label that is used for the super class input field.
	 * @since 3.2
	 */
	protected String getSuperClassLabel() {
		return NewWizardMessages.NewTypeWizardPage_superclass_label;
	}
	
	/**
	 * Creates the controls for the superclass name field. Expects a <code>GridLayout</code> 
	 * with at least 3 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createSuperClassControls(Composite composite, int nColumns) {
		fSuperClassDialogField.doFillIntoGrid(composite, nColumns);
		Text text= fSuperClassDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		
//		RubyTypeCompletionProcessor superClassCompletionProcessor= new RubyTypeCompletionProcessor(false, false);
//		superClassCompletionProcessor.setCompletionContextRequestor(new CompletionContextRequestor() {
//			public StubTypeContext getStubTypeContext() {
//				return getSuperClassStubTypeContext();
//			}
//		});
//
//		ControlContentAssistHelper.createTextContentAssistant(text, superClassCompletionProcessor);
//		TextFieldNavigationHandler.install(text);
	}
	
//	 -------- TypeFieldsAdapter --------

	private class TypeFieldsAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter, SelectionListener {
		
		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			typePageChangeControlPressed(field);
		}
		
		// -------- IListAdapter
		public void customButtonPressed(ListDialogField field, int index) {
			typePageCustomButtonPressed(field, index);
		}
		
		public void selectionChanged(ListDialogField field) {}
		
		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			typePageDialogFieldChanged(field);
		}
		
		public void doubleClicked(ListDialogField field) {
		}


		public void widgetSelected(SelectionEvent e) {
			typePageLinkActivated(e);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			typePageLinkActivated(e);
		}
	}
	
	private void typePageLinkActivated(SelectionEvent e) {
		ISourceFolder root= getSourceFolder();
		if (root != null) {
			// TODO Uncomment!
//			PreferenceDialog dialog= PreferencesUtil.createPropertyDialogOn(getShell(), root.getProject(), CodeTemplatePreferencePage.PROP_ID, null, null);
//			dialog.open();
		} else {
			String title= NewWizardMessages.NewTypeWizardPage_configure_templates_title; 
			String message= NewWizardMessages.NewTypeWizardPage_configure_templates_message; 
			MessageDialog.openInformation(getShell(), title, message);
		}
	}
	
	private void typePageChangeControlPressed(DialogField field) {
		if (field == fSuperClassDialogField) {
			IType type= chooseSuperClass();
			if (type != null) {
				// TODO Spit out fully qualified name?!
				fSuperClassDialogField.setText(type.getElementName());
			}
		}
	}
	
	private void typePageCustomButtonPressed(DialogField field, int index) {		
		if (field == fSuperModulesDialogField) {
			chooseSuperModules();
			List interfaces= fSuperModulesDialogField.getElements();
			if (!interfaces.isEmpty()) {
				Object element= interfaces.get(interfaces.size() - 1);
				fSuperModulesDialogField.editElement(element);
			}
		}
	}
	
	/**
	 * Sets the super class name.
	 * 
	 * @param name the new superclass name
	 * @param canBeModified  if <code>true</code> the superclass name field is
	 * editable; otherwise it is read-only.
	 */		
	public void setSuperClass(String name, boolean canBeModified) {
		fSuperClassDialogField.setText(name);
		fSuperClassDialogField.setEnabled(canBeModified);
	}	
	
	/**
	 * Hook method that gets called from <code>createType</code> to support adding of 
	 * unanticipated methods, fields, and inner types to the created type.
	 * <p>
	 * Implementers can use any methods defined on <code>IType</code> to manipulate the
	 * new type.
	 * </p>
	 * <p>
	 * The source code of the new type will be formatted using the platform's formatter. Needed 
	 * imports are added by the wizard at the end of the type creation process using the given 
	 * import manager.
	 * </p>
	 * 
	 * @param newType the new type created via <code>createType</code>
	 * @param monitor a progress monitor to report progress. Must not be <code>null</code>
	 * 
	 * @see #createType(IProgressMonitor)
	 */		
	protected void createTypeMembers(IType newType, IProgressMonitor monitor) throws CoreException {

	}
	
	/**
	 * Hook method that is called when evaluating the name of the compilation unit to create. By default, a file extension
	 * <code>rb</code> is added to the given type name, but implementors can override this behavior.
	 * 
	 * @param typeName the name of the type to create the compilation unit for.
	 * @return the name of the compilation unit to be created for the given name
	 * 
	 * @since 0.9.0
	 */
	protected String getRubyScriptName(String typeName) {
		int index = typeName.lastIndexOf("::");
		// TODO If they have set up namespace, should we offer to build nested folders? A::B::C -> a/b/c.rb
		if (index != -1) {
			typeName = typeName.substring(index + 2);
		}
		return Util.camelCaseToUnderscores(typeName) + RubyModelUtil.DEFAULT_SCRIPT_SUFFIX;
	}
	
	/**
	 * Creates the new type using the entered field values.
	 * 
	 * @param monitor a progress monitor to report progress.
	 * @throws CoreException Thrown when the creation failed.
	 * @throws InterruptedException Thrown when the operation was canceled.
	 */
	public void createType(IProgressMonitor monitor) throws CoreException, InterruptedException {		
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}

		monitor.beginTask(NewWizardMessages.NewTypeWizardPage_operationdesc, 8); 
		
		ISourceFolder pack= getSourceFolder();
		
//		if (!pack.exists()) {
//			String packName= pack.getElementName();
//			pack= root.createSourceFolder(packName, true, new SubProgressMonitor(monitor, 1));
//		} else {
			monitor.worked(1);
//		}
		
		boolean needsSave;
		IRubyScript connectedCU= null;
		
		try {	
			String typeName= getTypeName();
					
			IType createdType;
			int indent= 0;
			
			String lineDelimiter= StubUtility.getLineDelimiterUsed(pack.getRubyProject());
				
			String cuName= getRubyScriptName(typeName);
			IRubyScript parentCU= pack.createRubyScript(cuName, "", false, new SubProgressMonitor(monitor, 2)); //$NON-NLS-1$
			// create a working copy with a new owner
			
			needsSave= true;
			parentCU.becomeWorkingCopy(null, new SubProgressMonitor(monitor, 1)); // cu is now a (primary) working copy
			connectedCU= parentCU;
				
			IBuffer buffer= parentCU.getBuffer();
				
			String cuContent= constructSimpleTypeStub(lineDelimiter);
			buffer.setContents(cuContent);
							
			createdType= parentCU.getType(typeName);
			
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
						
			IRubyScript cu= createdType.getRubyScript();	
							
			RubyModelUtil.reconcile(cu);

			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}			
			
			createTypeMembers(createdType, new SubProgressMonitor(monitor, 1));
				
			RubyModelUtil.reconcile(cu);
			
			ISourceRange range= createdType.getSourceRange(); // FIXME The source range seems to be off by one... We have a workaround here, but need to fix it in IType
			int length = range.getLength();
			if (lineDelimiter.length() > 1) length++;
			IBuffer buf= cu.getBuffer();
			String originalContent= buf.getText(range.getOffset(), length);

			String formattedContent= CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, originalContent, indent, null, lineDelimiter, pack.getRubyProject());
//			formattedContent= Strings.trimLeadingTabsAndSpaces(formattedContent);
			buf.replace(range.getOffset(), length, formattedContent);
		
			fCreatedType= createdType;

			if (needsSave) {
				cu.commitWorkingCopy(true, new SubProgressMonitor(monitor, 1));
			} else {
				monitor.worked(1);
			}
			
		} finally {
			if (connectedCU != null) {
				connectedCU.discardWorkingCopy();
			}
			monitor.done();
		}
	}	
	
	private String constructSimpleTypeStub(String lineDelimiter) {
		StringBuffer buf= new StringBuffer(); //$NON-NLS-1$
		List<String> imports = addImports();
		if (imports != null) {
			for (String string : imports) {
				buf.append("require \"");
				buf.append(string);
				buf.append('"');
				buf.append(lineDelimiter);
			}
		}
		buf.append("class "); //$NON-NLS-1$
		buf.append(getTypeName());
		String superclass = getSuperClass();
		if (superclass != null && superclass.trim().length() > 0 && !superclass.trim().equals("Object") ) {
			buf.append(" < ");
			buf.append(superclass.trim());
		}
		buf.append(lineDelimiter);
		buf.append("end"); //$NON-NLS-1$
		return buf.toString();
	}
	
	protected List<String> addImports() {
		// This is an ugly hack since we don't have an Iportsmanager or ImportRewrite yet.
		return null;
	}

	/**
	 * Opens a selection dialog that allows to select the super interfaces. The selected interfaces are
	 * directly added to the wizard page using {@link #addSuperModule(String)}.
	 * 
	 * 	<p>
	 * Clients can override this method if they want to offer a different dialog.
	 * </p>
	 * 
	 * @since 1.0
	 */
	protected void chooseSuperModules() {
		ISourceFolder root= getSourceFolder();
		if (root == null) {
			return;
		}		

		IRubyProject project= root.getRubyProject();
		SuperModuleSelectionDialog dialog= new SuperModuleSelectionDialog(getShell(), getWizard().getContainer(), this, project);
		dialog.setTitle(getModuleDialogTitle());
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_InterfacesDialog_message); 
		dialog.open();
	}
	
	/**
	 * Sets the super interfaces.
	 * 
	 * @param interfacesNames a list of super interface. The method requires that
	 * the list's elements are of type <code>String</code>
	 * @param canBeModified if <code>true</code> the super interface field is
	 * editable; otherwise it is read-only.
	 */	
	public void setSuperModules(List interfacesNames, boolean canBeModified) {
		ArrayList interfaces= new ArrayList(interfacesNames.size());
		for (Iterator iter= interfacesNames.iterator(); iter.hasNext();) {
			interfaces.add(new InterfaceWrapper((String) iter.next()));
		}
		fSuperModulesDialogField.setElements(interfaces);
		fSuperModulesDialogField.setEnabled(canBeModified);
	}
	
	/**
	 * Returns the chosen super interfaces.
	 * 
	 * @return a list of chosen super interfaces. The list's elements
	 * are of type <code>String</code>
	 */
	public List getSuperModules() {
		List interfaces= fSuperModulesDialogField.getElements();
		ArrayList result= new ArrayList(interfaces.size());
		for (Iterator iter= interfaces.iterator(); iter.hasNext();) {
			InterfaceWrapper wrapper= (InterfaceWrapper) iter.next();
			result.add(wrapper.interfaceName);
		}
		return result;
	}
	
	/**
	 * Adds a super interface to the end of the list and selects it if it is not in the list yet.
	 * 
	 * @param superInterface the fully qualified type name of the interface.
	 * @return returns <code>true</code>if the interfaces has been added, <code>false</code>
	 * if the interface already is in the list.
	 * @since 1.0
	 */
	public boolean addSuperModule(String superInterface) {
		return fSuperModulesDialogField.addElement(new InterfaceWrapper(superInterface));
	}
	
	private String getModuleDialogTitle() {
	    if (fTypeKind == INTERFACE_TYPE)
	        return NewWizardMessages.NewTypeWizardPage_InterfacesDialog_interface_title; 
	    return NewWizardMessages.NewTypeWizardPage_InterfacesDialog_class_title; 
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
			String cuName= getRubyScriptName(getTypeName());
			return pack.getRubyScript(cuName).getResource();
		}
		return null;
	}
	
	/*
	 * A field on the type has changed. The fields' status and all dependent
	 * status are updated.
	 */
	private void typePageDialogFieldChanged(DialogField field) {
		String fieldName= null;
		if (field == fTypeNameDialogField) {
			fTypeNameStatus= typeNameChanged();
			fieldName= TYPENAME;
		} else if (field == fSuperClassDialogField) {
			fSuperClassStatus= superClassChanged();
			fieldName= SUPER;
		} else if (field == fSuperModulesDialogField) {
			fSuperModulesStatus= superInterfacesChanged();
			fieldName= INTERFACES;
		} else {
			fieldName= METHODS;
		}
		// tell all others
		handleFieldChanged(fieldName);
	}		
	
	/**
	 * Returns the content of the superclass input field.
	 * 
	 * @return the superclass name
	 */
	public String getSuperClass() {
		return fSuperClassDialogField.getText();
	}
	
	/**
	 * Hook method that gets called when the superclass name has changed. The method 
	 * validates the superclass name and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus superClassChanged() {
		StatusInfo status= new StatusInfo();
		ISourceFolder root= getSourceFolder();
		fSuperClassDialogField.enableButton(root != null);
				
		String sclassName= getSuperClass();
		if (sclassName.length() == 0) {
			// accept the empty field (stands for Object)
			return status;
		}
		
		if (root != null) {
			// TODO Check to make sure super class exists and is valid
		} else {
			status.setError(""); //$NON-NLS-1$
		}
		return status;
	}
	
	/**
	 * Returns the type name entered into the type input field.
	 * 
	 * @return the type name
	 */
	public String getTypeName() {
		return fTypeNameDialogField.getText();
	}
	
	private IStatus typeNameChanged() {
		StatusInfo status= new StatusInfo();
		fCurrType= null;
		String typeName = getTypeName();
		if (typeName.length() == 0) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_EnterTypeName);
			return status;
		}
		if (!isConstant(typeName)) {
			status.setError("Class name must be a constant. It must begin with a capital letter, and contain only letters, digits, or underscores.");
			return status;
		}
		return status;
	}
	
	
	// ------ validation --------
	protected void doStatusUpdate() {
		// status of all used components
		IStatus[] status= new IStatus[] {
			fContainerStatus,
			fTypeNameStatus,
			fSuperClassStatus,
			fSuperModulesStatus
		};
		
		// the mode severe status will be displayed and the OK button enabled/disabled.
		updateStatus(status);
	}
	
	
	/*
	 * @see NewContainerWizardPage#handleFieldChanged
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		if (fieldName == CONTAINER) {
//			/*fPackageStatus=*/ packageChanged();
//			fEnclosingTypeStatus= enclosingTypeChanged();			
			fTypeNameStatus= typeNameChanged();
			fSuperClassStatus= superClassChanged();
			fSuperModulesStatus= superInterfacesChanged();
		}
		doStatusUpdate();
	}
	
	/**
	 * Initializes all fields provided by the page with a given selection.
	 * 
	 * @param elem the selection used to initialize this page or <code>
	 * null</code> if no selection was available
	 */
	protected void initTypePage(IRubyElement elem) {
		String initSuperclass= "Object"; //$NON-NLS-1$
		ArrayList initSuperinterfaces= new ArrayList(5);

		IRubyProject project= null;
		ISourceFolder folder= getSourceFolder();
		IType enclosingType= null;
				
		if (elem != null) {
			// evaluate the enclosing type
			project= elem.getRubyProject();
			IType typeInCU= (IType) elem.getAncestor(IRubyElement.TYPE);
			if (typeInCU != null) {
				if (typeInCU.getRubyScript() != null) {
					enclosingType= typeInCU;
				}
			} else {
				IRubyScript cu= (IRubyScript) elem.getAncestor(IRubyElement.SCRIPT);
				if (cu != null) {
					enclosingType= cu.findPrimaryType();
				}
			}
			
//			try {
				IType type= null;
				if (elem.getElementType() == IRubyElement.TYPE) {
					type= (IType)elem;
					if (type.exists()) {
						String superName= type.getElementName();
						if (type.isModule()) {
							initSuperinterfaces.add(superName);
						} else {
							initSuperclass= superName;
						}
					}
				}
//			} catch (RubyModelException e) {
//				RubyPlugin.log(e);
//				// ignore this exception now
//			}			
		}
		
		String typeName= ""; //$NON-NLS-1$
		
		ITextSelection selection= getCurrentTextSelection();
		if (selection != null) {
			String text= selection.getText();
			if (text != null && RubyConventions.validateRubyTypeName(text).isOK()) {
				typeName= text;
			}
		}

		setSourceFolder(folder, true);
		
		setTypeName(typeName, true);
		setSuperClass(initSuperclass, true);
//		setSuperInterfaces(initSuperinterfaces, true);
		
//		setAddComments(StubUtility.doAddComments(project), true); // from project or workspace
	}		
	
	private boolean isConstant(String className) {
        if (className == null || className.length() == 0) return false;
        int namespaceDelimeterIndex = className.indexOf("::");
        if (namespaceDelimeterIndex != -1) {
        	return isConstant(className.substring(0, namespaceDelimeterIndex)) && isConstant(className.substring(namespaceDelimeterIndex+2));
        }
        return className.matches("^[A-Z]\\w*");
    }
	

	/**
	 * Hook method that gets called when the list of super interface has changed. The method 
	 * validates the super interfaces and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus superInterfacesChanged() {
		StatusInfo status= new StatusInfo();
		
		ISourceFolder root= getSourceFolder();
		fSuperModulesDialogField.enableButton(0, root != null);
						
		if (root != null) {
			List elements= fSuperModulesDialogField.getElements();
			int nElements= elements.size();
			for (int i= 0; i < nElements; i++) {
				// TODO Check to make sure each interface exists and is valid
//				String intfname= ((InterfaceWrapper) elements.get(i)).interfaceName;
//				Type type= TypeContextChecker.parseSuperInterface(intfname);
//				if (type == null) {
//					status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidSuperInterfaceName, intfname)); 
//					return status;
//				}
			}				
		}
		return status;
	}
	
	/**
	 * Opens a selection dialog that allows to select a super class.
	 * 
	 * @return returns the selected type or <code>null</code> if the dialog
	 *         has been canceled. The caller typically sets the result to the
	 *         super class input field.
	 *         <p>
	 *         Clients can override this method if they want to offer a
	 *         different dialog.
	 *         </p>
	 * 
	 * @since 0.9
	 */
	protected IType chooseSuperClass() {
		ISourceFolder root= getSourceFolder();
		if (root == null) {
			return null;
		}
		
		IRubyElement[] elements= new IRubyElement[] { root.getRubyProject() };
		IRubySearchScope scope= SearchEngine.createRubySearchScope(elements);

		TypeSelectionDialog2 dialog= new TypeSelectionDialog2(getShell(), false,
			getWizard().getContainer(), scope, IRubySearchConstants.CLASS);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_SuperClassDialog_title); 
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_SuperClassDialog_message); 
		dialog.setFilter(getSuperClass());

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}
	
	/**
	 * Returns the created type or <code>null</code> is the type has not been created yet. The method
	 * only returns a valid type after <code>createType</code> has been called.
	 * 
	 * @return the created type
	 * @see #createType(IProgressMonitor)
	 */			
	public IType getCreatedType() {
		return fCreatedType;
	}
}
