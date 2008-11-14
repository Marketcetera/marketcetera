package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.internal.ui.actions.FoldingActionGroup;
import org.rubypeople.rdt.ui.actions.IRubyEditorActionDefinitionIds;
import org.rubypeople.rdt.ui.actions.RdtActionConstants;
import org.rubypeople.rdt.ui.actions.RubyActionIds;

public class RubyEditorActionContributor extends BasicTextEditorActionContributor {

	private List fPartListeners= new ArrayList();
	
    protected RetargetTextEditorAction contentAssistProposal;
    private RetargetTextEditorAction fGotoMatchingBracket;
    private RetargetTextEditorAction fShowOutline;
    private RetargetTextEditorAction fOpenHierarchy;
	private RetargetTextEditorAction fQuickAssistAction;
	
	private RetargetAction fRetargetShowRubyDoc;
	private RetargetTextEditorAction fShowRubyDoc;

    public RubyEditorActionContributor() {
        super();
        ResourceBundle b = RubyEditorMessages.getBundleForConstructedKeys();
        
        fRetargetShowRubyDoc= new RetargetAction(RdtActionConstants.SHOW_RUBY_DOC, RubyEditorMessages.ShowRDoc_label);
		fRetargetShowRubyDoc.setActionDefinitionId(IRubyEditorActionDefinitionIds.SHOW_RDOC);
		markAsPartListener(fRetargetShowRubyDoc);

		fShowOutline= new RetargetTextEditorAction(RubyEditorMessages.getBundleForConstructedKeys(), "ShowOutline."); //$NON-NLS-1$
		fShowOutline.setActionDefinitionId(IRubyEditorActionDefinitionIds.SHOW_OUTLINE);
		
		fOpenHierarchy= new RetargetTextEditorAction(RubyEditorMessages.getBundleForConstructedKeys(), "OpenHierarchy."); //$NON-NLS-1$
		fOpenHierarchy.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_HIERARCHY);
		
        contentAssistProposal = new RetargetTextEditorAction(RubyEditorMessages.getBundleForConstructedKeys(), "ContentAssistProposal.");
        fGotoMatchingBracket = new RetargetTextEditorAction(b, "GotoMatchingBracket."); //$NON-NLS-1$
        fGotoMatchingBracket
                .setActionDefinitionId(IRubyEditorActionDefinitionIds.GOTO_MATCHING_BRACKET);
        
        fQuickAssistAction= new RetargetTextEditorAction(RubyEditorMessages.getBundleForConstructedKeys(), "CorrectionAssistProposal."); //$NON-NLS-1$
		fQuickAssistAction.setActionDefinitionId(ITextEditorActionDefinitionIds.QUICK_ASSIST);

		fShowRubyDoc= new RetargetTextEditorAction(b, "ShowRDoc."); //$NON-NLS-1$
		fShowRubyDoc.setActionDefinitionId(IRubyEditorActionDefinitionIds.SHOW_RDOC);
    }
    
	protected final void markAsPartListener(RetargetAction action) {
		fPartListeners.add(action);
	}

    public void contributeToMenu(IMenuManager menu) {
        IMenuManager editMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
        if (editMenu != null) {
            editMenu.add(new Separator());
            editMenu.add(contentAssistProposal);
            editMenu.add(fQuickAssistAction);
        }
        
		IMenuManager navigateMenu= menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null) {
			navigateMenu.appendToGroup(IWorkbenchActionConstants.SHOW_EXT, fShowOutline);
			navigateMenu.appendToGroup(IWorkbenchActionConstants.SHOW_EXT, fOpenHierarchy);
		}
        
        IMenuManager gotoMenu= menu.findMenuUsingPath("navigate/goTo"); //$NON-NLS-1$
        if (gotoMenu != null) {
            gotoMenu.add(new Separator("additions2"));  //$NON-NLS-1$
            gotoMenu.appendToGroup("additions2", fGotoMatchingBracket); //$NON-NLS-1$
        }       
    }

    public void setActiveEditor(IEditorPart part) {
        super.setActiveEditor(part);

        ITextEditor textEditor = null;
        if (part instanceof ITextEditor) textEditor = (ITextEditor) part;

        contentAssistProposal.setAction(getAction(textEditor, "ContentAssistProposal"));
        fGotoMatchingBracket.setAction(getAction(textEditor,
                GotoMatchingBracketAction.GOTO_MATCHING_BRACKET));
        fQuickAssistAction.setAction(getAction(textEditor, ITextEditorActionConstants.QUICK_ASSIST));
        fShowOutline.setAction(getAction(textEditor, IRubyEditorActionDefinitionIds.SHOW_OUTLINE));
        fOpenHierarchy.setAction(getAction(textEditor, IRubyEditorActionDefinitionIds.OPEN_HIERARCHY));
        fShowRubyDoc.setAction(getAction(textEditor, "ShowRDoc")); //$NON-NLS-1$
        
        if (part instanceof RubyEditor) {
        		RubyEditor javaEditor= (RubyEditor) part;
			javaEditor.getActionGroup().fillActionBars(getActionBars());
			FoldingActionGroup foldingActions= javaEditor.getFoldingActionGroup();
			if (foldingActions != null)
				foldingActions.updateActionBars();
		}
        
        IActionBars actionBars = getActionBars();
        actionBars.setGlobalActionHandler(RubyActionIds.COMMENT, getAction(textEditor, "Comment"));
        actionBars.setGlobalActionHandler(RubyActionIds.UNCOMMENT, getAction(textEditor,
                "Uncomment"));
        actionBars.setGlobalActionHandler(RubyActionIds.TOGGLE_COMMENT, getAction(textEditor, "ToggleComment")); //$NON-NLS-1$
        actionBars.setGlobalActionHandler(RubyActionIds.FORMAT, getAction(textEditor, "Format")); //$NON-NLS-1$

        /** The global actions to be connected with editor actions */
        IAction action = getAction(textEditor, ITextEditorActionConstants.NEXT);
        actionBars.setGlobalActionHandler(ITextEditorActionDefinitionIds.GOTO_NEXT_ANNOTATION,
                action);
        actionBars.setGlobalActionHandler(ITextEditorActionConstants.NEXT, action);
        action = getAction(textEditor, ITextEditorActionConstants.PREVIOUS);
        actionBars.setGlobalActionHandler(ITextEditorActionDefinitionIds.GOTO_PREVIOUS_ANNOTATION,
                action);
        actionBars.setGlobalActionHandler(ITextEditorActionConstants.PREVIOUS, action);
        

		actionBars.setGlobalActionHandler(IDEActionFactory.ADD_TASK.getId(), getAction(textEditor, IDEActionFactory.ADD_TASK.getId()));
		actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(), getAction(textEditor, IDEActionFactory.BOOKMARK.getId()));
    }
    
    @Override
    public void init(IActionBars bars, IWorkbenchPage page) {
    	Iterator e= fPartListeners.iterator();
		while (e.hasNext())
			page.addPartListener((RetargetAction) e.next());

		super.init(bars, page);
//
//		bars.setGlobalActionHandler(ITextEditorActionDefinitionIds.TOGGLE_SHOW_SELECTED_ELEMENT_ONLY, fTogglePresentation);
//		bars.setGlobalActionHandler(IRubyEditorActionDefinitionIds.TOGGLE_MARK_OCCURRENCES, fToggleMarkOccurrencesAction);

		bars.setGlobalActionHandler(RdtActionConstants.SHOW_RUBY_DOC, fShowRubyDoc);
    }
    
	/*
	 * @see IEditorActionBarContributor#dispose()
	 */
	public void dispose() {

		Iterator e= fPartListeners.iterator();
		while (e.hasNext())
			getPage().removePartListener((RetargetAction) e.next());
		fPartListeners.clear();

		if (fRetargetShowRubyDoc != null) {
			fRetargetShowRubyDoc.dispose();
			fRetargetShowRubyDoc= null;
		}
		
		setActiveEditor(null);
		super.dispose();
	}
}
