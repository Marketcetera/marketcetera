package org.rubypeople.rdt.ui.actions;

import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

public interface IRubyEditorActionDefinitionIds extends ITextEditorActionDefinitionIds {
	/**
	 * Value: org.rubypeople.rdt.ui.edit.text.ruby.comment
	 */
	public static final String COMMENT= "org.rubypeople.rdt.ui.edit.text.ruby.comment";

	/**
	 * Value: org.rubypeople.rdt.ui.edit.text.ruby.content.assist.proposals
	 */
	public static final String CONTENT_ASSIST_PROPOSALS= "org.rubypeople.rdt.ui.edit.text.ruby.content.assist.proposals";

	/**
	 * Value: org.rubypeople.rdt.ui.edit.text.ruby.uncomment
	 */
	public static final String UNCOMMENT = "org.rubypeople.rdt.ui.edit.text.ruby.uncomment";
	
	public static final String FORMAT = "org.rubypeople.rdt.ui.edit.text.ruby.format";

	/**
	 * Action definition ID of the source -> toggle comment action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.toggle.comment"</code>).
	 * @since 3.0
	 */
	public static final String TOGGLE_COMMENT= "org.rubypeople.rdt.ui.edit.text.ruby.toggle.comment"; //$NON-NLS-1$
		
	/**
	 * Action definition ID of the edit -> show RDoc action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.show.rdoc"</code>).
	 */
	public static final String SHOW_RDOC= "org.rubypeople.rdt.ui.edit.text.ruby.show.rdoc"; //$NON-NLS-1$

    public static final String SURROUND_WITH_BEGIN_RESCUE = "org.rubypeople.rdt.ui.edit.text.ruby.surround.with.begin.rescue"; //$NON-NLS-1$

    /**
     * Action definition ID of the edit -> go to matching bracket action
     * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.goto.matching.bracket"</code>).
     *
     * @since 0.8.0
     */
    public static final String GOTO_MATCHING_BRACKET= "org.rubypeople.rdt.ui.edit.text.ruby.goto.matching.bracket"; //$NON-NLS-1$

	/**
	 * Action definition ID of the navigate -> open action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.open.editor"</code>).
	 */
	public static final String OPEN_EDITOR= "org.rubypeople.rdt.ui.edit.text.ruby.open.editor"; //$NON-NLS-1$

	/**
	 * Action definition id of the collapse members action
	 * (value: <code>"org.rubypeople.rdt.ui.edit.text.ruby.folding.collapseMembers"</code>).
	 * @since 0.9.0
	 */
	public static final String FOLDING_COLLAPSE_MEMBERS= "org.rubypeople.rdt.ui.edit.text.ruby.folding.collapseMembers"; //$NON-NLS-1$

	/**
	 * Action definition id of the collapse comments action
	 * (value: <code>"org.rubypeople.rdt.ui.edit.text.ruby.folding.collapseComments"</code>).
	 * @since 0.9.0
	 */
	public static final String FOLDING_COLLAPSE_COMMENTS= "org.rubypeople.rdt.ui.edit.text.ruby.folding.collapseComments"; //$NON-NLS-1$

	// search
	
	/**
	 * Action definition ID of the search -> references in workspace action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.references.in.workspace"</code>).
	 */
	public static final String SEARCH_REFERENCES_IN_WORKSPACE= "org.rubypeople.rdt.ui.edit.text.ruby.search.references.in.workspace"; //$NON-NLS-1$

	/**
	 * Action definition ID of the search -> references in project action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.references.in.project"</code>).
	 */
	public static final String SEARCH_REFERENCES_IN_PROJECT= "org.rubypeople.rdt.ui.edit.text.ruby.search.references.in.project"; //$NON-NLS-1$

	/**
	 * Action definition ID of the search -> references in hierarchy action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.references.in.hierarchy"</code>).
	 */
	public static final String SEARCH_REFERENCES_IN_HIERARCHY= "org.rubypeople.rdt.ui.edit.text.ruby.search.references.in.hierarchy"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the search -> references in working set action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.references.in.working.set"</code>).
	 */
	public static final String SEARCH_REFERENCES_IN_WORKING_SET= "org.rubypeople.rdt.ui.edit.text.ruby.search.references.in.working.set"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the search -> read access in workspace action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.read.access.in.workspace"</code>).
	 */
	public static final String SEARCH_READ_ACCESS_IN_WORKSPACE= "org.rubypeople.rdt.ui.edit.text.ruby.search.read.access.in.workspace"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the search -> read access in project action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.read.access.in.project"</code>).
	 */
	public static final String SEARCH_READ_ACCESS_IN_PROJECT= "org.rubypeople.rdt.ui.edit.text.ruby.search.read.access.in.project"; //$NON-NLS-1$

	/**
	 * Action definition ID of the search -> read access in working set action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.read.access.in.working.set"</code>).
	 */
	public static final String SEARCH_READ_ACCESS_IN_WORKING_SET= "org.rubypeople.rdt.ui.edit.text.ruby.search.read.access.in.working.set"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the search -> occurrences in file quick menu action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.occurrences.in.file.quickMenu"</code>).
	 * @since 1.0
	 */
	public static final String SEARCH_OCCURRENCES_IN_FILE_QUICK_MENU= "org.rubypeople.rdt.ui.edit.text.ruby.search.occurrences.in.file.quickMenu"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the search -> occurrences in file > elements action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.occurrences.in.file"</code>).
	 * @since 1.0
	 */
	public static final String SEARCH_OCCURRENCES_IN_FILE= "org.rubypeople.rdt.ui.edit.text.ruby.search.occurrences.in.file"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the search -> write access in workspace action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.write.access.in.workspace"</code>).
	 */
	public static final String SEARCH_WRITE_ACCESS_IN_WORKSPACE= "org.rubypeople.rdt.ui.edit.text.ruby.search.write.access.in.workspace"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the search -> write access in project action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.write.access.in.project"</code>).
	 */
	public static final String SEARCH_WRITE_ACCESS_IN_PROJECT= "org.rubypeople.rdt.ui.edit.text.ruby.search.write.access.in.project"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the search -> write access in working set action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.write.access.in.working.set"</code>).
	 */
	public static final String SEARCH_WRITE_ACCESS_IN_WORKING_SET= "org.rubypeople.rdt.ui.edit.text.ruby.search.write.access.in.working.set"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the search -> declarations in workspace action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.declarations.in.workspace"</code>).
	 */
	public static final String SEARCH_DECLARATIONS_IN_WORKSPACE= "org.rubypeople.rdt.ui.edit.text.ruby.search.declarations.in.workspace"; //$NON-NLS-1$
	/**
	 * Action definition ID of the search -> declarations in project action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.declarations.in.project"</code>).
	 */
	public static final String SEARCH_DECLARATIONS_IN_PROJECTS= "org.rubypeople.rdt.ui.edit.text.ruby.search.declarations.in.project"; //$NON-NLS-1$
	/**
	 * Action definition ID of the search -> declarations in working set action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.search.declarations.in.working.set"</code>).
	 */
	public static final String SEARCH_DECLARATIONS_IN_WORKING_SET= "org.rubypeople.rdt.ui.edit.text.ruby.search.declarations.in.working.set"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the navigate -> Show Outline action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.show.outline"</code>).
	 * 
	 * @since 1.0
	 */
	public static final String SHOW_OUTLINE= "org.rubypeople.rdt.ui.edit.text.ruby.show.outline"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the Navigate -> Open Structure action
	 * (value <code>"org.rubypeople.rdt.ui.navigate.ruby.open.structure"</code>).
	 * 
	 * @since 1.0
	 */
	public static final String OPEN_STRUCTURE= "org.rubypeople.rdt.ui.navigate.ruby.open.structure"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the navigate -> Show Hierarchy action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.open.hierarchy"</code>).
	 * 
	 * @since 1.0
	 */
	public static final String OPEN_HIERARCHY= "org.rubypeople.rdt.ui.edit.text.ruby.open.hierarchy"; //$NON-NLS-1$

	/**
	 * Action definition ID of the navigate -> open type hierarchy action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.open.type.hierarchy"</code>).
	 */
	public static final String OPEN_TYPE_HIERARCHY= "org.rubypeople.rdt.ui.edit.text.ruby.open.type.hierarchy"; //$NON-NLS-1$
	
    /**
     * Action definition ID of the navigate -> open call hierarchy action
     * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.open.call.hierarchy"</code>).
     * @since 3.0
     */
    public static final String OPEN_CALL_HIERARCHY= "org.rubypeople.rdt.ui.edit.text.ruby.open.call.hierarchy"; //$NON-NLS-1$
	
	/**
	 * Action definition ID of the navigate -> show in ruby resources view action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.show.in.ruby.resources.view"</code>).
	 */
	public static final String SHOW_IN_RUBY_RESOURCES_VIEW= "org.rubypeople.rdt.ui.edit.text.ruby.show.in.ruby.resources.view"; //$NON-NLS-1$

}
