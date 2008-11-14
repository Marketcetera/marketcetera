/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core.util;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.rubypeople.rdt.internal.core.util.messages";//$NON-NLS-1$

    private Messages() {
        // Do not instantiate
    }

    public static String element_doesNotExist;
    public static String element_notOnClasspath;
    public static String element_invalidClassFileName;
    public static String element_reconciling;
    public static String element_attachingSource;
    public static String element_invalidResourceForProject;
    public static String element_nullName;
    public static String element_nullType;
    public static String element_illegalParent;
    public static String operation_needElements;
    public static String operation_needName;
    public static String operation_needPath;
    public static String operation_needAbsolutePath;
    public static String operation_needString;
    public static String operation_notSupported;
    public static String operation_cancelled;
    public static String operation_nullContainer;
    public static String operation_nullName;
    public static String operation_copyElementProgress;
    public static String operation_moveElementProgress;
    public static String operation_renameElementProgress;
    public static String operation_copyResourceProgress;
    public static String operation_moveResourceProgress;
    public static String operation_renameResourceProgress;
    public static String operation_createUnitProgress;
    public static String operation_createFieldProgress;
    public static String operation_createImportsProgress;
    public static String operation_createInitializerProgress;
    public static String operation_createMethodProgress;
    public static String operation_createPackageProgress;
    public static String operation_createPackageFragmentProgress;
    public static String operation_createTypeProgress;
    public static String operation_deleteElementProgress;
    public static String operation_deleteResourceProgress;
    public static String operation_cannotRenameDefaultPackage;
    public static String operation_pathOutsideProject;
    public static String operation_sortelements;
    public static String workingCopy_commit;
    public static String build_cannotSaveState;
    public static String build_cannotSaveStates;
    public static String build_initializationError;
    public static String build_serializationError;
    public static String status_cannotUseDeviceOnPath;
    public static String status_coreException;
    public static String status_evaluationError;
    public static String status_JDOMError;
    public static String status_IOException;
    public static String status_indexOutOfBounds;
    public static String status_invalidContents;
    public static String status_invalidDestination;
    public static String status_invalidName;
    public static String status_invalidPackage;
    public static String status_invalidPath;
    public static String status_invalidProject;
    public static String status_invalidResource;
    public static String status_invalidResourceType;
    public static String status_invalidSibling;
    public static String status_nameCollision;
    public static String status_noLocalContents;
    public static String status_OK;
    public static String status_readOnly;
    public static String status_targetException;
    public static String status_updateConflict;
    public static String classpath_buildPath;
    public static String classpath_cannotNestEntryInEntry;
    public static String classpath_cannotNestEntryInLibrary;
    public static String classpath_cannotNestEntryInOutput;
    public static String classpath_cannotNestOutputInEntry;
    public static String classpath_cannotNestOutputInOutput;
    public static String classpath_cannotReadClasspathFile;
    public static String classpath_cannotReferToItself;
    public static String classpath_cannotUseDistinctSourceFolderAsOutput;
    public static String classpath_cannotUseLibraryAsOutput;
    public static String classpath_closedProject;
    public static String classpath_couldNotWriteClasspathFile;
    public static String classpath_cycle;
    public static String classpath_duplicateEntryPath;
    public static String classpath_illegalContainerPath;
    public static String classpath_illegalEntryInClasspathFile;
    public static String classpath_illegalLibraryPath;
    public static String classpath_illegalLibraryArchive;
    public static String classpath_illegalExternalFolder;
    public static String classpath_illegalProjectPath;
    public static String classpath_illegalSourceFolderPath;
    public static String classpath_illegalVariablePath;
    public static String classpath_invalidClasspathInClasspathFile;
    public static String classpath_invalidContainer;
    public static String classpath_mustEndWithSlash;
    public static String classpath_unboundContainerPath;
    public static String classpath_unboundLibrary;
    public static String classpath_unboundProject;
    public static String classpath_settingOutputLocationProgress;
    public static String classpath_settingProgress;
    public static String classpath_unboundSourceAttachment;
    public static String classpath_unboundSourceFolder;
    public static String classpath_unboundVariablePath;
    public static String classpath_unknownKind;
    public static String classpath_xmlFormatError;
    public static String classpath_disabledInclusionExclusionPatterns;
    public static String classpath_disabledMultipleOutputLocations;
    public static String classpath_incompatibleLibraryJDKLevel;
    public static String classpath_duplicateEntryExtraAttribute;
    public static String file_notFound;
    public static String file_badFormat;
    public static String path_nullPath;
    public static String path_mustBeAbsolute;
    public static String cache_invalidLoadFactor;
    public static String savedState_jobName;
    public static String javamodel_initialization;
    public static String restrictedAccess_project;
    public static String restrictedAccess_library;
    public static String convention_unit_nullName;
    public static String convention_unit_notRubyName;
    public static String convention_unit_notERBName;
    public static String convention_classFile_nullName;
    public static String convention_classFile_notClassFileName;
    public static String convention_illegalIdentifier;
    public static String convention_import_nullImport;
    public static String convention_import_unqualifiedImport;
    public static String convention_type_nullName;
    public static String convention_type_nameWithBlanks;
    public static String convention_type_dollarName;
    public static String convention_type_lowercaseName;
    public static String convention_type_invalidName;
    public static String convention_package_nullName;
    public static String convention_package_emptyName;
    public static String convention_package_dotName;
    public static String convention_package_nameWithBlanks;
    public static String convention_package_consecutiveDotsName;
    public static String convention_package_uppercaseName;
	public static String build_saveStateProgress;
	public static String build_saveStateComplete;
	public static String project_has_no_ruby_nature;
	public static String manager_filesToIndex;
	public static String manager_indexingInProgress;
	public static String process_name;
	public static String exception_wrongFormat;
	public static String engine_searching_matching;
	public static String engine_searching_indexing;
	public static String engine_searching;
	public static String hierarchy_creating;
	public static String hierarchy_creatingOnType;
	public static String hierarchy_nullRegion;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    
    /**
     * Bind the given message's substitution locations with the given string values.
     * 
     * @param message the message to be manipulated
     * @return the manipulated String
     */
    public static String bind(String message) {
        return bind(message, null);
    }
    
    /**
     * Bind the given message's substitution locations with the given string values.
     * 
     * @param message the message to be manipulated
     * @param binding the object to be inserted into the message
     * @return the manipulated String
     */
    public static String bind(String message, Object binding) {
        return bind(message, new Object[] {binding});
    }

    /**
     * Bind the given message's substitution locations with the given string values.
     * 
     * @param message the message to be manipulated
     * @param binding1 An object to be inserted into the message
     * @param binding2 A second object to be inserted into the message
     * @return the manipulated String
     */
    public static String bind(String message, Object binding1, Object binding2) {
        return bind(message, new Object[] {binding1, binding2});
    }

    /**
     * Bind the given message's substitution locations with the given string values.
     * 
     * @param message the message to be manipulated
     * @param bindings An array of objects to be inserted into the message
     * @return the manipulated String
     */
    public static String bind(String message, Object[] bindings) {
        return format(message, bindings);
    }
	
	public static String format(String message, Object[] objects) {
		return MessageFormat.format(message, objects);
	}

	public static String format(String message, String binding) {
		return MessageFormat.format(message, binding);
	}
}