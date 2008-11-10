/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 * This file is based on a JDT equivalent:
 ********************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.NewFolderDialog;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.CPListElement;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.ExclusionInclusionDialog;

/**
 * Helper class for queries used by the <code>LoadpathModifier</code>. 
 * Clients can either decide to implement their own queries or just taking 
 * the predefined queries.
 */
public class LoadpathModifierQueries {

	/**
     * Query that processes the request of 
     * creating a link to an existing source 
     * folder.
     */
    public static interface ILinkToQuery {
        /**
         * Query that processes the request of 
         * creating a link to an existing source 
         * folder.
         * 
         * @return <code>true</code> if the query was 
         * executed successfully (that is the result of 
         * this query can be used), <code>false</code> 
         * otherwise
         */
        public boolean doQuery();
        
        /**
         * Get the newly created folder.
         * This method is only valid after having
         * called <code>doQuery</code>.
         * 
         * @return the created folder of type
         * <code>IFolder</code>
         */
        public IFolder getCreatedFolder();
    }
    /**
     * Query to get information about the inclusion and exclusion filters of
     * an element.
     */
    public static interface IInclusionExclusionQuery {
        /**
         * Query to get information about the
         * inclusion and exclusion filters of
         * an element.
         * 
         * While executing <code>doQuery</code>,
         * these filter might change.
         * 
         * On calling <code>getInclusionPattern()</code>
         * or <code>getExclusionPattern()</code> it
         * is expected to get the new and updated
         * filters back.
         * 
         * @param element the element to get the
         * information from
         * @param focusOnExcluded
         * @return <code>true</code> if changes
         * have been accepted and <code>getInclusionPatter</code>
         * or <code>getExclusionPattern</code> can
         * be called.
         */
        public boolean doQuery(CPListElement element, boolean focusOnExcluded);
        
        /**
         * Can only be called after <code>
         * doQuery</code> has been executed and
         * has returned <code>true</code>
         * 
         * @return the new inclusion filters
         */
        public IPath[] getInclusionPattern();
        
        /**
         * Can only be called after <code>
         * doQuery</code> has been executed and
         * has returned <code>true</code>
         *
         * @return the new exclusion filters
         */
        public IPath[] getExclusionPattern();
    }

    /**
	 * Query to determine whether a linked folder should be removed.
	 */
	public static interface IRemoveLinkedFolderQuery {

		/** Remove status indicating that the removal should be cancelled */
		public static final int REMOVE_CANCEL= 0;

		/** Remove status indicating that the folder should be removed from the build path only */
		public static final int REMOVE_BUILD_PATH= 1;

		/** Remove status indicating that the folder should be removed from the build path and deleted */
		public static final int REMOVE_BUILD_PATH_AND_FOLDER= 2;

		/**
		 * Query to determined whether the linked folder should be removed as well.
		 * 
		 * @param folder the linked folder to remove
		 * @return a status code corresponding to one of the IRemoveLinkedFolderQuery#REMOVE_XXX constants
		 */
		public int doQuery(IFolder folder);
	}

    /**
	 * Query to create a folder.
	 */
    public static interface ICreateFolderQuery {
        /**
         * Query to create a folder.
         * 
         * @return <code>true</code> if the operation
         * was successful (e.g. no cancelled), <code>
         * false</code> otherwise
         */
        public boolean doQuery();
        
        /**
         * Find out whether a source folder is about
         * to be created or a normal folder which
         * is not on the classpath (and therefore
         * might have to be excluded).
         * 
         * Should only be called after having executed
         * <code>doQuery</code>, because otherwise
         * it might not be sure if a result exists or
         * not.
         * 
         * @return <code>true</code> if a source
         * folder should be created, <code>false
         * </code> otherwise
         */
        public boolean isSourceFolder();
        
        /**
         * Get the newly created folder.
         * This method is only valid after having
         * called <code>doQuery</code>.
         * 
         * @return the created folder of type
         * <code>IFolder</code>
         */
        public IFolder getCreatedFolder();
    }

    /**
     * Query to add libraries to the buildpath.
     */
    public static interface IAddLibrariesQuery {
        /**
         * Get the new classpath entries for libraries to be added to the buildpath.
         * 
         * @param project the Ruby project
         * @param entries an array of classpath entries for the project
         * @return Returns the selected classpath container entries or an empty if the query has
         * been cancelled by the user.
         */
        public ILoadpathEntry[] doQuery(final IRubyProject project, final ILoadpathEntry[] entries);
    }
    
    /**
     * A default query for inclusion and exclusion filters.
     * The query is used to get information about the
     * inclusion and exclusion filters of an element.
     * 
     * @param shell shell if there is any or <code>null</code>
     * @return an <code>IInclusionExclusionQuery</code> that can be executed
     * 
     * @see LoadpathModifierQueries.IInclusionExclusionQuery
     * @see org.eclipse.jdt.internal.corext.buildpath.EditFiltersOperation
     */
	public static IInclusionExclusionQuery getDefaultInclusionExclusionQuery(final Shell shell) {
		return new IInclusionExclusionQuery() {
			
			protected IPath[] fInclusionPattern;
			protected IPath[] fExclusionPattern;
			
			public boolean doQuery(final CPListElement element, final boolean focusOnExcluded) {
				final boolean[] result= { false };
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						Shell sh= shell != null ? shell : RubyPlugin.getActiveWorkbenchShell();
						ExclusionInclusionDialog dialog= new ExclusionInclusionDialog(sh, element, focusOnExcluded);
						result[0]= dialog.open() == Window.OK;
						fInclusionPattern= dialog.getInclusionPattern();
						fExclusionPattern= dialog.getExclusionPattern();
					}
				});
				return result[0];
			}
			
			public IPath[] getInclusionPattern() {
				return fInclusionPattern;
			}
			
			public IPath[] getExclusionPattern() {
				return fExclusionPattern;
			}
		};
	}

    /**
     * Query to create a linked source folder.
     * 
     * The default query shows a dialog which allows
     * the user to specify the new folder that should
     * be created.
     * 
     * @param shell shell if there is any or <code>null</code>
     * @param project the Ruby project to create the linked source folder for
     * @return an <code>ILinkToQuery</code> showing a dialog
     * to create a linked source folder.
     * 
     * @see LoadpathModifierQueries.ICreateFolderQuery
     * @see LinkFolderDialog
     */
    public static ILinkToQuery getDefaultLinkQuery(final Shell shell, final IRubyProject project, final IPath desiredOutputLocation) {
        return new ILinkToQuery() {
            protected IFolder fFolder;
            
            public boolean doQuery() {
                final boolean[] isOK= {false};
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        Shell sh= shell != null ? shell : RubyPlugin.getActiveWorkbenchShell();

                        LinkFolderDialog dialog= new LinkFolderDialog(sh, project.getProject());
                        isOK[0]= dialog.open() == Window.OK;
                        if (isOK[0])
                            fFolder= dialog.getCreatedFolder();
                    }
                });
                return isOK[0];
            }

            public IFolder getCreatedFolder() {
                return fFolder;
            }
            
        };
    }
    
    /**
	 * Shows the UI to prompt whether a linked folder which has been removed from the build path should be deleted as well.
	 * 
	 * @param shell The parent shell for the dialog, can be <code>null</code>
	 * @return an <code>IRemoveLinkedFolderQuery</code> showing a dialog to prompt whether the linked folder should be deleted as well
	 * 
	 * @see IRemoveLinkedFolderQuery
	 */
	public static IRemoveLinkedFolderQuery getDefaultRemoveLinkedFolderQuery(final Shell shell) {
		return new IRemoveLinkedFolderQuery() {

			public final int doQuery(final IFolder folder) {
				final int[] result= { IRemoveLinkedFolderQuery.REMOVE_BUILD_PATH};
				Display.getDefault().syncExec(new Runnable() {

					public final void run() {
						final RemoveLinkedFolderDialog dialog= new RemoveLinkedFolderDialog((shell != null ? shell : RubyPlugin.getActiveWorkbenchShell()), folder);
						final int status= dialog.open();
						if (status == 0)
							result[0]= dialog.getRemoveStatus();
						else
							result[0]= IRemoveLinkedFolderQuery.REMOVE_CANCEL;
					}
				});
				return result[0];
			}
		};
	}

    /**
     * Shows the UI to create a new source folder. 
     * 
     * @param shell The parent shell for the dialog, can be <code>null</code>
     * @param project the Ruby project to create the source folder for
     * @return returns the query
     */
	public static ICreateFolderQuery getDefaultCreateFolderQuery(final Shell shell, final IRubyProject project) {
		return new ICreateFolderQuery() {

			private IFolder fNewFolder;

			public boolean doQuery() {
				final boolean[] isOK= {false};
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        Shell sh= shell != null ? shell : RubyPlugin.getActiveWorkbenchShell();
                        
                        NewFolderDialog dialog= new NewFolderDialog(sh, project.getProject());
                        isOK[0]= dialog.open() == Window.OK;
                        if (isOK[0]) {
                        	IResource sourceContainer= (IResource) dialog.getResult()[0];
                        	if (sourceContainer instanceof IFolder) {
                        		fNewFolder= (IFolder)sourceContainer;
                        	} else {
                        		fNewFolder= null;
                        	}
                        }
                    }
                });
                return isOK[0];
			}


			public boolean isSourceFolder() {
				return true;
			}

			public IFolder getCreatedFolder() {
				return fNewFolder;
			}
			
		};
	}
}
