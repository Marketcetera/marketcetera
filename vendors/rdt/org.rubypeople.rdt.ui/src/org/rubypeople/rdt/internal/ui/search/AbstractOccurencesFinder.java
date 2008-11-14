package org.rubypeople.rdt.internal.ui.search;


public abstract class AbstractOccurencesFinder implements IOccurrencesFinder {
	
	protected boolean fMarkOccurrenceAnnotations;
	protected boolean fStickyOccurrenceAnnotations;
	protected boolean fMarkTypeOccurrences;
	protected boolean fMarkMethodOccurrences;
	protected boolean fMarkConstantOccurrences;
	protected boolean fMarkFieldOccurrences;
	protected boolean fMarkLocalVariableOccurrences;
	protected boolean fMarkMethodExitPoints;

	public void setFMarkConstantOccurrences(boolean markConstantOccurrences) {
		fMarkConstantOccurrences = markConstantOccurrences;
	}

	public void setFMarkFieldOccurrences(boolean markFieldOccurrences) {
		fMarkFieldOccurrences = markFieldOccurrences;
	}

	public void setFMarkLocalVariableOccurrences(boolean markLocalVariableOccurrences) {
		fMarkLocalVariableOccurrences = markLocalVariableOccurrences;		
	}

	public void setFMarkMethodExitPoints(boolean markMethodExitPoints) {
		fMarkMethodExitPoints = markMethodExitPoints;		
	}

	public void setFMarkMethodOccurrences(boolean markMethodOccurrences) {
		fMarkMethodOccurrences = markMethodOccurrences;		
	}

	public void setFMarkOccurrenceAnnotations(boolean markOccurrenceAnnotations) {
		fMarkOccurrenceAnnotations = markOccurrenceAnnotations;
	}

	public void setFMarkTypeOccurrences(boolean markTypeOccurrences) {
		fMarkTypeOccurrences = markTypeOccurrences;		
	}

	public void setFStickyOccurrenceAnnotations(boolean stickyOccurrenceAnnotations) {
		fStickyOccurrenceAnnotations = stickyOccurrenceAnnotations;		
	}

}
