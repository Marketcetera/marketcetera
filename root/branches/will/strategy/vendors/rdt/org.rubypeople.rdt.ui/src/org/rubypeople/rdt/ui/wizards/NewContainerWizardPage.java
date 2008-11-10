package org.rubypeople.rdt.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.viewsupport.IViewPartInputProvider;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.rubypeople.rdt.internal.ui.wizards.TypedViewerFilter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.rubypeople.rdt.ui.RubyElementLabelProvider;
import org.rubypeople.rdt.ui.RubyElementSorter;
import org.rubypeople.rdt.ui.StandardRubyElementContentProvider;

public abstract class NewContainerWizardPage extends NewElementWizardPage {
	/** Id of the container field */
	protected static final String CONTAINER= "NewContainerWizardPage.container"; //$NON-NLS-1$
	
	/** The status of the last validation. */
	protected IStatus fContainerStatus;
	
	private IWorkspaceRoot fWorkspaceRoot;
	private StringButtonDialogField fContainerDialogField;

	private ISourceFolder fCurrSourceFolder;	

	/**
	 * Create a new <code>NewContainerWizardPage</code>
	 * 
	 * @param name the wizard page's name
	 */
	public NewContainerWizardPage(String name) {
		super(name);
		
		fWorkspaceRoot= ResourcesPlugin.getWorkspace().getRoot();	
		ContainerFieldAdapter adapter= new ContainerFieldAdapter();
		
		fContainerDialogField= new StringButtonDialogField(adapter);
		fContainerDialogField.setDialogFieldListener(adapter);
		fContainerDialogField.setLabelText(getContainerLabel()); 
		fContainerDialogField.setButtonLabel(NewWizardMessages.NewContainerWizardPage_container_button); 
		
		fContainerStatus= new StatusInfo();
		fCurrSourceFolder= null;
	}
	
	/**
	 * Utility method to inspect a selection to find a Ruby element. 
	 * 
	 * @param selection the selection to be inspected
	 * @return a Ruby element to be used as the initial selection, or <code>null</code>,
	 * if no Ruby element exists in the given selection
	 */
	protected IRubyElement getInitialRubyElement(IStructuredSelection selection) {
		IRubyElement jelem= null;
		if (selection != null && !selection.isEmpty()) {
			Object selectedElement= selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable= (IAdaptable) selectedElement;			
				
				jelem= (IRubyElement) adaptable.getAdapter(IRubyElement.class);
				if (jelem == null) {
					IResource resource= (IResource) adaptable.getAdapter(IResource.class);
					if (resource != null && resource.getType() != IResource.ROOT) {
						while (jelem == null && resource.getType() != IResource.PROJECT) {
							resource= resource.getParent();
							jelem= (IRubyElement) resource.getAdapter(IRubyElement.class);
						}
						if (jelem == null) {
							jelem= RubyCore.create(resource); // ruby project
						}
					}
				}
			}
		}
		if (jelem == null) {
			IWorkbenchPart part= RubyPlugin.getActivePage().getActivePart();
			if (part instanceof ContentOutline) {
				part= RubyPlugin.getActivePage().getActiveEditor();
			}
			
			if (part instanceof IViewPartInputProvider) {
				Object elem= ((IViewPartInputProvider)part).getViewPartInput();
				if (elem instanceof IRubyElement) {
					jelem= (IRubyElement) elem;
				}
			}
		}

		if (jelem == null || jelem.getElementType() == IRubyElement.RUBY_MODEL) {
			try {
				IRubyProject[] projects= RubyCore.create(getWorkspaceRoot()).getRubyProjects();
				if (projects.length == 1) {
					jelem= projects[0];
				}
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
		}
		return jelem;
	}
	
	/**
	 * Returns the workspace root.
	 * 
	 * @return the workspace root
	 */ 
	protected IWorkspaceRoot getWorkspaceRoot() {
		return fWorkspaceRoot;
	}	
	
	/**
	 * Creates the necessary controls (label, text field and browse button) to edit
	 * the source folder location. The method expects that the parent composite
	 * uses a <code>GridLayout</code> as its layout manager and that the
	 * grid layout has at least 3 columns.
	 * 
	 * @param parent the parent composite
	 * @param nColumns the number of columns to span. This number must be
	 *  greater or equal three
	 */
	protected void createContainerControls(Composite parent, int nColumns) {
		fContainerDialogField.doFillIntoGrid(parent, nColumns);
		LayoutUtil.setWidthHint(fContainerDialogField.getTextControl(null), getMaxFieldWidth());
	}
	
	/**
	 * Returns the recommended maximum width for text fields (in pixels). This
	 * method requires that createContent has been called before this method is
	 * call. Subclasses may override to change the maximum width for text 
	 * fields.
	 * 
	 * @return the recommended maximum width for text fields.
	 */
	protected int getMaxFieldWidth() {
		return convertWidthInCharsToPixels(40);
	}
	
	/**
	 * Returns the label that is used for the container input field.
	 * 
	 * @return the label that is used for the container input field.
	 * @since 3.2
	 */
	protected String getContainerLabel() {
		return NewWizardMessages.NewContainerWizardPage_container_label;
	}
	
	/**
	 * Returns the text selection of the current editor. <code>null</code> is returned
	 * when the current editor does not have focus or does not return a text selection.
	 * @return Returns the text selection of the current editor or <code>null</code>.
     *
     * @since 3.0 
	 */
	protected ITextSelection getCurrentTextSelection() {
		IWorkbenchPart part= RubyPlugin.getActivePage().getActivePart();
		if (part instanceof IEditorPart) {
			ISelectionProvider selectionProvider= part.getSite().getSelectionProvider();
			if (selectionProvider != null) {
				ISelection selection= selectionProvider.getSelection();
				if (selection instanceof ITextSelection) {
					return (ITextSelection) selection;
				}
			}
		}
		return null;
	}
	
	private void containerChangeControlPressed(DialogField field) {
//		 take the current rproject as init element of the dialog
		ISourceFolder root= chooseContainer();
		if (root != null) {
			setSourceFolder(root, true);
		}
	}
	
	/**
	 * Returns the current text of source folder text field.
	 * 
	 * @return the text of the source folder text field
	 */ 	
	public String getProjectText() {
		return fContainerDialogField.getText();
	}
	
	/**
	 * Returns the current text of source folder text field.
	 * 
	 * @return the text of the source folder text field
	 */ 	
	public String getSourceFolderText() {
		return fContainerDialogField.getText();
	}
	
	/**
	 * This method is a hook which gets called after the source folder's
	 * text input field has changed. This default implementation updates
	 * the model and returns an error status. The underlying model
	 * is only valid if the returned status is OK.
	 * 
	 * @return the model's error status
	 */
	protected IStatus containerChanged() {
		StatusInfo status= new StatusInfo();
		
		fCurrSourceFolder= null;
		String str= getSourceFolderText();
		if (str.length() == 0) {
			status.setError(NewWizardMessages.NewContainerWizardPage_error_EnterContainerName); 
			return status;
		}
		IPath path= new Path(str);
		IResource res= fWorkspaceRoot.findMember(path);
		if (res != null) {
			int resType= res.getType();
			if (resType == IResource.PROJECT || resType == IResource.FOLDER) {
				IProject proj= res.getProject();
				if (!proj.isOpen()) {
					status.setError(Messages.format(NewWizardMessages.NewContainerWizardPage_error_ProjectClosed, proj.getFullPath().toString())); 
					return status;
				}				
				IRubyProject rproject= RubyCore.create(proj);
				IRubyElement element = RubyCore.create(res);
				fCurrSourceFolder= RubyModelUtil.getSourceFolder(element);
				if (res.exists()) {
					try {
						if (!proj.hasNature(RubyCore.NATURE_ID)) {
							if (resType == IResource.PROJECT) {
								status.setError(NewWizardMessages.NewContainerWizardPage_warning_NotARubyProject); 
							} else {
								status.setWarning(NewWizardMessages.NewContainerWizardPage_warning_NotInARubyProject); 
							}
							return status;
						}
						if (!rproject.isOnLoadpath(fCurrSourceFolder)) {
							status.setWarning(Messages.format(NewWizardMessages.NewContainerWizardPage_warning_NotOnLoadPath, str)); 
						}		
					} catch (CoreException e) {
						status.setWarning(NewWizardMessages.NewContainerWizardPage_warning_NotARubyProject); 
					}
				}
				return status;
			} else {
				status.setError(Messages.format(NewWizardMessages.NewContainerWizardPage_error_NotAFolder, str)); 
				return status;
			}
		} else {
			status.setError(Messages.format(NewWizardMessages.NewContainerWizardPage_error_ContainerDoesNotExist, str)); 
			return status;
		}
	}
	
	/**
	 * A hook method that gets called when the package field has changed. The method 
	 * validates the package name and returns the status of the validation. The validation
	 * also updates the package fragment model.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
//	protected IStatus packageChanged() { FIXME Combine this with chooseContainer to properly validate the selection
//		StatusInfo status= new StatusInfo();
//		fSourceFolderDialogField.enableButton(getSourceFolderRoot() != null);
//		
//		String packName= getPackageText();
//		if (packName.length() > 0) {
//			IStatus val= RubyConventions.validateSourceFolderName(packName);
//			if (val.getSeverity() == IStatus.ERROR) {
//				status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidPackageName, val.getMessage())); 
//				return status;
//			} else if (val.getSeverity() == IStatus.WARNING) {
//				status.setWarning(Messages.format(NewWizardMessages.NewTypeWizardPage_warning_DiscouragedPackageName, val.getMessage())); 
//				// continue
//			}
//		}
//		
//		ISourceFolderRoot root= getSourceFolderRoot();
//		if (root != null) {		
//			fCurrSourceFolder= root.getSourceFolder(packName);
//		} else {
//			status.setError(""); //$NON-NLS-1$
//		}
//		return status;
//	}
	
	/**
	 * Sets the current source folder (model and text field) to the given source folder
	 * root.
	 * @param root The new root.
	 * @param canBeModified if <code>false</code> the source folder field can 
	 * not be changed by the user. If <code>true</code> the field is editable
	 */ 
	public void setSourceFolder(ISourceFolder root, boolean canBeModified) {
		fCurrSourceFolder= root;
		String str= (root == null) ? "" : root.getPath().makeRelative().toString(); //$NON-NLS-1$
		fContainerDialogField.setText(str);
		fContainerDialogField.setEnabled(canBeModified);
	}	
	/**
	 * Initializes the source folder field with a valid package fragment root.
	 * The package fragment root is computed from the Ruby element.
	 * 
	 * @param elem the Ruby element used to compute the initial package
	 *    fragment root used as the source folder
	 */
	protected void initContainerPage(IRubyElement elem) {
		ISourceFolder initRoot= null;
		if (elem != null && !elem.isType(IRubyElement.RUBY_MODEL)) {
			initRoot= RubyModelUtil.getSourceFolder(elem);
			  try {
				if (initRoot == null || ((ISourceFolderRoot)initRoot.getParent()).isExternal()) {
					IRubyProject rproject= elem.getRubyProject();
					if (rproject != null) {
							initRoot= null;
							if (rproject.exists()) {
								ISourceFolderRoot[] roots= rproject.getSourceFolderRoots();
								for (int i= 0; i < roots.length; i++) {
									if (!roots[i].isExternal()) {
										initRoot= roots[i].getSourceFolder("");
										break;
									}
								}							
							}
						if (initRoot == null) {
							initRoot= rproject.getSourceFolderRoot(rproject.getResource()).getSourceFolder("");
						}
					}
				}		
			  } catch(RubyModelException e) {
				  RubyCore.log(e);
			}
		}	
		setSourceFolder(initRoot, true);
	}
	
	private void containerDialogFieldChanged(DialogField field) {
		if (field == fContainerDialogField) {
			fContainerStatus= containerChanged();
		}
		// tell all others
		handleFieldChanged(CONTAINER);
	}
	
	/**
	 * Hook method that gets called when a field on this page has changed. For this page the 
	 * method gets called when the source folder field changes.
	 * <p>
	 * Every sub type is responsible to call this method when a field on its page has changed.
	 * Subtypes override (extend) the method to add verification when a own field has a
	 * dependency to an other field. For example the class name input must be verified
	 * again when the package field changes (check for duplicated class names).
	 * 
	 * @param fieldName The name of the field that has changed (field id). For the
	 * source folder the field id is <code>CONTAINER</code>
	 */
	protected void handleFieldChanged(String fieldName) {
	}	
	
	// -------- ContainerFieldAdapter --------

	private class ContainerFieldAdapter implements IStringButtonAdapter, IDialogFieldListener {

		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			containerChangeControlPressed(field);
		}
		
		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			containerDialogFieldChanged(field);
		}
	}
	
	/**
	 * Opens a selection dialog that allows to select a source container. 
	 * 
	 * @return returns the selected package fragment root  or <code>null</code> if the dialog has been canceled.
	 * The caller typically sets the result to the container input field.
	 * <p>
	 * Clients can override this method if they want to offer a different dialog.
	 * </p>
	 * 
	 * @since 0.9.0
	 */
	protected ISourceFolder chooseContainer() {
		IRubyElement initElement= getSourceFolder();
		Class[] acceptedClasses= new Class[] { IRubyProject.class, ISourceFolderRoot.class, ISourceFolder.class };
		TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, false) {
			public boolean isSelectedValid(Object element) {
				try {
					if (element instanceof IRubyProject) {
						IRubyProject jproject= (IRubyProject)element;
						IPath path= jproject.getProject().getFullPath();
						return (jproject.findSourceFolderRoot(path) != null);
					} else if (element instanceof ISourceFolderRoot) {
						return (!((ISourceFolderRoot)element).isExternal());
					}
					return true;
				} catch (RubyModelException e) {
					RubyPlugin.log(e.getStatus()); // just log, no UI in validation
				}
				return false;
			}
		};
		
		acceptedClasses= new Class[] { IRubyModel.class, ISourceFolderRoot.class, IRubyProject.class, ISourceFolder.class };
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
			public boolean select(Viewer viewer, Object parent, Object element) {
				if (element instanceof ISourceFolderRoot) {
					return (!((ISourceFolderRoot)element).isExternal());					
				}
				return super.select(viewer, parent, element);
			}
		};		

		StandardRubyElementContentProvider provider= new StandardRubyElementContentProvider(); // FIXME Labels shouldn't give fulle path for source folders, just last name segment
		ILabelProvider labelProvider= new RubyElementLabelProvider(RubyElementLabelProvider.SHOW_DEFAULT); 
		ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(getShell(), labelProvider, provider);
		dialog.setValidator(validator);
		dialog.setSorter(new RubyElementSorter());
		dialog.setTitle(NewWizardMessages.NewContainerWizardPage_ChooseSourceContainerDialog_title); 
		dialog.setMessage(NewWizardMessages.NewContainerWizardPage_ChooseSourceContainerDialog_description); 
		dialog.addFilter(filter);
		dialog.setInput(RubyCore.create(fWorkspaceRoot));
		dialog.setInitialSelection(initElement);
		dialog.setHelpAvailable(false);
		
		if (dialog.open() == Window.OK) {
			Object element= dialog.getFirstResult();
			if (element instanceof IRubyProject) {
				IRubyProject jproject= (IRubyProject)element;
				return jproject.getSourceFolderRoot(jproject.getProject()).getSourceFolder("");
			} else if (element instanceof ISourceFolderRoot) {
				return ((ISourceFolderRoot)element).getSourceFolder("");
			} else if (element instanceof ISourceFolder) {
				return (ISourceFolder)element;
			}
			return null;
		}
		return null;
	}	
	
	/**
	 * Returns the <code>IPackageFragmentRoot</code> that corresponds to the current
	 * value of the source folder field.
	 * 
	 * @return the IPackageFragmentRoot or <code>null</code> if the current source
	 * folder value is not a valid package fragment root
	 * 
	 */ 
	public ISourceFolder getSourceFolder() {
		return fCurrSourceFolder;
	}

}
