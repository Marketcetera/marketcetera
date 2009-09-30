package org.marketcetera.photon.java.internal;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.internal.ui.text.HTMLAnnotationHover;
import org.eclipse.jdt.internal.ui.text.JavaCommentScanner;
import org.eclipse.jdt.internal.ui.text.JavaPresentationReconciler;
import org.eclipse.jdt.internal.ui.text.SingleTokenJavaScanner;
import org.eclipse.jdt.internal.ui.text.java.JavaCodeScanner;
import org.eclipse.jdt.internal.ui.text.java.JavaDoubleClickSelector;
import org.eclipse.jdt.internal.ui.text.java.JavaFormattingStrategy;
import org.eclipse.jdt.internal.ui.text.java.JavadocDoubleClickStrategy;
import org.eclipse.jdt.internal.ui.text.java.PartitionDoubleClickSelector;
import org.eclipse.jdt.internal.ui.text.javadoc.JavaDocScanner;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The source viewer configuration for {@link JavaEditor}. Based on
 * org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration.
 * <p>
 * The original class was modified to remove functionality that did not work
 * without the core Java model, e.g. quick assist, text hover, auto edit
 * strategies, etc.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class JavaSourceViewerConfiguration extends
        TextSourceViewerConfiguration {

    private ITextEditor fTextEditor;
    /**
     * The document partitioning.
     */
    private String fDocumentPartitioning;
    /**
     * The Java source code scanner.
     */
    private AbstractJavaScanner fCodeScanner;
    /**
     * The Java multi-line comment scanner.
     */
    private AbstractJavaScanner fMultilineCommentScanner;
    /**
     * The Java single-line comment scanner.
     */
    private AbstractJavaScanner fSinglelineCommentScanner;
    /**
     * The Java string scanner.
     */
    private AbstractJavaScanner fStringScanner;
    /**
     * The Javadoc scanner.
     */
    private AbstractJavaScanner fJavaDocScanner;
    /**
     * The color manager.
     */
    private IColorManager fColorManager;
    /**
     * The double click strategy.
     */
    private JavaDoubleClickSelector fJavaDoubleClickSelector;

    /**
     * Creates a new Java source viewer configuration for viewers in the given
     * editor using the given preference store, the color manager and the
     * specified document partitioning.
     * <p>
     * Creates a Java source viewer configuration in the new setup without text
     * tools. Clients are allowed to call
     * {@link JavaSourceViewerConfiguration#handlePropertyChangeEvent(PropertyChangeEvent)}
     * on the resulting Java source viewer configuration.
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
     */
    public JavaSourceViewerConfiguration(IColorManager colorManager,
            IPreferenceStore preferenceStore, ITextEditor editor,
            String partitioning) {
        super(preferenceStore);
        fColorManager = colorManager;
        fTextEditor = editor;
        fDocumentPartitioning = partitioning;
        initializeScanners();
    }

    /**
     * Returns the Java source code scanner for this configuration.
     * 
     * @return the Java source code scanner
     */
    protected RuleBasedScanner getCodeScanner() {
        return fCodeScanner;
    }

    /**
     * Returns the Java multi-line comment scanner for this configuration.
     * 
     * @return the Java multi-line comment scanner
     */
    protected RuleBasedScanner getMultilineCommentScanner() {
        return fMultilineCommentScanner;
    }

    /**
     * Returns the Java single-line comment scanner for this configuration.
     * 
     * @return the Java single-line comment scanner
     */
    protected RuleBasedScanner getSinglelineCommentScanner() {
        return fSinglelineCommentScanner;
    }

    /**
     * Returns the Java string scanner for this configuration.
     * 
     * @return the Java string scanner
     */
    protected RuleBasedScanner getStringScanner() {
        return fStringScanner;
    }

    /**
     * Returns the JavaDoc scanner for this configuration.
     * 
     * @return the JavaDoc scanner
     */
    protected RuleBasedScanner getJavaDocScanner() {
        return fJavaDocScanner;
    }

    /**
     * Returns the color manager for this configuration.
     * 
     * @return the color manager
     */
    protected IColorManager getColorManager() {
        return fColorManager;
    }

    /**
     * Returns the editor in which the configured viewer(s) will reside.
     * 
     * @return the enclosing editor
     */
    protected ITextEditor getEditor() {
        return fTextEditor;
    }

    /**
     * Initializes the scanners.
     */
    private void initializeScanners() {
        fCodeScanner = new JavaCodeScanner(getColorManager(), fPreferenceStore);
        fMultilineCommentScanner = new JavaCommentScanner(getColorManager(),
                fPreferenceStore, IJavaColorConstants.JAVA_MULTI_LINE_COMMENT);
        fSinglelineCommentScanner = new JavaCommentScanner(getColorManager(),
                fPreferenceStore, IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT);
        fStringScanner = new SingleTokenJavaScanner(getColorManager(),
                fPreferenceStore, IJavaColorConstants.JAVA_STRING);
        fJavaDocScanner = new JavaDocScanner(getColorManager(),
                fPreferenceStore);
    }

    @Override
    public IPresentationReconciler getPresentationReconciler(
            ISourceViewer sourceViewer) {

        PresentationReconciler reconciler = new JavaPresentationReconciler();
        reconciler
                .setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCodeScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        dr = new DefaultDamagerRepairer(getJavaDocScanner());
        reconciler.setDamager(dr, IJavaPartitions.JAVA_DOC);
        reconciler.setRepairer(dr, IJavaPartitions.JAVA_DOC);

        dr = new DefaultDamagerRepairer(getMultilineCommentScanner());
        reconciler.setDamager(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
        reconciler.setRepairer(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);

        dr = new DefaultDamagerRepairer(getSinglelineCommentScanner());
        reconciler.setDamager(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
        reconciler.setRepairer(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);

        dr = new DefaultDamagerRepairer(getStringScanner());
        reconciler.setDamager(dr, IJavaPartitions.JAVA_STRING);
        reconciler.setRepairer(dr, IJavaPartitions.JAVA_STRING);

        dr = new DefaultDamagerRepairer(getStringScanner());
        reconciler.setDamager(dr, IJavaPartitions.JAVA_CHARACTER);
        reconciler.setRepairer(dr, IJavaPartitions.JAVA_CHARACTER);

        return reconciler;
    }

    @Override
    public ITextDoubleClickStrategy getDoubleClickStrategy(
            ISourceViewer sourceViewer, String contentType) {
        if (IJavaPartitions.JAVA_DOC.equals(contentType))
            return new JavadocDoubleClickStrategy(
                    getConfiguredDocumentPartitioning(sourceViewer));
        if (IJavaPartitions.JAVA_SINGLE_LINE_COMMENT.equals(contentType))
            return new PartitionDoubleClickSelector(
                    getConfiguredDocumentPartitioning(sourceViewer), 0, 0);
        if (IJavaPartitions.JAVA_MULTI_LINE_COMMENT.equals(contentType))
            return new PartitionDoubleClickSelector(
                    getConfiguredDocumentPartitioning(sourceViewer), 0, 0);
        else if (IJavaPartitions.JAVA_STRING.equals(contentType)
                || IJavaPartitions.JAVA_CHARACTER.equals(contentType))
            return new PartitionDoubleClickSelector(
                    getConfiguredDocumentPartitioning(sourceViewer), 1, 1);
        if (fJavaDoubleClickSelector == null) {
            fJavaDoubleClickSelector = new JavaDoubleClickSelector();
            fJavaDoubleClickSelector.setSourceVersion(fPreferenceStore
                    .getString(JavaCore.COMPILER_SOURCE));
        }
        return fJavaDoubleClickSelector;
    }

    @Override
    public String[] getDefaultPrefixes(ISourceViewer sourceViewer,
            String contentType) {
        return new String[] { "//", "" }; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        return new HTMLAnnotationHover(false) {
            protected boolean isIncluded(Annotation annotation) {
                return isShowInVerticalRuler(annotation);
            }
        };
    }

    @Override
    public IAnnotationHover getOverviewRulerAnnotationHover(
            ISourceViewer sourceViewer) {
        return new HTMLAnnotationHover(true) {
            protected boolean isIncluded(Annotation annotation) {
                return isShowInOverviewRuler(annotation);
            }
        };
    }

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
                IJavaPartitions.JAVA_DOC,
                IJavaPartitions.JAVA_MULTI_LINE_COMMENT,
                IJavaPartitions.JAVA_SINGLE_LINE_COMMENT,
                IJavaPartitions.JAVA_STRING, IJavaPartitions.JAVA_CHARACTER };
    }

    @Override
    public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
        if (fDocumentPartitioning != null)
            return fDocumentPartitioning;
        return super.getConfiguredDocumentPartitioning(sourceViewer);
    }

    @Override
    public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
        final MultiPassContentFormatter formatter = new MultiPassContentFormatter(
                getConfiguredDocumentPartitioning(sourceViewer),
                IDocument.DEFAULT_CONTENT_TYPE);
        formatter.setMasterStrategy(new JavaFormattingStrategy());
        return formatter;
    }

    @Override
    public IInformationControlCreator getInformationControlCreator(
            ISourceViewer sourceViewer) {
        return new IInformationControlCreator() {
            public IInformationControl createInformationControl(Shell parent) {
                return new DefaultInformationControl(parent, false);
            }
        };
    }

    /**
     * Determines whether the preference change encoded by the given event
     * changes the behavior of one of its contained components.
     * 
     * @param event
     *            the event to be investigated
     * @return <code>true</code> if event causes a behavioral change
     */
    public boolean affectsTextPresentation(PropertyChangeEvent event) {
        return fCodeScanner.affectsBehavior(event)
                || fMultilineCommentScanner.affectsBehavior(event)
                || fSinglelineCommentScanner.affectsBehavior(event)
                || fStringScanner.affectsBehavior(event)
                || fJavaDocScanner.affectsBehavior(event);
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
     * @see JavaSourceViewerConfiguration#JavaSourceViewerConfiguration(IColorManager,
     *      IPreferenceStore, ITextEditor, String)
     */
    public void handlePropertyChangeEvent(PropertyChangeEvent event) {
        if (fCodeScanner.affectsBehavior(event))
            fCodeScanner.adaptToPreferenceChange(event);
        if (fMultilineCommentScanner.affectsBehavior(event))
            fMultilineCommentScanner.adaptToPreferenceChange(event);
        if (fSinglelineCommentScanner.affectsBehavior(event))
            fSinglelineCommentScanner.adaptToPreferenceChange(event);
        if (fStringScanner.affectsBehavior(event))
            fStringScanner.adaptToPreferenceChange(event);
        if (fJavaDocScanner.affectsBehavior(event))
            fJavaDocScanner.adaptToPreferenceChange(event);
        if (fJavaDoubleClickSelector != null
                && JavaCore.COMPILER_SOURCE.equals(event.getProperty()))
            if (event.getNewValue() instanceof String)
                fJavaDoubleClickSelector.setSourceVersion((String) event
                        .getNewValue());
    }

}
