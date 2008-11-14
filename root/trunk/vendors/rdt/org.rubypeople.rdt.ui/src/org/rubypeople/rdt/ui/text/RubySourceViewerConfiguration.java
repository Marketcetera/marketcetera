package org.rubypeople.rdt.ui.text;

import java.util.Vector;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;
import org.rubypeople.rdt.internal.corext.util.CodeFormatterUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyScriptDocumentProvider;
import org.rubypeople.rdt.internal.ui.text.ContentAssistPreference;
import org.rubypeople.rdt.internal.ui.text.HTMLTextPresenter;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.internal.ui.text.RubyAnnotationHover;
import org.rubypeople.rdt.internal.ui.text.RubyCompositeReconcilingStrategy;
import org.rubypeople.rdt.internal.ui.text.RubyDoubleClickSelector;
import org.rubypeople.rdt.internal.ui.text.RubyElementProvider;
import org.rubypeople.rdt.internal.ui.text.RubyOutlineInformationControl;
import org.rubypeople.rdt.internal.ui.text.RubyPartitionScanner;
import org.rubypeople.rdt.internal.ui.text.RubyPresentationReconciler;
import org.rubypeople.rdt.internal.ui.text.RubyReconciler;
import org.rubypeople.rdt.internal.ui.text.comment.CommentFormattingStrategy;
import org.rubypeople.rdt.internal.ui.text.comment.RubyCommentAutoIndentStrategy;
import org.rubypeople.rdt.internal.ui.text.correction.RubyCorrectionAssistant;
import org.rubypeople.rdt.internal.ui.text.hyperlinks.RubyHyperLinkDetector;
import org.rubypeople.rdt.internal.ui.text.ruby.AbstractRubyScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.AbstractRubyTokenScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyAutoIndentStrategy;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyColoringTokenScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCommentScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCompletionProcessor;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyFormattingStrategy;
import org.rubypeople.rdt.internal.ui.text.ruby.SingleTokenRubyScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.hover.RubyEditorTextHoverDescriptor;
import org.rubypeople.rdt.internal.ui.text.ruby.hover.RubyEditorTextHoverProxy;
import org.rubypeople.rdt.internal.ui.text.ruby.hover.RubyInformationProvider;
import org.rubypeople.rdt.ui.actions.IRubyEditorActionDefinitionIds;

public class RubySourceViewerConfiguration extends TextSourceViewerConfiguration {

	protected RubyTextTools textTools;
	protected ITextEditor fTextEditor;

	/**
	 * The document partitioning.
	 * 
	 * @since 0.8.0
	 */
	private String fDocumentPartitioning;

	/**
	 * The color manager.
	 * 
	 * @since 0.8.0
	 */
	private IColorManager fColorManager;

	protected AbstractRubyTokenScanner fCodeScanner;

	protected AbstractRubyScanner fMultilineCommentScanner, fSinglelineCommentScanner, fStringScanner, fRegexScanner, fCommandScanner;
	private RubyDoubleClickSelector fRubyDoubleClickSelector;

	/**
	 * Creates a new Ruby source viewer configuration for viewers in the given
	 * editor using the given preference store, the color manager and the
	 * specified document partitioning.
	 * <p>
	 * Creates a Ruby source viewer configuration in the new setup without text
	 * tools. Clients are allowed to call
	 * {@link RubySourceViewerConfiguration#handlePropertyChangeEvent(PropertyChangeEvent)}
	 * and disallowed to call
	 * {@link RubySourceViewerConfiguration#getPreferenceStore()} on the
	 * resulting Ruby source viewer configuration.
	 * </p>
	 * 
	 * @param colorManager
	 *            the color manager
	 * @param preferenceStore
	 *            the preference store, can be read-only
	 * @param editor
	 *            the editor in which the configured viewer(s) will reside, or
	 *            <code>null</code> if none
	 * @param partitioning
	 *            the document partitioning for this configuration, or
	 *            <code>null</code> for the default partitioning
	 * @since 3.0
	 */
	public RubySourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		super(preferenceStore);
		fColorManager = colorManager;
		fTextEditor = editor;
		fDocumentPartitioning = partitioning;
		initializeScanners();
	}

	/*
	 * @see SourceViewerConfiguration#getContentFormatter(ISourceViewer)
	 */
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IDocument.DEFAULT_CONTENT_TYPE);

		formatter.setMasterStrategy(new RubyFormattingStrategy());
		formatter.setSlaveStrategy(new CommentFormattingStrategy(), IRubyPartitions.RUBY_SINGLE_LINE_COMMENT);
		formatter.setSlaveStrategy(new CommentFormattingStrategy(), IRubyPartitions.RUBY_MULTI_LINE_COMMENT);
		return formatter;
	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (!fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;

		IHyperlinkDetector[] inheritedDetectors = super.getHyperlinkDetectors(sourceViewer);

		if (fTextEditor == null)
			return inheritedDetectors;

		int inheritedDetectorsLength = inheritedDetectors != null ? inheritedDetectors.length : 0;
		IHyperlinkDetector[] detectors = new IHyperlinkDetector[inheritedDetectorsLength + 1];
		detectors[0] = new RubyHyperLinkDetector(fTextEditor.getEditorInput());
		for (int i = 0; i < inheritedDetectorsLength; i++)
			detectors[i + 1] = inheritedDetectors[i];

		return detectors;
	}

	/**
	 * Determines whether the preference change encoded by the given event
	 * changes the behavior of one of its contained components.
	 * 
	 * @param event
	 *            the event to be investigated
	 * @return <code>true</code> if event causes a behavioral change
	 * @since 0.8.0
	 */
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return fCodeScanner.affectsBehavior(event) || fMultilineCommentScanner.affectsBehavior(event) 
		|| fSinglelineCommentScanner.affectsBehavior(event)	|| fStringScanner.affectsBehavior(event)
		|| fRegexScanner.affectsBehavior(event);
	}

	/**
	 * Adapts the behavior of the contained components to the change encoded in
	 * the given event.
	 * <p>
	 * Clients are not allowed to call this method if the old setup with text
	 * tools is in use.
	 * </p>
	 * 
	 * @param event
	 *            the event to which to adapt
	 * @see RubySourceViewerConfiguration#RubySourceViewerConfiguration(IColorManager,
	 *      IPreferenceStore, ITextEditor, String)
	 * @since 0.8.0
	 */
	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
		Assert.isTrue(isNewSetup());
		if (fCodeScanner.affectsBehavior(event))
			fCodeScanner.adaptToPreferenceChange(event);
		if (fMultilineCommentScanner.affectsBehavior(event))
			fMultilineCommentScanner.adaptToPreferenceChange(event);
		if (fSinglelineCommentScanner.affectsBehavior(event))
			fSinglelineCommentScanner.adaptToPreferenceChange(event);
		if (fStringScanner.affectsBehavior(event))
			fStringScanner.adaptToPreferenceChange(event);
		if (fRegexScanner.affectsBehavior(event))
			fRegexScanner.adaptToPreferenceChange(event);
	}

	/**
	 * Initializes the scanners.
	 * 
	 * @since 3.0
	 */
	private void initializeScanners() {
		Assert.isTrue(isNewSetup());
		fCodeScanner = new RubyColoringTokenScanner(getColorManager(), fPreferenceStore);
		fMultilineCommentScanner = new RubyCommentScanner(getColorManager(), fPreferenceStore, IRubyColorConstants.RUBY_MULTI_LINE_COMMENT);
		fSinglelineCommentScanner = new RubyCommentScanner(getColorManager(), fPreferenceStore, IRubyColorConstants.RUBY_SINGLE_LINE_COMMENT);
		fStringScanner = new SingleTokenRubyScanner(getColorManager(), fPreferenceStore, IRubyColorConstants.RUBY_STRING);
		fRegexScanner = new SingleTokenRubyScanner(getColorManager(), fPreferenceStore, IRubyColorConstants.RUBY_REGEXP);
		fCommandScanner = new SingleTokenRubyScanner(getColorManager(), fPreferenceStore, IRubyColorConstants.RUBY_COMMAND);
	}

	/**
	 * @return <code>true</code> iff the new setup without text tools is in
	 *         use.
	 * 
	 * @since 3.0
	 */
	private boolean isNewSetup() {
		return textTools == null;
	}

	/**
	 * Returns the color manager for this configuration.
	 * 
	 * @return the color manager
	 */
	protected IColorManager getColorManager() {
		return fColorManager;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new RubyPresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getMultilineCommentScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.RUBY_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, RubyPartitionScanner.RUBY_MULTI_LINE_COMMENT);
		
		dr = new DefaultDamagerRepairer(getSinglelineCommentScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.RUBY_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, RubyPartitionScanner.RUBY_SINGLE_LINE_COMMENT);
		
		dr = new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.RUBY_STRING);
		reconciler.setRepairer(dr, RubyPartitionScanner.RUBY_STRING);
		
		dr = new DefaultDamagerRepairer(getRegexScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.RUBY_REGULAR_EXPRESSION);
		reconciler.setRepairer(dr, RubyPartitionScanner.RUBY_REGULAR_EXPRESSION);
		
		dr = new DefaultDamagerRepairer(getCommandScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.RUBY_COMMAND);
		reconciler.setRepairer(dr, RubyPartitionScanner.RUBY_COMMAND);
		return reconciler;
	}

	protected ITokenScanner getCodeScanner() {
		return fCodeScanner;
	}

	protected ITokenScanner getMultilineCommentScanner() {
		return fMultilineCommentScanner;
	}
	
	protected ITokenScanner getSinglelineCommentScanner() {
		return fSinglelineCommentScanner;
	}
	
	protected ITokenScanner getStringScanner() {
		return fStringScanner;
	}
	
	protected ITokenScanner getRegexScanner() {
		return fRegexScanner;
	}
	
	protected ITokenScanner getCommandScanner() {
		return fCommandScanner;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return RubyPartitionScanner.LEGAL_CONTENT_TYPES;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
	 * @since 0.8.0
	 */
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		if (fDocumentPartitioning != null)
			return fDocumentPartitioning;
		return super.getConfiguredDocumentPartitioning(sourceViewer);
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if (getEditor() != null) {

			ContentAssistant assistant= new ContentAssistant();
			assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

			assistant.setRestoreCompletionProposalSize(getSettings("completion_proposal_size")); //$NON-NLS-1$

			IContentAssistProcessor rubyProcessor= new RubyCompletionProcessor(getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
			assistant.setContentAssistProcessor(rubyProcessor, IDocument.DEFAULT_CONTENT_TYPE);
		
			ContentAssistPreference.configure(assistant, fPreferenceStore);

			assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
			
			return assistant;
		}

		return null;
	}
	
	/**
	 * Returns the settings for the given section.
	 *
	 * @param sectionName the section name
	 * @return the settings
	 * @since 1.0.0
	 */
	private IDialogSettings getSettings(String sectionName) {
		IDialogSettings settings= RubyPlugin.getDefault().getDialogSettings().getSection(sectionName);
		if (settings == null)
			settings= RubyPlugin.getDefault().getDialogSettings().addNewSection(sectionName);

		return settings;
	}

	/*
	 * @see SourceViewerConfiguration#getAnnotationHover(ISourceViewer)
	 */
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new RubyAnnotationHover(RubyAnnotationHover.VERTICAL_RULER_HOVER);
	}

	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		String partitioning = getConfiguredDocumentPartitioning(sourceViewer);
		if (IRubyPartitions.RUBY_SINGLE_LINE_COMMENT.equals(contentType) || IRubyPartitions.RUBY_MULTI_LINE_COMMENT.equals(contentType)) {
			return new IAutoEditStrategy[] { new RubyCommentAutoIndentStrategy(fTextEditor, partitioning, getProject()) };
		} else if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) {
			return new IAutoEditStrategy[] { new RubyAutoIndentStrategy(partitioning, getProject()) };
		} else {
			return super.getAutoEditStrategies(sourceViewer, contentType);
		}
	}

	/*
	 * @see SourceViewerConfiguration#getInformationControlCreator(ISourceViewer)
	 * @since 2.0
	 */
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true));
			}
		};
	}
	
	/*
	 * @see SourceViewerConfiguration#getInformationPresenter(ISourceViewer)
	 * @since 2.0
	 */
	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		InformationPresenter presenter= new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		
		// Register information provider
		IInformationProvider provider= new RubyInformationProvider(getEditor());
		String[] contentTypes= getConfiguredContentTypes(sourceViewer);
		for (int i= 0; i < contentTypes.length; i++)
			presenter.setInformationProvider(provider, contentTypes[i]);
		
		presenter.setSizeConstraints(60, 10, true, true);
		return presenter;
	}
	
	/**
	 * Returns the information presenter control creator. The creator is a factory creating the
	 * presenter controls for the given source viewer. This implementation always returns a creator
	 * for <code>DefaultInformationControl</code> instances.
	 *
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @return an information control creator
	 * @since 2.1
	 */
	private IInformationControlCreator getInformationPresenterControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle= SWT.RESIZE | SWT.TOOL;
				int style= SWT.V_SCROLL | SWT.H_SCROLL;
				return new DefaultInformationControl(parent, shellStyle, style, new HTMLTextPresenter(false));
			}
		};
	}

	/**
	 * Returns the editor in which the configured viewer(s) will reside.
	 * 
	 * @return the enclosing editor
	 */
	protected ITextEditor getEditor() {
		return fTextEditor;
	}

	protected IPreferenceStore getPreferenceStore() {
		return RubyPlugin.getDefault().getPreferenceStore();
	}

	public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { "#", "" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		final ITextEditor editor = getEditor();
		if (editor != null && editor.isEditable()) {
			
			RubyCompositeReconcilingStrategy strategy = new RubyCompositeReconcilingStrategy(editor, getConfiguredDocumentPartitioning(sourceViewer));
			RubyReconciler reconciler = new RubyReconciler(editor, strategy, false);
			reconciler.setIsIncrementalReconciler(false);
			reconciler.setIsAllowedToModifyDocument(false);
			reconciler.setProgressMonitor(new NullProgressMonitor());
			reconciler.setDelay(500);
			
			return reconciler;
		}
		return null;
	}

	private IRubyProject getProject() {
		ITextEditor editor = getEditor();
		if (editor == null)
			return null;

		IRubyElement element = null;
		IEditorInput input = editor.getEditorInput();
		IDocumentProvider provider = editor.getDocumentProvider();
		if (provider instanceof IRubyScriptDocumentProvider) {
			IRubyScriptDocumentProvider cudp = (IRubyScriptDocumentProvider) provider;
			element = cudp.getWorkingCopy(input);
		}

		if (element == null)
			return null;

		return element.getRubyProject();
	}

	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {

		Vector vector = new Vector();

		// prefix[0] is either '\t' or ' ' x tabWidth, depending on useSpaces

		IRubyProject project = getProject();
		final int tabWidth = CodeFormatterUtil.getTabWidth(project);
		final int indentWidth = CodeFormatterUtil.getIndentWidth(project);
		int spaceEquivalents = Math.min(tabWidth, indentWidth);
		boolean useSpaces;
		if (project == null)
			useSpaces = RubyCore.SPACE.equals(RubyCore.getOption(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR)) || tabWidth > indentWidth;
		else
			useSpaces = RubyCore.SPACE.equals(project.getOption(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, true)) || tabWidth > indentWidth;

		for (int i = 0; i <= spaceEquivalents; i++) {
			StringBuffer prefix = new StringBuffer();

			if (useSpaces) {
				for (int j = 0; j + i < spaceEquivalents; j++)
					prefix.append(' ');

				if (i != 0)
					prefix.append('\t');
			} else {
				for (int j = 0; j < i; j++)
					prefix.append(' ');

				if (i != spaceEquivalents)
					prefix.append('\t');
			}

			vector.add(prefix.toString());
		}

		vector.add(""); //$NON-NLS-1$

		return (String[]) vector.toArray(new String[vector.size()]);
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		if (fRubyDoubleClickSelector == null) {
			fRubyDoubleClickSelector = new RubyDoubleClickSelector();
		}
		return fRubyDoubleClickSelector;
	}

	/*
	 * @see SourceViewerConfiguration#getTabWidth(ISourceViewer)
	 */
	public int getTabWidth(ISourceViewer sourceViewer) {
		return CodeFormatterUtil.getTabWidth(getProject());
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		RubyEditorTextHoverDescriptor[] hoverDescs= RubyPlugin.getDefault().getRubyEditorTextHoverDescriptors();
		int i= 0;
		while (i < hoverDescs.length) {
			if (hoverDescs[i].isEnabled() &&  hoverDescs[i].getStateMask() == stateMask)
				return new RubyEditorTextHoverProxy(hoverDescs[i], getEditor());
			i++;
		}
		return null;
	}
	
	/*
	 * @see SourceViewerConfiguration#getConfiguredTextHoverStateMasks(ISourceViewer, String)
	 * @since 2.1
	 */
	public int[] getConfiguredTextHoverStateMasks(ISourceViewer sourceViewer, String contentType) {
		RubyEditorTextHoverDescriptor[] hoverDescs= RubyPlugin.getDefault().getRubyEditorTextHoverDescriptors();
		int stateMasks[]= new int[hoverDescs.length];
		int stateMasksLength= 0;
		for (int i= 0; i < hoverDescs.length; i++) {
			if (hoverDescs[i].isEnabled()) {
				int j= 0;
				int stateMask= hoverDescs[i].getStateMask();
				while (j < stateMasksLength) {
					if (stateMasks[j] == stateMask)
						break;
					j++;
				}
				if (j == stateMasksLength)
					stateMasks[stateMasksLength++]= stateMask;
			}
		}
		if (stateMasksLength == hoverDescs.length)
			return stateMasks;

		int[] shortenedStateMasks= new int[stateMasksLength];
		System.arraycopy(stateMasks, 0, shortenedStateMasks, 0, stateMasksLength);
		return shortenedStateMasks;
	}

	@Override
	public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
		if (getEditor() != null)
			return new RubyCorrectionAssistant(getEditor());
		return null;
	}
	
	/**
	 * Returns the outline presenter which will determine and shown
	 * information requested for the current cursor position.
	 *
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @param doCodeResolve a boolean which specifies whether code resolve should be used to compute the Java element
	 * @return an information presenter
	 * @since 2.1
	 */
	public IInformationPresenter getOutlinePresenter(ISourceViewer sourceViewer, boolean doCodeResolve) {
		InformationPresenter presenter;
		if (doCodeResolve)
			presenter= new InformationPresenter(getOutlinePresenterControlCreator(sourceViewer, IRubyEditorActionDefinitionIds.OPEN_STRUCTURE));
		else
			presenter= new InformationPresenter(getOutlinePresenterControlCreator(sourceViewer, IRubyEditorActionDefinitionIds.SHOW_OUTLINE));
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
		IInformationProvider provider= new RubyElementProvider(getEditor(), doCodeResolve);
		presenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
		presenter.setInformationProvider(provider, IRubyPartitions.RUBY_MULTI_LINE_COMMENT);
		presenter.setInformationProvider(provider, IRubyPartitions.RUBY_SINGLE_LINE_COMMENT);
		presenter.setInformationProvider(provider, IRubyPartitions.RUBY_STRING);
		presenter.setInformationProvider(provider, IRubyPartitions.RUBY_REGULAR_EXPRESSION);
		presenter.setInformationProvider(provider, IRubyPartitions.RUBY_COMMAND);
		presenter.setSizeConstraints(50, 20, true, false);
		return presenter;
	}
	
	/**
	 * Returns the outline presenter control creator. The creator is a factory creating outline
	 * presenter controls for the given source viewer. This implementation always returns a creator
	 * for <code>JavaOutlineInformationControl</code> instances.
	 *
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @param commandId the ID of the command that opens this control
	 * @return an information control creator
	 * @since 1.0
	 */
	private IInformationControlCreator getOutlinePresenterControlCreator(ISourceViewer sourceViewer, final String commandId) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle= SWT.RESIZE;
				int treeStyle= SWT.V_SCROLL | SWT.H_SCROLL;
				return new RubyOutlineInformationControl(parent, shellStyle, treeStyle, commandId);
			}
		};
	}

}
