package org.rubypeople.rdt.internal.ui.text.spelling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.ISpellCheckEngine;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.ISpellChecker;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.RankedWordProposal;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.ruby.IInvocationContext;
import org.rubypeople.rdt.ui.text.ruby.IProblemLocation;
import org.rubypeople.rdt.ui.text.ruby.IQuickFixProcessor;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposal;

public class WordQuickFixProcessor implements IQuickFixProcessor {

	public IRubyCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
		final int threshold= PreferenceConstants.getPreferenceStore().getInt(PreferenceConstants.SPELLING_PROPOSAL_THRESHOLD);

		int size= 0;
		List proposals= null;
		String[] arguments= null;

		IProblemLocation location= null;
		RankedWordProposal proposal= null;
		IRubyCompletionProposal[] result= null;

		boolean fixed= false;
		boolean match= false;
		boolean sentence= false;

		final ISpellCheckEngine engine= SpellCheckEngine.getInstance();
		final ISpellChecker checker= engine.createSpellChecker(engine.getLocale(), PreferenceConstants.getPreferenceStore());

		if (checker != null) {

			for (int index= 0; index < locations.length; index++) {

				location= locations[index];
				if (location.getProblemId() == RubySpellingReconcileStrategy.SPELLING_PROBLEM_ID) {

					arguments= location.getProblemArguments();
					if (arguments != null && arguments.length > 4) {

						sentence= Boolean.valueOf(arguments[3]).booleanValue();
						match= Boolean.valueOf(arguments[4]).booleanValue();
//						fixed= arguments[0].charAt(0) == HTML_TAG_PREFIX || arguments[0].charAt(0) == JAVADOC_TAG_PREFIX;

						if ((sentence && match) && !fixed)
							result= new IRubyCompletionProposal[] { new ChangeCaseProposal(arguments, location.getOffset(), location.getLength(), context, engine.getLocale())};
						else {

							proposals= new ArrayList(checker.getProposals(arguments[0], sentence));
							size= proposals.size();

							if (threshold > 0 && size > threshold) {

								Collections.sort(proposals);
								proposals= proposals.subList(size - threshold - 1, size - 1);
								size= proposals.size();
							}

							boolean extendable= !fixed ? checker.acceptsWords() : false;
							result= new IRubyCompletionProposal[size + (extendable ? 2 : 1)];

							for (index= 0; index < size; index++) {

								proposal= (RankedWordProposal)proposals.get(index);
								result[index]= new WordCorrectionProposal(proposal.getText(), arguments, location.getOffset(), location.getLength(), context, proposal.getRank());
							}

							if (extendable)
								result[index++]= new AddWordProposal(arguments[0], context);

							result[index++]= new WordIgnoreProposal(arguments[0], context);
						}
						break;
					}
				}
			}
		}
		return result;
	}

	public boolean hasCorrections(IRubyScript unit, int id) {
		return id == RubySpellingReconcileStrategy.SPELLING_PROBLEM_ID;
	}

}
