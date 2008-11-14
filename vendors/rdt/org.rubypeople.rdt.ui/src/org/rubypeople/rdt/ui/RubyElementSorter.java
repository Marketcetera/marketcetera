/*
 * Created on Mar 21, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.ui;

import java.text.Collator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.packageview.LoadPathContainer;
import org.rubypeople.rdt.internal.ui.preferences.MembersOrderPreferenceCache;

/**
 * @author Chris
 */
public class RubyElementSorter extends ViewerSorter {

    private static final int PROJECTS = 1;
    private static final int SOURCEFOLDERROOTS= 2;
	private static final int SOURCEFOLDER= 3;

    private static final int RUBYSCRIPTS = 4;

    private static final int RESOURCEFOLDERS = 7;
    private static final int RESOURCES = 8;
    private static final int STORAGE = 9;

    private static final int IMPORT_CONTAINER = 11;
    private static final int IMPORT_DECLARATION = 12;

    // Includes all categories ordered using the OutlineSortOrderPage:
    // types, initializers, methods & fields
    private static final int MEMBERSOFFSET = 15;

    private static final int RUBYELEMENTS = 50;
    private static final int OTHERS = 51;

    private MembersOrderPreferenceCache fMemberOrderCache;
    private Collator fNewCollator; // collator from ICU

    /**
     * Constructor.
     */
    public RubyElementSorter() {    
        super(null); // delay initialization of collator
        fMemberOrderCache= RubyPlugin.getDefault().getMemberOrderPreferenceCache();
        fNewCollator= null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerSorter#getCollator()
     */
    public final Collator getCollator() {
        if (collator == null) {
            collator= Collator.getInstance();
        }
        return collator;
    }
    
    /*
     * @see ViewerSorter#category
     */
    public int category(Object element) {
        if (element instanceof IRubyElement) {

            IRubyElement je = (IRubyElement) element;

            switch (je.getElementType()) {
            case IRubyElement.METHOD: {
                IMethod method = (IMethod) je;
                if (method.isConstructor()) { return getMemberCategory(MembersOrderPreferenceCache.CONSTRUCTORS_INDEX); }
                if (method.isSingleton())
                    return getMemberCategory(MembersOrderPreferenceCache.STATIC_METHODS_INDEX);
                else
                    return getMemberCategory(MembersOrderPreferenceCache.METHOD_INDEX);
            }
            case IRubyElement.FIELD:
            case IRubyElement.CLASS_VAR:
            case IRubyElement.INSTANCE_VAR:
            case IRubyElement.CONSTANT: {

                return getMemberCategory(MembersOrderPreferenceCache.FIELDS_INDEX);
            }
            case IRubyElement.TYPE:
                return getMemberCategory(MembersOrderPreferenceCache.TYPE_INDEX);
            case IRubyElement.IMPORT_CONTAINER:
                return IMPORT_CONTAINER;
            case IRubyElement.IMPORT_DECLARATION:
                return IMPORT_DECLARATION;
            case IRubyElement.SOURCE_FOLDER :
				return SOURCEFOLDER;
			case IRubyElement.SOURCE_FOLDER_ROOT :
				return SOURCEFOLDERROOTS;
            case IRubyElement.RUBY_PROJECT:
                return PROJECTS;
            case IRubyElement.SCRIPT:
                return RUBYSCRIPTS;
            }

            return RUBYELEMENTS;
        } else if (element instanceof IFile) {
            return RESOURCES;
        } else if (element instanceof IProject) {
            return PROJECTS;
        } else if (element instanceof IContainer) {
            return RESOURCEFOLDERS;
        } else if (element instanceof IStorage) { 
        	return STORAGE; 
        } else if (element instanceof LoadPathContainer) {
			return SOURCEFOLDERROOTS;
		}
        return OTHERS;
    }

    private int getMemberCategory(int kind) {
        int offset = fMemberOrderCache.getCategoryIndex(kind);
        return offset + MEMBERSOFFSET;
    }

    /*
     * @see ViewerSorter#compare
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        int cat1 = category(e1);
        int cat2 = category(e2);
        
        if (needsLoadpathComparision(e1, cat1, e2, cat2)) {
			ISourceFolderRoot root1= getSourceFolderRoot(e1);
			ISourceFolderRoot root2= getSourceFolderRoot(e2);
			if (root1 == null) {
				if (root2 == null) {
					return 0;
				} else {
					return 1;
				}
			} else if (root2 == null) {
				return -1;
			}
			// check if not same to avoid expensive class path access
			if (!root1.getPath().equals(root2.getPath())) {
				int p1= getLoadPathIndex(root1);
				int p2= getLoadPathIndex(root2);
				if (p1 != p2) {
					return p1 - p2;
				}
			}
		}

        if (cat1 != cat2) return cat1 - cat2;

        if (cat1 == PROJECTS || cat1 == RESOURCES || cat1 == RESOURCEFOLDERS || cat1 == STORAGE
                || cat1 == OTHERS) {
            String name1 = getNonRubyElementLabel(viewer, e1);
            String name2 = getNonRubyElementLabel(viewer, e2);
            if (name1 != null && name2 != null) { return getCollator().compare(name1, name2); }
            return 0; // can't compare
        }
        // only ruby elements from this point

        if (e1 instanceof IMethod) {
            if (fMemberOrderCache.isSortByVisibility()) {
                try {
                    int flags1 = ((IMethod) e1).getVisibility();
                    int flags2 = ((IMethod) e2).getVisibility();
                    int vis = fMemberOrderCache.getVisibilityIndex(flags1)
                            - fMemberOrderCache.getVisibilityIndex(flags2);
                    if (vis != 0) { return vis; }
                } catch (RubyModelException ignore) {
                }
            }
        }

        if (e1 instanceof IMember) {
            // FIXME Sort members differently! (Constants, Class vars, instance
            // vars)
        }

        String name1 = getElementName(e1);
        String name2 = getElementName(e2);

        if (e1 instanceof IType) { // handle anonymous types
            if (name1.length() == 0) {
                if (name2.length() == 0) {
                    try {
                        return getCollator().compare(((IType) e1).getSuperclassName(),
                                ((IType) e2).getSuperclassName());
                    } catch (RubyModelException e) {
                        return 0;
                    }
                } else {
                    return 1;
                }
            } else if (name2.length() == 0) { return -1; }
        }

        int cmp = getCollator().compare(name1, name2);
        if (cmp != 0) { return cmp; }
        try {
            if (e1 instanceof IMethod) {
                String[] params1 = ((IMethod) e1).getParameterNames();
                String[] params2 = ((IMethod) e2).getParameterNames();
                int len = Math.min(params1.length, params2.length);
                for (int i = 0; i < len; i++) {
                    cmp = getCollator().compare(params1[i], params2[i]);
                    if (cmp != 0) { return cmp; }
                }
                return params1.length - params2.length;
            }
            return 0;
        } catch (RubyModelException e) {
            return 0;
        }
    }

    protected String getElementName(Object element) {
        if (element instanceof IRubyElement) {
            return ((IRubyElement) element).getElementName();
        } else {
            return element.toString();
        }
    }

    private String getNonRubyElementLabel(Viewer viewer, Object element) {
        // try to use the workbench adapter for non - ruby resources or if not
        // available, use the viewers label provider

        if (element instanceof IAdaptable) {
            IWorkbenchAdapter adapter = (IWorkbenchAdapter) ((IAdaptable) element)
                    .getAdapter(IWorkbenchAdapter.class);
            if (adapter != null) { return adapter.getLabel(element); }
        }
        if (viewer instanceof ContentViewer) {
            IBaseLabelProvider prov = ((ContentViewer) viewer).getLabelProvider();
            if (prov instanceof ILabelProvider) { return ((ILabelProvider) prov).getText(element); }
        }
        return null;
    }
    
	private boolean needsLoadpathComparision(Object e1, int cat1, Object e2, int cat2) {
		if ((cat1 == SOURCEFOLDERROOTS && cat2 == SOURCEFOLDERROOTS) ||
			(cat1 == SOURCEFOLDER && 
				((ISourceFolder)e1).getParent().getResource() instanceof IProject && 
				cat2 == SOURCEFOLDERROOTS) ||
			(cat1 == SOURCEFOLDERROOTS &&
				cat2 == SOURCEFOLDER && 
				((ISourceFolder)e2).getParent().getResource() instanceof IProject)) {
			IRubyProject p1= getRubyProject(e1);
			return p1 != null && p1.equals(getRubyProject(e2));
		}
		return false;
	}
	
	private IRubyProject getRubyProject(Object element) {
		if (element instanceof IRubyElement) {
			return ((IRubyElement)element).getRubyProject();
		} else if (element instanceof LoadPathContainer) {
			return ((LoadPathContainer)element).getRubyProject();
		}
		return null;
	}
	
	private ISourceFolderRoot getSourceFolderRoot(Object element) {
		if (element instanceof LoadPathContainer) {
			// return first source folder root from the container
			LoadPathContainer cp= (LoadPathContainer)element;
			Object[] roots= cp.getSourceFolderRoots();
			if (roots.length > 0)
				return (ISourceFolderRoot)roots[0];
			// non resolvable - return null
			return null;
		}
		return RubyModelUtil.getSourceFolderRoot((IRubyElement)element);
	}
	
	private int getLoadPathIndex(ISourceFolderRoot root) {
		try {
			IPath rootPath= root.getPath();
			ISourceFolderRoot[] roots= root.getRubyProject().getSourceFolderRoots();
			for (int i= 0; i < roots.length; i++) {
				if (roots[i].getPath().equals(rootPath)) {
					return i;
				}
			}
		} catch (RubyModelException e) {
		}

		return Integer.MAX_VALUE;
	}

}