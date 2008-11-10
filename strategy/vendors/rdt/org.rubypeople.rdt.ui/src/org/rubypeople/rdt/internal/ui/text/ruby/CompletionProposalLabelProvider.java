/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Assert;
import org.rubypeople.rdt.core.CompletionProposal;
import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.ui.RubyElementImageDescriptor;
import org.rubypeople.rdt.ui.RubyElementLabels;

/**
 * Provides labels for ruby content assist proposals. The functionality is
 * similar to the one provided by {@link org.rubypeople.rdt.ui.RubyElementLabels},
 * but based on signatures and {@link CompletionProposal}s.
 * 
 * @since 0.8.0
 */
public class CompletionProposalLabelProvider {
	/**
	 * Creates and returns a decorated image descriptor for a completion
	 * proposal.
	 * 
	 * @param proposal
	 *            the proposal for which to create an image descriptor
	 * @return the created image descriptor, or <code>null</code> if no image
	 *         is available
	 */
	public ImageDescriptor createImageDescriptor(CompletionProposal proposal) {
		final int flags = proposal.getFlags();

		ImageDescriptor descriptor;
		switch (proposal.getKind()) {
		case CompletionProposal.METHOD_DECLARATION:
		case CompletionProposal.METHOD_NAME_REFERENCE:
		case CompletionProposal.METHOD_REF:
		case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
			descriptor = RubyElementImageProvider.getMethodImageDescriptor(flags);
			break;
		case CompletionProposal.TYPE_REF:
			descriptor = RubyElementImageProvider.getTypeImageDescriptor(
						false, false, false);
			break;
		case CompletionProposal.CONSTANT_REF:
			descriptor = RubyElementImageProvider.getConstantImageDescriptor();
			break;
		case CompletionProposal.GLOBAL_REF:
			descriptor = RubyElementImageProvider.getGlobalVariableImageDescriptor();
			break;
		case CompletionProposal.INSTANCE_VARIABLE_REF:
			descriptor = RubyElementImageProvider.getInstanceVariableImageDescriptor();
			break;
		case CompletionProposal.CLASS_VARIABLE_REF:
			descriptor = RubyElementImageProvider.getClassVariableImageDescriptor();
			break;
		case CompletionProposal.LOCAL_VARIABLE_REF:
		case CompletionProposal.VARIABLE_DECLARATION:
			descriptor = RubyPluginImages.DESC_OBJS_LOCAL_VAR;
			break;
		case CompletionProposal.KEYWORD:
			descriptor = null;
			break;
		default:
			descriptor = null;
			Assert.isTrue(false);
		}

		if (descriptor == null)
			return null;
		return decorateImageDescriptor(descriptor, proposal);
	}
	
	/**
	 * Returns a version of <code>descriptor</code> decorated according to
	 * the passed <code>modifier</code> flags.
	 *
	 * @param descriptor the image descriptor to decorate
	 * @param proposal the proposal
	 * @return an image descriptor for a method proposal
	 * @see Flags
	 */
	private ImageDescriptor decorateImageDescriptor(ImageDescriptor descriptor, CompletionProposal proposal) {
		int adornments= 0;
		int flags= proposal.getFlags();
		int kind= proposal.getKind();

		if (kind == CompletionProposal.CONSTANT_REF || kind == CompletionProposal.METHOD_DECLARATION || kind == CompletionProposal.METHOD_DECLARATION || kind == CompletionProposal.METHOD_NAME_REFERENCE || kind == CompletionProposal.METHOD_REF)
			if (Flags.isStatic(flags))
				adornments |= RubyElementImageDescriptor.STATIC;

		return new RubyElementImageDescriptor(descriptor, adornments, RubyElementImageProvider.SMALL_SIZE);
	}

	public String createLabel(CompletionProposal proposal) {
		switch (proposal.getKind()) {
		case CompletionProposal.METHOD_NAME_REFERENCE:
		case CompletionProposal.METHOD_REF:
		case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
			return createMethodProposalLabel(proposal);
//		case CompletionProposal.METHOD_DECLARATION:
//			return createOverrideMethodProposalLabel(proposal);
		case CompletionProposal.TYPE_REF:
			return createTypeProposalLabel(proposal);
		case CompletionProposal.CONSTANT_REF:
		case CompletionProposal.CLASS_VARIABLE_REF:
		case CompletionProposal.INSTANCE_VARIABLE_REF:
		case CompletionProposal.GLOBAL_REF:
		case CompletionProposal.LOCAL_VARIABLE_REF:
		case CompletionProposal.VARIABLE_DECLARATION:
		case CompletionProposal.METHOD_DECLARATION:
			return createSimpleLabelWithType(proposal);
		case CompletionProposal.KEYWORD:
			return createSimpleLabel(proposal);
		default:
			Assert.isTrue(false);
			return null;
		}
	}
	
	/**
	 * Creates a display label for a given type proposal. The display label
	 * consists of:
	 * <ul>
	 *   <li>the simple type name (erased when the context is in javadoc)</li>
	 *   <li>the package name</li>
	 * </ul>
	 * <p>
	 * Examples:
	 * A proposal for the generic type <code>java.util.List&lt;E&gt;</code>, the display label
	 * is: <code>List<E> - java.util</code>.
	 * </p>
	 *
	 * @param typeProposal the method proposal to display
	 * @return the display label for the given type proposal
	 */
	String createTypeProposalLabel(CompletionProposal typeProposal) {
		return typeProposal.getType();
	}
	
	/**
	 * Creates a display label for the given method proposal. The display label
	 * consists of:
	 * <ul>
	 *   <li>the method name</li>
	 *   <li>the parameter list (see {@link #createParameterList(CompletionProposal)})</li>
	 *   <li>the upper bound of the return type (see {@link SignatureUtil#getUpperBound(String)})</li>
	 *   <li>the raw simple name of the declaring type</li>
	 * </ul>
	 * <p>
	 * Examples:
	 * For the <code>get(int)</code> method of a variable of type <code>List<? extends Number></code>, the following
	 * display name is returned: <code>get(int index)  Number - List</code>.<br>
	 * For the <code>add(E)</code> method of a variable of type <code>List<? super Number></code>, the following
	 * display name is returned: <code>add(Number o)  void - List</code>.<br>
	 * </p>
	 *
	 * @param methodProposal the method proposal to display
	 * @return the display label for the given method proposal
	 */
	String createMethodProposalLabel(CompletionProposal methodProposal) {
		StringBuffer nameBuffer= new StringBuffer();

		// method name
		nameBuffer.append(methodProposal.getName());

		// parameters		
		appendUnboundedParameterList(nameBuffer, methodProposal);

		// declaring type
		nameBuffer.append(RubyElementLabels.CONCAT_STRING);
		String declaringType= methodProposal.getDeclaringType();
		nameBuffer.append(declaringType);

		return nameBuffer.toString();
	}
	
	private final StringBuffer appendUnboundedParameterList(StringBuffer buffer, CompletionProposal methodProposal) {
		String[] names = methodProposal.getParameterNames();
		if (names == null) return buffer;
		if (names.length > 0) {
			buffer.append('(');
		}
		for (int i = 0; i < names.length; i++) {
			if (i > 0) {
				buffer.append(',');
				buffer.append(' ');
			}
			buffer.append(names[i]);
		}
		if (names.length > 0) {
			buffer.append(')');
		}
		return buffer;		
	}

	String createSimpleLabel(CompletionProposal proposal) {
		return String.valueOf(proposal.getCompletion());
	}
	
	String createSimpleLabelWithType(CompletionProposal proposal) {
		StringBuffer buf= new StringBuffer();
		buf.append(proposal.getCompletion());
		String typeName= proposal.getType();
		if (typeName.length() > 0) {
			buf.append("    "); //$NON-NLS-1$
			buf.append(typeName);
		}
		return buf.toString();
	}

	/**
	 * Creates and returns a parameter list of the given method proposal
	 * suitable for display. The list does not include parentheses. The lower
	 * bound of parameter types is returned.
	 * <p>
	 * Examples:
	 * <pre>
	 *   &quot;void method(int i, Strings)&quot; -&gt; &quot;int i, String s&quot;
	 *   &quot;? extends Number method(java.lang.String s, ? super Number n)&quot; -&gt; &quot;String s, Number n&quot;
	 * </pre>
	 * </p>
	 *
	 * @param methodProposal the method proposal to create the parameter list
	 *        for. Must be of kind {@link CompletionProposal#METHOD_REF}.
	 * @return the list of comma-separated parameters suitable for display
	 */
	public String createParameterList(CompletionProposal methodProposal) {
		Assert.isTrue(methodProposal.getKind() == CompletionProposal.METHOD_REF);
		return appendUnboundedParameterList(new StringBuffer(), methodProposal).toString();
	}

}
