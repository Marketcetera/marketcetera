/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
 * @since 1.0
 */
public final class ProposalSorterRegistry {
	private static final String EXTENSION_POINT= "rubyCompletionProposalSorters"; //$NON-NLS-1$
	private static final String DEFAULT_ID= "org.rubypeople.rdt.ui.RelevanceSorter"; //$NON-NLS-1$

	private static ProposalSorterRegistry fInstance;

	public static synchronized ProposalSorterRegistry getDefault() {
		if (fInstance == null)
			fInstance= new ProposalSorterRegistry(RubyPlugin.getDefault().getPreferenceStore(), PreferenceConstants.CODEASSIST_SORTER);
		return fInstance;
	}

	private final IPreferenceStore fPreferenceStore;
	private final String fKey;

	private Map fSorters= null;
	private ProposalSorterHandle fDefaultSorter;

	private ProposalSorterRegistry(final IPreferenceStore preferenceStore, final String key) {
		Assert.isTrue(preferenceStore != null);
		Assert.isTrue(key != null);
		fPreferenceStore= preferenceStore;
		fKey= key;
	}

	public ProposalSorterHandle getCurrentSorter() {
		ensureSortersRead();
		String id= fPreferenceStore.getString(fKey);
		ProposalSorterHandle sorter= (ProposalSorterHandle) fSorters.get(id);
		return sorter != null ? sorter : fDefaultSorter;
	}

	private synchronized void ensureSortersRead() {
		if (fSorters != null)
			return;

		Map sorters= new LinkedHashMap();
		IExtensionRegistry registry= Platform.getExtensionRegistry();
		List elements= new ArrayList(Arrays.asList(registry.getConfigurationElementsFor(RubyPlugin.getPluginId(), EXTENSION_POINT)));

		for (Iterator iter= elements.iterator(); iter.hasNext();) {
			IConfigurationElement element= (IConfigurationElement) iter.next();
			
			try {
			
				ProposalSorterHandle handle= new ProposalSorterHandle(element);
				final String id= handle.getId();
				sorters.put(id, handle);
				if (DEFAULT_ID.equals(id))
					fDefaultSorter= handle;

			} catch (InvalidRegistryObjectException x) {
				/*
				 * Element is not valid any longer as the contributing plug-in was unloaded or for
				 * some other reason. Do not include the extension in the list and inform the user
				 * about it.
				 */
				Object[] args= { element.toString() };
				String message= Messages.format(RubyTextMessages.CompletionProposalComputerRegistry_invalid_message, args);
				IStatus status= new Status(IStatus.WARNING, RubyPlugin.getPluginId(), IStatus.OK, message, x);
				informUser(status);
			}
		}
		
		fSorters= sorters;
	}

	private void informUser(IStatus status) {
		RubyPlugin.log(status);
		String title= RubyTextMessages.CompletionProposalComputerRegistry_error_dialog_title;
		String message= status.getMessage();
		MessageDialog.openError(RubyPlugin.getActiveWorkbenchShell(), title, message);
	}

	public ProposalSorterHandle[] getSorters() {
		ensureSortersRead();
		Collection sorters= fSorters.values();
		return (ProposalSorterHandle[]) sorters.toArray(new ProposalSorterHandle[sorters.size()]);
	}

	public void select(ProposalSorterHandle handle) {
		Assert.isTrue(handle != null);
		String id= handle.getId();
		
		fPreferenceStore.setValue(fKey, id);
	}
}
