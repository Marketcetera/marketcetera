/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.packageview;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.dnd.RdtViewerDropAdapter;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;

// XXX Uncomment and implement the methods properly!
public class SelectionTransferDropAdapter extends RdtViewerDropAdapter implements TransferDropTargetListener {

	private List fElements;
//	private RubyMoveProcessor fMoveProcessor;
	private int fCanMoveElements;
//	private RubyCopyProcessor fCopyProcessor;
	private int fCanCopyElements;
	private ISelection fSelection;

	private static final long DROP_TIME_DIFF_TRESHOLD= 150;

	public SelectionTransferDropAdapter(StructuredViewer viewer) {
		super(viewer, DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND);
	}

	//---- TransferDropTargetListener interface ---------------------------------------
	
	public Transfer getTransfer() {
		return LocalSelectionTransfer.getInstance();
	}
	
	public boolean isEnabled(DropTargetEvent event) {
		Object target= event.item != null ? event.item.getData() : null;
		if (target == null)
			return false;
		return target instanceof IRubyElement || target instanceof IResource;
	}

	//---- Actual DND -----------------------------------------------------------------
	
	public void dragEnter(DropTargetEvent event) {
		clear();
		super.dragEnter(event);
	}
	
	public void dragLeave(DropTargetEvent event) {
		clear();
		super.dragLeave(event);
	}
	
	private void clear() {
		fElements= null;
		fSelection= null;
//		fMoveProcessor= null;
		fCanMoveElements= 0;
//		fCopyProcessor= null;
		fCanCopyElements= 0;
	}
	
	public void validateDrop(Object target, DropTargetEvent event, int operation) {
		event.detail= DND.DROP_NONE;
		
		if (tooFast(event)) 
			return;
		
		initializeSelection();
				
		try {
			switch(operation) {
				case DND.DROP_DEFAULT:	event.detail= handleValidateDefault(target, event); break;
				case DND.DROP_COPY: 	event.detail= handleValidateCopy(target, event); break;
				case DND.DROP_MOVE: 	event.detail= handleValidateMove(target, event); break;
			}
		} catch (RubyModelException e){
			ExceptionHandler.handle(e, PackagesMessages.SelectionTransferDropAdapter_error_title, PackagesMessages.SelectionTransferDropAdapter_error_message); 
			event.detail= DND.DROP_NONE;
		}	
	}

	protected void initializeSelection(){
		if (fElements != null)
			return;
		ISelection s= LocalSelectionTransfer.getInstance().getSelection();
		if (!(s instanceof IStructuredSelection))
			return;
		fSelection= s;	
		fElements= ((IStructuredSelection)s).toList();
	}
	
	protected ISelection getSelection(){
		return fSelection;
	}
	
	private boolean tooFast(DropTargetEvent event) {
		return Math.abs(LocalSelectionTransfer.getInstance().getSelectionSetTime() - (event.time & 0xFFFFFFFFL)) < DROP_TIME_DIFF_TRESHOLD;
	}	

	public void drop(Object target, DropTargetEvent event) {
		try{
			switch(event.detail) {
				case DND.DROP_MOVE: handleDropMove(target, event); break;
				case DND.DROP_COPY: handleDropCopy(target, event); break;
			}
		} catch (RubyModelException e){
			ExceptionHandler.handle(e, PackagesMessages.SelectionTransferDropAdapter_error_title, PackagesMessages.SelectionTransferDropAdapter_error_message); 
		} catch (InterruptedException e) {
			//ok
		} finally {
			// The drag source listener must not perform any operation
			// since this drop adapter did the remove of the source even
			// if we moved something.
			event.detail= DND.DROP_NONE;
		}
	}
	
	private int handleValidateDefault(Object target, DropTargetEvent event) throws RubyModelException{
		if (target == null)
			return DND.DROP_NONE;
		
		if ((event.operations & DND.DROP_MOVE) != 0) {
			return handleValidateMove(target, event);
		}
		if ((event.operations & DND.DROP_COPY) != 0) {
			return handleValidateCopy(target, event);
		}
		return DND.DROP_NONE;
	}
	
	private int handleValidateMove(Object target, DropTargetEvent event) throws RubyModelException{
		if (target == null)
			return DND.DROP_NONE; // TODO Only allow moves of files and folders...
//		
//		if (fMoveProcessor == null) {
//			IMovePolicy policy= ReorgPolicyFactory.createMovePolicy(ReorgUtils.getResources(fElements), ReorgUtils.getRubyElements(fElements));
//			if (policy.canEnable())
//				fMoveProcessor= new RubyMoveProcessor(policy);
//		}
//
//		if (!canMoveElements())
//			return DND.DROP_NONE;	
//
//		if (target instanceof IResource && fMoveProcessor != null && fMoveProcessor.setDestination((IResource)target).isOK())
//			return DND.DROP_MOVE;
//		else if (target instanceof IRubyElement && fMoveProcessor != null && fMoveProcessor.setDestination((IRubyElement)target).isOK())
			return DND.DROP_MOVE;
//		else
//			return DND.DROP_NONE;	
	}
	
//	private boolean canMoveElements() {
//		if (fCanMoveElements == 0) {
//			fCanMoveElements= 2;
//			if (fMoveProcessor == null)
//				fCanMoveElements= 1;
//		}
//		return fCanMoveElements == 2;
//	}

	private void handleDropMove(final Object target, DropTargetEvent event) throws RubyModelException, InterruptedException{
		// if the target is a project or a folder, then we can move a folder or file there
		try {
			if (!(event.data instanceof IStructuredSelection)) return;
			IStructuredSelection selection = (IStructuredSelection) event.data;
			IResource resource = null;
			if (selection.getFirstElement() instanceof IResource) {
				resource = (IResource) selection.getFirstElement();
			} else if (selection.getFirstElement() instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) selection.getFirstElement();
				resource = (IResource) adaptable.getAdapter(IResource.class);
			}
			if (resource == null) return;
			IContainer container = null;
			if (target instanceof IContainer) {
				container = (IContainer) target;
			} else if (target instanceof IRubyElement) {
				IRubyElement element = (IRubyElement) target;
				IResource blah = element.getCorrespondingResource();
				if (blah instanceof IContainer) {
					container = (IContainer) blah;
				}
			}
			if (container == null) return;
			if (container.equals(resource.getParent())) return; // Drag and drop file into same folder it's already in
			resource.move(container.getFullPath().append(resource.getName()), false, new NullProgressMonitor());
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
	}

	private int handleValidateCopy(Object target, DropTargetEvent event) throws RubyModelException{
//
//		if (fCopyProcessor == null)
//			fCopyProcessor= RubyCopyProcessor.create(ReorgUtils.getResources(fElements), ReorgUtils.getRubyElements(fElements));
//
//		if (!canCopyElements())
//			return DND.DROP_NONE;	
//
//		if (target instanceof IResource && fCopyProcessor != null && fCopyProcessor.setDestination((IResource)target).isOK())
//			return DND.DROP_COPY;
//		else if (target instanceof IRubyElement && fCopyProcessor != null && fCopyProcessor.setDestination((IRubyElement)target).isOK())
			return DND.DROP_COPY;
//		else
//			return DND.DROP_NONE;					
	}
			
//	private boolean canCopyElements() {
//		if (fCanCopyElements == 0) {
//			fCanCopyElements= 2;
//			if (fCopyProcessor == null)
//				fCanCopyElements= 1;
//		}
//		return fCanCopyElements == 2;
//	}		
	
	private void handleDropCopy(final Object target, DropTargetEvent event) throws RubyModelException, InterruptedException{

	}

	private Shell getShell() {
		return getViewer().getControl().getShell();
	}
}
