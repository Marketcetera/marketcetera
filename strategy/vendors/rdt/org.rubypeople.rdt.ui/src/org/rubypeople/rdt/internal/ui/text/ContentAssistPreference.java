package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCompletionProcessor;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.IColorManager;
import org.rubypeople.rdt.ui.text.RubyTextTools;

public class ContentAssistPreference {

	/** Preference key for content assist auto activation */
	private final static String AUTOACTIVATION = PreferenceConstants.CODEASSIST_AUTOACTIVATION;
	/** Preference key for content assist auto activation delay */
	private final static String AUTOACTIVATION_DELAY = PreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY;
	/** Preference key for content assist proposal color */
	private final static String PROPOSALS_FOREGROUND = PreferenceConstants.CODEASSIST_PROPOSALS_FOREGROUND;
	/** Preference key for content assist proposal color */
	private final static String PROPOSALS_BACKGROUND = PreferenceConstants.CODEASSIST_PROPOSALS_BACKGROUND;
	/** Preference key for content assist parameters color */
	private final static String PARAMETERS_FOREGROUND = PreferenceConstants.CODEASSIST_PARAMETERS_FOREGROUND;
	/** Preference key for content assist parameters color */
	private final static String PARAMETERS_BACKGROUND = PreferenceConstants.CODEASSIST_PARAMETERS_BACKGROUND;
	/** Preference key for content assist auto insert */
	private final static String AUTOINSERT = PreferenceConstants.CODEASSIST_AUTOINSERT;
	/** Preference key for prefix completion. */
	private static final String PREFIX_COMPLETION= PreferenceConstants.CODEASSIST_PREFIX_COMPLETION;
	
	/** Preference key for ruby content assist auto activation triggers */
	private final static String AUTOACTIVATION_TRIGGERS_RUBY= PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_RUBY;


	public static void changeConfiguration(ContentAssistant assistant,
			IPreferenceStore store, PropertyChangeEvent event) {
		String p = event.getProperty();

		if (AUTOACTIVATION.equals(p)) {
			boolean enabled = store.getBoolean(AUTOACTIVATION);
			assistant.enableAutoActivation(enabled);
		} else if (AUTOACTIVATION_DELAY.equals(p)) {
			int delay = store.getInt(AUTOACTIVATION_DELAY);
			assistant.setAutoActivationDelay(delay);
		} else if (PROPOSALS_FOREGROUND.equals(p)) {
			Color c = getColor(store, PROPOSALS_FOREGROUND);
			assistant.setProposalSelectorForeground(c);
		} else if (PROPOSALS_BACKGROUND.equals(p)) {
			Color c = getColor(store, PROPOSALS_BACKGROUND);
			assistant.setProposalSelectorBackground(c);
		} else if (PARAMETERS_FOREGROUND.equals(p)) {
			Color c = getColor(store, PARAMETERS_FOREGROUND);
			assistant.setContextInformationPopupForeground(c);
			assistant.setContextSelectorForeground(c);
		} else if (PARAMETERS_BACKGROUND.equals(p)) {
			Color c = getColor(store, PARAMETERS_BACKGROUND);
			assistant.setContextInformationPopupBackground(c);
			assistant.setContextSelectorBackground(c);
		} else if (AUTOINSERT.equals(p)) {
			boolean enabled = store.getBoolean(AUTOINSERT);
			assistant.enableAutoInsert(enabled);
		} else if (PREFIX_COMPLETION.equals(p)) {
			boolean enabled = store.getBoolean(PREFIX_COMPLETION);
			assistant.enablePrefixCompletion(enabled);
		}

		changeRubyProcessor(assistant, store, p);
	}
	
	/**
	 * Configure the given content assistant from the given store.
	 */
	public static void configure(ContentAssistant assistant, IPreferenceStore store) {

		RubyTextTools textTools= RubyPlugin.getDefault().getRubyTextTools();
		IColorManager manager= textTools.getColorManager();


		boolean enabled= store.getBoolean(AUTOACTIVATION);
		assistant.enableAutoActivation(enabled);

		int delay= store.getInt(AUTOACTIVATION_DELAY);
		assistant.setAutoActivationDelay(delay);

		Color c= getColor(store, PROPOSALS_FOREGROUND, manager);
		assistant.setProposalSelectorForeground(c);

		c= getColor(store, PROPOSALS_BACKGROUND, manager);
		assistant.setProposalSelectorBackground(c);

		c= getColor(store, PARAMETERS_FOREGROUND, manager);
		assistant.setContextInformationPopupForeground(c);
		assistant.setContextSelectorForeground(c);

		c= getColor(store, PARAMETERS_BACKGROUND, manager);
		assistant.setContextInformationPopupBackground(c);
		assistant.setContextSelectorBackground(c);

		enabled= store.getBoolean(AUTOINSERT);
		assistant.enableAutoInsert(enabled);

		enabled= store.getBoolean(PREFIX_COMPLETION);
		assistant.enablePrefixCompletion(enabled);

		configureRubyProcessor(assistant, store);
	}
	
	private static void configureRubyProcessor(ContentAssistant assistant, IPreferenceStore store) {
		RubyCompletionProcessor jcp= getRubyProcessor(assistant);
		if (jcp == null)
			return;
		
		String triggers= store.getString(AUTOACTIVATION_TRIGGERS_RUBY);
		if (triggers != null)
			jcp.setCompletionProposalAutoActivationCharacters(triggers.toCharArray());

		// TODO Uncomment when we can handle auto-activation, visibility limitations or case sensitivity
//		boolean enabled= store.getBoolean(SHOW_VISIBLE_PROPOSALS);
//		jcp.restrictProposalsToVisibility(enabled);
//
//		enabled= store.getBoolean(CASE_SENSITIVITY);
//		jcp.restrictProposalsToMatchingCases(enabled);
	}

	private static Color getColor(IPreferenceStore store, String key,
			IColorManager manager) {
		RGB rgb = PreferenceConverter.getColor(store, key);
		return manager.getColor(rgb);
	}

	private static Color getColor(IPreferenceStore store, String key) {
		RubyTextTools textTools = RubyPlugin.getDefault().getRubyTextTools();
		return getColor(store, key, textTools.getColorManager());
	}

	private static void changeRubyProcessor(ContentAssistant assistant,
			IPreferenceStore store, String key) {
		RubyCompletionProcessor jcp = getRubyProcessor(assistant);
		if (jcp == null)
			return;
		if (AUTOACTIVATION_TRIGGERS_RUBY.equals(key)) {
			String triggers = store.getString(AUTOACTIVATION_TRIGGERS_RUBY);
			if (triggers != null)
				jcp.setCompletionProposalAutoActivationCharacters(triggers
						.toCharArray());
			// TODO Uncomment when we can handle auto-activation, visibility limitations or case sensitivity
//		} else if (SHOW_VISIBLE_PROPOSALS.equals(key)) {
//			boolean enabled = store.getBoolean(SHOW_VISIBLE_PROPOSALS);
//			jcp.restrictProposalsToVisibility(enabled);
//		} else if (CASE_SENSITIVITY.equals(key)) {
//			boolean enabled = store.getBoolean(CASE_SENSITIVITY);
//			jcp.restrictProposalsToMatchingCases(enabled);
		}
	}

	private static RubyCompletionProcessor getRubyProcessor(
			ContentAssistant assistant) {
		IContentAssistProcessor p = assistant
				.getContentAssistProcessor(IDocument.DEFAULT_CONTENT_TYPE);
		if (p instanceof RubyCompletionProcessor)
			return (RubyCompletionProcessor) p;
		return null;
	}

}
