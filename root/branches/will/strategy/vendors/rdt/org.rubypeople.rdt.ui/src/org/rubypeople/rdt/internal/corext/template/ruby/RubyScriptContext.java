package org.rubypeople.rdt.internal.corext.template.ruby;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.ui.text.template.contentassist.MultiVariableGuess;

public class RubyScriptContext extends DocumentTemplateContext {

    private IRubyScript fRubyScript;
	/** A flag to force evaluation in head-less mode. */
	protected boolean fForceEvaluation;
	/** A global state for proposals that change if a master proposal changes. */
	protected MultiVariableGuess fMultiVariableGuess;

    /**
     * Creates a ruby script context.
     * 
     * @param type
     *            the context type
     * @param document
     *            the document
     * @param completionOffset
     *            the completion position within the document
     * @param completionLength
     *            the completion length within the document
     * @param rubyScript
     *            the ruby script (may be <code>null</code>)
     */
    protected RubyScriptContext(TemplateContextType type, IDocument document, int completionOffset,
            int completionLength, IRubyScript rubyScript) {
        super(type, document, completionOffset, completionLength);
        fRubyScript = rubyScript;
    }

    /**
     * Returns the ruby script if one is associated with this context,
     * <code>null</code> otherwise.
     * 
     * @return the ruby script of this context or <code>null</code>
     */
    public final IRubyScript getRubyScript() {
        return fRubyScript;
    }

    /**
	 * Sets whether evaluation is forced or not.
	 * 
	 * @param evaluate <code>true</code> in order to force evaluation,
	 *            <code>false</code> otherwise
	 */
	public void setForceEvaluation(boolean evaluate) {
		fForceEvaluation= evaluate;	
	}
	
	/**
	 * Returns the multi-variable guess.
	 * 
	 * @return the multi-variable guess
	 */
	public MultiVariableGuess getMultiVariableGuess() {
		return fMultiVariableGuess;
	}

	/**
	 * @param multiVariableGuess The multiVariableGuess to set.
	 */
	public void setMultiVariableGuess(MultiVariableGuess multiVariableGuess) {
		fMultiVariableGuess= multiVariableGuess;
	}

}
