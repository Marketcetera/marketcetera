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
package org.rubypeople.rdt.internal.ui.text.correction;

import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ti.util.OffsetNodeLocator;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyAnnotation;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyMarkerAnnotation;
import org.rubypeople.rdt.ui.text.ruby.IProblemLocation;

/**
 *
 */
public class ProblemLocation implements IProblemLocation {

	private final int fId;
	private final String[] fArguments;
	private final int fOffset;
	private final int fLength;
	private final boolean fIsError;
	private final String fMarkerType;

	public ProblemLocation(int offset, int length, IRubyAnnotation annotation) {
		fId= annotation.getId();
		fArguments= annotation.getArguments();
		fOffset= offset;
		fLength= length;
		fIsError= RubyMarkerAnnotation.ERROR_ANNOTATION_TYPE.equals(annotation.getType());
		
		String markerType= annotation.getMarkerType();
		fMarkerType= markerType != null ? markerType : IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER;
	}

	public ProblemLocation(int offset, int length, int id, String[] arguments, boolean isError, String markerType) {
		fId= id;
		fArguments= arguments;
		fOffset= offset;
		fLength= length;
		fIsError= isError;
		fMarkerType= markerType;
	}
	
	public ProblemLocation(IProblem problem) {
		fId= problem.getID();
		fArguments= problem.getArguments();
		fOffset= problem.getSourceStart();
		fLength= problem.getSourceEnd() - fOffset + 1;
		fIsError= problem.isError();
		fMarkerType= problem instanceof CategorizedProblem ? ((CategorizedProblem) problem).getMarkerType() : IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getProblemId()
	 */
	public int getProblemId() {
		return fId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getProblemArguments()
	 */
	public String[] getProblemArguments() {
		return fArguments;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getLength()
	 */
	public int getLength() {
		return fLength;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getOffset()
	 */
	public int getOffset() {
		return fOffset;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.text.java.IProblemLocation#isError()
	 */
	public boolean isError() {
		return fIsError;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.text.java.IProblemLocation#getMarkerType()
	 */
	public String getMarkerType() {
		return fMarkerType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getCoveringNode(org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	public Node getCoveringNode(RootNode astRoot) {
		return ClosestSpanningNodeLocator.Instance().findClosestSpanner(astRoot, fOffset - 1, new INodeAcceptor() {
		
			public boolean doesAccept(Node node) {
				return node.getPosition().getEndOffset() >= fOffset + fLength;
			}
		
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getCoveredNode(org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	public Node getCoveredNode(RootNode astRoot) {
		return ClosestSpanningNodeLocator.Instance().findClosestSpanner(astRoot, fOffset, new INodeAcceptor() {
			
			public boolean doesAccept(Node node) {
				return node.getPosition().getEndOffset() >= fOffset + fLength;
			}
		
		});
	}

	public String toString() {
		StringBuffer buf= new StringBuffer();
		buf.append("Id: ").append(getErrorCode(fId)).append('\n'); //$NON-NLS-1$
		buf.append('[').append(fOffset).append(", ").append(fLength).append(']').append('\n'); //$NON-NLS-1$
//		String[] arg= fArguments;
//		if (arg != null) {
//			for (int i= 0; i < arg.length; i++) {
//				buf.append(arg[i]);
//				buf.append('\n');				 
//			}
//		}
		return buf.toString();
	}

	private String getErrorCode(int code) {
		StringBuffer buf= new StringBuffer();

		if ((code & IProblem.TypeRelated) != 0) {
			buf.append("TypeRelated + "); //$NON-NLS-1$
		}
		if ((code & IProblem.FieldRelated) != 0) {
			buf.append("FieldRelated + "); //$NON-NLS-1$
		}
		if ((code & IProblem.ConstructorRelated) != 0) {
			buf.append("ConstructorRelated + "); //$NON-NLS-1$
		}
		if ((code & IProblem.MethodRelated) != 0) {
			buf.append("MethodRelated + "); //$NON-NLS-1$
		}
		if ((code & IProblem.ImportRelated) != 0) {
			buf.append("ImportRelated + "); //$NON-NLS-1$
		}
		if ((code & IProblem.Internal) != 0) {
			buf.append("Internal + "); //$NON-NLS-1$
		}
		if ((code & IProblem.Syntax) != 0) {
			buf.append("Syntax + "); //$NON-NLS-1$
		}
		buf.append(code & IProblem.IgnoreCategoriesMask);

		return buf.toString();
	}


}
