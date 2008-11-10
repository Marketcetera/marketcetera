package org.rubypeople.rdt.internal.ui;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class RubyUIMessages extends NLS {

    private static final String BUNDLE_NAME = RubyUIMessages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String StatusBarUpdater_num_elements_selected;
    
    public static String RubyElementLabels_anonym_type;
    public static String RubyElementLabels_anonym;
    public static String RubyElementLabels_import_container;
    public static String RubyElementLabels_initializer;
    public static String RubyElementLabels_concat_string;
    public static String RubyElementLabels_comma_string;
    public static String RubyElementLabels_declseparator_string;
    public static String RubyImageLabelprovider_assert_wrongImage;
	public static String CoreUtility_buildproject_taskname;
	public static String CoreUtility_buildall_taskname;
	public static String CoreUtility_job_title;
	public static String MultiTypeSelectionDialog_errorTitle;
	public static String MultiTypeSelectionDialog_errorMessage;
	public static String TypeSelectionDialog_errorTitle;
	public static String TypeSelectionDialog_dialogMessage;
	public static String RubyElementLabels_default_package;
	public static String RdtUiPlugin_internalErrorOccurred;
	public static String RubyProjectLibraryPage_project;
	public static String RubyProjectLibraryPage_elementNotIProject;
	public static String RubyProjectPropertyPage_rubyProjectClosed;
	public static String RubyProjectLibraryPage_tabName;
	public static String RubyProjectPropertyPage_performOkException;
	public static String RubyProjectPropertyPage_performOkExceptionDialogMessage;
	public static String OptionalMessageDialog_dontShowAgain;
	public static String FoldingConfigurationBlock_error_not_exist;
	public static String FoldingConfigurationBlock_info_no_preferences;
	public static String RubyBasePreferencePage_label;
	public static String RDocPathErrorTitle;
	public static String RDocPathError;
	public static String ErrorRunningRdocTitle;
	public static String ToggleMenuRubyFilesOnly_Tooltip;
	public static String ToggleMenuRubyFilesOnly;
	public static String RubySearchPage_SearchForGroupLabel;
	public static String RubySearch_SearchForClassSymbol;
	public static String RubySearch_SearchForMethodSymbol;
	public static String RubySearch_ResultLabel;
	public static String HTML2TextReader_listItemPrefix;
	public static String HTMLTextPresenter_ellipsis;
	public static String RubyAnnotationHover_multipleMarkersAtThisLine;
	public static String ExceptionDialog_seeErrorLogMessage;
	public static String NewProjectCreationWizard_windowTitle;
	public static String NewProjectCreationWizard_projectCreationMessage;
	public static String WizardNewProjectCreationPage_pageName;
	public static String WizardNewProjectCreationPage_pageTitle;
	public static String WizardNewProjectCreationPage_pageDescription;

	public static String TypeSelectionComponent_show_status_line_label;
	public static String TypeSelectionComponent_fully_qualify_duplicates_label;
	public static String TypeSelectionComponent_label;
	public static String TypeSelectionComponent_menu;
	public static String TypeSelectionDialog2_title_format;
	public static String TypeSelectionDialog_error_type_doesnot_exist;
	public static String TypeSelectionDialog_error3Title;
	public static String TypeSelectionDialog_error3Message;
	public static String TypeSelectionDialog_progress_consistency;
	public static String TypeInfoViewer_default_package;
	public static String TypeInfoViewer_library_name_format;
	public static String TypeInfoViewer_progressJob_label;
	public static String TypeInfoViewer_progress_label;
	public static String TypeInfoViewer_job_label;
	public static String TypeInfoViewer_job_error;
	public static String TypeInfoViewer_job_cancel;
	public static String TypeInfoViewer_searchJob_taskName;
	public static String TypeInfoViewer_syncJob_label;
	public static String TypeInfoViewer_syncJob_taskName;
	public static String TypeInfoViewer_remove_from_history;
	public static String TypeInfoViewer_separator_message;
	public static String TypeInfoLabelProvider_default_package;
	public static String OpenTypeAction_dialogTitle;
	public static String OpenTypeAction_dialogMessage;
	public static String OpenTypeAction_label;
	public static String OpenTypeAction_description;
	public static String OpenTypeAction_tooltip;
	public static String OpenTypeAction_errorTitle;
	public static String OpenTypeAction_errorMessage;
	public static String RubyOutlineControl_statusFieldText_hideInheritedMembers;
	public static String RubyOutlineControl_statusFieldText_showInheritedMembers;
	public static String OpenTypeHierarchyUtil_selectionDialog_title;
	public static String OpenTypeHierarchyUtil_selectionDialog_message;
	public static String OpenTypeHierarchyUtil_error_open_perspective;
	public static String OpenTypeHierarchyUtil_error_open_editor;
	public static String OpenTypeHierarchyUtil_error_open_view;
	public static String RubyUI_defaultDialogMessage;
	public static String Spelling_error_case_label;
	public static String Spelling_error_label;
	public static String AbstractSpellingDictionary_encodingError;
	public static String Spelling_dictionary_file_extension;
	public static String Spelling_correct_label;
	public static String Spelling_case_label;
	public static String Spelling_add_info;
	public static String Spelling_add_label;
	public static String Spelling_ignore_info;
	public static String Spelling_ignore_label;
	public static String RubyPlugin_initializing_ui;
	public static String InitializeAfterLoadJob_starter_job_name;
	public static String RubyElementProperties_name;
	public static String RubyInstalledDetector_title;
	public static String RubyInstalledDetector_message;
	public static String RubyInstalledDetector_download_button;
	public static String RubyInstalledDetector_preferences_button;
	public static String RubyInstalledDetector_cancel_button;
	public static String RubyEditor_codeassist_noCompletions;
	public static String SelectionListenerWithASTManager_job_title;
	public static String RDocExecutionError;
	public static String RDocExecutionErrorAdditionalMessage;
	public static String RDocExecutionErrorAdditionalMessageWithStderr;

    private RubyUIMessages() {
    }

    public static String getFormattedString(String key, String arg) {
        return getFormattedString(key, new String[] { arg});
    }

    public static String getFormattedString(String key, String[] args) {
        return MessageFormat.format(key, (Object[])args);
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, RubyUIMessages.class);
    }
}
