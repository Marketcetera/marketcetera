/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.text;


import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;


public class NotifyingReconciler extends MonoReconciler {

	private ArrayList fReconcilingParticipants= new ArrayList();
	
	/**
	 * Constructor for NotifyingReconciler.
	 * @param strategy
	 * @param isIncremental
	 */
	public NotifyingReconciler(IReconcilingStrategy strategy, boolean isIncremental) {
		super(strategy, isIncremental);
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#process(org.eclipse.jface.text.reconciler.DirtyRegion)
	 */
	protected void process(DirtyRegion dirtyRegion) {
		super.process(dirtyRegion);
		notifyReconcilingParticipants();
	}

	public void addReconcilingParticipant(IReconcilingParticipant participant) {
		fReconcilingParticipants.add(participant);
	}

	public void removeReconcilingParticipant(IReconcilingParticipant participant) {
		fReconcilingParticipants.remove(participant);
	}

	protected void notifyReconcilingParticipants() {
		Iterator i= new ArrayList(fReconcilingParticipants).iterator();
		while (i.hasNext()) {
			((IReconcilingParticipant) i.next()).reconciled();
		}
	}
}
