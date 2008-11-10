package org.rubypeople.rdt.ui.text.ruby;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.core.CompletionProposal;
import org.rubypeople.rdt.core.CompletionRequestor;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.ruby.AbstractRubyCompletionProposal;
import org.rubypeople.rdt.internal.ui.text.ruby.CompletionProposalLabelProvider;
import org.rubypeople.rdt.internal.ui.text.ruby.FillArgsAndBlockProposal;
import org.rubypeople.rdt.internal.ui.text.ruby.FillMethodArgumentsProposal;
import org.rubypeople.rdt.internal.ui.text.ruby.ProposalInfo;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCompletionProposal;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyContentAssistInvocationContext;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.viewsupport.ImageDescriptorRegistry;

public class CompletionProposalCollector extends CompletionRequestor {

	/** Tells whether this class is in debug mode. */
	private static final boolean DEBUG= "true".equalsIgnoreCase(Platform.getDebugOption("org.rubypeople.rdt.ui/debug/ResultCollector"));  //$NON-NLS-1$//$NON-NLS-2$

	private final CompletionProposalLabelProvider fLabelProvider= new CompletionProposalLabelProvider();
	private final ImageDescriptorRegistry fRegistry= RubyPlugin.getImageDescriptorRegistry();

	/** Triggers for variables. Do not modify. */
	protected final static char[] VAR_TRIGGER= new char[] { '\t', ' ', '=', ';', '.' };
	
	private final List fRubyProposals= new ArrayList();
	private final List fKeywords= new ArrayList();
	private final Set fSuggestedMethodNames= new HashSet();

	private final IRubyScript fRubyScript;
	private final IRubyProject fRubyProject;
	private int fUserReplacementLength;

	private IProblem fLastProblem;

	/* performance instrumentation */
	private long fStartTime;
	private long fUITime;

	private RubyContentAssistInvocationContext context;
	
	/**
	 * Creates a new instance ready to collect proposals. If the passed
	 * <code>IRubyScript</code> is not contained in an
	 * {@link IRubyProject}, no javadoc will be available as
	 * {@link org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo() additional info}
	 * on the created proposals.
	 *
	 * @param cu the compilation unit that the result collector will operate on
	 */	
	public CompletionProposalCollector(RubyContentAssistInvocationContext context) {
		this(context.getProject(), context.getRubyScript());
		this.context = context;
	}
	
	private CompletionProposalCollector(IRubyProject project, IRubyScript cu) {
		fRubyProject= project;
		fRubyScript= cu;

		fUserReplacementLength= -1;
	}
	
	/**
	 * Returns the unsorted list of received proposals.
	 *
	 * @return the unsorted list of received proposals
	 */
	public final IRubyCompletionProposal[] getRubyCompletionProposals() {
		return (IRubyCompletionProposal[]) fRubyProposals.toArray(new IRubyCompletionProposal[fRubyProposals.size()]);
	}

	/**
	 * Returns the unsorted list of received keyword proposals.
	 *
	 * @return the unsorted list of received keyword proposals
	 */
	public final IRubyCompletionProposal[] getKeywordCompletionProposals() {
		return (IRubyCompletionProposal[]) fKeywords.toArray(new RubyCompletionProposal[fKeywords.size()]);
	}
	
	@Override
	public void accept(CompletionProposal proposal) {
		if (proposal == null) return;
		long start= DEBUG ? System.currentTimeMillis() : 0;
		try {
			if (isFiltered(proposal))
				return;

			if (proposal.getKind() == CompletionProposal.POTENTIAL_METHOD_DECLARATION) {
				// TODO Handle potential method declarations
//				acceptPotentialMethodDeclaration(proposal);
			} else {
				IRubyCompletionProposal rubyProposal= createRubyCompletionProposal(proposal);
				if (rubyProposal != null) {					
					fRubyProposals.add(rubyProposal);
					if (proposal.getKind() == CompletionProposal.KEYWORD)
						fKeywords.add(rubyProposal);
				}
			}
		} catch (IllegalArgumentException e) {
			// all signature processing method may throw IAEs
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=84657
			// don't abort, but log and show all the valid proposals
			RubyPlugin.log(new Status(IStatus.ERROR, RubyPlugin.getPluginId(), IStatus.OK, "Exception when processing proposal for: " + String.valueOf(proposal.getCompletion()), e)); //$NON-NLS-1$
		}

		if (DEBUG) fUITime += System.currentTimeMillis() - start;
	}
	
	/**
	 * Computes the relevance for a given <code>CompletionProposal</code>.
	 * <p>
	 * Subclasses may replace, but usually should not need to.
	 * </p>
	 * @param proposal the proposal to compute the relevance for
	 * @return the relevance for <code>proposal</code>
	 */
	protected int computeRelevance(CompletionProposal proposal) {
		final int baseRelevance= proposal.getRelevance() * 16;
		switch (proposal.getKind()) {
			case CompletionProposal.KEYWORD:
				return baseRelevance + 2;
			case CompletionProposal.TYPE_REF:
				return baseRelevance + 3;
			case CompletionProposal.METHOD_REF:
			case CompletionProposal.METHOD_NAME_REFERENCE:
			case CompletionProposal.METHOD_DECLARATION:
				return baseRelevance + 4;
			case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
				return baseRelevance + 4 /* + 99 */;
			case CompletionProposal.CONSTANT_REF:			
			case CompletionProposal.CLASS_VARIABLE_REF:
			case CompletionProposal.INSTANCE_VARIABLE_REF:
				return baseRelevance + 5;
			case CompletionProposal.LOCAL_VARIABLE_REF:
			case CompletionProposal.VARIABLE_DECLARATION:
				return baseRelevance + 6;
			default:
				return baseRelevance;
		}
	}
	
	/**
	 * Returns <code>true</code> if <code>proposal</code> is filtered, e.g.
	 * should not be proposed to the user, <code>false</code> if it is valid.
	 * <p>
	 * Subclasses may extends this method. The default implementation filters
	 * proposals set to be ignored via
	 * {@linkplain CompletionRequestor#setIgnored(int, boolean) setIgnored} and
	 * types set to be ignored in the preferences.
	 * </p>
	 *
	 * @param proposal the proposal to filter
	 * @return <code>true</code> to filter <code>proposal</code>,
	 *         <code>false</code> to let it pass
	 */
	protected boolean isFiltered(CompletionProposal proposal) {
		if (isIgnored(proposal.getKind()))
			return true;
		// TODO Handle Type filtering?
//		char[] declaringType= getDeclaringType(proposal);
//		return declaringType!= null && TypeFilter.isFiltered(declaringType);
		return false;
	}
	
	/**
	 * Creates a new ruby completion proposal from a core proposal. This may
	 * involve computing the display label and setting up some context.
	 * <p>
	 * This method is called for every proposal that will be displayed to the
	 * user, which may be hundreds. Implementations should therefore defer as
	 * much work as possible: Labels should be computed lazily to leverage
	 * virtual table usage, and any information only needed when
	 * <em>applying</em> a proposal should not be computed yet.
	 * </p>
	 * <p>
	 * Implementations may return <code>null</code> if a proposal should not
	 * be included in the list presented to the user.
	 * </p>
	 * <p>
	 * Subclasses may extend or replace this method.
	 * </p>
	 *
	 * @param proposal the core completion proposal to create a UI proposal for
	 * @return the created ruby completion proposal, or <code>null</code> if
	 *         no proposal should be displayed
	 */
	protected IRubyCompletionProposal createRubyCompletionProposal(CompletionProposal proposal) {
		switch (proposal.getKind()) {
//			case CompletionProposal.KEYWORD:
//				return createKeywordProposal(proposal);
//			case CompletionProposal.TYPE_REF:
//				return createTypeProposal(proposal);
//			case CompletionProposal.FIELD_REF:
//				return createFieldProposal(proposal);
			case CompletionProposal.METHOD_REF:
//			case CompletionProposal.METHOD_NAME_REFERENCE:
				IRubyCompletionProposal proposal2 = createMethodReferenceProposal(proposal);
				if (fSuggestedMethodNames.contains(proposal2.getDisplayString())) return null;
				fSuggestedMethodNames.add(proposal2.getDisplayString());
				return proposal2;
//			case CompletionProposal.METHOD_DECLARATION:
//				return createMethodDeclarationProposal(proposal);
//			case CompletionProposal.LOCAL_VARIABLE_REF:
//			case CompletionProposal.VARIABLE_DECLARATION:
//				return createLocalVariableProposal(proposal);
//			case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
//			default:
//				return null;
		}
		return createProposal(proposal);
	}
	
	
	private IRubyCompletionProposal createMethodReferenceProposal(CompletionProposal methodProposal) {
		boolean fillArgs = RubyPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CODEASSIST_FILL_ARGUMENT_NAMES);
		if (fillArgs) {
			
			boolean fillBlockArgs = RubyPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CODEASSIST_FILL_METHOD_BLOCK_ARGUMENTS);
			
			String completion= String.valueOf(methodProposal.getCompletion());
			// super class' behavior if this is not a normal completion or has no parameters
			if ((completion.length() == 0) || ((completion.length() == 1) && completion.charAt(0) == ')') || methodProposal.getParameterNames() == null || methodProposal.getParameterNames().length == 0)
				return createProposal(methodProposal);
			
			if (fillBlockArgs)
				return new FillArgsAndBlockProposal(methodProposal, context);
			else
				return new FillMethodArgumentsProposal(methodProposal, context);
		} else {
			return createProposal(methodProposal); 
		}
	}

	/**
	 * Returns the ruby script that the receiver operates on, or
	 * <code>null</code> if the <code>IRubyProject</code> constructor was
	 * used to create the receiver.
	 *
	 * @return the ruby script that the receiver operates on, or
	 *         <code>null</code>
	 */
	protected final IRubyScript getRubyScript() {
		return fRubyScript;
	}
	
	/**
	 * Returns a cached image for the given descriptor.
	 *
	 * @param descriptor the image descriptor to get an image for, may be
	 *        <code>null</code>
	 * @return the image corresponding to <code>descriptor</code>
	 */
	protected final Image getImage(ImageDescriptor descriptor) {
		return (descriptor == null) ? null : fRegistry.get(descriptor);
	}
	
	private IRubyCompletionProposal createProposal(CompletionProposal proposal) {
		String completion= proposal.getCompletion();
		int start= proposal.getReplaceStart();
		int length= getLength(proposal);
		String label= fLabelProvider.createLabel(proposal);
		int relevance= computeRelevance(proposal);
		Image image = getImage(fLabelProvider.createImageDescriptor(proposal));
		AbstractRubyCompletionProposal rubyProposal = new RubyCompletionProposal(completion, start, length, image, label, relevance);
		if(proposal.getElement() != null && proposal.getElement() instanceof IMember) {
			ProposalInfo info = new ProposalInfo((IMember) proposal.getElement());
			rubyProposal.setProposalInfo(info);
		}
		return rubyProposal;
	}
	
	/**
	 * Returns the replacement length of a given completion proposal. The
	 * replacement length is usually the difference between the return values of
	 * <code>proposal.getReplaceEnd</code> and
	 * <code>proposal.getReplaceStart</code>, but this behavior may be
	 * overridden by calling {@link #setReplacementLength(int)}.
	 *
	 * @param proposal the completion proposal to get the replacement length for
	 * @return the replacement length for <code>proposal</code>
	 */
	protected final int getLength(CompletionProposal proposal) {
		int start= proposal.getReplaceStart();
		int end= proposal.getReplaceEnd();
		int length;
		if (fUserReplacementLength == -1) {
			length= end - start;
		} else {
			length= fUserReplacementLength;
			// extend length to begin at start
			int behindCompletion= proposal.getCompletionLocation() + 1;
			if (start < behindCompletion) {
				length+= behindCompletion - start;
			}
		}
		return length;
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * Subclasses may extend, but must call the super implementation.
	 */
	public void beginReporting() {
		if (DEBUG) {
			fStartTime= System.currentTimeMillis();
			fUITime= 0;
		}

		fLastProblem= null;
		fRubyProposals.clear();
		fKeywords.clear();
		fSuggestedMethodNames.clear();
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * Subclasses may extend, but must call the super implementation.
	 */
	public void completionFailure(IProblem problem) {
		fLastProblem= problem;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Subclasses may extend, but must call the super implementation.
	 */
	public void endReporting() {
		if (DEBUG) {
			long total= System.currentTimeMillis() - fStartTime;
			System.err.println("Core Collector (core):\t" + (total - fUITime)); //$NON-NLS-1$
			System.err.println("Core Collector (ui):\t" + fUITime); //$NON-NLS-1$
		}
		fSuggestedMethodNames.clear();
	}
	
	/**
	 * Returns an error message about any error that may have occurred during
	 * code completion, or the empty string if none.
	 * <p>
	 * Subclasses may replace or extend.
	 * </p>
	 * @return an error message or the empty string
	 */
	public String getErrorMessage() {
		if (fLastProblem != null)
			return fLastProblem.getMessage();
		return ""; //$NON-NLS-1$
	}
}
