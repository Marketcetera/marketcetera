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
package org.rubypeople.rdt.internal.ui.search;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PerformanceStats;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.internal.ui.text.SearchResultUpdater;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.corext.util.SearchUtils;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.ui.RubyElementLabels;
import org.rubypeople.rdt.ui.search.ElementQuerySpecification;
import org.rubypeople.rdt.ui.search.IMatchPresentation;
import org.rubypeople.rdt.ui.search.IQueryParticipant;
import org.rubypeople.rdt.ui.search.ISearchRequestor;
import org.rubypeople.rdt.ui.search.PatternQuerySpecification;
import org.rubypeople.rdt.ui.search.QuerySpecification;

public class RubySearchQuery implements ISearchQuery {

	private static final String PERF_SEARCH_PARTICIPANT= "org.rubypeople.rdt.ui/perf/search/participants"; //$NON-NLS-1$

	private ISearchResult fResult;
	private final QuerySpecification fPatternData;
	
	public RubySearchQuery(QuerySpecification data) {
		if (data == null) {
			throw new IllegalArgumentException("data must not be null"); //$NON-NLS-1$
		}
		fPatternData= data;
	}
	
	private static class SearchRequestor implements ISearchRequestor {
		private IQueryParticipant fParticipant;
		private RubySearchResult fSearchResult;
		public void reportMatch(Match match) {
			IMatchPresentation participant= fParticipant.getUIParticipant();
			if (participant == null || match.getElement() instanceof IRubyElement || match.getElement() instanceof IResource) {
				fSearchResult.addMatch(match);
			} else {
				fSearchResult.addMatch(match, participant);
			}
		}
		
		protected SearchRequestor(IQueryParticipant participant, RubySearchResult result) {
			super();
			fParticipant= participant;
			fSearchResult= result;
		}
	}
	
	public IStatus run(IProgressMonitor monitor) {
		final RubySearchResult textResult= (RubySearchResult) getSearchResult();
		textResult.removeAll();
		// Don't need to pass in working copies in 3.0 here
		SearchEngine engine= new SearchEngine();
		try {

			int totalTicks= 1000;
			IProject[] projects= RubySearchScopeFactory.getInstance().getProjects(fPatternData.getScope());
			final SearchParticipantRecord[] participantDescriptors= SearchParticipantsExtensionPoint.getInstance().getSearchParticipants(projects);
			final int[] ticks= new int[participantDescriptors.length];
			for (int i= 0; i < participantDescriptors.length; i++) {
				final int iPrime= i;
				ISafeRunnable runnable= new ISafeRunnable() {
					public void handleException(Throwable exception) {
						ticks[iPrime]= 0;
						String message= SearchMessages.RubySearchQuery_error_participant_estimate; 
						RubyPlugin.log(new Status(IStatus.ERROR, RubyPlugin.getPluginId(), 0, message, exception));
					}

					public void run() throws Exception {
						ticks[iPrime]= participantDescriptors[iPrime].getParticipant().estimateTicks(fPatternData);
					}
				};
				
				SafeRunner.run(runnable);
				totalTicks+= ticks[i];
			}
			
			SearchPattern pattern;
			String stringPattern;
			
			if (fPatternData instanceof ElementQuerySpecification) {
				IRubyElement element= ((ElementQuerySpecification) fPatternData).getElement();
				stringPattern= RubyElementLabels.getElementLabel(element, RubyElementLabels.ALL_DEFAULT);
				if (!element.exists()) {
					return new Status(IStatus.ERROR, RubyPlugin.getPluginId(), 0, Messages.format(SearchMessages.RubySearchQuery_error_element_does_not_exist, stringPattern), null);  
				}
				pattern= SearchPattern.createPattern(element, fPatternData.getLimitTo(), SearchUtils.GENERICS_AGNOSTIC_MATCH_RULE);
			} else {
				PatternQuerySpecification patternSpec = (PatternQuerySpecification) fPatternData;
				stringPattern= patternSpec.getPattern();
				int matchMode= getMatchMode(stringPattern) | SearchPattern.R_ERASURE_MATCH;
				if (patternSpec.isCaseSensitive())
					matchMode |= SearchPattern.R_CASE_SENSITIVE;
				pattern= SearchPattern.createPattern(patternSpec.getPattern(), patternSpec.getSearchFor(), patternSpec.getLimitTo(), matchMode);
			}
			
			if (pattern == null) {
				return new Status(IStatus.ERROR, RubyPlugin.getPluginId(), 0, Messages.format(SearchMessages.RubySearchQuery_error_unsupported_pattern, stringPattern), null);  
			}
			monitor.beginTask(Messages.format(SearchMessages.RubySearchQuery_task_label, stringPattern), totalTicks); 
			IProgressMonitor mainSearchPM= new SubProgressMonitor(monitor, 1000);

			boolean ignorePotentials= NewSearchUI.arePotentialMatchesIgnored();
			NewSearchResultCollector collector= new NewSearchResultCollector(textResult, ignorePotentials);
			
			
			engine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, fPatternData.getScope(), collector, mainSearchPM);
			for (int i= 0; i < participantDescriptors.length; i++) {
				final ISearchRequestor requestor= new SearchRequestor(participantDescriptors[i].getParticipant(), textResult);
				final IProgressMonitor participantPM= new SubProgressMonitor(monitor, ticks[i]);

				final int iPrime= i;
				ISafeRunnable runnable= new ISafeRunnable() {
					public void handleException(Throwable exception) {
						participantDescriptors[iPrime].getDescriptor().disable();
						String message= SearchMessages.RubySearchQuery_error_participant_search; 
						RubyPlugin.log(new Status(IStatus.ERROR, RubyPlugin.getPluginId(), 0, message, exception));
					}

					public void run() throws Exception {

						final IQueryParticipant participant= participantDescriptors[iPrime].getParticipant();

						final PerformanceStats stats= PerformanceStats.getStats(PERF_SEARCH_PARTICIPANT, participant);
						stats.startRun();

						participant.search(requestor, fPatternData, participantPM);

						stats.endRun();
					}
				};
				
				SafeRunner.run(runnable);
			}
			
		} catch (CoreException e) {
			return e.getStatus();
		}
		String message= Messages.format(SearchMessages.RubySearchQuery_status_ok_message, String.valueOf(textResult.getMatchCount())); 
		return new Status(IStatus.OK, RubyPlugin.getPluginId(), 0, message, null);
	}
	
	private int getMatchMode(String pattern) {
		if (pattern.indexOf('*') != -1 || pattern.indexOf('?') != -1) {
			return SearchPattern.R_PATTERN_MATCH;
		} else if (SearchUtils.isCamelCasePattern(pattern)) {
			return SearchPattern.R_CAMELCASE_MATCH;
		}
		return SearchPattern.R_EXACT_MATCH;
	}

	public String getLabel() {
		return SearchMessages.RubySearchQuery_label; 
	}

	public String getResultLabel(int nMatches) {
		if (nMatches == 1) {
			String[] args= { getSearchPatternDescription(), fPatternData.getScopeDescription() };
			switch (fPatternData.getLimitTo()) {
//				case IRubySearchConstants.IMPLEMENTORS:
//					return Messages.format(SearchMessages.RubySearchOperation_singularImplementorsPostfix, args); 
				case IRubySearchConstants.DECLARATIONS:
					return Messages.format(SearchMessages.RubySearchOperation_singularDeclarationsPostfix, args); 
				case IRubySearchConstants.REFERENCES:
					return Messages.format(SearchMessages.RubySearchOperation_singularReferencesPostfix, args); 
				case IRubySearchConstants.ALL_OCCURRENCES:
					return Messages.format(SearchMessages.RubySearchOperation_singularOccurrencesPostfix, args); 
				case IRubySearchConstants.READ_ACCESSES:
					return Messages.format(SearchMessages.RubySearchOperation_singularReadReferencesPostfix, args); 
				case IRubySearchConstants.WRITE_ACCESSES:
					return Messages.format(SearchMessages.RubySearchOperation_singularWriteReferencesPostfix, args); 
				default:
					return Messages.format(SearchMessages.RubySearchOperation_singularOccurrencesPostfix, args); 
			}
		} else {
			Object[] args= { getSearchPatternDescription(), new Integer(nMatches), fPatternData.getScopeDescription() };
			switch (fPatternData.getLimitTo()) {
//				case IRubySearchConstants.IMPLEMENTORS:
//					return Messages.format(SearchMessages.RubySearchOperation_pluralImplementorsPostfix, args); 
				case IRubySearchConstants.DECLARATIONS:
					return Messages.format(SearchMessages.RubySearchOperation_pluralDeclarationsPostfix, args); 
				case IRubySearchConstants.REFERENCES:
					return Messages.format(SearchMessages.RubySearchOperation_pluralReferencesPostfix, args); 
				case IRubySearchConstants.ALL_OCCURRENCES:
					return Messages.format(SearchMessages.RubySearchOperation_pluralOccurrencesPostfix, args); 
				case IRubySearchConstants.READ_ACCESSES:
					return Messages.format(SearchMessages.RubySearchOperation_pluralReadReferencesPostfix, args); 
				case IRubySearchConstants.WRITE_ACCESSES:
					return Messages.format(SearchMessages.RubySearchOperation_pluralWriteReferencesPostfix, args); 
				default:
					return Messages.format(SearchMessages.RubySearchOperation_pluralOccurrencesPostfix, args); 
			}
		}
	}
	
	private String getSearchPatternDescription() {
		if (fPatternData instanceof ElementQuerySpecification) {
			IRubyElement element= ((ElementQuerySpecification) fPatternData).getElement();
			return RubyElementLabels.getElementLabel(element, RubyElementLabels.ALL_DEFAULT
					| RubyElementLabels.ALL_FULLY_QUALIFIED | RubyElementLabels.USE_RESOLVED);
		} 
		return ((PatternQuerySpecification) fPatternData).getPattern();
	}

	ImageDescriptor getImageDescriptor() {
		if (/*fPatternData.getLimitTo() == IRubySearchConstants.IMPLEMENTORS ||*/ fPatternData.getLimitTo() == IRubySearchConstants.DECLARATIONS)
			return RubyPluginImages.DESC_OBJS_SEARCH_DECL;
		else
			return RubyPluginImages.DESC_OBJS_SEARCH_REF;
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	public ISearchResult getSearchResult() {
		if (fResult == null) {
			fResult= new RubySearchResult(this);
			new SearchResultUpdater((RubySearchResult) fResult);
		}
		return fResult;
	}
	
	QuerySpecification getSpecification() {
		return fPatternData;
	}
}
