package org.rubypeople.rdt.core.search;

import org.rubypeople.rdt.internal.core.search.processing.IJob;

public interface IRubySearchConstants {
	
	/**
	 * The search result is a declaration.
	 * Can be used in conjunction with any of the nature of searched elements
	 * so as to better narrow down the search.
	 */
	int DECLARATIONS= 0;
	
	/**
	 * The search result is a reference.
	 * Can be used in conjunction with any of the nature of searched elements
	 * so as to better narrow down the search.
	 * References can contain implementers since they are more generic kind
	 * of matches.
	 */
	int REFERENCES= 1;
	
	/**
	 * The search result is a declaration, a reference, or an implementer 
	 * of an interface.
	 * Can be used in conjunction with any of the nature of searched elements
	 * so as to better narrow down the search.
	 */
	int ALL_OCCURRENCES= 2;
	
	/**
	 * When searching for field matches, it will exclusively find read accesses, as
	 * opposed to write accesses. Note that some expressions are considered both
	 * as field read/write accesses: for example, x++; x+= 1;
	 * 
	 * @since 2.0
	 */
	int READ_ACCESSES = 3;
	
	/**
	 * When searching for field matches, it will exclusively find write accesses, as
	 * opposed to read accesses. Note that some expressions are considered both
	 * as field read/write accesses: for example,  x++; x+= 1;
	 * 
	 * @since 2.0
	 */
	int WRITE_ACCESSES = 4;
	
	/**
	 * Ignore declaring type while searching result.
	 * Can be used in conjunction with any of the nature of match.
	 * @since 1.0
	 */
	int IGNORE_DECLARING_TYPE = 0x10;
	
/* Nature of searched element */
	
	/**
	 * The searched element is a type, which may include classes and modules.
	 */
	int TYPE= 0;
	
	/**
	 * The searched element is a method.
	 */
	int METHOD= 1;

	/**
	 * The searched element is a constructor.
	 */
	int CONSTRUCTOR= 2;

	/**
	 * The searched element is a field.
	 */
	int FIELD= 3;

	/**
	 * The searched element is a class. 
	 * More selective than using {@link #TYPE}.
	 */
	int CLASS= 4;

	/**
	 * The searched element is a module.
	 * More selective than using {@link #TYPE}.
	 */
	int MODULE= 5;
	
	/* Waiting policies */
	
	/**
	 * The search operation starts immediately, even if the underlying indexer
	 * has not finished indexing the workspace. Results will more likely
	 * not contain all the matches.
	 */
	int FORCE_IMMEDIATE_SEARCH = IJob.ForceImmediate;
	/**
	 * The search operation throws an <code>org.eclipse.core.runtime.OperationCanceledException</code>
	 * if the underlying indexer has not finished indexing the workspace.
	 */
	int CANCEL_IF_NOT_READY_TO_SEARCH = IJob.CancelIfNotReady;
	/**
	 * The search operation waits for the underlying indexer to finish indexing 
	 * the workspace before starting the search.
	 */
	int WAIT_UNTIL_READY_TO_SEARCH = IJob.WaitUntilReady;

}
