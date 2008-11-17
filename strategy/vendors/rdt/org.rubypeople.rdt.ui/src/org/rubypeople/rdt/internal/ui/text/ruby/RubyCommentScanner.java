/*
 * Created on Feb 19, 2005
 **/
package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.ruby.CombinedWordRule.WordMatcher;
import org.rubypeople.rdt.ui.text.IColorManager;

/**
 * @author Chris
 * 
 */
public class RubyCommentScanner extends AbstractRubyScanner {

	private static class AtRubyIdentifierDetector implements IWordDetector {

		public boolean isWordStart(char c) {
			return c == '@' || Character.isJavaIdentifierStart(c);
		}

		public boolean isWordPart(char c) {
			return Character.isJavaIdentifierPart(c);
		}
	}

	private class TaskTagMatcher extends CombinedWordRule.WordMatcher {

		private IToken fToken;
		/**
		 * Uppercase words
		 * 
		 * @since 3.0
		 */
		private Map fUppercaseWords = new HashMap();
		/**
		 * <code>true</code> if task tag detection is case-sensitive.
		 * 
		 * @since 3.0
		 */
		private boolean fCaseSensitive = true;
		/**
		 * Buffer for uppercase word
		 * 
		 * @since 3.0
		 */
		private CombinedWordRule.CharacterBuffer fBuffer = new CombinedWordRule.CharacterBuffer(16);

		public TaskTagMatcher(IToken token) {
			fToken = token;
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher#clearWords()
		 * @since 3.0
		 */
		public synchronized void clearWords() {
			super.clearWords();
			fUppercaseWords.clear();
		}

		public synchronized void addTaskTags(String value) {
			String[] tasks = split(value, ","); //$NON-NLS-1$
			for (int i = 0; i < tasks.length; i++) {
				if (tasks[i].length() > 0) {
					addWord(tasks[i], fToken);
				}
			}
		}

		private String[] split(String value, String delimiters) {
			StringTokenizer tokenizer = new StringTokenizer(value, delimiters);
			int size = tokenizer.countTokens();
			String[] tokens = new String[size];
			int i = 0;
			while (i < size)
				tokens[i++] = tokenizer.nextToken();
			return tokens;
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher#addWord(java.lang.String,
		 *      org.eclipse.jface.text.rules.IToken)
		 * @since 3.0
		 */
		public synchronized void addWord(String word, IToken token) {
			Assert.isNotNull(word);
			Assert.isNotNull(token);

			super.addWord(word, token);
			fUppercaseWords.put(new CombinedWordRule.CharacterBuffer(word.toUpperCase()), token);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher#evaluate(org.eclipse.jface.text.rules.ICharacterScanner,
		 *      org.eclipse.jdt.internal.ui.text.CombinedWordRule.CharacterBuffer)
		 * @since 3.0
		 */
		public synchronized IToken evaluate(ICharacterScanner scanner, CombinedWordRule.CharacterBuffer word) {
			if (fCaseSensitive) return super.evaluate(scanner, word);

			fBuffer.clear();
			for (int i = 0, n = word.length(); i < n; i++)
				fBuffer.append(Character.toUpperCase(word.charAt(i)));

			IToken token = (IToken) fUppercaseWords.get(fBuffer);
			if (token != null) return token;
			return Token.UNDEFINED;
		}

		/**
		 * Is task tag detection case-senstive?
		 * 
		 * @return <code>true</code> iff task tag detection is case-sensitive
		 * @since 3.0
		 */
		public boolean isCaseSensitive() {
			return fCaseSensitive;
		}

		/**
		 * Enables/disables the case-sensitivity of the task tag detection.
		 * 
		 * @param caseSensitive
		 *            <code>true</code> iff case-sensitivity should be enabled
		 * @since 3.0
		 */
		public void setCaseSensitive(boolean caseSensitive) {
			fCaseSensitive = caseSensitive;
		}
	}

	private static final String COMPILER_TASK_TAGS = RubyCore.COMPILER_TASK_TAGS;
	protected static final String TASK_TAG = IRubyColorConstants.TASK_TAG;
	/**
	 * Preference key of a string preference, specifying if task tag detection
	 * is case-sensitive.
	 * 
	 * @since 3.0
	 */
	private static final String COMPILER_TASK_CASE_SENSITIVE = RubyCore.COMPILER_TASK_CASE_SENSITIVE;
	/**
	 * Preference value of enabled preferences.
	 * 
	 * @since 3.0
	 */
	private static final String ENABLED = RubyCore.ENABLED;
	private Preferences fCorePreferenceStore;
	private String fDefaultTokenProperty;
	private String[] fTokenProperties;
	private TaskTagMatcher fTaskTagMatcher;

	public RubyCommentScanner(IColorManager manager, IPreferenceStore store, Preferences coreStore, String defaultTokenProperty) {
		this(manager, store, coreStore, defaultTokenProperty, new String[] { defaultTokenProperty, TASK_TAG});
	}

	public RubyCommentScanner(IColorManager manager, IPreferenceStore store, Preferences coreStore, String defaultTokenProperty, String[] tokenProperties) {
		super(manager, store);

		fCorePreferenceStore = coreStore;
		fDefaultTokenProperty = defaultTokenProperty;
		fTokenProperties = tokenProperties;

		initialize();
	}
    
    /**
     * Initialize with the given arguments.
     *
     * @param manager Color manager
     * @param store Preference store
     * @param defaultTokenProperty Default token property
     *
     * @since 0.8.0
     */
    public RubyCommentScanner(IColorManager manager, IPreferenceStore store, String defaultTokenProperty) {
        this(manager, store, null, defaultTokenProperty, new String[] { defaultTokenProperty, TASK_TAG });
    }

	public boolean affectsBehavior(PropertyChangeEvent event) {
		return event.getProperty().equals(COMPILER_TASK_TAGS) || event.getProperty().equals(COMPILER_TASK_CASE_SENSITIVE) || super.affectsBehavior(event);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#adaptToPreferenceChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void adaptToPreferenceChange(PropertyChangeEvent event) {
		if (fTaskTagMatcher != null && event.getProperty().equals(COMPILER_TASK_TAGS)) {
			Object value = event.getNewValue();
			if (value instanceof String) {
				synchronized (fTaskTagMatcher) {
					fTaskTagMatcher.clearWords();
					fTaskTagMatcher.addTaskTags((String) value);
				}
			}
		} else if (fTaskTagMatcher != null && event.getProperty().equals(COMPILER_TASK_CASE_SENSITIVE)) {
			Object value = event.getNewValue();
			if (value instanceof String) fTaskTagMatcher.setCaseSensitive(ENABLED.equals(value));
		} else if (super.affectsBehavior(event)) super.adaptToPreferenceChange(event);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#getTokenProperties()
	 */
	protected String[] getTokenProperties() {
		return fTokenProperties;
	}

	/*
	 * @see AbstractRubyScanner#createRules()
	 */
	protected List createRules() {
		List list = new ArrayList();
		Token defaultToken = getToken(fDefaultTokenProperty);

		List matchers = createMatchers();
		if (matchers.size() > 0) {
			CombinedWordRule combinedWordRule = new CombinedWordRule(new AtRubyIdentifierDetector(), defaultToken);
			for (int i = 0, n = matchers.size(); i < n; i++)
				combinedWordRule.addWordMatcher((WordMatcher) matchers.get(i));
			list.add(combinedWordRule);
		}

		setDefaultReturnToken(defaultToken);

		return list;
	}

	/**
	 * Creates a list of word matchers.
	 * 
	 * @return the list of word matchers
	 */
	protected List createMatchers() {
		List list = new ArrayList();

		// Add rule for Task Tags.
		boolean isCaseSensitive = true;
		String tasks = null;
		if (getPreferenceStore().contains(COMPILER_TASK_TAGS)) {
			tasks = getPreferenceStore().getString(COMPILER_TASK_TAGS);
			isCaseSensitive = ENABLED.equals(getPreferenceStore().getString(COMPILER_TASK_CASE_SENSITIVE));
		} else if (fCorePreferenceStore != null) {
			tasks = fCorePreferenceStore.getString(COMPILER_TASK_TAGS);
			isCaseSensitive = ENABLED.equals(fCorePreferenceStore.getString(COMPILER_TASK_CASE_SENSITIVE));
		}
		if (tasks != null) {
			fTaskTagMatcher = new TaskTagMatcher(getToken(TASK_TAG));
			fTaskTagMatcher.addTaskTags(tasks);
			fTaskTagMatcher.setCaseSensitive(isCaseSensitive);
			list.add(fTaskTagMatcher);
		}

		return list;
	}

}
