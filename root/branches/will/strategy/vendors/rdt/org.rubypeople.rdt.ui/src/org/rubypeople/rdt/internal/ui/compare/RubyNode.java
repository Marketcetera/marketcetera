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
package org.rubypeople.rdt.internal.ui.compare;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * Comparable Ruby elements are represented as RubyNodes.
 * Extends the DocumentRangeNode with method signature information.
 */
class RubyNode extends DocumentRangeNode implements ITypedElement {
	
	public static final int SCRIPT= 0;
	public static final int IMPORT_CONTAINER= 2;
	public static final int IMPORT= 3;
	public static final int MODULE= 4;
	public static final int CLASS= 5;
	public static final int FIELD= 8;
	public static final int CONSTRUCTOR= 10;
	public static final int METHOD= 11;

	private int fInitializerCount= 1;
	private boolean fIsEditable;
	private RubyNode fParent;


	/**
	 * Creates a RubyNode under the given parent.
	 * @param type the Ruby elements type. Legal values are from the range CU to METHOD of this class.
	 * @param name the name of the Ruby element
	 * @param start the starting position of the java element in the underlying document
	 * @param length the number of characters of the java element in the underlying document
	 */
	public RubyNode(RubyNode parent, int type, String name, int start, int length) {
		super(type, RubyCompareUtilities.buildID(type, name), parent.getDocument(), start, length);
		fParent= parent;
		if (parent != null) {
			parent.addChild(this);
			fIsEditable= parent.isEditable();
		}
	}	
	
	/**
	 * Creates a RubyNode for a CU. It represents the root of a
	 * RubyNode tree, so its parent is null.
	 * @param document the document which contains the Ruby element
	 * @param editable whether the document can be modified
	 */
	public RubyNode(IDocument document, boolean editable) {
		super(SCRIPT, RubyCompareUtilities.buildID(SCRIPT, "root"), document, 0, document.getLength()); //$NON-NLS-1$
		fIsEditable= editable;
	}	

	public String getInitializerCount() {
		return Integer.toString(fInitializerCount++);
	}
	
	/**
	 * Extracts the method name from the signature.
	 * Used for smart matching.
	 */
	public String extractMethodName() {
		String id= getId();
		int pos= id.indexOf('(');
		if (pos > 0)
			return id.substring(1, pos);
		return id.substring(1);
	}
	
	/**
	 * Extracts the method's arguments name the signature.
	 * Used for smart matching.
	 */
	public String extractArgumentList() {
		String id= getId();
		int pos= id.indexOf('(');
		if (pos >= 0)
			return id.substring(pos+1);
		return id.substring(1);
	}
	
	/**
	 * Returns a name which is presented in the UI.
	 * @see ITypedElement#getName()
	 */
	public String getName() {
		
		switch (getTypeCode()) {
		case IMPORT_CONTAINER:
			return CompareMessages.RubyNode_importDeclarations; 
		case SCRIPT:
			return CompareMessages.RubyNode_script;
		}
		return getId().substring(1);	// we strip away the type character
	}
	
	/*
	 * @see ITypedElement#getType()
	 */
	public String getType() {
		return "java2"; //$NON-NLS-1$
	}
	
	/* (non Rubydoc)
	 * see IEditableContent.isEditable
	 */
	public boolean isEditable() {
		return fIsEditable;
	}
		
	/**
	 * Returns a shared image for this Ruby element.
	 *
	 * see ITypedInput.getImage
	 */
	public Image getImage() {
						
		ImageDescriptor id= null;
					
		switch (getTypeCode()) {
		case SCRIPT:
			id= RubyCompareUtilities.getImageDescriptor(IRubyElement.SCRIPT);
			break;
		case IMPORT:
			id= RubyCompareUtilities.getImageDescriptor(IRubyElement.IMPORT_DECLARATION);
			break;
		case IMPORT_CONTAINER:
			id= RubyCompareUtilities.getImageDescriptor(IRubyElement.IMPORT_CONTAINER);
			break;
		case CLASS:
			id= RubyCompareUtilities.getTypeImageDescriptor(true);
			break;
		case MODULE:
			id= RubyCompareUtilities.getTypeImageDescriptor(false);
			break;
		case CONSTRUCTOR:
		case METHOD:
			id= RubyCompareUtilities.getImageDescriptor(IRubyElement.METHOD);
			break;
		case FIELD:
			id= RubyCompareUtilities.getImageDescriptor(IRubyElement.FIELD);
			break;					
		}
		return RubyPlugin.getImageDescriptorRegistry().get(id);
	}

	public void setContent(byte[] content) {
		super.setContent(content);
		nodeChanged(this);
	}
	
	public ITypedElement replace(ITypedElement child, ITypedElement other) {
		nodeChanged(this);
		return child;
	}

	void nodeChanged(RubyNode node) {
		if (fParent != null)
			fParent.nodeChanged(node);
	}
}

