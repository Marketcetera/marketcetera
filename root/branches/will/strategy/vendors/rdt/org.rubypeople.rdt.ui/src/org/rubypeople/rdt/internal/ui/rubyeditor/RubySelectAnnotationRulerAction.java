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

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.AnnotationPreferenceLookup;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.SelectMarkerRulerAction;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.correction.RubyCorrectionProcessor;

/**
 * Action which gets triggered when selecting (annotations) in the vertical ruler.
 *
 * <p>
 * Was originally called <code>RubySelectMarkerRulerAction</code>.
 * </p>
 */
public class RubySelectAnnotationRulerAction extends SelectMarkerRulerAction {

	private ITextEditor fTextEditor;
	private Position fPosition;
	private Annotation fAnnotation;
	private AnnotationPreferenceLookup fAnnotationPreferenceLookup;
	private IPreferenceStore fStore;
	private boolean fHasCorrection;
	private ResourceBundle fBundle;

	public RubySelectAnnotationRulerAction(ResourceBundle bundle, String prefix, ITextEditor editor, IVerticalRulerInfo ruler) {
		super(bundle, prefix, editor, ruler);
		fBundle= bundle;
		fTextEditor= editor;

		fAnnotationPreferenceLookup= EditorsUI.getAnnotationPreferenceLookup();
		fStore= RubyPlugin.getDefault().getCombinedPreferenceStore();

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.JAVA_SELECT_MARKER_RULER_ACTION);
	}

	public void run() {
//		if (fStore.getBoolean(PreferenceConstants.EDITOR_ANNOTATION_ROLL_OVER))
//			return;
		
		runWithEvent(null);
	}
	
	/*
	 * @see org.eclipse.jface.action.IAction#runWithEvent(org.eclipse.swt.widgets.Event)
	 * @since 3.2
	 */
	public void runWithEvent(Event event) {
		if (fAnnotation instanceof OverrideIndicatorManager.OverrideIndicator) {
			((OverrideIndicatorManager.OverrideIndicator)fAnnotation).open();
			return;
		}

		if (fHasCorrection) {
			ITextOperationTarget operation= (ITextOperationTarget) fTextEditor.getAdapter(ITextOperationTarget.class);
			final int opCode= ISourceViewer.QUICK_ASSIST;
			if (operation != null && operation.canDoOperation(opCode)) {
				fTextEditor.selectAndReveal(fPosition.getOffset(), fPosition.getLength());
				operation.doOperation(opCode);
			}
			return;
		}

		super.run();
	}

	public void update() {
		findRubyAnnotation();
		setEnabled(true); // super.update() might change this later

		if (fAnnotation instanceof OverrideIndicatorManager.OverrideIndicator) {
			initialize(fBundle, "RubySelectAnnotationRulerAction.OpenSuperImplementation."); //$NON-NLS-1$
			return;
		}
		if (fHasCorrection) {
//			if (fAnnotation instanceof AssistAnnotation)
//				initialize(fBundle, "RubySelectAnnotationRulerAction.QuickAssist."); //$NON-NLS-1$
//			else
				initialize(fBundle, "RubySelectAnnotationRulerAction.QuickFix."); //$NON-NLS-1$
			return;
		}

		initialize(fBundle, "RubySelectAnnotationRulerAction.GotoAnnotation."); //$NON-NLS-1$;
		super.update();
	}

	private void findRubyAnnotation() {
		fPosition= null;
		fAnnotation= null;
		fHasCorrection= false;

		AbstractMarkerAnnotationModel model= getAnnotationModel();
		IAnnotationAccessExtension annotationAccess= getAnnotationAccessExtension();

		IDocument document= getDocument();
		if (model == null)
			return ;

		boolean hasAssistLightbulb= false/*fStore.getBoolean(PreferenceConstants.EDITOR_QUICKASSIST_LIGHTBULB)*/;

		Iterator iter= model.getAnnotationIterator();
		int layer= Integer.MIN_VALUE;

		while (iter.hasNext()) {
			Annotation annotation= (Annotation) iter.next();
			if (annotation.isMarkedDeleted())
				continue;

			int annotationLayer= annotationAccess.getLayer(annotation);
			if (annotationAccess != null) {
				if (annotationLayer < layer)
					continue;
			}

			Position position= model.getPosition(annotation);
			if (!includesRulerLine(position, document))
				continue;

			boolean isReadOnly= fTextEditor instanceof ITextEditorExtension && ((ITextEditorExtension)fTextEditor).isEditorInputReadOnly();
			if (!isReadOnly
					&& (
						/*((hasAssistLightbulb && annotation instanceof AssistAnnotation)
						|| */RubyCorrectionProcessor.hasCorrections(annotation)/*)*/)) {
				fPosition= position;
				fAnnotation= annotation;
				fHasCorrection= true;
				layer= annotationLayer;
				continue;
			} else {
				AnnotationPreference preference= fAnnotationPreferenceLookup.getAnnotationPreference(annotation);
				if (preference == null)
					continue;

				String key= preference.getVerticalRulerPreferenceKey();
				if (key == null)
					continue;

				if (fStore.getBoolean(key)) {
					fPosition= position;
					fAnnotation= annotation;
					fHasCorrection= false;
					layer= annotationLayer;
				}
			}
		}
	}
}

