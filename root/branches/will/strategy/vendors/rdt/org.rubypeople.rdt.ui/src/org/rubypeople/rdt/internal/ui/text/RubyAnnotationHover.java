/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;

/**
 * Determines all markers for the given line and collects, concatenates, and
 * formates their messages.
 */
public class RubyAnnotationHover implements IAnnotationHover {

	private static class RubyAnnotationHoverType {
	}

	public static final RubyAnnotationHoverType OVERVIEW_RULER_HOVER = new RubyAnnotationHoverType();
	public static final RubyAnnotationHoverType TEXT_RULER_HOVER = new RubyAnnotationHoverType();
	public static final RubyAnnotationHoverType VERTICAL_RULER_HOVER = new RubyAnnotationHoverType();

	private IPreferenceStore fStore = RubyPlugin.getDefault().getCombinedPreferenceStore();
	private RubyAnnotationHoverType fType;

	public RubyAnnotationHover(RubyAnnotationHoverType type) {
		Assert.isTrue(OVERVIEW_RULER_HOVER.equals(type) || TEXT_RULER_HOVER.equals(type) || VERTICAL_RULER_HOVER.equals(type));
		fType = type;
	}

	private boolean isDuplicateRubyAnnotation(Map messagesAtPosition, Position position, String message) {
		if (messagesAtPosition.containsKey(position)) {
			Object value = messagesAtPosition.get(position);
			if (message.equals(value)) return true;

			if (value instanceof List) {
				List messages = (List) value;
				if (messages.contains(message)) return true;
				messages.add(message);
			} else {
				ArrayList messages = new ArrayList();
				messages.add(value);
				messages.add(message);
				messagesAtPosition.put(position, messages);
			}
		} else
			messagesAtPosition.put(position, message);
		return false;
	}

	/**
	 * Returns one marker which includes the ruler's line of activity.
	 */
	protected List getRubyAnnotationsForLine(ISourceViewer viewer, int line) {

		IDocument document = viewer.getDocument();
		IAnnotationModel model = viewer.getAnnotationModel();

		if (model == null) return null;

		List exact = new ArrayList();
		List including = new ArrayList();

		Iterator e = model.getAnnotationIterator();
		HashMap messagesAtPosition = new HashMap();
		while (e.hasNext()) {
			Annotation annotation = (Annotation) e.next();

			if (annotation.getText() == null) continue;

			Position position = model.getPosition(annotation);
			if (position == null) continue;

			AnnotationPreference preference = getAnnotationPreference(annotation);
			if (preference == null) continue;

			if (OVERVIEW_RULER_HOVER.equals(fType)) {
				String key = preference.getOverviewRulerPreferenceKey();
				if (key == null || !fStore.getBoolean(key)) continue;
			} else if (TEXT_RULER_HOVER.equals(fType)) {
				String key = preference.getTextPreferenceKey();
				if (key != null) {
					if (!fStore.getBoolean(key)) continue;
				} else {
					key = preference.getHighlightPreferenceKey();
					if (key == null || !fStore.getBoolean(key)) continue;
				}
			} else if (VERTICAL_RULER_HOVER.equals(fType)) {
				String key = preference.getVerticalRulerPreferenceKey();
				// backward compatibility
				if (key != null && !fStore.getBoolean(key)) continue;
			}

			if (isDuplicateRubyAnnotation(messagesAtPosition, position, annotation.getText())) continue;

			switch (compareRulerLine(position, document, line)) {
			case 1:
				exact.add(annotation);
				break;
			case 2:
				including.add(annotation);
				break;
			}
		}

		return select(exact, including);
	}

	/**
	 * Selects a set of markers from the two lists. By default, it just returns
	 * the set of exact matches.
	 */
	protected List select(List exactMatch, List including) {
		return exactMatch;
	}

	/**
	 * Returns the distance to the ruler line.
	 */
	protected int compareRulerLine(Position position, IDocument document, int line) {

		if (position.getOffset() > -1 && position.getLength() > -1) {
			try {
				int javaAnnotationLine = document.getLineOfOffset(position.getOffset());
				if (line == javaAnnotationLine) return 1;
				if (javaAnnotationLine <= line && line <= document.getLineOfOffset(position.getOffset() + position.getLength())) return 2;
			} catch (BadLocationException x) {}
		}

		return 0;
	}

	/*
	 * @see IVerticalRulerHover#getHoverInfo(ISourceViewer, int)
	 */
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		List javaAnnotations = getRubyAnnotationsForLine(sourceViewer, lineNumber);
		if (javaAnnotations != null) {

			if (javaAnnotations.size() == 1) {

				// optimization
				Annotation annotation = (Annotation) javaAnnotations.get(0);
				String message = annotation.getText();
				if (message != null && message.trim().length() > 0) return formatSingleMessage(message);

			} else {

				List messages = new ArrayList();

				Iterator e = javaAnnotations.iterator();
				while (e.hasNext()) {
					Annotation annotation = (Annotation) e.next();
					String message = annotation.getText();
					if (message != null && message.trim().length() > 0) messages.add(message.trim());
				}

				if (messages.size() == 1) return formatSingleMessage((String) messages.get(0));

				if (messages.size() > 1) return formatMultipleMessages(messages);
			}
		}
		return null;
	}

	/*
	 * Formats a message as HTML text.
	 */
	private String formatSingleMessage(String message) {
		StringBuffer buffer = new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(message));
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}

	/*
	 * Formats several message as HTML text.
	 */
	private String formatMultipleMessages(List messages) {
		StringBuffer buffer = new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(RubyUIMessages.RubyAnnotationHover_multipleMarkersAtThisLine));

		HTMLPrinter.startBulletList(buffer);
		Iterator e = messages.iterator();
		while (e.hasNext())
			HTMLPrinter.addBullet(buffer, HTMLPrinter.convertToHTMLContent((String) e.next()));
		HTMLPrinter.endBulletList(buffer);

		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}

	/**
	 * Returns the annotation preference for the given annotation.
	 * 
	 * @param annotation
	 *            the annotation
	 * @return the annotation preference or <code>null</code> if none
	 */
	private AnnotationPreference getAnnotationPreference(Annotation annotation) {
		return EditorsUI.getAnnotationPreferenceLookup().getAnnotationPreference(annotation);
	}
}
