package org.rubypeople.rdt.ui.extensions;

import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;

/**
 * Interface for extensions to the rubyTemplateProvider extension point.
 * 
 * @author mkent
 * 
 */
public interface IRubyTemplateProvider {

	/**
	 * This method should return an array of TemplatePersistenceData objects
	 * representing templates that the client wishes to contribute to the
	 * primary template store.
	 * 
	 * @return
	 */
	public TemplatePersistenceData[] getTemplateData();
}
