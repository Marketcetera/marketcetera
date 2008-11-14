/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 */
package org.rubypeople.rdt.internal.ui.packageview;

import org.eclipse.osgi.util.NLS;

public class PackagesMessages extends NLS {

	private static final String BUNDLE_NAME = PackagesMessages.class.getName();
	
	public static String ClassPathContainer_unbound_label;
	public static String ClassPathContainer_unknown_label;

	public static String CollapseAllAction_label;
	public static String CollapseAllAction_description;
	public static String CollapseAllAction_tooltip;

	public static String GotoResource_dialog_title;
	public static String GotoResource_action_label;

	public static String GotoType_action_label;
	public static String GotoType_action_description;
	public static String GotoType_error_message;
	public static String GotoType_dialog_message;
	public static String GotoType_dialog_title;

	public static String PackageExplorer_element_not_present;

	public static String PackageExplorerPart_workspace;
	public static String PackageExplorerPart_workingSetModel;
	public static String PackageExplorer_title;
	public static String PackageExplorer_toolTip;
	public static String PackageExplorer_toolTip2;
	public static String PackageExplorer_toolTip3;
	public static String PackageExplorer_notFound;
	public static String PackageExplorer_filteredDialog_title;
	public static String PackageExplorer_removeFilters;

	public static String DragAdapter_deleting;
	public static String DragAdapter_refreshing;
	public static String DragAdapter_problem;
	public static String DragAdapter_problemTitle;

	public static String DropAdapter_errorTitle;
	public static String DropAdapter_errorMessage;

	public static String SelectionTransferDropAdapter_error_title;
	public static String SelectionTransferDropAdapter_error_message;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, PackagesMessages.class);
	}
}
