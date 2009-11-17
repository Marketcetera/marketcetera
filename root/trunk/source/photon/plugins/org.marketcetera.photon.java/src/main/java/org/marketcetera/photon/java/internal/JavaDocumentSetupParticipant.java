package org.marketcetera.photon.java.internal;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The document setup participant for {@link JavaEditor}. Based on
 * org.eclipse.jdt.internal.ui.javaeditor.JavaDocumentSetupParticipant.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class JavaDocumentSetupParticipant implements IDocumentSetupParticipant {

    @Override
    public void setup(IDocument document) {
        setupJavaDocumentPartitioner(document,
                IJavaPartitions.JAVA_PARTITIONING);
    }

    /**
     * Sets up the Java document partitioner for the given document for the
     * given partitioning.
     * 
     * @param document
     *            the document to be set up
     * @param partitioning
     *            the document partitioning
     */
    public void setupJavaDocumentPartitioner(IDocument document,
            String partitioning) {
        IDocumentPartitioner partitioner = createDocumentPartitioner();
        if (document instanceof IDocumentExtension3) {
            IDocumentExtension3 extension3 = (IDocumentExtension3) document;
            extension3.setDocumentPartitioner(partitioning, partitioner);
        } else {
            document.setDocumentPartitioner(partitioner);
        }
        partitioner.connect(document);
    }

    /**
     * Array with legal content types.
     */
    private final static String[] LEGAL_CONTENT_TYPES = new String[] {
            IJavaPartitions.JAVA_DOC, IJavaPartitions.JAVA_MULTI_LINE_COMMENT,
            IJavaPartitions.JAVA_SINGLE_LINE_COMMENT,
            IJavaPartitions.JAVA_STRING, IJavaPartitions.JAVA_CHARACTER };

    /**
     * Factory method for creating a Java-specific document partitioner using
     * this object's partitions scanner. This method is a convenience method.
     * 
     * @return a newly created Java document partitioner
     */
    public IDocumentPartitioner createDocumentPartitioner() {
        return new FastPartitioner(getPartitionScanner(), LEGAL_CONTENT_TYPES);
    }

    /**
     * Returns a scanner which is configured to scan Java-specific partitions,
     * which are multi-line comments, Javadoc comments, and regular Java source
     * code.
     * 
     * @return a Java partition scanner
     */
    public IPartitionTokenScanner getPartitionScanner() {
        return new FastJavaPartitionScanner();
    }
}
