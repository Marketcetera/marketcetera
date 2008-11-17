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
package org.rubypeople.rdt.internal.ui.search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class SearchParticipantsExtensionPoint {

	private Set fActiveParticipants= null;
	private static SearchParticipantsExtensionPoint fgInstance;

	public boolean hasAnyParticipants() {
		return Platform.getExtensionRegistry().getConfigurationElementsFor(RubySearchPage.PARTICIPANT_EXTENSION_POINT).length > 0;
	}

	private synchronized Set getAllParticipants() {
		if (fActiveParticipants != null)
			return fActiveParticipants;
		IConfigurationElement[] allParticipants= Platform.getExtensionRegistry().getConfigurationElementsFor(RubySearchPage.PARTICIPANT_EXTENSION_POINT);
		fActiveParticipants= new HashSet(allParticipants.length);
		for (int i= 0; i < allParticipants.length; i++) {
			SearchParticipantDescriptor descriptor= new SearchParticipantDescriptor(allParticipants[i]);
			IStatus status= descriptor.checkSyntax();
			if (status.isOK()) {
				fActiveParticipants.add(descriptor); 
			} else {
				RubyPlugin.log(status);
			}
		}
		return fActiveParticipants;
	}

	private void collectParticipants(Set participants, IProject[] projects) {
		Iterator activeParticipants= getAllParticipants().iterator();
		Set seenParticipants= new HashSet();
		while (activeParticipants.hasNext()) {
			SearchParticipantDescriptor participant= (SearchParticipantDescriptor) activeParticipants.next();
			if (participant.isEnabled()) {
				String id= participant.getID();
				for (int i= 0; i < projects.length; i++) {
					if (seenParticipants.contains(id))
						continue;
					try {
						if (projects[i].hasNature(participant.getNature())) {
							participants.add(new SearchParticipantRecord(participant, participant.create()));
							seenParticipants.add(id);
						}
					} catch (CoreException e) {
						RubyPlugin.log(e.getStatus());
						participant.disable();
					}
				}
			}
		}
	}



	public SearchParticipantRecord[] getSearchParticipants(IProject[] concernedProjects) throws CoreException {
		Set participantSet= new HashSet();
		collectParticipants(participantSet, concernedProjects);
		SearchParticipantRecord[] participants= new SearchParticipantRecord[participantSet.size()];
		return (SearchParticipantRecord[]) participantSet.toArray(participants);
	}

	public static SearchParticipantsExtensionPoint getInstance() {
		if (fgInstance == null)
			fgInstance= new SearchParticipantsExtensionPoint();
		return fgInstance;
	}
	
	public static void debugSetInstance(SearchParticipantsExtensionPoint instance) {
		fgInstance= instance;
	}
}
