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
package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.VerticalRulerEvent;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.SelectAnnotationRulerAction;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.text.correction.RubyCorrectionProcessor;

/**
 * A special select marker ruler action which activates quick fix if clicked on a quick fixable problem.
 */
public class RubySelectMarkerRulerAction2 extends SelectAnnotationRulerAction {

	public RubySelectMarkerRulerAction2(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.JAVA_SELECT_MARKER_RULER_ACTION);
	}

	/*
	 * @see org.eclipse.ui.texteditor.IVerticalRulerListener#annotationDefaultSelected(org.eclipse.ui.texteditor.VerticalRulerEvent)
	 */
	public void annotationDefaultSelected(VerticalRulerEvent event) {
		Annotation annotation= event.getSelectedAnnotation();
		IAnnotationModel model= getAnnotationModel();

		if (isOverrideIndicator(annotation)) {
			((OverrideIndicatorManager.OverrideIndicator)annotation).open();
			return;
		}

		if (isBreakpoint(annotation))
			triggerAction(ITextEditorActionConstants.RULER_DOUBLE_CLICK);

		Position position= model.getPosition(annotation);
		if (position == null)
			return;

		if (isQuickFixTarget(annotation)) {
			ITextOperationTarget operation= (ITextOperationTarget) getTextEditor().getAdapter(ITextOperationTarget.class);
			final int opCode= ISourceViewer.QUICK_ASSIST;
			if (operation != null && operation.canDoOperation(opCode)) {
				getTextEditor().selectAndReveal(position.getOffset(), position.getLength());
				operation.doOperation(opCode);
				return;
			}
		}

		// default:
		super.annotationDefaultSelected(event);
	}

	/**
	 * Tells whether the given annotation is an override annotation.
	 *
	 * @param annotation the annotation
	 * @return <code>true</code> iff the annotation is an override annotation
	 */
	private boolean isOverrideIndicator(Annotation annotation) {
		return annotation instanceof OverrideIndicatorManager.OverrideIndicator;
	}

	/**
	 * Checks whether the given annotation is a breakpoint annotation.
	 * 
	 * @param annotation
	 * @return <code>true</code> if the annotation is a breakpoint annotation
	 */
	private boolean isBreakpoint(Annotation annotation) {
		return annotation.getType().equals("org.eclipse.debug.core.breakpoint")/* || annotation.getType().equals(RubyExpandHover.NO_BREAKPOINT_ANNOTATION)*/; //$NON-NLS-1$
	}

	private boolean isQuickFixTarget(Annotation a) {
		return RubyCorrectionProcessor.hasCorrections(a)/* || a instanceof AssistAnnotation*/;
	}

	private void triggerAction(String actionID) {
		IAction action= getTextEditor().getAction(actionID);
		if (action != null) {
			if (action instanceof IUpdate)
				((IUpdate) action).update();
			// hack to propagate line change
			if (action instanceof ISelectionListener) {
				((ISelectionListener)action).selectionChanged(null, null);
			}
			if (action.isEnabled())
				action.run();
		}
	}

}

