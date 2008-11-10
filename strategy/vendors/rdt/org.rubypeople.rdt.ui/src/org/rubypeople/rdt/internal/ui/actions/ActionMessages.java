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
package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.osgi.util.NLS;

public final class ActionMessages extends NLS {

    private static final String BUNDLE_NAME= ActionMessages.class.getName();//$NON-NLS-1$

    private ActionMessages() {
        // Do not instantiate
    }
   
    public static String ToggleLinkingAction_label;
    public static String ToggleLinkingAction_tooltip;
    public static String ToggleLinkingAction_description;
    public static String MemberFilterActionGroup_hide_fields_label;
    public static String MemberFilterActionGroup_hide_fields_tooltip;
    public static String MemberFilterActionGroup_hide_fields_description;
    public static String MemberFilterActionGroup_hide_static_label;
    public static String MemberFilterActionGroup_hide_static_tooltip;
    public static String MemberFilterActionGroup_hide_static_description;
    public static String MemberFilterActionGroup_hide_nonpublic_label;
    public static String MemberFilterActionGroup_hide_nonpublic_tooltip;
    public static String MemberFilterActionGroup_hide_nonpublic_description;
    public static String MemberFilterActionGroup_hide_localtypes_label;
    public static String MemberFilterActionGroup_hide_localtypes_tooltip;
    public static String MemberFilterActionGroup_hide_localtypes_description;
	public static String OpenAction_label;
	public static String OpenAction_tooltip;
	public static String OpenAction_description;
	public static String OpenAction_declaration_label;
	public static String OpenAction_select_element;
	public static String OpenAction_error_messageBadSelection;
	public static String OpenAction_error_message;
	public static String OpenAction_error_messageProblems;
	public static String OpenAction_error_messageArgs;
	public static String OpenAction_error_title;
	public static String ActionUtil_notOnBuildPath_title;
	public static String ActionUtil_notOnBuildPath_message;
	public static String OpenWithMenu_label;
	public static String OpenTypeAction_error_title;
	public static String OpenTypeAction_error_messageProblems;
	public static String OpenTypeAction_message;
	public static String OpenNewSourceFolderWizardAction_text2;
	public static String OpenNewSourceFolderWizardAction_description;
	public static String OpenNewSourceFolderWizardAction_tooltip;
	public static String BuildPath_label;
	
	public static String OpenNewRubyProjectWizardAction_text;
	public static String OpenNewRubyProjectWizardAction_description;
	public static String OpenNewRubyProjectWizardAction_tooltip;
	
	public static String SelectAllAction_label;
	public static String SelectAllAction_tooltip;
	public static String NewWizardsActionGroup_new;
	public static String OpenTypeInHierarchyAction_label;
	public static String OpenTypeInHierarchyAction_description;
	public static String OpenTypeInHierarchyAction_tooltip;
	public static String OpenTypeInHierarchyAction_dialogTitle;
	public static String OpenTypeInHierarchyAction_dialogMessage;
	
	public static String SelectionConverter_codeResolve_failed;
	
	public static String OpenTypeHierarchyAction_label;
	public static String OpenTypeHierarchyAction_tooltip;
	public static String OpenTypeHierarchyAction_description;
	public static String OpenTypeHierarchyAction_messages_no_ruby_element;
	public static String OpenTypeHierarchyAction_messages_title;
	public static String OpenTypeHierarchyAction_dialog_title;
	public static String OpenTypeHierarchyAction_messages_no_ruby_resources;
	public static String OpenTypeHierarchyAction_messages_unknown_import_decl;
	public static String OpenTypeHierarchyAction_messages_no_types;
	public static String OpenTypeHierarchyAction_messages_no_valid_ruby_element;
	
	public static String SurroundWithBeginRescueAction_label;
	public static String SurroundWithBeginRescueAction_error;
	public static String SurroundWithBeginRescueAction_dialog_title;

	public static String QuickMenuAction_menuTextWithShortcut;
	public static String ShowInPackageViewAction_label;
	public static String ShowInPackageViewAction_description;
	public static String ShowInPackageViewAction_tooltip;
	public static String ShowInPackageViewAction_error_message;
	public static String ShowInPackageViewAction_dialog_title;
	public static String OpenProjectAction_dialog_title;
	public static String OpenProjectAction_dialog_message;
	public static String OpenProjectAction_error_message;
	public static String RefreshAction_label;
	public static String RefreshAction_toolTip;
	public static String RefreshAction_progressMessage;
	public static String RefreshAction_error_title;
	public static String RefreshAction_error_message;
	public static String RefreshAction_locationDeleted_message;
	public static String RefreshAction_locationDeleted_title;
	public static String BuildAction_label;

    static {
        NLS.initializeMessages(BUNDLE_NAME, ActionMessages.class);
    }
}